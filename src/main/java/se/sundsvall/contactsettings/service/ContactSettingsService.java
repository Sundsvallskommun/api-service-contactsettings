package se.sundsvall.contactsettings.service;

import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.mergeIntoContactSettingEntity;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSetting;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntity;
import static se.sundsvall.contactsettings.service.util.FilterEvaluationUtils.evaluate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.service.mapper.ContactSettingMapper;

@Service
public class ContactSettingsService {

	private final ContactSettingRepository contactSettingRepository;
	private final DelegateRepository delegateRepository;

	public ContactSettingsService(ContactSettingRepository contactSettingRepository, DelegateRepository delegateRepository) {
		this.contactSettingRepository = contactSettingRepository;
		this.delegateRepository = delegateRepository;
	}

	public String createContactSetting(final String municipalityId, final ContactSettingCreateRequest contactSettingCreateRequest) {
		Optional.ofNullable(contactSettingCreateRequest.getPartyId()).ifPresent(partyId -> {
			if (contactSettingRepository.findByMunicipalityIdAndPartyId(municipalityId, partyId).isPresent()) {
				throw Problem.valueOf(CONFLICT, ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS.formatted(contactSettingCreateRequest.getPartyId()));
			}
		});

		return contactSettingRepository.save(toContactSettingEntity(municipalityId, contactSettingCreateRequest)).getId();
	}

	public ContactSetting readContactSetting(final String municipalityId, final String id) {
		return contactSettingRepository.findByMunicipalityIdAndId(municipalityId, id).map(ContactSettingMapper::toContactSetting)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND.formatted(id)));
	}

	public List<ContactSetting> readContactSettingChildren(final String municipalityId, final String id) {
		verifyThatContactSettingExists(municipalityId, id);
		return contactSettingRepository.findByMunicipalityIdAndCreatedById(municipalityId, id).stream()
			.map(ContactSettingMapper::toContactSetting)
			.toList();
	}

	public List<ContactSetting> findByChannelsDestination(final String municipalityId, final String destination) {
		return contactSettingRepository.findByMunicipalityIdAndChannelsDestination(municipalityId, destination).stream()
			.map(ContactSettingMapper::toContactSetting)
			.toList();
	}

	public List<ContactSetting> findByPartyIdAndQueryFilter(final String municipalityId, final String partyId, final Map<String, List<String>> inputQuery) {

		// Fetch root entity, or throw a 404.
		final var parent = contactSettingRepository.findByMunicipalityIdAndPartyId(municipalityId, partyId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND.formatted(partyId)));

		// Call the actual search-and-collect logic.
		return searchAndCollectFromDelegateChain(parent, Optional.ofNullable(inputQuery).orElse(emptyMap()), new HashSet<>()).stream()
			.map(ContactSettingMapper::toContactSetting)
			.toList();
	}

	private List<ContactSettingEntity> searchAndCollectFromDelegateChain(ContactSettingEntity contactSetting, final Map<String, List<String>> inputQuery, HashSet<String> lookupRegistry) {
		lookupRegistry.add(contactSetting.getId()); // Add contactSetting to lookupRegistry.
		return Stream.concat(
			Stream.of(contactSetting), // This will ensure that returned list always contains the provided contactSetting.
			delegateRepository.findByPrincipalId(contactSetting.getId()).stream() // Find all agents for this contactSetting.
				.filter(delegate -> evaluate(inputQuery, delegate.getFilters())) // Evaluate inputQuery against delegate filters (stored in DB).
				.map(DelegateEntity::getAgent) // Extract agent from delegate.
				.filter(agent -> !lookupRegistry.contains(agent.getId())) // The lookupRegistry must not already contain the ID of this agent (prevent circular references).
				.flatMap(agent -> searchAndCollectFromDelegateChain(agent, inputQuery, lookupRegistry).stream())) // Recurse.
			.toList();
	}

	public ContactSetting updateContactSetting(final String municipalityId, final String id, final ContactSettingUpdateRequest contactSettingUpdateRequest) {
		final var contactSettingEntity = contactSettingRepository.findByMunicipalityIdAndId(municipalityId, id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND.formatted(id)));

		var updatedContactSettingEntity = mergeIntoContactSettingEntity(contactSettingEntity, contactSettingUpdateRequest);

		return toContactSetting(contactSettingRepository.save(updatedContactSettingEntity));
	}

	public void deleteContactSetting(final String municipalityId, final String id) {

		// Fetch entity, or throw a 404.
		final var contactSetting = contactSettingRepository.findByMunicipalityIdAndId(municipalityId, id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND.formatted(id)));

		// Delete all related delegates (delegates where this entity is principal or agent).
		deleteAllRelatedDelegates(id);

		// For all "real" (i.e. non-virtual) instances: delete all created virtual instances.
		if (nonNull(contactSetting.getPartyId())) {
			contactSettingRepository.findByMunicipalityIdAndCreatedById(municipalityId, id).stream()
				.filter(child -> isNull(child.getPartyId()))
				.forEach(contactSettingEntity -> deleteContactSetting(contactSettingEntity.getMunicipalityId(), contactSettingEntity.getId()));
		}

		contactSettingRepository.deleteById(id);
	}

	private void deleteAllRelatedDelegates(String contactSettingId) {

		// Collect ID:s of all delegate-instances to delete.
		final var idList = Stream.concat(
			// All delegates where this contactSetting acts as an agent:
			delegateRepository.findByAgentId(contactSettingId).stream(),
			// All delegates where this contactSetting acts as a principal:
			delegateRepository.findByPrincipalId(contactSettingId).stream()).map(DelegateEntity::getId).toList();

		// Delete all delegates where this contactSetting occurs.
		if (!idList.isEmpty()) {
			delegateRepository.deleteAllById(idList);
		}
	}

	private void verifyThatContactSettingExists(final String municipalityId, final String id) {
		if (!contactSettingRepository.existsByMunicipalityIdAndId(municipalityId, id)) {
			throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND.formatted(id));
		}
	}
}

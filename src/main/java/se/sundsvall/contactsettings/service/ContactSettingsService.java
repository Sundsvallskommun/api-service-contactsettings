package se.sundsvall.contactsettings.service;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSetting;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntityFromCreateRequest;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntityFromUpdateRequest;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.toListFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.Filter;
import se.sundsvall.contactsettings.service.mapper.ContactSettingMapper;

@Service
@Transactional
public class ContactSettingsService {

	static final String ENTITY_NOT_FOUND = "No contact-setting with id '%s' could be found";
	static final String ENTITY_BY_PARTY_ID_NOT_FOUND = "No contact-setting for partyId '%s' could be found";
	static final String PARTY_ID_ALREADY_EXISTS = "A contact-setting with party-id '%s' already exists";

	@Autowired
	private ContactSettingRepository contactSettingRepository;

	@Autowired
	private DelegateRepository delegateRepository;

	public String createContactSetting(final ContactSettingCreateRequest contactSettingCreateRequest) {
		Optional.ofNullable(contactSettingCreateRequest.getPartyId()).ifPresent(partyId -> {
			if (contactSettingRepository.findByPartyId(partyId).isPresent()) {
				throw Problem.valueOf(Status.CONFLICT, String.format(PARTY_ID_ALREADY_EXISTS, contactSettingCreateRequest.getPartyId()));
			}
		});

		return contactSettingRepository.save(toContactSettingEntityFromCreateRequest(contactSettingCreateRequest)).getId();
	}

	public ContactSetting readContactSetting(final String id) {
		return contactSettingRepository.findById(id).map(ContactSettingMapper::toContactSetting)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, String.format(ENTITY_NOT_FOUND, id)));
	}

	public List<ContactSetting> readContactSettingChildren(final String id) {
		verifyThatContactSettingExists(id);
		return contactSettingRepository.findByCreatedById(id).stream()
			.map(ContactSettingMapper::toContactSetting)
			.toList();
	}

	public List<ContactSetting> findByChannelsDestination(final String destination) {
		return contactSettingRepository.findByChannelsDestination(destination).stream()
			.map(ContactSettingMapper::toContactSetting)
			.toList();
	}

	public List<ContactSetting> findByPartyIdAndFilter(final String partyId, final Map<String, List<String>> filter) {
		final var parent = contactSettingRepository.findByPartyId(partyId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, String.format(ENTITY_BY_PARTY_ID_NOT_FOUND, partyId)));

		// Call the actual search-and-collect logic.
		return searchAndCollectFromDelegateChain(parent, toListFilter(filter), new HashSet<>()).stream()
			.map(ContactSettingMapper::toContactSetting)
			.toList();
	}

	private List<ContactSettingEntity> searchAndCollectFromDelegateChain(ContactSettingEntity contactSetting, List<Filter> filter, HashSet<String> lookupRegistry) {
		lookupRegistry.add(contactSetting.getId()); // Add contactSetting to lookupRegistry.
		return Stream.concat(
			Stream.of(contactSetting), // This will ensure that returned list always contains the provided contactSetting.
			delegateRepository.findByPrincipalId(contactSetting.getId()).stream() // Find all agents for this contactSetting.
				.filter(delegate -> filtersAreEqual(delegate.getFilters(), filter)) // Filter must match provided filter.
				.map(DelegateEntity::getAgent) // Extract agent from delegate.
				.filter(agent -> !lookupRegistry.contains(agent.getId())) // The lookupRegistry must not already contain the ID of this agent (prevent circular references).
				.flatMap(agent -> searchAndCollectFromDelegateChain(agent, filter, lookupRegistry).stream())) // Recurse.
			.toList();
	}

	private boolean filtersAreEqual(List<Filter> filter1, List<Filter> filter2) {
		return isEqualCollection(
			Optional.ofNullable(filter1).orElse(emptyList()),
			Optional.ofNullable(filter2).orElse(emptyList()));
	}

	public ContactSetting updateContactSetting(final String id, final ContactSettingUpdateRequest contactSettingUpdateRequest) {
		verifyThatContactSettingExists(id);

		return toContactSetting(contactSettingRepository.save(toContactSettingEntityFromUpdateRequest(contactSettingUpdateRequest)));
	}

	public void deleteContactSetting(final String id) {

		// Fetch entity, or throw a 404.
		final var contactSetting = contactSettingRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ENTITY_NOT_FOUND, id)));

		// Delete all related delegates (delegates where this entity is principal or agent).
		deleteAllRelatedDelegates(id);

		// For all "real" (i.e. non-virtual) instances: delete all created virtual instances.
		if (nonNull(contactSetting.getPartyId())) {
			contactSettingRepository.findByCreatedById(id).stream()
				.filter(child -> isNull(child.getPartyId()))
				.map(ContactSettingEntity::getId)
				.forEach(this::deleteContactSetting);
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

	private void verifyThatContactSettingExists(final String id) {
		if (!contactSettingRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, format(ENTITY_NOT_FOUND, id));
		}
	}
}

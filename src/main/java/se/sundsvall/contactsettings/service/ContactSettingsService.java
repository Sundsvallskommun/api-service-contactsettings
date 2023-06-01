package se.sundsvall.contactsettings.service;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.api.model.enums.Operator.toEnum;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSetting;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntityFromCreateRequest;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntityFromUpdateRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.service.mapper.ContactSettingMapper;

@Service
public class ContactSettingsService {

	@Autowired
	private ContactSettingRepository contactSettingRepository;

	@Autowired
	private DelegateRepository delegateRepository;

	public String createContactSetting(final ContactSettingCreateRequest contactSettingCreateRequest) {
		Optional.ofNullable(contactSettingCreateRequest.getPartyId()).ifPresent(partyId -> {
			if (contactSettingRepository.findByPartyId(partyId).isPresent()) {
				throw Problem.valueOf(Status.CONFLICT, String.format(ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS, contactSettingCreateRequest.getPartyId()));
			}
		});

		return contactSettingRepository.save(toContactSettingEntityFromCreateRequest(contactSettingCreateRequest)).getId();
	}

	public ContactSetting readContactSetting(final String id) {
		return contactSettingRepository.findById(id).map(ContactSettingMapper::toContactSetting)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, String.format(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND, id)));
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

	public List<ContactSetting> findByPartyIdAndFilter(final String partyId, final Map<String, List<String>> queryFilter) {
		final var parent = contactSettingRepository.findByPartyId(partyId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, String.format(ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND, partyId)));

		// Call the actual search-and-collect logic.
		return searchAndCollectFromDelegateChain(parent, Optional.ofNullable(queryFilter).orElse(emptyMap()), new HashSet<>()).stream()
			.map(ContactSettingMapper::toContactSetting)
			.toList();
	}

	private List<ContactSettingEntity> searchAndCollectFromDelegateChain(ContactSettingEntity contactSetting, final Map<String, List<String>> queryFilter, HashSet<String> lookupRegistry) {
		lookupRegistry.add(contactSetting.getId()); // Add contactSetting to lookupRegistry.
		return Stream.concat(
			Stream.of(contactSetting), // This will ensure that returned list always contains the provided contactSetting.
			delegateRepository.findByPrincipalId(contactSetting.getId()).stream() // Find all agents for this contactSetting.
				.filter(delegate -> filtersMatches(queryFilter, Optional.ofNullable(delegate.getFilters()).orElse(emptyList()))) // Filter must match provided filter.
				.map(DelegateEntity::getAgent) // Extract agent from delegate.
				.filter(agent -> !lookupRegistry.contains(agent.getId())) // The lookupRegistry must not already contain the ID of this agent (prevent circular references).
				.flatMap(agent -> searchAndCollectFromDelegateChain(agent, queryFilter, lookupRegistry).stream())) // Recurse.
			.toList();
	}

	public boolean filtersMatches(final Map<String, List<String>> queryFilter, List<DelegateFilterEntity> delegateFilters) {
		return delegateFilters.stream().allMatch(delegateFilter -> filterMatches(queryFilter, delegateFilter));
	}

	private boolean filterMatches(final Map<String, List<String>> queryFilter, DelegateFilterEntity delegateFilter) {
		return delegateFilter.getFilterRules().stream().allMatch(rule -> switch (toEnum(rule.getOperator())) {
			case EQUALS -> Optional.ofNullable(queryFilter.get(rule.getAttributeName())).orElse(emptyList()).contains(rule.getAttributeValue());
			case NOT_EQUALS -> !Optional.ofNullable(queryFilter.get(rule.getAttributeName())).orElse(emptyList()).contains(rule.getAttributeValue());
		});
	}

	public ContactSetting updateContactSetting(final String id, final ContactSettingUpdateRequest contactSettingUpdateRequest) {
		verifyThatContactSettingExists(id);

		return toContactSetting(contactSettingRepository.save(toContactSettingEntityFromUpdateRequest(contactSettingUpdateRequest)));
	}

	public void deleteContactSetting(final String id) {

		// Fetch entity, or throw a 404.
		final var contactSetting = contactSettingRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND, id)));

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
			throw Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND, id));
		}
	}
}

package se.sundsvall.contactsettings.service;

import static com.nimbusds.oauth2.sdk.util.CollectionUtils.isNotEmpty;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSetting;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntityFromCreateRequest;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntityFromUpdateRequest;

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
import se.sundsvall.contactsettings.service.mapper.ContactSettingMapper;

@Service
@Transactional
public class ContactSettingsService {

	private static final String ENTITY_NOT_FOUND = "A contact-setting with id '%s' could not be found";
	private static final String PARTY_ID_ALREADY_EXISTS = "A contact-setting with party-id '%s' already exists";

	@Autowired
	private ContactSettingRepository contactSettingRepository;

	@Autowired
	private DelegateRepository delegateRepository;

	public String createContactSetting(final ContactSettingCreateRequest contactSettingCreateRequest) {
		Optional.ofNullable(contactSettingCreateRequest.getPartyId()).ifPresent(partyId -> {
			if (isNotEmpty(contactSettingRepository.findByPartyId(partyId))) {
				throw Problem.valueOf(Status.CONFLICT, String.format(PARTY_ID_ALREADY_EXISTS, contactSettingCreateRequest.getPartyId()));
			}
		});

		return contactSettingRepository.save(toContactSettingEntityFromCreateRequest(contactSettingCreateRequest)).getId();
	}

	public ContactSetting readContactSetting(final String id) {
		return contactSettingRepository.findById(id).map(ContactSettingMapper::toContactSetting)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, String.format(ENTITY_NOT_FOUND, id)));
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

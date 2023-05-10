package se.sundsvall.contactsettings.service;

import static java.util.Objects.isNull;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSetting;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntityFromCreateRequest;
import static se.sundsvall.contactsettings.service.mapper.ContactSettingMapper.toContactSettingEntityFromUpdateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.service.mapper.ContactSettingMapper;

@Service
@Transactional
public class ContactSettingsService {

	private static final String ENTITY_NOT_FOUND = "A contact-setting with id '%s' could not be found";
	private static final String PARTY_ID_ALREADY_EXISTS = "A contact-setting with party-id '%s' already exists";

	@Autowired
	private ContactSettingRepository repository;

	public String createContactSetting(final ContactSettingCreateRequest contactSettingCreateRequest) {
		if (!isNull(contactSettingCreateRequest.getPartyId())) {
			repository.findByPartyId(contactSettingCreateRequest.getPartyId())
				.ifPresent(contactSettingEntity -> {
					throw Problem.valueOf(Status.CONFLICT, String.format(PARTY_ID_ALREADY_EXISTS, contactSettingCreateRequest.getPartyId()));
				});
		}
		final var entity = repository.save(toContactSettingEntityFromCreateRequest(contactSettingCreateRequest));
		return entity.getId();
	}

	public ContactSetting readContactSetting(final String id) {
		return repository.findById(id).map(ContactSettingMapper::toContactSetting)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, String.format(ENTITY_NOT_FOUND, id)));
	}

	public ContactSetting updateContactSetting(final String id, final ContactSettingUpdateRequest contactSettingUpdateRequest) {
		verifyExistingContactSetting(id);

		return toContactSetting(repository.save(toContactSettingEntityFromUpdateRequest(contactSettingUpdateRequest)));
	}

	public void deleteContactSetting(final String id) {
		verifyExistingContactSetting(id);
		repository.deleteById(id);
	}

	private void verifyExistingContactSetting(final String id) {
		if (!repository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, String.format(ENTITY_NOT_FOUND, id));
		}
	}
}

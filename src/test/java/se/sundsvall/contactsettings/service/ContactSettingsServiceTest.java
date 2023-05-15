package se.sundsvall.contactsettings.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.contactsettings.api.model.ContactChannel;
import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.api.model.enums.ContactMethod;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.model.Channel;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ContactSettingsServiceTest {

	private static final String ID = "contactSettingId";

	@Mock
	private ContactSettingRepository repositoryMock;

	@InjectMocks
	private ContactSettingsService service;

	@Test
	void createContactSetting() {
		//Mock
		when(repositoryMock.findByPartyId(any(String.class))).thenReturn(Optional.empty());
		when(repositoryMock.save(any(ContactSettingEntity.class))).thenReturn(ContactSettingEntity.create().withId(ID));

		//Call
		final var result = service.createContactSetting(buildContactSettingCreateRequest());

		//Assert and verify
		assertThat(result).isEqualTo(ID);

		verify(repositoryMock).findByPartyId(any(String.class));
		verify(repositoryMock).save(any(ContactSettingEntity.class));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void createContactSettingPartyIdExists() {
		//Parameters
		final var contactSettingCreateRequest = buildContactSettingCreateRequest();
		//Mock
		when(repositoryMock.findByPartyId(any(String.class))).thenReturn(Optional.of(ContactSettingEntity.create().withId(ID)));

		//Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.createContactSetting(contactSettingCreateRequest));

		//Assert and verify
		assertThat(exception.getStatus()).isEqualTo(CONFLICT);
		assertThat(exception.getTitle()).isEqualTo(CONFLICT.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format("A contact-setting with party-id '%s' already exists", buildContactSettingCreateRequest().getPartyId()));
	}

	@Test
	void readContactSetting() {
		//Mock
		when(repositoryMock.findById(ID)).thenReturn(Optional.of(ContactSettingEntity.create().withId(ID)));

		//Call
		final var result = service.readContactSetting(ID);

		//Assert and verify
		assertThat(result).isEqualTo(ContactSetting.create().withId(ID).withVirtual(true).withContactChannels(emptyList()));

		verify(repositoryMock).findById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void readContactSettingNotFound() {
		//Mock
		when(repositoryMock.findById(ID)).thenReturn(Optional.empty());

		//Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readContactSetting(ID));

		//Assert and verify
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format("A contact-setting with id '%s' could not be found", ID));
		verify(repositoryMock).findById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void updateContactSetting() {
		//Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);
		when(repositoryMock.save(any(ContactSettingEntity.class))).thenReturn(buildContactSettingEntity());

		//Call
		final var result = service.updateContactSetting(ID, buildContactSettingUpdateRequest());

		//Assert and verify
		assertThat(result).isEqualTo(buildContactSetting());

		verify(repositoryMock).existsById(ID);
		verify(repositoryMock).save(any(ContactSettingEntity.class));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void updateContactSettingNotFound() {
		//Parameters
		final var contactSettingUpdateRequest = buildContactSettingUpdateRequest();
		//Mock
		when(repositoryMock.existsById(ID)).thenReturn(false);

		//Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.updateContactSetting(ID, contactSettingUpdateRequest));

		//Assert and verify
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format("A contact-setting with id '%s' could not be found", ID));
		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteContactSetting() {
		//Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);

		//Call
		service.deleteContactSetting(ID);

		//Assert and verify
		verify(repositoryMock).existsById(ID);
		verify(repositoryMock).deleteById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteContactSettingNotFound() {
		//Mock
		when(repositoryMock.existsById(ID)).thenReturn(false);

		//Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteContactSetting(ID));

		//Assert and verify
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format("A contact-setting with id '%s' could not be found", ID));
		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	private ContactSettingCreateRequest buildContactSettingCreateRequest() {
		return ContactSettingCreateRequest.create()
			.withPartyId("partyId")
			.withAlias("alias")
			.withCreatedById("createdById")
			.withContactChannels(List.of(ContactChannel.create()
				.withContactMethod(ContactMethod.EMAIL)
				.withDestination("destination")
				.withAlias("channelAlias")));
	}

	private static ContactSettingUpdateRequest buildContactSettingUpdateRequest() {
		return ContactSettingUpdateRequest.create()
			.withAlias("alias")
			.withContactChannels(List.of(ContactChannel.create()
				.withContactMethod(ContactMethod.EMAIL)
				.withDestination("destination")
				.withAlias("channelAlias")));
	}

	private static ContactSetting buildContactSetting() {
		return ContactSetting.create()
			.withId(ID)
			.withVirtual(false)
			.withPartyId("partyId")
			.withAlias("alias")
			.withCreatedById("createdById")
			.withContactChannels(List.of(ContactChannel.create()
				.withContactMethod(ContactMethod.EMAIL)
				.withDestination("destination")
				.withAlias("channelAlias")));
	}

	private ContactSettingEntity buildContactSettingEntity() {
		return ContactSettingEntity.create()
			.withId(ID)
			.withPartyId("partyId")
			.withAlias("alias")
			.withCreatedById("createdById")
			.withChannels(List.of(Channel.create()
				.withContactMethod(ContactMethod.EMAIL.toString())
				.withDestination("destination")
				.withAlias("channelAlias")));
	}
}

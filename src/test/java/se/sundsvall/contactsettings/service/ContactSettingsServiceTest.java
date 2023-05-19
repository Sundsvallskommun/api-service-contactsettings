package se.sundsvall.contactsettings.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.contactsettings.integration.db.model.Channel;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;

@ExtendWith(MockitoExtension.class)
class ContactSettingsServiceTest {

	private static final String ID = "contactSettingId";

	@Mock
	private ContactSettingRepository contactSettingRepositoryMock;

	@Mock
	private DelegateRepository delegateRepositoryMock;

	@InjectMocks
	private ContactSettingsService service;

	@Test
	void createContactSetting() {

		// Arrange
		when(contactSettingRepositoryMock.findByPartyId(any(String.class))).thenReturn(emptyList());
		when(contactSettingRepositoryMock.save(any(ContactSettingEntity.class))).thenReturn(ContactSettingEntity.create().withId(ID));

		// Act
		final var result = service.createContactSetting(buildContactSettingCreateRequest());

		//Assert
		assertThat(result).isEqualTo(ID);

		verify(contactSettingRepositoryMock).findByPartyId(any(String.class));
		verify(contactSettingRepositoryMock).save(any(ContactSettingEntity.class));
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void createContactSettingPartyIdExists() {

		// Arrange
		final var contactSettingCreateRequest = buildContactSettingCreateRequest();
		when(contactSettingRepositoryMock.findByPartyId(any(String.class))).thenReturn(List.of(ContactSettingEntity.create().withId(ID)));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.createContactSetting(contactSettingCreateRequest));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(CONFLICT);
		assertThat(exception.getTitle()).isEqualTo(CONFLICT.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format("A contact-setting with party-id '%s' already exists", buildContactSettingCreateRequest().getPartyId()));
	}

	@Test
	void readContactSetting() {

		// Arrange
		when(contactSettingRepositoryMock.findById(ID)).thenReturn(Optional.of(ContactSettingEntity.create().withId(ID)));

		// Act
		final var result = service.readContactSetting(ID);

		// Assert
		assertThat(result).isEqualTo(ContactSetting.create().withId(ID).withVirtual(true).withContactChannels(emptyList()));

		verify(contactSettingRepositoryMock).findById(ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSettingNotFound() {

		//Arrange
		when(contactSettingRepositoryMock.findById(ID)).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readContactSetting(ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format("A contact-setting with id '%s' could not be found", ID));
		verify(contactSettingRepositoryMock).findById(ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void updateContactSetting() {

		// Arrange
		when(contactSettingRepositoryMock.existsById(ID)).thenReturn(true);
		when(contactSettingRepositoryMock.save(any(ContactSettingEntity.class))).thenReturn(buildContactSettingEntity());

		// Act
		final var result = service.updateContactSetting(ID, buildContactSettingUpdateRequest());

		// Assert
		assertThat(result).isEqualTo(buildContactSetting());

		verify(contactSettingRepositoryMock).existsById(ID);
		verify(contactSettingRepositoryMock).save(any(ContactSettingEntity.class));
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void updateContactSettingNotFound() {

		// Arrange
		final var contactSettingUpdateRequest = buildContactSettingUpdateRequest();

		when(contactSettingRepositoryMock.existsById(ID)).thenReturn(false);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.updateContactSetting(ID, contactSettingUpdateRequest));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format("A contact-setting with id '%s' could not be found", ID));
		verify(contactSettingRepositoryMock).existsById(ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void deleteContactSetting() {

		// Arrange
		final var CHILD_ENTITY_ID_1 = UUID.randomUUID().toString();
		final var CHILD_ENTITY_ID_2 = UUID.randomUUID().toString();

		when(contactSettingRepositoryMock.findById(ID)).thenReturn(Optional.of(ContactSettingEntity.create().withId(ID).withPartyId("partyId")));
		when(contactSettingRepositoryMock.findById(CHILD_ENTITY_ID_1)).thenReturn(Optional.of(ContactSettingEntity.create().withId(CHILD_ENTITY_ID_1)));
		when(contactSettingRepositoryMock.findById(CHILD_ENTITY_ID_2)).thenReturn(Optional.of(ContactSettingEntity.create().withId(CHILD_ENTITY_ID_2)));
		when(contactSettingRepositoryMock.findByCreatedById(ID)).thenReturn(List.of(
			ContactSettingEntity.create().withId(CHILD_ENTITY_ID_1),
			ContactSettingEntity.create().withId(CHILD_ENTITY_ID_2)));
		when(delegateRepositoryMock.findByAgentId(ID)).thenReturn(List.of(
			DelegateEntity.create().withId("1")));
		when(delegateRepositoryMock.findByPrincipalId(ID)).thenReturn(List.of(
			DelegateEntity.create().withId("2"),
			DelegateEntity.create().withId("3")));

		// Act
		service.deleteContactSetting(ID);

		// Assert
		verify(contactSettingRepositoryMock).findById(ID);
		verify(contactSettingRepositoryMock).findByCreatedById(ID);
		verify(contactSettingRepositoryMock).deleteById(ID);
		verify(contactSettingRepositoryMock).deleteById(CHILD_ENTITY_ID_1);
		verify(contactSettingRepositoryMock).deleteById(CHILD_ENTITY_ID_2);
		// Delete delegates for main entity.
		verify(delegateRepositoryMock).findByPrincipalId(ID);
		verify(delegateRepositoryMock).findByAgentId(ID);
		verify(delegateRepositoryMock).deleteAllById(List.of("1", "2", "3"));
		// Delete delegates for child entities
		verify(delegateRepositoryMock).findByPrincipalId(CHILD_ENTITY_ID_1);
		verify(delegateRepositoryMock).findByAgentId(CHILD_ENTITY_ID_1);
		verify(delegateRepositoryMock).findByPrincipalId(CHILD_ENTITY_ID_2);
		verify(delegateRepositoryMock).findByAgentId(CHILD_ENTITY_ID_2);

		verifyNoMoreInteractions(contactSettingRepositoryMock);
		verifyNoMoreInteractions(delegateRepositoryMock);
	}

	@Test
	void deleteContactSettingNotFound() {

		// Arrange
		when(contactSettingRepositoryMock.findById(ID)).thenReturn(empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteContactSetting(ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format("A contact-setting with id '%s' could not be found", ID));
		verify(contactSettingRepositoryMock).findById(ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
		verifyNoInteractions(delegateRepositoryMock);
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

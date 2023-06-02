package se.sundsvall.contactsettings.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.SMS;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
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
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

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
		when(contactSettingRepositoryMock.findByPartyId(any(String.class))).thenReturn(empty());
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
		when(contactSettingRepositoryMock.findByPartyId(any(String.class))).thenReturn(Optional.of(ContactSettingEntity.create().withId(ID)));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.createContactSetting(contactSettingCreateRequest));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(CONFLICT);
		assertThat(exception.getTitle()).isEqualTo(CONFLICT.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format(ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS, contactSettingCreateRequest.getPartyId()));
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

		// Arrange
		when(contactSettingRepositoryMock.findById(ID)).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readContactSetting(ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND, ID));
		verify(contactSettingRepositoryMock).findById(ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSettingChildren() {

		// Arrange
		final var children = List.of(
			ContactSettingEntity.create().withCreatedById(ID).withAlias("Child-1"),
			ContactSettingEntity.create().withCreatedById(ID).withAlias("Child-2"));

		when(contactSettingRepositoryMock.existsById(ID)).thenReturn(true);
		when(contactSettingRepositoryMock.findByCreatedById(ID)).thenReturn(children);

		// Act
		final var result = service.readContactSettingChildren(ID);

		// Assert
		assertThat(result).isEqualTo(List.of(
			ContactSetting.create().withCreatedById(ID).withAlias("Child-1").withVirtual(true).withContactChannels(emptyList()),
			ContactSetting.create().withCreatedById(ID).withAlias("Child-2").withVirtual(true).withContactChannels(emptyList())));

		verify(contactSettingRepositoryMock).existsById(ID);
		verify(contactSettingRepositoryMock).findByCreatedById(ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSettingChildrenNotFoundParent() {

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readContactSettingChildren(ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND, ID));
		verify(contactSettingRepositoryMock).existsById(ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSettingChildrenNotFoundChildren() {

		// Arrange
		when(contactSettingRepositoryMock.existsById(ID)).thenReturn(true);
		when(contactSettingRepositoryMock.findByCreatedById(ID)).thenReturn(emptyList());

		// Act
		final var result = service.readContactSettingChildren(ID);

		// Assert
		assertThat(result).isEmpty();

		verify(contactSettingRepositoryMock).existsById(ID);
		verify(contactSettingRepositoryMock).findByCreatedById(ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByChannelsDestination() {

		// Arrange
		final var destination = "0701234567";
		when(contactSettingRepositoryMock.findByChannelsDestination(destination)).thenReturn(List.of(ContactSettingEntity.create().withChannels(List.of(
			Channel.create().withDestination(destination).withContactMethod("SMS")))));

		// Act
		final var result = service.findByChannelsDestination(destination);

		// Assert
		assertThat(result).hasSize(1);

		verify(contactSettingRepositoryMock).findByChannelsDestination(destination);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByChannelsDestinationNotFound() {

		// Arrange
		final var destination = "0701234567";
		when(contactSettingRepositoryMock.findByChannelsDestination(any())).thenReturn(emptyList());

		// Act
		final var result = service.findByChannelsDestination(destination);

		// Assert
		assertThat(result).isEmpty();

		verify(contactSettingRepositoryMock).findByChannelsDestination(destination);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndFilter_noQueryProvided() {

		// Arrange
		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withId(randomUUID().toString());

		final var delegate = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal);

		when(contactSettingRepositoryMock.findByPartyId(principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate));

		// Act
		final var result = service.findByPartyIdAndFilter(principalPartyId, null);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))),
				tuple(agent.getId(), agentPartyId, "Agent", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070222222222"))));

		verify(contactSettingRepositoryMock).findByPartyId(principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verify(delegateRepositoryMock).findByPrincipalId(agent.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndFilter_queryWithWithFilterEqualsOperatorMatch() {

		// Arrange
		final var inputFilter = new LinkedMultiValueMap<String, String>();
		inputFilter.put("key1", List.of("value1", "value2"));
		inputFilter.put("key2", List.of("value3", "value4", "value5"));

		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withId(randomUUID().toString());

		final var delegate = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal)
			.withFilters(List.of(
				DelegateFilterEntity.create().withFilterRules(List.of(
					DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
					DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value4")))));

		when(contactSettingRepositoryMock.findByPartyId(principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate));

		// Act
		final var result = service.findByPartyIdAndFilter(principalPartyId, inputFilter);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))),
				tuple(agent.getId(), agentPartyId, "Agent", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070222222222"))));

		verify(contactSettingRepositoryMock).findByPartyId(principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verify(delegateRepositoryMock).findByPrincipalId(agent.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndFilter_queryWithWithFilterNotEqualsOperatorMatch() {

		// Arrange
		final var inputFilter = new LinkedMultiValueMap<String, String>();
		inputFilter.put("key1", List.of("value1", "value2"));
		inputFilter.put("key2", List.of("value3", "value4", "value5"));

		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withId(randomUUID().toString());

		final var delegate = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal)
			.withFilters(List.of(
				DelegateFilterEntity.create().withFilterRules(List.of(
					DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("theForbiddenValue")))));

		when(contactSettingRepositoryMock.findByPartyId(principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate));

		// Act
		final var result = service.findByPartyIdAndFilter(principalPartyId, inputFilter);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))),
				tuple(agent.getId(), agentPartyId, "Agent", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070222222222"))));

		verify(contactSettingRepositoryMock).findByPartyId(principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verify(delegateRepositoryMock).findByPrincipalId(agent.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndFilter_queryWithNoFilterMatch() {

		// Arrange
		final var inputFilter = new LinkedMultiValueMap<String, String>();
		inputFilter.put("unknown-key", List.of("unknown-value"));

		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withId(randomUUID().toString());

		final var delegate = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal)
			.withFilters(List.of(
				DelegateFilterEntity.create().withFilterRules(List.of(
					DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value1"),
					DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value2")))));

		when(contactSettingRepositoryMock.findByPartyId(principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate));

		// Act
		final var result = service.findByPartyIdAndFilter(principalPartyId, inputFilter);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))));

		verify(contactSettingRepositoryMock).findByPartyId(principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndFilter_circularReferences() {

		// Arrange
		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withId(randomUUID().toString());

		final var delegate1 = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal);

		final var delegate2 = DelegateEntity.create() // This delegate will cause the agent to delegate back to the principal.
			.withAgent(principal)
			.withPrincipal(agent);

		when(contactSettingRepositoryMock.findByPartyId(principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate1));
		when(delegateRepositoryMock.findByPrincipalId(agent.getId())).thenReturn(List.of(delegate2));

		// Act
		final var result = service.findByPartyIdAndFilter(principalPartyId, null);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))),
				tuple(agent.getId(), agentPartyId, "Agent", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070222222222"))));

		verify(contactSettingRepositoryMock).findByPartyId(principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verify(delegateRepositoryMock).findByPrincipalId(agent.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndFilterNotFound() {

		// Arrange
		final var partyId = randomUUID().toString();
		when(contactSettingRepositoryMock.findByPartyId(any())).thenReturn(empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.findByPartyIdAndFilter(partyId, null));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(String.format(ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND, partyId));
		verify(contactSettingRepositoryMock).findByPartyId(partyId);
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
		assertThat(exception.getDetail()).isEqualTo(String.format(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND, ID));
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
		assertThat(exception.getDetail()).isEqualTo(String.format(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND, ID));
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

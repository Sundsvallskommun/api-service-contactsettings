package se.sundsvall.contactsettings.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.EMAIL;
import static se.sundsvall.contactsettings.api.model.enums.ContactMethod.SMS;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND;

import java.util.List;
import java.util.Optional;
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
	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private ContactSettingRepository contactSettingRepositoryMock;

	@Mock
	private DelegateRepository delegateRepositoryMock;

	@InjectMocks
	private ContactSettingsService service;

	@Test
	void createContactSetting() {

		// Arrange
		when(contactSettingRepositoryMock.findByMunicipalityIdAndPartyId(any(String.class), any(String.class))).thenReturn(empty());
		when(contactSettingRepositoryMock.save(any(ContactSettingEntity.class))).thenReturn(ContactSettingEntity.create().withId(ID));

		// Act
		final var result = service.createContactSetting(MUNICIPALITY_ID, buildContactSettingCreateRequest());

		// Assert
		assertThat(result).isEqualTo(ID);

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndPartyId(eq(MUNICIPALITY_ID), any(String.class));
		verify(contactSettingRepositoryMock).save(any(ContactSettingEntity.class));
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void createContactSettingPartyIdExists() {

		// Arrange
		final var contactSettingCreateRequest = buildContactSettingCreateRequest();

		when(contactSettingRepositoryMock.findByMunicipalityIdAndPartyId(any(String.class), any(String.class))).thenReturn(Optional.of(ContactSettingEntity.create().withId(ID)));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.createContactSetting(MUNICIPALITY_ID, contactSettingCreateRequest));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(CONFLICT);
		assertThat(exception.getTitle()).isEqualTo(CONFLICT.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS.formatted(contactSettingCreateRequest.getPartyId()));

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndPartyId(eq(MUNICIPALITY_ID), any(String.class));
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSetting() {

		// Arrange
		when(contactSettingRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, ID)).thenReturn(Optional.of(ContactSettingEntity.create().withId(ID)));

		// Act
		final var result = service.readContactSetting(MUNICIPALITY_ID, ID);

		// Assert
		assertThat(result).isEqualTo(ContactSetting.create().withId(ID).withVirtual(true).withContactChannels(emptyList()));

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSettingNotFound() {

		// Arrange
		when(contactSettingRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, ID)).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readContactSetting(MUNICIPALITY_ID, ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND.formatted(ID));
		verify(contactSettingRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSettingChildren() {

		// Arrange
		final var children = List.of(
			ContactSettingEntity.create().withCreatedById(ID).withAlias("Child-1"),
			ContactSettingEntity.create().withCreatedById(ID).withAlias("Child-2"));

		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(MUNICIPALITY_ID, ID)).thenReturn(true);
		when(contactSettingRepositoryMock.findByMunicipalityIdAndCreatedById(MUNICIPALITY_ID, ID)).thenReturn(children);

		// Act
		final var result = service.readContactSettingChildren(MUNICIPALITY_ID, ID);

		// Assert
		assertThat(result).isEqualTo(List.of(
			ContactSetting.create().withCreatedById(ID).withAlias("Child-1").withVirtual(true).withContactChannels(emptyList()),
			ContactSetting.create().withCreatedById(ID).withAlias("Child-2").withVirtual(true).withContactChannels(emptyList())));

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verify(contactSettingRepositoryMock).findByMunicipalityIdAndCreatedById(MUNICIPALITY_ID, ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSettingChildrenNotFoundParent() {

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readContactSettingChildren(MUNICIPALITY_ID, ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND.formatted(ID));
		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void readContactSettingChildrenNotFoundChildren() {

		// Arrange
		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(MUNICIPALITY_ID, ID)).thenReturn(true);
		when(contactSettingRepositoryMock.findByMunicipalityIdAndCreatedById(MUNICIPALITY_ID, ID)).thenReturn(emptyList());

		// Act
		final var result = service.readContactSettingChildren(MUNICIPALITY_ID, ID);

		// Assert
		assertThat(result).isEmpty();

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verify(contactSettingRepositoryMock).findByMunicipalityIdAndCreatedById(MUNICIPALITY_ID, ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByChannelsDestination() {

		// Arrange
		final var destination = "0701234567";
		when(contactSettingRepositoryMock.findByMunicipalityIdAndChannelsDestination(MUNICIPALITY_ID, destination)).thenReturn(List.of(ContactSettingEntity.create().withChannels(List.of(
			Channel.create().withDestination(destination).withContactMethod("SMS")))));

		// Act
		final var result = service.findByChannelsDestination(MUNICIPALITY_ID, destination);

		// Assert
		assertThat(result).hasSize(1);

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndChannelsDestination(MUNICIPALITY_ID, destination);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByChannelsDestinationNotFound() {

		// Arrange
		final var destination = "0701234567";
		when(contactSettingRepositoryMock.findByMunicipalityIdAndChannelsDestination(any(), any())).thenReturn(emptyList());

		// Act
		final var result = service.findByChannelsDestination(MUNICIPALITY_ID, destination);

		// Assert
		assertThat(result).isEmpty();

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndChannelsDestination(MUNICIPALITY_ID, destination);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndQueryFilter_noQueryProvided() {

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

		when(contactSettingRepositoryMock.findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate));

		// Act
		final var result = service.findByPartyIdAndQueryFilter(MUNICIPALITY_ID, principalPartyId, null);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))),
				tuple(agent.getId(), agentPartyId, "Agent", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070222222222"))));

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verify(delegateRepositoryMock).findByPrincipalId(agent.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndQueryFilter_queryWithWithFilterEqualsOperatorMatch() {

		// Arrange
		final var inputQuery = new LinkedMultiValueMap<String, String>();
		inputQuery.put("key1", List.of("value1", "value2"));
		inputQuery.put("key2", List.of("value3", "value4", "value5"));

		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withId(randomUUID().toString());

		final var delegate = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal)
			.withFilters(List.of(
				DelegateFilterEntity.create().withFilterRules(List.of(
					DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
					DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value4")))));

		when(contactSettingRepositoryMock.findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate));

		// Act
		final var result = service.findByPartyIdAndQueryFilter(MUNICIPALITY_ID, principalPartyId, inputQuery);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getMunicipalityId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), "2281", principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))),
				tuple(agent.getId(), "2281", agentPartyId, "Agent", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070222222222"))));

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verify(delegateRepositoryMock).findByPrincipalId(agent.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndQueryFilter_queryWithWithFilterNotEqualsOperatorMatch() {

		// Arrange
		final var inputQuery = new LinkedMultiValueMap<String, String>();
		inputQuery.put("key1", List.of("value1", "value2"));
		inputQuery.put("key2", List.of("value3", "value4", "value5"));

		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withId(randomUUID().toString());

		final var delegate = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal)
			.withFilters(List.of(
				DelegateFilterEntity.create().withFilterRules(List.of(
					DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("theForbiddenValue")))));

		when(contactSettingRepositoryMock.findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate));

		// Act
		final var result = service.findByPartyIdAndQueryFilter(MUNICIPALITY_ID, principalPartyId, inputQuery);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getMunicipalityId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), MUNICIPALITY_ID, principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))),
				tuple(agent.getId(), MUNICIPALITY_ID, agentPartyId, "Agent", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070222222222"))));

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verify(delegateRepositoryMock).findByPrincipalId(agent.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndQueryFilter_queryWithNoFilterMatch() {

		// Arrange
		final var inputQuery = new LinkedMultiValueMap<String, String>();
		inputQuery.put("unknown-key", List.of("unknown-value"));

		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withId(randomUUID().toString());

		final var delegate = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal)
			.withFilters(List.of(
				DelegateFilterEntity.create().withFilterRules(List.of(
					DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value1"),
					DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value2")))));

		when(contactSettingRepositoryMock.findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate));

		// Act
		final var result = service.findByPartyIdAndQueryFilter(MUNICIPALITY_ID, principalPartyId, inputQuery);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getMunicipalityId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), MUNICIPALITY_ID, principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))));

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndQueryFilter_circularReferences() {

		// Arrange
		final var principalPartyId = randomUUID().toString();
		final var agentPartyId = randomUUID().toString();

		final var principal = ContactSettingEntity.create()
			.withAlias("Principal")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070111111111")))
			.withPartyId(principalPartyId)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withId(randomUUID().toString());

		final var agent = ContactSettingEntity.create()
			.withAlias("Agent")
			.withChannels(List.of(Channel.create().withContactMethod(SMS.toString()).withDestination("070222222222")))
			.withPartyId(agentPartyId)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withId(randomUUID().toString());

		final var delegate1 = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal);

		final var delegate2 = DelegateEntity.create() // This delegate will cause the agent to delegate back to the principal.
			.withAgent(principal)
			.withPrincipal(agent);

		when(contactSettingRepositoryMock.findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principal.getPartyId())).thenReturn(Optional.of(principal));
		when(delegateRepositoryMock.findByPrincipalId(principal.getId())).thenReturn(List.of(delegate1));
		when(delegateRepositoryMock.findByPrincipalId(agent.getId())).thenReturn(List.of(delegate2));

		// Act
		final var result = service.findByPartyIdAndQueryFilter(MUNICIPALITY_ID, principalPartyId, null);

		// Assert
		assertThat(result)
			.extracting(ContactSetting::getId, ContactSetting::getMunicipalityId, ContactSetting::getPartyId, ContactSetting::getAlias, ContactSetting::getContactChannels)
			.containsExactlyInAnyOrder(
				tuple(principal.getId(), "2281", principalPartyId, "Principal", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070111111111"))),
				tuple(agent.getId(), "2281", agentPartyId, "Agent", List.of(ContactChannel.create().withContactMethod(SMS).withDestination("070222222222"))));

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, principalPartyId);
		verify(delegateRepositoryMock).findByPrincipalId(principal.getId());
		verify(delegateRepositoryMock).findByPrincipalId(agent.getId());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void findByPartyIdAndQueryFilterNotFound() {

		// Arrange
		final var partyId = randomUUID().toString();
		when(contactSettingRepositoryMock.findByMunicipalityIdAndPartyId(any(), any())).thenReturn(empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.findByPartyIdAndQueryFilter(MUNICIPALITY_ID, partyId, null));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND.formatted(partyId));
		verify(contactSettingRepositoryMock).findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, partyId);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void updateContactSetting() {

		// Arrange
		final var contactSettingEntity = ContactSettingEntity.create().withId(ID);

		when(contactSettingRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, ID)).thenReturn(Optional.of(contactSettingEntity));
		when(contactSettingRepositoryMock.save(any(ContactSettingEntity.class))).thenReturn(buildContactSettingEntity());

		// Act
		final var result = service.updateContactSetting(MUNICIPALITY_ID, ID, buildContactSettingUpdateRequest());

		// Assert
		assertThat(result).isEqualTo(buildContactSetting());

		verify(contactSettingRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verify(contactSettingRepositoryMock).save(contactSettingEntity);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void updateContactSettingNotFound() {

		// Arrange
		final var contactSettingUpdateRequest = buildContactSettingUpdateRequest();

		when(contactSettingRepositoryMock.findByMunicipalityIdAndId(any(), any())).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.updateContactSetting(MUNICIPALITY_ID, ID, contactSettingUpdateRequest));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND.formatted(ID));
		verify(contactSettingRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
	}

	@Test
	void deleteContactSetting() {

		// Arrange
		final var CHILD_ENTITY_ID_1 = randomUUID().toString();
		final var CHILD_ENTITY_ID_2 = randomUUID().toString();

		when(contactSettingRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, ID)).thenReturn(Optional.of(ContactSettingEntity.create()
			.withId(ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withPartyId("partyId")));
		when(contactSettingRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, CHILD_ENTITY_ID_1)).thenReturn(Optional.of(ContactSettingEntity.create().withId(CHILD_ENTITY_ID_1)));
		when(contactSettingRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, CHILD_ENTITY_ID_2)).thenReturn(Optional.of(ContactSettingEntity.create().withId(CHILD_ENTITY_ID_2)));
		when(contactSettingRepositoryMock.findByMunicipalityIdAndCreatedById(MUNICIPALITY_ID, ID)).thenReturn(List.of(
			ContactSettingEntity.create().withId(CHILD_ENTITY_ID_1).withMunicipalityId(MUNICIPALITY_ID),
			ContactSettingEntity.create().withId(CHILD_ENTITY_ID_2).withMunicipalityId(MUNICIPALITY_ID)));
		when(delegateRepositoryMock.findByAgentId(ID)).thenReturn(List.of(
			DelegateEntity.create().withId("1")));
		when(delegateRepositoryMock.findByPrincipalId(ID)).thenReturn(List.of(
			DelegateEntity.create().withId("2"),
			DelegateEntity.create().withId("3")));

		// Act
		service.deleteContactSetting(MUNICIPALITY_ID, ID);

		// Assert
		verify(contactSettingRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verify(contactSettingRepositoryMock).findByMunicipalityIdAndCreatedById(MUNICIPALITY_ID, ID);
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
		when(contactSettingRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, ID)).thenReturn(empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteContactSetting(MUNICIPALITY_ID, ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo(ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND.formatted(ID));
		verify(contactSettingRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, ID);
		verifyNoMoreInteractions(contactSettingRepositoryMock);
		verifyNoInteractions(delegateRepositoryMock);
	}

	private ContactSettingCreateRequest buildContactSettingCreateRequest() {
		return ContactSettingCreateRequest.create()
			.withPartyId("partyId")
			.withAlias("alias")
			.withCreatedById("createdById")
			.withContactChannels(List.of(ContactChannel.create()
				.withContactMethod(EMAIL)
				.withDestination("destination")
				.withAlias("channelAlias")));
	}

	private static ContactSettingUpdateRequest buildContactSettingUpdateRequest() {
		return ContactSettingUpdateRequest.create()
			.withAlias("alias")
			.withContactChannels(List.of(ContactChannel.create()
				.withContactMethod(EMAIL)
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
				.withContactMethod(EMAIL)
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
				.withContactMethod(EMAIL.toString())
				.withDestination("destination")
				.withAlias("channelAlias")));
	}
}

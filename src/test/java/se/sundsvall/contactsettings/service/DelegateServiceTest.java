package se.sundsvall.contactsettings.service;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.contactsettings.api.model.Delegate;
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.FindDelegatesParameters;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.api.model.enums.Operator;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

@ExtendWith(MockitoExtension.class)
class DelegateServiceTest {

	@Mock
	private DelegateRepository delegateRepositoryMock;

	@Mock
	private ContactSettingRepository contactSettingRepositoryMock;

	@Captor
	private ArgumentCaptor<DelegateEntity> delegateEntityCaptor;

	@InjectMocks
	private DelegateService service;

	@Test
	void create() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "municipalityId";
		final var delegateCreateRequest = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId)
			.withFilters(List.of(
				Filter.create()
					.withAlias("Filter1")
					.withRules(List.of(Rule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS)
						.withAttributeValue("value1"))),
				Filter.create()
					.withAlias("Filter2")
					.withRules(List.of(Rule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS)
						.withAttributeValue("value2")))));

		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(any(), any())).thenReturn(true);
		when(delegateRepositoryMock.findByPrincipalIdAndAgentId(any(), any())).thenReturn(emptyList());
		when(delegateRepositoryMock.save(any())).thenReturn(DelegateEntity.create());

		// Act
		final var result = service.create(municipalityId, delegateCreateRequest);

		// Assert.
		assertThat(result).isNotNull();

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, agentId);
		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, principalId);
		verify(delegateRepositoryMock).findByPrincipalIdAndAgentId(principalId, agentId);
		verify(delegateRepositoryMock).save(delegateEntityCaptor.capture());

		final var capturedDelegateEntity = delegateEntityCaptor.getValue();
		assertThat(capturedDelegateEntity).isNotNull();
		assertThat(capturedDelegateEntity.getAgent()).isEqualTo(ContactSettingEntity.create().withId(agentId));
		assertThat(capturedDelegateEntity.getPrincipal()).isEqualTo(ContactSettingEntity.create().withId(principalId));
		assertThat(capturedDelegateEntity.getFilters()).containsExactlyInAnyOrder(
			DelegateFilterEntity.create()
				.withAlias("Filter1")
				.withFilterRules(List.of(DelegateFilterRule.create()
					.withAttributeName("key1")
					.withOperator(Operator.EQUALS.toString())
					.withAttributeValue("value1"))),
			DelegateFilterEntity.create()
				.withAlias("Filter2")
				.withFilterRules(List.of(DelegateFilterRule.create()
					.withAttributeName("key2")
					.withOperator(Operator.NOT_EQUALS.toString())
					.withAttributeValue("value2"))));
	}

	@Test
	void createAgentNotFound() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "municipalityId";
		final var delegateCreateRequest = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId);

		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(any(), any())).thenReturn(false);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(municipalityId, delegateCreateRequest));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No agent with contactSettingsId: '" + agentId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No agent with contactSettingsId: '" + agentId + "' could be found for this municipality!");

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, agentId);
		verify(contactSettingRepositoryMock, never()).existsByMunicipalityIdAndId(municipalityId, principalId);
		verifyNoInteractions(delegateRepositoryMock);
	}

	@Test
	void createPrincipalNotFound() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "municipalityId";
		final var delegateCreateRequest = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId);

		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(municipalityId, agentId)).thenReturn(true);
		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(municipalityId, principalId)).thenReturn(false);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(municipalityId, delegateCreateRequest));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No principal with contactSettingsId: '" + principalId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No principal with contactSettingsId: '" + principalId + "' could be found for this municipality!");

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, agentId);
		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, principalId);
		verifyNoInteractions(delegateRepositoryMock);
	}

	@Test
	void createDelegateAlreadyExists() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "municipalityId";
		final var delegateCreateRequest = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId);

		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(municipalityId, agentId)).thenReturn(true);
		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(municipalityId, principalId)).thenReturn(true);
		when(delegateRepositoryMock.findByPrincipalIdAndAgentId(principalId, agentId)).thenReturn(List.of(DelegateEntity.create()));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(municipalityId, delegateCreateRequest));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(CONFLICT);
		assertThat(exception.getDetail()).isEqualTo("A delegate with this this principal and agent already exists!");
		assertThat(exception.getMessage()).isEqualTo("Conflict: A delegate with this this principal and agent already exists!");

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, agentId);
		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, principalId);
		verify(delegateRepositoryMock).findByPrincipalIdAndAgentId(principalId, agentId);
		verify(delegateRepositoryMock, never()).save(any());
	}

	@Test
	void read() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId).withMunicipalityId(municipalityId))
			.withFilters(List.of(
				DelegateFilterEntity.create()
					.withAlias("Filter1")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS.toString())
						.withAttributeValue("value1"))),
				DelegateFilterEntity.create()
					.withAlias("Filter2")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS.toString())
						.withAttributeValue("value2")))))
			.withId(delegateId)
			.withPrincipal(ContactSettingEntity.create().withId(principalId).withMunicipalityId(municipalityId));

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.of(delgateEntity));

		// Act
		final var result = service.read(municipalityId, delegateId);

		// Assert.
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(delegateId);
		assertThat(result.getAgentId()).isEqualTo(agentId);
		assertThat(result.getPrincipalId()).isEqualTo(principalId);
		assertThat(result.getFilters()).containsExactly(
			Filter.create()
				.withAlias("Filter1")
				.withRules(List.of(Rule.create()
					.withAttributeName("key1")
					.withOperator(Operator.EQUALS)
					.withAttributeValue("value1"))),
			Filter.create()
				.withAlias("Filter2")
				.withRules(List.of(Rule.create()
					.withAttributeName("key2")
					.withOperator(Operator.NOT_EQUALS)
					.withAttributeValue("value2"))));

		verify(delegateRepositoryMock).findById(delegateId);
	}

	@Test
	void readNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var municipalityId = "2281";

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.read(municipalityId, delegateId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found for this municipality!");

		verify(delegateRepositoryMock).findById(delegateId);
	}

	@Test
	void readWhenWrongMunicipalityOnAgent() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId).withMunicipalityId("something-else"))  // Wrong municipality
			.withFilters(List.of(
				DelegateFilterEntity.create()
					.withAlias("Filter1")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS.toString())
						.withAttributeValue("value1"))),
				DelegateFilterEntity.create()
					.withAlias("Filter2")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS.toString())
						.withAttributeValue("value2")))))
			.withId(delegateId)
			.withPrincipal(ContactSettingEntity.create().withId(principalId).withMunicipalityId(municipalityId));

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.of(delgateEntity));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.read(municipalityId, delegateId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found for this municipality!");

		verify(delegateRepositoryMock).findById(delegateId);
	}

	@Test
	void readWhenWrongMunicipalityOnPrincipal() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId).withMunicipalityId(municipalityId))
			.withFilters(List.of(
				DelegateFilterEntity.create()
					.withAlias("Filter1")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS.toString())
						.withAttributeValue("value1"))),
				DelegateFilterEntity.create()
					.withAlias("Filter2")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS.toString())
						.withAttributeValue("value2")))))
			.withId(delegateId)
			.withPrincipal(ContactSettingEntity.create().withId(principalId).withMunicipalityId("something-else")); // Wrong municipality

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.of(delgateEntity));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.read(municipalityId, delegateId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found for this municipality!");

		verify(delegateRepositoryMock).findById(delegateId);
	}

	@Test
	void delete() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var delgateEntity = DelegateEntity.create()
			.withId(delegateId)
			.withAgent(ContactSettingEntity.create().withId(agentId).withMunicipalityId(municipalityId))
			.withPrincipal(ContactSettingEntity.create().withId(principalId).withMunicipalityId(municipalityId));

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.of(delgateEntity));

		// Act
		service.delete(municipalityId, delegateId);

		// Assert.
		verify(delegateRepositoryMock).findById(delegateId);
		verify(delegateRepositoryMock).delete(delgateEntity);
	}

	@Test
	void deleteWhenWrongMunicipality() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var delgateEntity = DelegateEntity.create()
			.withId(delegateId)
			.withAgent(ContactSettingEntity.create().withId(agentId).withMunicipalityId(municipalityId))
			.withPrincipal(ContactSettingEntity.create().withId(principalId).withMunicipalityId("something-else")); // Wrong municipality

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.of(delgateEntity));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.delete(municipalityId, delegateId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found for this municipality!");

		// Assert.
		verify(delegateRepositoryMock).findById(delegateId);
		verify(delegateRepositoryMock, never()).delete(delgateEntity);
	}

	@Test
	void deleteNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var municipalityId = "2281";

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.delete(municipalityId, delegateId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found for this municipality!");

		// Assert.
		verify(delegateRepositoryMock).findById(delegateId);
		verify(delegateRepositoryMock, never()).delete(any());
	}

	@Test
	void findByAgentId() {

		// Arrange
		final var id = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var municipalityId = "2281";
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId).withMunicipalityId(municipalityId))
			.withFilters(List.of(
				DelegateFilterEntity.create()
					.withAlias("Filter1")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS.toString())
						.withAttributeValue("value1"))),
				DelegateFilterEntity.create()
					.withAlias("Filter2")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS.toString())
						.withAttributeValue("value2")))))
			.withId(id);
		final var parameters = FindDelegatesParameters.create().withAgentId(agentId);

		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(any(), any())).thenReturn(true);
		when(delegateRepositoryMock.findByAgentId(any())).thenReturn(List.of(delgateEntity));

		// Act
		final var result = service.find(municipalityId, parameters);

		// Assert.
		assertThat(result)
			.extracting(Delegate::getId, Delegate::getAgentId, Delegate::getFilters)
			.containsExactly(tuple(id, agentId, List.of(
				Filter.create()
					.withAlias("Filter1")
					.withRules(List.of(Rule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS)
						.withAttributeValue("value1"))),
				Filter.create()
					.withAlias("Filter2")
					.withRules(List.of(Rule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS)
						.withAttributeValue("value2"))))));

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, agentId);
		verify(delegateRepositoryMock).findByAgentId(agentId);
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByAgentIdNotFound() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var municipalityId = "2281";
		final var parameters = FindDelegatesParameters.create().withAgentId(agentId);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.find(municipalityId, parameters));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No agent with contactSettingsId: '" + agentId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No agent with contactSettingsId: '" + agentId + "' could be found for this municipality!");

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, agentId);
		verify(delegateRepositoryMock, never()).findByAgentId(agentId);
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByPrincipalId() {

		// Arrange
		final var id = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var delgateEntity = DelegateEntity.create()
			.withPrincipal(ContactSettingEntity.create().withId(principalId).withMunicipalityId(municipalityId))
			.withFilters(List.of(
				DelegateFilterEntity.create()
					.withAlias("Filter1")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS.toString())
						.withAttributeValue("value1"))),
				DelegateFilterEntity.create()
					.withAlias("Filter2")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS.toString())
						.withAttributeValue("value2")))))
			.withId(id);
		final var parameters = FindDelegatesParameters.create().withPrincipalId(principalId);

		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(any(), any())).thenReturn(true);
		when(delegateRepositoryMock.findByPrincipalId(any())).thenReturn(List.of(delgateEntity));

		// Act
		final var result = service.find(municipalityId, parameters);

		// Assert.
		assertThat(result)
			.extracting(Delegate::getId, Delegate::getPrincipalId, Delegate::getFilters)
			.containsExactly(tuple(id, principalId, List.of(
				Filter.create()
					.withAlias("Filter1")
					.withRules(List.of(Rule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS)
						.withAttributeValue("value1"))),
				Filter.create()
					.withAlias("Filter2")
					.withRules(List.of(Rule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS)
						.withAttributeValue("value2"))))));

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, principalId);
		verify(delegateRepositoryMock).findByPrincipalId(principalId);
		verify(delegateRepositoryMock, never()).findByAgentId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByPrincipalIdNotFound() {

		// Arrange
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var parameters = FindDelegatesParameters.create().withPrincipalId(principalId);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.find(municipalityId, parameters));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No principal with contactSettingsId: '" + principalId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No principal with contactSettingsId: '" + principalId + "' could be found for this municipality!");

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, principalId);
		verify(delegateRepositoryMock, never()).findByAgentId(principalId);
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByPrincipalIdAndAgentId() {

		// Arrange
		final var id = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId).withMunicipalityId(municipalityId))
			.withPrincipal(ContactSettingEntity.create().withId(principalId).withMunicipalityId(municipalityId))
			.withFilters(List.of(
				DelegateFilterEntity.create()
					.withAlias("Filter1")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS.toString())
						.withAttributeValue("value1"))),
				DelegateFilterEntity.create()
					.withAlias("Filter2")
					.withFilterRules(List.of(DelegateFilterRule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS.toString())
						.withAttributeValue("value2")))))
			.withId(id);
		final var parameters = FindDelegatesParameters.create().withAgentId(agentId).withPrincipalId(principalId);

		when(contactSettingRepositoryMock.existsByMunicipalityIdAndId(any(), any())).thenReturn(true);
		when(delegateRepositoryMock.findByPrincipalIdAndAgentId(any(), any())).thenReturn(List.of(delgateEntity));

		// Act
		final var result = service.find(municipalityId, parameters);

		// Assert.
		assertThat(result)
			.hasSize(1)
			.extracting(Delegate::getId, Delegate::getAgentId, Delegate::getPrincipalId, Delegate::getFilters)
			.containsExactly(tuple(id, agentId, principalId, List.of(
				Filter.create()
					.withAlias("Filter1")
					.withRules(List.of(Rule.create()
						.withAttributeName("key1")
						.withOperator(Operator.EQUALS)
						.withAttributeValue("value1"))),
				Filter.create()
					.withAlias("Filter2")
					.withRules(List.of(Rule.create()
						.withAttributeName("key2")
						.withOperator(Operator.NOT_EQUALS)
						.withAttributeValue("value2"))))));

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, agentId);
		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, principalId);
		verify(delegateRepositoryMock).findByPrincipalIdAndAgentId(principalId, agentId);
		verify(delegateRepositoryMock, never()).findByAgentId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
	}

	@Test
	void findByPrincipalIdAndAgentIdNotFound() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var municipalityId = "2281";
		final var parameters = FindDelegatesParameters.create().withAgentId(agentId).withPrincipalId(principalId);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.find(municipalityId, parameters));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No agent with contactSettingsId: '" + agentId + "' could be found for this municipality!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No agent with contactSettingsId: '" + agentId + "' could be found for this municipality!");

		verify(contactSettingRepositoryMock).existsByMunicipalityIdAndId(municipalityId, agentId);
		verify(contactSettingRepositoryMock, never()).existsByMunicipalityIdAndId(municipalityId, principalId);
		verify(delegateRepositoryMock, never()).findByAgentId(agentId);
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByNullParameter() {

		// Arrange
		final var municipalityId = "2281";

		// Act
		final var result = service.find(municipalityId, null);

		// Assert.
		assertThat(result).isEmpty();

		verify(contactSettingRepositoryMock, never()).existsByMunicipalityIdAndId(any(), any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
		verify(delegateRepositoryMock, never()).findByAgentId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
	}
}

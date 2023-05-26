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
import java.util.Map;
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
import se.sundsvall.contactsettings.api.model.DelegateUpdateRequest;
import se.sundsvall.contactsettings.api.model.FindDelegatesParameters;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.Filter;

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
		final var delegateCreateRequest = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId)
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")));

		when(contactSettingRepositoryMock.existsById(any())).thenReturn(true);
		when(delegateRepositoryMock.findByPrincipalIdAndAgentId(any(), any())).thenReturn(emptyList());
		when(delegateRepositoryMock.save(any())).thenReturn(DelegateEntity.create());

		// Act
		final var result = service.create(delegateCreateRequest);

		// Assert.
		assertThat(result).isNotNull();

		verify(contactSettingRepositoryMock).existsById(agentId);
		verify(contactSettingRepositoryMock).existsById(principalId);
		verify(delegateRepositoryMock).findByPrincipalIdAndAgentId(principalId, agentId);
		verify(delegateRepositoryMock).save(delegateEntityCaptor.capture());

		final var capturedDelegateEntity = delegateEntityCaptor.getValue();
		assertThat(capturedDelegateEntity).isNotNull();
		assertThat(capturedDelegateEntity.getAgent()).isEqualTo(ContactSettingEntity.create().withId(agentId));
		assertThat(capturedDelegateEntity.getPrincipal()).isEqualTo(ContactSettingEntity.create().withId(principalId));
		assertThat(capturedDelegateEntity.getFilters()).containsExactlyInAnyOrder(
			Filter.create().withKey("key1").withValue("value1"),
			Filter.create().withKey("key1").withValue("value2"),
			Filter.create().withKey("key1").withValue("value3"),
			Filter.create().withKey("key2").withValue("value4"),
			Filter.create().withKey("key2").withValue("value5"),
			Filter.create().withKey("key3").withValue("value6"));
	}

	@Test
	void createAgentNotFound() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var delegateCreateRequest = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId)
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")));

		when(contactSettingRepositoryMock.existsById(agentId)).thenReturn(false);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(delegateCreateRequest));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No agent with contactSettingsId: '" + agentId + "' exists!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No agent with contactSettingsId: '" + agentId + "' exists!");

		verify(contactSettingRepositoryMock).existsById(agentId);
		verify(contactSettingRepositoryMock, never()).existsById(principalId);
		verifyNoInteractions(delegateRepositoryMock);
	}

	@Test
	void createPrincipalNotFound() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var delegateCreateRequest = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId)
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")));

		when(contactSettingRepositoryMock.existsById(agentId)).thenReturn(true);
		when(contactSettingRepositoryMock.existsById(principalId)).thenReturn(false);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(delegateCreateRequest));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No principal with contactSettingsId: '" + principalId + "' exists!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No principal with contactSettingsId: '" + principalId + "' exists!");

		verify(contactSettingRepositoryMock).existsById(agentId);
		verify(contactSettingRepositoryMock).existsById(principalId);
		verifyNoInteractions(delegateRepositoryMock);
	}

	@Test
	void createDelegateAlreadyExists() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var delegateCreateRequest = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withPrincipalId(principalId)
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")));

		when(contactSettingRepositoryMock.existsById(agentId)).thenReturn(true);
		when(contactSettingRepositoryMock.existsById(principalId)).thenReturn(true);
		when(delegateRepositoryMock.findByPrincipalIdAndAgentId(principalId, agentId)).thenReturn(List.of(DelegateEntity.create()));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(delegateCreateRequest));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(CONFLICT);
		assertThat(exception.getDetail()).isEqualTo("A delegate with this this principal and agent already exists!");
		assertThat(exception.getMessage()).isEqualTo("Conflict: A delegate with this this principal and agent already exists!");

		verify(contactSettingRepositoryMock).existsById(agentId);
		verify(contactSettingRepositoryMock).existsById(principalId);
		verify(delegateRepositoryMock).findByPrincipalIdAndAgentId(principalId, agentId);
		verify(delegateRepositoryMock, never()).save(any());
	}

	@Test
	void read() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId))
			.withFilters(List.of(
				Filter.create().withKey("key1").withValue("value1"),
				Filter.create().withKey("key1").withValue("value2"),
				Filter.create().withKey("key2").withValue("value3")))
			.withId(delegateId)
			.withPrincipal(ContactSettingEntity.create().withId(principalId));

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.of(delgateEntity));

		// Act
		final var result = service.read(delegateId);

		// Assert.
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(delegateId);
		assertThat(result.getAgentId()).isEqualTo(agentId);
		assertThat(result.getPrincipalId()).isEqualTo(principalId);
		assertThat(result.getFilter()).containsExactlyInAnyOrderEntriesOf(Map.of(
			"key1", List.of("value1", "value2"),
			"key2", List.of("value3")));

		verify(delegateRepositoryMock).findById(delegateId);
	}

	@Test
	void readNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.read(delegateId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' exists!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' exists!");

		verify(delegateRepositoryMock).findById(delegateId);
	}

	@Test
	void update() {

		// Arrange
		final var delegateId = "id";
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(randomUUID().toString()))
			.withFilters(List.of(
				Filter.create().withKey("key1").withValue("oldValue1"),
				Filter.create().withKey("key1").withValue("oldValue2"),
				Filter.create().withKey("key2").withValue("oldValue3")))
			.withId(delegateId)
			.withPrincipal(ContactSettingEntity.create().withId(randomUUID().toString()));

		final var delegateUpdateRequest = DelegateUpdateRequest.create()
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")));

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.of(delgateEntity));
		when(delegateRepositoryMock.save(any())).thenReturn(DelegateEntity.create());

		// Act
		final var result = service.update(delegateId, delegateUpdateRequest);

		// Assert.
		assertThat(result).isNotNull();
		verify(delegateRepositoryMock).findById(delegateId);
		verify(delegateRepositoryMock).save(delegateEntityCaptor.capture());

		final var capturedDelegateEntity = delegateEntityCaptor.getValue();
		assertThat(capturedDelegateEntity).isEqualTo(delgateEntity);
	}

	@Test
	void updateDelegateNotFOund() {

		// Arrange
		final var delegateId = randomUUID().toString();

		final var delegateUpdateRequest = DelegateUpdateRequest.create()
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")));

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.update(delegateId, delegateUpdateRequest));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' exists!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' exists!");

		verify(delegateRepositoryMock).findById(delegateId);
		verify(delegateRepositoryMock, never()).save(any());
	}

	@Test
	void delete() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId))
			.withFilters(List.of(
				Filter.create().withKey("key1").withValue("value1"),
				Filter.create().withKey("key1").withValue("value2"),
				Filter.create().withKey("key2").withValue("value3")))
			.withId(delegateId)
			.withPrincipal(ContactSettingEntity.create().withId(principalId));

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.of(delgateEntity));

		// Act
		service.delete(delegateId);

		// Assert.
		verify(delegateRepositoryMock).findById(delegateId);
		verify(delegateRepositoryMock).delete(delgateEntity);
	}

	@Test
	void deleteNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();

		when(delegateRepositoryMock.findById(any())).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.delete(delegateId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' exists!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' exists!");

		// Assert.
		verify(delegateRepositoryMock).findById(delegateId);
		verify(delegateRepositoryMock, never()).delete(any());
	}

	@Test
	void findByAgentId() {

		// Arrange
		final var id = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId))
			.withFilters(List.of(
				Filter.create().withKey("key1").withValue("value1"),
				Filter.create().withKey("key1").withValue("value2"),
				Filter.create().withKey("key2").withValue("value3")))
			.withId(id);
		final var parameters = FindDelegatesParameters.create().withAgentId(agentId);

		when(delegateRepositoryMock.findByAgentId(any())).thenReturn(List.of(delgateEntity));

		// Act
		final var result = service.find(parameters);

		// Assert.
		assertThat(result)
			.extracting(Delegate::getId, Delegate::getAgentId, Delegate::getFilter)
			.containsExactly(tuple(id, agentId, Map.of(
				"key1", List.of("value1", "value2"),
				"key2", List.of("value3"))));

		verify(delegateRepositoryMock).findByAgentId(agentId);
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByAgentIdNotFound() {

		// Arrange
		final var agentId = randomUUID().toString();
		final var parameters = FindDelegatesParameters.create().withAgentId(agentId);

		when(delegateRepositoryMock.findByAgentId(any())).thenReturn(emptyList());

		// Act
		final var result = service.find(parameters);

		// Assert.
		assertThat(result).isEmpty();

		verify(delegateRepositoryMock).findByAgentId(agentId);
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByPrincipalId() {

		// Arrange
		final var id = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var delgateEntity = DelegateEntity.create()
			.withPrincipal(ContactSettingEntity.create().withId(principalId))
			.withFilters(List.of(
				Filter.create().withKey("key1").withValue("value1"),
				Filter.create().withKey("key1").withValue("value2"),
				Filter.create().withKey("key2").withValue("value3")))
			.withId(id);
		final var parameters = FindDelegatesParameters.create().withPrincipalId(principalId);

		when(delegateRepositoryMock.findByPrincipalId(any())).thenReturn(List.of(delgateEntity));

		// Act
		final var result = service.find(parameters);

		// Assert.
		assertThat(result)
			.extracting(Delegate::getId, Delegate::getPrincipalId, Delegate::getFilter)
			.containsExactly(tuple(id, principalId, Map.of(
				"key1", List.of("value1", "value2"),
				"key2", List.of("value3"))));

		verify(delegateRepositoryMock).findByPrincipalId(principalId);
		verify(delegateRepositoryMock, never()).findByAgentId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByPrincipalIdNotFound() {

		// Arrange
		final var principalId = randomUUID().toString();
		final var parameters = FindDelegatesParameters.create().withPrincipalId(principalId);

		when(delegateRepositoryMock.findByPrincipalId(any())).thenReturn(emptyList());

		// Act
		final var result = service.find(parameters);

		// Assert.
		assertThat(result).isEmpty();

		verify(delegateRepositoryMock).findByPrincipalId(principalId);
		verify(delegateRepositoryMock, never()).findByAgentId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
	}

	@Test
	void findByPrincipalIdAndAgentId() {

		// Arrange
		final var id = randomUUID().toString();

		final var principalAndAgentId = randomUUID().toString();
		final var agentId = principalAndAgentId;
		final var principalId = principalAndAgentId;
		final var delgateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId))
			.withPrincipal(ContactSettingEntity.create().withId(principalId))
			.withFilters(List.of(
				Filter.create().withKey("key1").withValue("value1"),
				Filter.create().withKey("key1").withValue("value2"),
				Filter.create().withKey("key2").withValue("value3")))
			.withId(id);
		final var parameters = FindDelegatesParameters.create().withAgentId(agentId).withPrincipalId(principalId);

		when(delegateRepositoryMock.findByPrincipalIdAndAgentId(any(), any())).thenReturn(List.of(delgateEntity));

		// Act
		final var result = service.find(parameters);

		// Assert.
		assertThat(result)
			.hasSize(1)
			.extracting(Delegate::getId, Delegate::getAgentId, Delegate::getPrincipalId, Delegate::getFilter)
			.containsExactly(tuple(id, agentId, principalId, Map.of(
				"key1", List.of("value1", "value2"),
				"key2", List.of("value3"))));

		verify(delegateRepositoryMock).findByPrincipalIdAndAgentId(principalId, agentId);
		verify(delegateRepositoryMock, never()).findByAgentId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
	}

	@Test
	void findByPrincipalIdAndAgentIdNotFound() {

		// Arrange
		final var principalAndAgentId = randomUUID().toString();
		final var agentId = principalAndAgentId;
		final var principalId = principalAndAgentId;
		final var parameters = FindDelegatesParameters.create().withAgentId(agentId).withPrincipalId(principalId);

		when(delegateRepositoryMock.findByPrincipalIdAndAgentId(any(), any())).thenReturn(emptyList());

		// Act
		final var result = service.find(parameters);

		// Assert.
		assertThat(result).isEmpty();

		verify(delegateRepositoryMock).findByPrincipalIdAndAgentId(principalId, agentId);
		verify(delegateRepositoryMock, never()).findByAgentId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
	}

	@Test
	void findByNullParameter() {

		// Act
		final var result = service.find(null);

		// Assert.
		assertThat(result).isEmpty();

		verify(delegateRepositoryMock, never()).findByPrincipalIdAndAgentId(any(), any());
		verify(delegateRepositoryMock, never()).findByAgentId(any());
		verify(delegateRepositoryMock, never()).findByPrincipalId(any());
	}
}

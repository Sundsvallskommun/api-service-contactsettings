package se.sundsvall.contactsettings.service;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.ArrayList;
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

import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.api.model.enums.Operator;
import se.sundsvall.contactsettings.integration.db.DelegateFilterRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

@ExtendWith(MockitoExtension.class)
class DelegateFilterServiceTest {

	@Mock
	private DelegateRepository delegateRepositoryMock;

	@Mock
	private DelegateFilterRepository delegateFilterRepositoryMock;

	@Captor
	private ArgumentCaptor<DelegateEntity> delegateEntityCaptor;

	@Captor
	private ArgumentCaptor<DelegateFilterEntity> delegateFilterEntityCaptor;

	@InjectMocks
	private DelegateFilterService service;

	@Test
	void create() {

		// Arrange
		final var delegateFilterEntity1 = DelegateFilterEntity.create()
			.withAlias("Filter1")
			.withFilterRules(List.of(DelegateFilterRule.create()
				.withAttributeName("key1")
				.withOperator(Operator.EQUALS.toString())
				.withAttributeValue("value1")));

		final var delegateFilterEntity2 = DelegateFilterEntity.create()
			.withAlias("Filter2")
			.withFilterRules(List.of(DelegateFilterRule.create()
				.withAttributeName("key2")
				.withOperator(Operator.EQUALS.toString())
				.withAttributeValue("value2")));

		final var delegateFilterEntity3 = DelegateFilterEntity.create()
			.withAlias("Filter3")
			.withFilterRules(List.of(DelegateFilterRule.create()
				.withAttributeName("key3")
				.withOperator(Operator.EQUALS.toString())
				.withAttributeValue("value3")));

		final var delegateId = randomUUID().toString();
		final var agentId = randomUUID().toString();
		final var principalId = randomUUID().toString();
		final var delegateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId))
			.withFilters(List.of(delegateFilterEntity1, delegateFilterEntity2))
			.withId(delegateId)
			.withPrincipal(ContactSettingEntity.create().withId(principalId));

		final var filterToCreate = Filter.create()
			.withAlias("Filter3")
			.withRules(List.of(Rule.create().withAttributeName("key3").withAttributeValue("value3").withOperator(Operator.EQUALS)));

		when(delegateRepositoryMock.findById(delegateId)).thenReturn(Optional.of(delegateEntity));
		when(delegateFilterRepositoryMock.save(any())).thenReturn(delegateFilterEntity3);

		// Act
		final var result = service.create(delegateId, filterToCreate);

		// Assert.
		assertThat(result).isEqualTo(filterToCreate);

		verify(delegateRepositoryMock).findById(delegateId);
		verify(delegateRepositoryMock).save(delegateEntityCaptor.capture());
		verify(delegateFilterRepositoryMock).save(delegateFilterEntityCaptor.capture());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(delegateFilterRepositoryMock);

		final var capturedDelegateFilterEntity = delegateFilterEntityCaptor.getValue();
		assertThat(capturedDelegateFilterEntity).isEqualTo(delegateFilterEntity3);

		final var capturedDelegateEntity = delegateEntityCaptor.getValue();
		assertThat(capturedDelegateEntity).isNotNull();
		assertThat(capturedDelegateEntity.getAgent()).isEqualTo(ContactSettingEntity.create().withId(agentId));
		assertThat(capturedDelegateEntity.getPrincipal()).isEqualTo(ContactSettingEntity.create().withId(principalId));
		assertThat(capturedDelegateEntity.getFilters()).containsExactlyInAnyOrder(delegateFilterEntity1, delegateFilterEntity2, delegateFilterEntity3);
	}

	@Test
	void createDelegateNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var filter = Filter.create();

		when(delegateRepositoryMock.findById(delegateId)).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(delegateId, filter));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found!");

		verify(delegateRepositoryMock).findById(delegateId);
		verifyNoInteractions(delegateFilterRepositoryMock);
		verifyNoMoreInteractions(delegateRepositoryMock);
	}

	@Test
	void read() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();

		final var delegateFilterEntity = DelegateFilterEntity.create()
			.withAlias("Filter1")
			.withFilterRules(List.of(DelegateFilterRule.create()
				.withAttributeName("key1")
				.withOperator(Operator.EQUALS.toString())
				.withAttributeValue("value1")));

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(true);
		when(delegateFilterRepositoryMock.findById(delegateFilterId)).thenReturn(Optional.of(delegateFilterEntity));

		// Act
		final var result = service.read(delegateId, delegateFilterId);

		// Assert.
		assertThat(result)
			.isNotNull()
			.isEqualTo(Filter.create()
				.withAlias("Filter1")
				.withRules(List.of(Rule.create().withAttributeName("key1").withAttributeValue("value1").withOperator(Operator.EQUALS))));

		verify(delegateRepositoryMock).existsById(delegateId);
		verify(delegateFilterRepositoryMock).findById(delegateFilterId);
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(delegateFilterRepositoryMock);
	}

	@Test
	void readDelegateNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(false);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.read(delegateId, delegateFilterId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found!");

		verify(delegateRepositoryMock).existsById(delegateId);
		verifyNoInteractions(delegateFilterRepositoryMock);
		verifyNoMoreInteractions(delegateRepositoryMock);
	}

	@Test
	void readNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(true);
		when(delegateFilterRepositoryMock.findById(delegateFilterId)).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.read(delegateId, delegateFilterId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No filter with id: '" + delegateFilterId + "' could be found!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No filter with id: '" + delegateFilterId + "' could be found!");

		verify(delegateRepositoryMock).existsById(delegateId);
		verify(delegateFilterRepositoryMock).findById(delegateFilterId);
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(delegateFilterRepositoryMock);
	}

	@Test
	void update() {

		// Arrange
		final var delegateFilterEntity = DelegateFilterEntity.create()
			.withAlias("My filter")
			.withFilterRules(new ArrayList<>(List.of(DelegateFilterRule.create()
				.withAttributeName("key1")
				.withOperator(Operator.EQUALS.toString())
				.withAttributeValue("value1"))));

		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();

		final var updatedFilter = Filter.create()
			.withAlias("My updated filter")
			.withRules(List.of(
				Rule.create().withAttributeName("key1").withAttributeValue("value1").withOperator(Operator.EQUALS),
				Rule.create().withAttributeName("key2").withAttributeValue("value2").withOperator(Operator.EQUALS),
				Rule.create().withAttributeName("key3").withAttributeValue("value3").withOperator(Operator.NOT_EQUALS)));

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(true);
		when(delegateFilterRepositoryMock.findById(delegateFilterId)).thenReturn(Optional.of(delegateFilterEntity));
		when(delegateFilterRepositoryMock.save(any())).thenReturn(delegateFilterEntity);

		// Act
		final var result = service.update(delegateId, delegateFilterId, updatedFilter);

		// Assert.
		assertThat(result).isEqualTo(updatedFilter);

		verify(delegateRepositoryMock).existsById(delegateId);
		verify(delegateFilterRepositoryMock).findById(delegateFilterId);
		verify(delegateFilterRepositoryMock).save(delegateFilterEntityCaptor.capture());
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(delegateFilterRepositoryMock);

		final var capturedDelegateFilterEntity = delegateFilterEntityCaptor.getValue();
		assertThat(capturedDelegateFilterEntity).isEqualTo(DelegateFilterEntity.create()
			.withAlias("My updated filter")
			.withFilterRules(List.of(
				DelegateFilterRule.create()
					.withAttributeName("key1")
					.withOperator(Operator.EQUALS.toString())
					.withAttributeValue("value1"),
				DelegateFilterRule.create()
					.withAttributeName("key2")
					.withOperator(Operator.EQUALS.toString())
					.withAttributeValue("value2"),
				DelegateFilterRule.create()
					.withAttributeName("key3")
					.withOperator(Operator.NOT_EQUALS.toString())
					.withAttributeValue("value3"))));
	}

	@Test
	void updateDelegateNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();
		final var filter = Filter.create();

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(false);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.update(delegateId, delegateFilterId, filter));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found!");

		verify(delegateRepositoryMock).existsById(delegateId);
		verifyNoInteractions(delegateFilterRepositoryMock);
		verifyNoMoreInteractions(delegateRepositoryMock);
	}

	@Test
	void updateNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();
		final var filter = Filter.create();

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(true);
		when(delegateFilterRepositoryMock.findById(delegateFilterId)).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.update(delegateId, delegateFilterId, filter));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No filter with id: '" + delegateFilterId + "' could be found!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No filter with id: '" + delegateFilterId + "' could be found!");

		verify(delegateRepositoryMock).existsById(delegateId);
		verify(delegateFilterRepositoryMock).findById(delegateFilterId);
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(delegateFilterRepositoryMock);
	}

	@Test
	void delete() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();

		final var delegateFilterEntity = DelegateFilterEntity.create()
			.withAlias("Filter1")
			.withFilterRules(List.of(DelegateFilterRule.create()
				.withAttributeName("key1")
				.withOperator(Operator.EQUALS.toString())
				.withAttributeValue("value1")));

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(true);
		when(delegateFilterRepositoryMock.findById(delegateFilterId)).thenReturn(Optional.of(delegateFilterEntity));

		// Act
		service.delete(delegateId, delegateFilterId);

		// Assert.
		verify(delegateRepositoryMock).existsById(delegateId);
		verify(delegateFilterRepositoryMock).findById(delegateFilterId);
		verify(delegateFilterRepositoryMock).delete(delegateFilterEntity);
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(delegateFilterRepositoryMock);
	}

	@Test
	void deleteDelegateNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(false);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.delete(delegateId, delegateFilterId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No delegate with id: '" + delegateId + "' could be found!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No delegate with id: '" + delegateId + "' could be found!");

		verify(delegateRepositoryMock).existsById(delegateId);
		verifyNoInteractions(delegateFilterRepositoryMock);
		verifyNoMoreInteractions(delegateRepositoryMock);
	}

	@Test
	void deleteNotFound() {

		// Arrange
		final var delegateId = randomUUID().toString();
		final var delegateFilterId = randomUUID().toString();

		when(delegateRepositoryMock.existsById(delegateId)).thenReturn(true);
		when(delegateFilterRepositoryMock.findById(delegateFilterId)).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.delete(delegateId, delegateFilterId));

		// Assert.
		assertThat(exception).isNotNull();
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getDetail()).isEqualTo("No filter with id: '" + delegateFilterId + "' could be found!");
		assertThat(exception.getMessage()).isEqualTo("Not Found: No filter with id: '" + delegateFilterId + "' could be found!");

		verify(delegateRepositoryMock).existsById(delegateId);
		verify(delegateFilterRepositoryMock).findById(delegateFilterId);
		verifyNoMoreInteractions(delegateRepositoryMock);
		verifyNoMoreInteractions(delegateFilterRepositoryMock);
	}
}

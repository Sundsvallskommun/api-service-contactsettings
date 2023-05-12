package se.sundsvall.contactsettings.service.mapper;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.DelegateUpdateRequest;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.Filter;

class DelegateMapperTest {

	@Test
	void toDelegate() {

		// Arrange
		final var agentId = "agentId";
		final var principalId = "principalId";
		final var id = "id";
		final var created = now(ZoneId.systemDefault());
		final var modified = now(ZoneId.systemDefault());

		final var delegateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId))
			.withFilters(List.of(
				Filter.create().withKey("key1").withValue("value1"),
				Filter.create().withKey("key1").withValue("value2"),
				Filter.create().withKey("key1").withValue("value3"),
				Filter.create().withKey("key2").withValue("value4"),
				Filter.create().withKey("key2").withValue("value5"),
				Filter.create().withKey("key3").withValue("value6")))
			.withId(id)
			.withPrincipal(ContactSettingEntity.create().withId(principalId))
			.withCreated(created)
			.withModified(modified);

		// Act
		final var result = DelegateMapper.toDelegate(delegateEntity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getAgentId()).isEqualTo(agentId);
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getFilter()).containsExactlyInAnyOrderEntriesOf(Map.of(
			"key1", List.of("value1", "value2", "value3"),
			"key2", List.of("value4", "value5"),
			"key3", List.of("value6")));
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getModified()).isEqualTo(modified);
		assertThat(result.getPrincipalId()).isEqualTo(principalId);
	}

	@Test
	void toDelegateWhenNull() {

		// Act
		final var result = DelegateMapper.toDelegate(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toDelegateEntityFromDelegateCreateRequest() {

		// Arrange
		final var agentId = "agentId";
		final var principalId = "principalId";

		final var delegate = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")))
			.withPrincipalId(principalId);

		// Act
		final var result = DelegateMapper.toDelegateEntity(delegate);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getAgent()).isEqualTo(ContactSettingEntity.create().withId(agentId));
		assertThat(result.getCreated()).isNull();
		assertThat(result.getFilters()).containsExactlyInAnyOrder(
			Filter.create().withKey("key1").withValue("value1"),
			Filter.create().withKey("key1").withValue("value2"),
			Filter.create().withKey("key1").withValue("value3"),
			Filter.create().withKey("key2").withValue("value4"),
			Filter.create().withKey("key2").withValue("value5"),
			Filter.create().withKey("key3").withValue("value6"));
		assertThat(result.getId()).isNull();
		assertThat(result.getModified()).isNull();
		assertThat(result.getPrincipal()).isEqualTo(ContactSettingEntity.create().withId(principalId));
	}

	@Test
	void toDelegateEntityFromDelegateCreateRequestWhenNull() {

		// Act
		final var result = DelegateMapper.toDelegateEntity((DelegateCreateRequest) null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void mergeIntoDelegateEntity() {

		// Arrange
		final var delegateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(randomUUID().toString()))
			.withPrincipal(ContactSettingEntity.create().withId(randomUUID().toString()))
			.withId("id")
			.withFilters(List.of(Filter.create().withKey("key1").withValue("value1")))
			.withModified(now(systemDefault()))
			.withCreated(now(systemDefault()));

		final var delegateUpdateRequest = DelegateUpdateRequest.create()
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")));

		// Act
		final var result = DelegateMapper.mergeIntoDelegateEntity(delegateEntity, delegateUpdateRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result).usingRecursiveComparison().ignoringFields("filters").isEqualTo(delegateEntity);
		assertThat(result.getFilters()).containsExactlyInAnyOrder(
			Filter.create().withKey("key1").withValue("value1"),
			Filter.create().withKey("key1").withValue("value2"),
			Filter.create().withKey("key1").withValue("value3"),
			Filter.create().withKey("key2").withValue("value4"),
			Filter.create().withKey("key2").withValue("value5"),
			Filter.create().withKey("key3").withValue("value6"));
	}

	@Test
	void toDelegateEntityFromDelegateUpdateRequestWhenNull() {

		// Act
		final var result = DelegateMapper.mergeIntoDelegateEntity(null, (DelegateUpdateRequest) null);

		// Assert
		assertThat(result).isNull();
	}
}

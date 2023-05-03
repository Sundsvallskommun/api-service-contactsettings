package se.sundsvall.contactsettings.service.mapper;

import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import se.sundsvall.contactsettings.api.model.Delegate;
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
	void toDelegateEntity() {

		// Arrange
		final var agentId = "agentId";
		final var principalId = "principalId";
		final var id = "id";
		final var created = now(ZoneId.systemDefault());
		final var modified = now(ZoneId.systemDefault());

		final var delegate = Delegate.create()
			.withAgentId(agentId)
			.withCreated(created)
			.withFilter(Map.of(
				"key1", List.of("value1", "value2", "value3"),
				"key2", List.of("value4", "value5"),
				"key3", List.of("value6")))
			.withId(id)
			.withModified(modified)
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
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getModified()).isNull();
		assertThat(result.getPrincipal()).isEqualTo(ContactSettingEntity.create().withId(principalId));
	}

	@Test
	void toDelegateEntityWhenNull() {

		// Act
		final var result = DelegateMapper.toDelegateEntity(null);

		// Assert
		assertThat(result).isNull();
	}
}

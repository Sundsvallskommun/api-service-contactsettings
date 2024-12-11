package se.sundsvall.contactsettings.service.mapper;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.api.model.enums.Operator;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

class DelegateMapperTest {

	@Test
	void toDelegate() {

		// Arrange
		final var agentId = "agentId";
		final var principalId = "principalId";
		final var id = "id";
		final var created = now(ZoneId.systemDefault());
		final var modified = now(ZoneId.systemDefault()).plusDays(1);

		final var delegateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId))
			.withFilters(List.of(
				DelegateFilterEntity.create()
					.withAlias("alias1")
					.withChannel("channel1")
					.withCreated(created)
					.withFilterRules(List.of(DelegateFilterRule.create().withAttributeName("attribute1").withOperator("EQUALS").withAttributeValue("value1")))
					.withId("id1")
					.withModified(modified),
				DelegateFilterEntity.create()
					.withAlias("alias2")
					.withChannel("channel2")
					.withCreated(created)
					.withFilterRules(List.of(DelegateFilterRule.create().withAttributeName("attribute2").withOperator("NOT_EQUALS").withAttributeValue("value2")))
					.withId("id2")
					.withModified(modified)))
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
		assertThat(result.getFilters()).containsExactly(
			Filter.create()
				.withAlias("alias1")
				.withChannel("channel1")
				.withCreated(created)
				.withId("id1")
				.withModified(modified)
				.withRules(List.of(
					Rule.create().withAttributeName("attribute1").withAttributeValue("value1").withOperator(Operator.EQUALS))),
			Filter.create()
				.withAlias("alias2")
				.withChannel("channel2")
				.withCreated(created)
				.withId("id2")
				.withModified(modified)
				.withRules(List.of(
					Rule.create().withAttributeName("attribute2").withAttributeValue("value2").withOperator(Operator.NOT_EQUALS))));
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
	void toDelegateList() {

		// Arrange
		final var agentId = "agentId";
		final var principalId = "principalId";
		final var id = "id";
		final var created = now(ZoneId.systemDefault());
		final var modified = now(ZoneId.systemDefault()).plusDays(1);

		final var delegateEntity = DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(agentId))
			.withFilters(List.of(
				DelegateFilterEntity.create()
					.withAlias("alias1")
					.withChannel("channel1")
					.withCreated(created)
					.withFilterRules(List.of(DelegateFilterRule.create().withAttributeName("attribute1").withOperator("EQUALS").withAttributeValue("value1")))
					.withId("id1")
					.withModified(modified),
				DelegateFilterEntity.create()
					.withAlias("alias2")
					.withChannel("channel2")
					.withCreated(created)
					.withFilterRules(List.of(DelegateFilterRule.create().withAttributeName("attribute2").withOperator("NOT_EQUALS").withAttributeValue("value2")))
					.withId("id2")
					.withModified(modified)))
			.withId(id)
			.withPrincipal(ContactSettingEntity.create().withId(principalId))
			.withCreated(created)
			.withModified(modified);

		final var delegateEntityList = List.of(delegateEntity);

		// Act
		final var result = DelegateMapper.toDelegateList(delegateEntityList);

		// Assert
		assertThat(result)
			.isNotNull()
			.hasSize(1);
		assertThat(result.getFirst().getAgentId()).isEqualTo(agentId);
		assertThat(result.getFirst().getCreated()).isEqualTo(created);
		assertThat(result.getFirst().getFilters()).containsExactly(
			Filter.create()
				.withAlias("alias1")
				.withChannel("channel1")
				.withCreated(created)
				.withId("id1")
				.withModified(modified)
				.withRules(List.of(
					Rule.create().withAttributeName("attribute1").withAttributeValue("value1").withOperator(Operator.EQUALS))),
			Filter.create()
				.withAlias("alias2")
				.withChannel("channel2")
				.withCreated(created)
				.withId("id2")
				.withModified(modified)
				.withRules(List.of(
					Rule.create().withAttributeName("attribute2").withAttributeValue("value2").withOperator(Operator.NOT_EQUALS))));
		assertThat(result.getFirst().getId()).isEqualTo(id);
		assertThat(result.getFirst().getModified()).isEqualTo(modified);
		assertThat(result.getFirst().getPrincipalId()).isEqualTo(principalId);
	}

	@Test
	void toDelegateListWhenNull() {

		// Act
		final var result = DelegateMapper.toDelegateList(null);

		// Assert
		assertThat(result)
			.isNotNull()
			.isEmpty();
	}

	@Test
	void toDelegateEntityFromDelegateCreateRequest() {

		// Arrange
		final var agentId = "agentId";
		final var principalId = "principalId";
		final var created = now(ZoneId.systemDefault());
		final var modified = now(ZoneId.systemDefault()).plusDays(1);

		final var delegate = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withFilters(List.of(
				Filter.create()
					.withAlias("alias1")
					.withChannel("channel1")
					.withCreated(created)
					.withId("id1")
					.withModified(modified)
					.withRules(List.of(
						Rule.create().withAttributeName("attribute1").withAttributeValue("value1").withOperator(Operator.EQUALS))),
				Filter.create()
					.withAlias("alias2")
					.withChannel("channel2")
					.withCreated(created)
					.withId("id2")
					.withModified(modified)
					.withRules(List.of(
						Rule.create().withAttributeName("attribute2").withAttributeValue("value2").withOperator(Operator.EQUALS)))))
			.withPrincipalId(principalId);

		// Act
		final var result = DelegateMapper.toDelegateEntity(delegate);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getAgent()).isEqualTo(ContactSettingEntity.create().withId(agentId));
		assertThat(result.getCreated()).isNull();
		assertThat(result.getFilters()).containsExactly(
			DelegateFilterEntity.create()
				.withAlias("alias1")
				.withChannel("channel1")
				.withFilterRules(List.of(DelegateFilterRule.create().withAttributeName("attribute1").withOperator("EQUALS").withAttributeValue("value1")))
				.withId("id1"),
			DelegateFilterEntity.create()
				.withChannel("channel2")
				.withAlias("alias2")
				.withFilterRules(List.of(DelegateFilterRule.create().withAttributeName("attribute2").withOperator("EQUALS").withAttributeValue("value2")))
				.withId("id2"));
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
	void mergeIntoDelegateFilterEntity() {

		// Arrange
		final var delegateFilterEntity = DelegateFilterEntity.create()
			.withAlias("alias1")
			.withChannel("channel1")
			.withFilterRules(List.of(DelegateFilterRule.create().withAttributeName("attribute1").withOperator("EQUALS").withAttributeValue("value1")))
			.withId("id1")
			.withModified(now(systemDefault()))
			.withCreated(now(systemDefault()));

		final var filter = Filter.create()
			.withAlias("alias2")
			.withChannel("channel2")
			.withId("id2")
			.withRules(List.of(
				Rule.create().withAttributeName("attribute2").withAttributeValue("value2").withOperator(Operator.EQUALS)));
		// Act
		final var result = DelegateMapper.mergeIntoDelegateFilterEntity(delegateFilterEntity, filter);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getAlias()).isEqualTo("alias2");
		assertThat(result.getChannel()).isEqualTo("channel2");
		assertThat(result.getFilterRules()).containsExactly(
			DelegateFilterRule.create()
				.withAttributeName("attribute2")
				.withAttributeValue("value2")
				.withOperator(Operator.EQUALS.toString()));
	}

	@Test
	void mergeIntoDelegateFilterEntityWhenFilterIsNull() {

		// Arrange
		final var delegateFilterEntity = DelegateFilterEntity.create();

		// Act
		final var result = DelegateMapper.mergeIntoDelegateFilterEntity(DelegateFilterEntity.create(), null);

		// Assert
		assertThat(result).isEqualTo(delegateFilterEntity);
	}

	@Test
	void mergeIntoDelegateFilterEntityWhenEntityIsNull() {

		// Act
		final var result = DelegateMapper.mergeIntoDelegateFilterEntity(null, Filter.create());

		// Assert
		assertThat(result).isNull();
	}
}

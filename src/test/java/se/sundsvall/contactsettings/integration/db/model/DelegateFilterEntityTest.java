package se.sundsvall.contactsettings.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DelegateFilterEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(DelegateFilterEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {

		final var alias = "alias";
		final var channel = "channel";
		final var created = now(ZoneId.systemDefault());
		final var delegateId = "delegateId";
		final var filterRules = List.of(DelegateFilterRule.create().withAttributeName("facitlityId").withOperator("EQUALS").withAttributeValue("12345"));
		final var id = "id";
		final var modified = now(ZoneId.systemDefault()).plusDays(1);

		final var entity = DelegateFilterEntity.create()
			.withAlias(alias)
			.withChannel(channel)
			.withCreated(created)
			.withDelegateId(delegateId)
			.withFilterRules(filterRules)
			.withId(id)
			.withModified(modified);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getAlias()).isEqualTo(alias);
		assertThat(entity.getChannel()).isEqualTo(channel);
		assertThat(entity.getCreated()).isEqualTo(created);
		assertThat(entity.getDelegateId()).isEqualTo(delegateId);
		assertThat(entity.getFilterRules()).isEqualTo(filterRules);
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getModified()).isEqualTo(modified);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(new DelegateFilterEntity()).hasAllNullFieldsOrProperties();
		assertThat(DelegateFilterEntity.create()).hasAllNullFieldsOrProperties();
	}
}

package se.sundsvall.contactsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FilterTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(Filter.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var alias = "alias";
		final var channel = "channel";
		final var created = now();
		final var id = randomUUID().toString();
		final var modified = now();
		final var rules = List.of(Rule.create());

		final var bean = Filter.create()
			.withAlias(alias)
			.withChannel(channel)
			.withCreated(created)
			.withId(id)
			.withModified(modified)
			.withRules(rules);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAlias()).isEqualTo(alias);
		assertThat(bean.getChannel()).isEqualTo(channel);
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getModified()).isEqualTo(modified);
		assertThat(bean.getRules()).isEqualTo(rules);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Filter.create()).hasAllNullFieldsOrProperties();
		assertThat(new Filter()).hasAllNullFieldsOrProperties();
	}
}

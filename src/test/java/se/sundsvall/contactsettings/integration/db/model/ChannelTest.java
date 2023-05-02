package se.sundsvall.contactsettings.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.sundsvall.contactsettings.integration.db.model.enums.ContactMethod;

class ChannelTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(Channel.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {

		final var alias = "alias";
		final var contactMethod = ContactMethod.EMAIL;
		final var destination = "destination";
		final var disabled = true;

		final var entity = Channel.create()
			.withAlias(alias)
			.withContactMethod(contactMethod)
			.withDestination(destination)
			.withDisabled(disabled);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getAlias()).isEqualTo(alias);
		assertThat(entity.getContactMethod()).isEqualTo(contactMethod);
		assertThat(entity.getDestination()).isEqualTo(destination);
		assertThat(entity.isDisabled()).isEqualTo(disabled);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(new Channel()).hasAllNullFieldsOrPropertiesExcept("disabled");
		assertThat(Channel.create()).hasAllNullFieldsOrPropertiesExcept("disabled");
	}
}

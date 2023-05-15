package se.sundsvall.contactsettings.api.model;

import org.junit.jupiter.api.Test;
import se.sundsvall.contactsettings.api.model.enums.ContactMethod;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class ContactChannelTest {
	@Test
	void testBean() {
		assertThat(ContactChannel.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var alias = "alias";
		final var destination = "destination";

		final var bean = ContactChannel.create()
			.withAlias(alias)
			.withContactMethod(ContactMethod.EMAIL)
			.withDestination(destination)
			.withDisabled(true);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAlias()).isEqualTo(alias);
		assertThat(bean.getContactMethod()).isEqualTo(ContactMethod.EMAIL);
		assertThat(bean.getDestination()).isEqualTo(destination);
		assertThat(bean.isDisabled()).isTrue();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContactChannel.create()).hasAllNullFieldsOrPropertiesExcept("disabled");
		assertThat(new ContactChannel()).hasAllNullFieldsOrPropertiesExcept("disabled");
	}
}

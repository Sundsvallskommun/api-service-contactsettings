package se.sundsvall.contactsettings.api.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class DelegateTest {
	@Test
	void testBean() {
		assertThat(Delegate.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var contactSettingId = "contactSettingId";
		final var filter = Filter.create().withKey("key").withValues(List.of("value"));

		final var bean = Delegate.create()
			.withContactSettingId(contactSettingId)
			.withFilter(filter);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getContactSettingId()).isEqualTo(contactSettingId);
		assertThat(bean.getFilter()).isEqualTo(filter);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContactChannel.create()).hasAllNullFieldsOrPropertiesExcept("send");
		assertThat(new ContactChannel()).hasAllNullFieldsOrPropertiesExcept("send");
	}
}

package se.sundsvall.contactsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ContactSettingUpdateRequestTest {

	@Test
	void testBean() {
		assertThat(ContactSettingUpdateRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var alias = "alias";
		final var contactChannels = List.of(ContactChannel.create());

		final var bean = ContactSettingUpdateRequest.create()
			.withAlias(alias)
			.withContactChannels(contactChannels);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAlias()).isEqualTo(alias);
		assertThat(bean.getContactChannels()).isEqualTo(contactChannels);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContactSettingUpdateRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new ContactSettingUpdateRequest()).hasAllNullFieldsOrProperties();
	}
}

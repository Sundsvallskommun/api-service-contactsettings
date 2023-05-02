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

class ContactSettingTest {
	@Test
	void testBean() {
		assertThat(ContactSetting.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var id = "id";
		final var partyId = "partyId";
		final var alias = "alias";
		final var contactChannels = List.of(ContactChannel.create());
		final var isVirtual = true;

		final var bean = ContactSetting.create()
			.withId(id)
			.withPartyId(partyId)
			.withAlias(alias)
			.withContactChannels(contactChannels)
			.withVirtual(isVirtual);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getPartyId()).isEqualTo(partyId);
		assertThat(bean.getAlias()).isEqualTo(alias);
		assertThat(bean.getContactChannels()).isEqualTo(contactChannels);
		assertThat(bean.isVirtual()).isEqualTo(isVirtual);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContactSetting.create()).hasAllNullFieldsOrPropertiesExcept("virtual");
		assertThat(new ContactSetting()).hasAllNullFieldsOrPropertiesExcept("virtual");
	}
}

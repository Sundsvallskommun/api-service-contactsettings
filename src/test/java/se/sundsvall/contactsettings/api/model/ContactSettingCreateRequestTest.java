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

class ContactSettingCreateRequestTest {

	@Test
	void testBean() {
		assertThat(ContactSettingCreateRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var partyId = "partyId";
		final var createdById = "createdById";
		final var alias = "alias";
		final var contactChannels = List.of(ContactChannel.create());

		final var bean = ContactSettingCreateRequest.create()
			.withCreatedById(createdById)
			.withPartyId(partyId)
			.withAlias(alias)
			.withContactChannels(contactChannels);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getPartyId()).isEqualTo(partyId);
		assertThat(bean.getCreatedById()).isEqualTo(createdById);
		assertThat(bean.getAlias()).isEqualTo(alias);
		assertThat(bean.getContactChannels()).isEqualTo(contactChannels);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContactSettingCreateRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new ContactSettingCreateRequest()).hasAllNullFieldsOrProperties();
	}
}

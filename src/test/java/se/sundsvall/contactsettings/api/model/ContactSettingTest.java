package se.sundsvall.contactsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ContactSettingTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

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
		final var created = now();
		final var createdById = "createdById";
		final var alias = "alias";
		final var modified = now();
		final var contactChannels = List.of(ContactChannel.create());
		final var isVirtual = true;

		final var bean = ContactSetting.create()
			.withCreated(created)
			.withCreatedById(createdById)
			.withId(id)
			.withModified(modified)
			.withPartyId(partyId)
			.withAlias(alias)
			.withContactChannels(contactChannels)
			.withVirtual(isVirtual);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getCreatedById()).isEqualTo(createdById);
		assertThat(bean.getModified()).isEqualTo(modified);
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

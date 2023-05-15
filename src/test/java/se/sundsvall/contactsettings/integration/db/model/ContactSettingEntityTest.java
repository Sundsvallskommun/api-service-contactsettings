package se.sundsvall.contactsettings.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ContactSettingEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(ContactSettingEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {

		final var alias = "alias";
		final var channels = List.of(Channel.create());
		final var created = now();
		final var createdById = randomUUID().toString();
		final var id = randomUUID().toString();
		final var modified = now();
		final var partyId = randomUUID().toString();

		final var entity = ContactSettingEntity.create()
			.withAlias(alias)
			.withChannels(channels)
			.withCreated(created)
			.withCreatedById(createdById)
			.withId(id)
			.withModified(modified)
			.withPartyId(partyId);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getAlias()).isEqualTo(alias);
		assertThat(entity.getChannels()).isEqualTo(channels);
		assertThat(entity.getCreated()).isEqualTo(created);
		assertThat(entity.getCreatedById()).isEqualTo(createdById);
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getModified()).isEqualTo(modified);
		assertThat(entity.getPartyId()).isEqualTo(partyId);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(new ContactSettingEntity()).hasAllNullFieldsOrProperties();
		assertThat(ContactSettingEntity.create()).hasAllNullFieldsOrProperties();
	}
}

package se.sundsvall.contactsettings.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import static se.sundsvall.contactsettings.integration.db.model.enums.ContactMethod.EMAIL;
import static se.sundsvall.contactsettings.integration.db.model.enums.ContactMethod.SMS;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import se.sundsvall.contactsettings.integration.db.model.Channel;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.enums.ContactMethod;

/**
 * ContactSettingRepository tests
 *
 * @see /src/test/resources/db/testdata-junit.sql for data setup.
 */
@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class ContactSettingRepositoryTest {

	private static final String CONTACT_SETTING_ENTITY_ID = "a42bfd69-ab22-443c-bdef-1cc6a70bcab3";
	private static final String CONTACT_SETTING_ENTITY_PARTY_ID = "db96ca23-7c52-412e-b251-f75fb45551d5";

	@Autowired
	private ContactSettingRepository contactSettingRepository;

	@Test
	void create() {

		// Arrange
		final var entity = createContactSettingEntity();

		// Act
		final var result = contactSettingRepository.save(entity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(isValidUUID(result.getId())).isTrue();
		assertThat(result.getCreated()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getModified()).isNull();
		assertThat(result.getChannels())
			.extracting(Channel::getAlias, Channel::getContactMethod, Channel::getDestination)
			.containsExactly(tuple("Email", EMAIL, "0701234567"));
	}

	@Test
	void update() {

		// Arrange
		final var entity = contactSettingRepository.findById(CONTACT_SETTING_ENTITY_ID).orElseThrow();
		assertThat(entity).isNotNull();
		assertThat(entity.getChannels())
			.extracting(Channel::getAlias, Channel::getContactMethod, Channel::getDestination)
			.containsExactly(
				tuple("Email", EMAIL, "john.smith@example.com"),
				tuple("SMS", SMS, "46701111111"));

		// Act
		entity.setAlias("changed-alias");
		entity.getChannels().stream()
			.filter(c -> ContactMethod.EMAIL.equals(c.getContactMethod()))
			.findFirst()
			.orElseThrow()
			.setDestination("changed.email@example.com");

		final var result = contactSettingRepository.save(entity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getAlias()).isEqualTo("changed-alias");
		assertThat(result.getModified()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getChannels())
			.extracting(Channel::getAlias, Channel::getContactMethod, Channel::getDestination)
			.containsExactly(
				tuple("Email", EMAIL, "changed.email@example.com"),
				tuple("SMS", SMS, "46701111111"));
	}

	@Test
	void findByPartyId() {

		// Act
		final var result = contactSettingRepository.findById(CONTACT_SETTING_ENTITY_ID).orElseThrow();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(CONTACT_SETTING_ENTITY_ID);
		assertThat(result.getPartyId()).isEqualTo(CONTACT_SETTING_ENTITY_PARTY_ID);
		assertThat(result.getChannels())
			.extracting(Channel::getAlias, Channel::getContactMethod, Channel::getDestination)
			.containsExactly(
				tuple("Email", EMAIL, "john.smith@example.com"),
				tuple("SMS", SMS, "46701111111"));
	}

	@Test
	void findByPartyIdNotFound() {

		// Act
		final var result = contactSettingRepository.findById("non-existing");

		// Assert
		assertThat(result).isEmpty();
	}

	private static ContactSettingEntity createContactSettingEntity() {
		return ContactSettingEntity.create()
			.withAlias("alias")
			.withChannels(List.of(Channel.create()
				.withAlias("Email")
				.withContactMethod(ContactMethod.EMAIL)
				.withDestination("0701234567")))
			.withPartyId(randomUUID().toString());
	}

	private boolean isValidUUID(final String value) {
		try {
			UUID.fromString(String.valueOf(value));
		} catch (final Exception e) {
			return false;
		}

		return true;
	}
}
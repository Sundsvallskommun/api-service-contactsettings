package se.sundsvall.contactsettings.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

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
	private static final String CONTACT_SETTING_VIRTUAL_ENTITY_ID = "2c94ea99-a1b4-4073-b094-9ff79bad23b0";
	private static final String MUNICIPALITY_ID = "2281";

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
			.containsExactly(tuple("Email", "EMAIL", "0701234567"));
	}

	@Test
	void update() {

		// Arrange
		final var entity = contactSettingRepository.findById(CONTACT_SETTING_ENTITY_ID).orElseThrow();
		assertThat(entity).isNotNull();
		assertThat(entity.getChannels())
			.extracting(Channel::getAlias, Channel::getContactMethod, Channel::getDestination)
			.containsExactly(
				tuple("Email", "EMAIL", "john.smith@example.com"),
				tuple("SMS", "SMS", "46701111111"));

		// Act
		entity.setAlias("changed-alias");
		entity.getChannels().stream()
			.filter(c -> "EMAIL".equals(c.getContactMethod()))
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
				tuple("Email", "EMAIL", "changed.email@example.com"),
				tuple("SMS", "SMS", "46701111111"));
	}

	@Test
	void findByMunicipalityIdAndId() {

		// Act
		final var result = contactSettingRepository.findByMunicipalityIdAndId(MUNICIPALITY_ID, CONTACT_SETTING_ENTITY_ID);

		// Assert
		assertThat(result).isNotNull().isPresent();
		assertThat(result.get().getId()).isEqualTo(CONTACT_SETTING_ENTITY_ID);
		assertThat(result.get().getPartyId()).isEqualTo(CONTACT_SETTING_ENTITY_PARTY_ID);
		assertThat(result.get().getChannels())
			.extracting(Channel::getAlias, Channel::getContactMethod, Channel::getDestination)
			.containsExactly(
				tuple("Email", "EMAIL", "john.smith@example.com"),
				tuple("SMS", "SMS", "46701111111"));
	}

	@Test
	void findByMunicipalityIdAndIdNotFound() {

		// Act
		final var result = contactSettingRepository.findByMunicipalityIdAndId("non-existing", CONTACT_SETTING_ENTITY_ID);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void existsByMunicipalityIdAndId() {

		// Act
		final var result = contactSettingRepository.existsByMunicipalityIdAndId(MUNICIPALITY_ID, CONTACT_SETTING_ENTITY_ID);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	void existsByMunicipalityIdAndIdNotFound() {

		// Act
		final var result = contactSettingRepository.existsByMunicipalityIdAndId("non-existing", CONTACT_SETTING_ENTITY_ID);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	void findByMunicipalityIdAndPartyId() {

		// Act
		final var result = contactSettingRepository.findByMunicipalityIdAndPartyId(MUNICIPALITY_ID, CONTACT_SETTING_ENTITY_PARTY_ID);

		// Assert
		assertThat(result).isNotNull().isPresent();
		assertThat(result.get().getId()).isEqualTo(CONTACT_SETTING_ENTITY_ID);
		assertThat(result.get().getPartyId()).isEqualTo(CONTACT_SETTING_ENTITY_PARTY_ID);
		assertThat(result.get().getChannels())
			.extracting(Channel::getAlias, Channel::getContactMethod, Channel::getDestination)
			.containsExactly(
				tuple("Email", "EMAIL", "john.smith@example.com"),
				tuple("SMS", "SMS", "46701111111"));
	}

	@Test
	void findByMunicipalityIdAndPartyIdNotFound() {

		// Act
		final var result = contactSettingRepository.findByMunicipalityIdAndPartyId("non-existing", CONTACT_SETTING_ENTITY_PARTY_ID);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findByMunicipalityIdAndChannelsDestination() {

		// Arrange
		final var municipalityId = "2281";
		final var destinationSearchParameter = "46701111111";

		// Act
		final var result = contactSettingRepository.findByMunicipalityIdAndChannelsDestination(municipalityId, destinationSearchParameter);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(CONTACT_SETTING_ENTITY_ID);
		assertThat(result.get(0).getPartyId()).isEqualTo(CONTACT_SETTING_ENTITY_PARTY_ID);
		assertThat(result.get(0).getChannels())
			.extracting(Channel::getAlias, Channel::getContactMethod, Channel::getDestination)
			.containsExactly(
				tuple("Email", "EMAIL", "john.smith@example.com"),
				tuple("SMS", "SMS", "46701111111"));
	}

	@Test
	void findByChannelsDestinationNotFound() {

		// Arrange
		final var municipalityId = "2281";
		final var destinationSearchParameter = "non-existing";

		// Act
		final var result = contactSettingRepository.findByMunicipalityIdAndChannelsDestination(municipalityId, destinationSearchParameter);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findByMunicipalityIdAndCreatedById() {

		// Act
		final var result = contactSettingRepository.findByMunicipalityIdAndCreatedById(MUNICIPALITY_ID, CONTACT_SETTING_ENTITY_ID);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result)
			.extracting(ContactSettingEntity::getAlias, ContactSettingEntity::getId, ContactSettingEntity::getCreatedById)
			.containsExactly(tuple("Virtual friend", CONTACT_SETTING_VIRTUAL_ENTITY_ID, CONTACT_SETTING_ENTITY_ID));
	}

	@Test
	void findByCreatedByIdNotFound() {

		// Act
		final var result = contactSettingRepository.findByMunicipalityIdAndCreatedById("non-existing", CONTACT_SETTING_ENTITY_ID);

		// Assert
		assertThat(result).isEmpty();
	}

	private static ContactSettingEntity createContactSettingEntity() {
		return ContactSettingEntity.create()
			.withAlias("alias")
			.withChannels(List.of(Channel.create()
				.withAlias("Email")
				.withContactMethod("EMAIL")
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

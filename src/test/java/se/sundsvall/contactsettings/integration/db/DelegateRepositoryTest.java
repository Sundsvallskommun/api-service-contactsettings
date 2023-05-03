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
import java.util.Map;
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
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.Filter;
import se.sundsvall.contactsettings.integration.db.model.enums.ContactMethod;

/**
 * DelegateRepository tests
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
class DelegateRepositoryTest {

	private static final String DELEGATE_ENTITY_ID = "4d6adb65-172a-4671-a667-5e142bfc353e";
	private static final String DELEGATE_ENTITY_PRINCIPAL_ID = "534ba8a0-7484-45b3-b041-ff90f1228c16";
	private static final String DELEGATE_ENTITY_PRINCIPAL_PARTY_ID = "62fd9c95-99c0-4874-b0ef-e990aaab03c6";
	private static final String DELEGATE_ENTITY_AGENT_ID = "07025549-3fbd-4db2-ab40-e1b93034b254";
	private static final String DELEGATE_ENTITY_AGENT_PARTY_ID = "7af1869c-a8c2-4690-8d89-112ef15b4ffd";

	@Autowired
	private ContactSettingRepository contactSettingRepository;

	@Autowired
	private DelegateRepository delegateRepository;

	@Test
	void create() {

		// Arrange
		final var principal = contactSettingRepository.save(createContactSettingEntity());
		final var agent = contactSettingRepository.save(createContactSettingEntity());
		final var entity = DelegateEntity.create().withAgent(agent).withPrincipal(principal);

		// Act
		final var result = delegateRepository.save(entity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(isValidUUID(result.getId())).isTrue();
		assertThat(result.getCreated()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getModified()).isNull();
		assertThat(result.getAgent()).isEqualTo(agent);
		assertThat(result.getPrincipal()).isEqualTo(principal);
	}

	@Test
	void update() {

		// Arrange
		final var entity = delegateRepository.findById(DELEGATE_ENTITY_ID).orElseThrow();
		assertThat(entity).isNotNull();
		assertThat(entity.getPrincipal().getAlias()).isEqualTo("Joe Doe");
		assertThat(entity.getAgent().getAlias()).isEqualTo("Jane Doe");

		// Act
		final var result = delegateRepository.save(entity.withFilters(List.of(
			Filter.create().withKey("filter1").withValue("value1"),
			Filter.create().withKey("filter1").withValue("value2"),
			Filter.create().withKey("filter2").withValue("value3"))));

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getFilters())
			.extracting(Filter::getKey, Filter::getValue)
			.containsExactly(
				tuple("filter1", "value1"),
				tuple("filter1", "value2"),
				tuple("filter2", "value3"));
		assertThat(result.filtersAsMap()).isEqualTo(Map.of(
			"filter1", List.of("value1", "value2"),
			"filter2", List.of("value3")));
	}

	@Test
	void findByAgentPartyId() {

		// Act
		final var result = delegateRepository.findByAgentPartyId(DELEGATE_ENTITY_AGENT_PARTY_ID).orElseThrow();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(DELEGATE_ENTITY_ID);
		assertThat(result.getAgent().getId()).isEqualTo(DELEGATE_ENTITY_AGENT_ID);
		assertThat(result.getAgent().getPartyId()).isEqualTo(DELEGATE_ENTITY_AGENT_PARTY_ID);
		assertThat(result.getPrincipal().getId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_ID);
		assertThat(result.getPrincipal().getPartyId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_PARTY_ID);
	}

	@Test
	void findByAgentPartyIdNotFound() {

		// Act
		final var result = delegateRepository.findByAgentPartyId("non-existing");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findByPrincipalPartyId() {

		// Act
		final var result = delegateRepository.findByPrincipalPartyId(DELEGATE_ENTITY_PRINCIPAL_PARTY_ID).orElseThrow();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(DELEGATE_ENTITY_ID);
		assertThat(result.getAgent().getId()).isEqualTo(DELEGATE_ENTITY_AGENT_ID);
		assertThat(result.getAgent().getPartyId()).isEqualTo(DELEGATE_ENTITY_AGENT_PARTY_ID);
		assertThat(result.getPrincipal().getId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_ID);
		assertThat(result.getPrincipal().getPartyId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_PARTY_ID);
	}

	@Test
	void findByPrincipalPartyIdNotFound() {

		// Act
		final var result = delegateRepository.findByPrincipalPartyId("non-existing");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findByAgentPartyIdAndPrincipalPartyId() {

		// Act
		final var result = delegateRepository.findByAgentPartyIdAndPrincipalPartyId(DELEGATE_ENTITY_AGENT_PARTY_ID, DELEGATE_ENTITY_PRINCIPAL_PARTY_ID).orElseThrow();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(DELEGATE_ENTITY_ID);
		assertThat(result.getAgent().getId()).isEqualTo(DELEGATE_ENTITY_AGENT_ID);
		assertThat(result.getAgent().getPartyId()).isEqualTo(DELEGATE_ENTITY_AGENT_PARTY_ID);
		assertThat(result.getPrincipal().getId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_ID);
		assertThat(result.getPrincipal().getPartyId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_PARTY_ID);
	}

	@Test
	void findByAgentPartyIdAndPrincipalPartyIdNotFound() {

		// Act
		final var result = delegateRepository.findByAgentPartyIdAndPrincipalPartyId("non-existing", "non-existing");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void delete() {

		// Arrange
		assertThat(delegateRepository.findById(DELEGATE_ENTITY_ID)).isPresent();
		assertThat(contactSettingRepository.findById(DELEGATE_ENTITY_PRINCIPAL_ID)).isPresent();
		assertThat(contactSettingRepository.findById(DELEGATE_ENTITY_AGENT_ID)).isPresent();

		// Act
		delegateRepository.deleteById(DELEGATE_ENTITY_ID);

		// Assert
		assertThat(delegateRepository.findById(DELEGATE_ENTITY_ID)).isNotPresent();
		assertThat(contactSettingRepository.findById(DELEGATE_ENTITY_PRINCIPAL_ID)).isPresent();
		assertThat(contactSettingRepository.findById(DELEGATE_ENTITY_AGENT_ID)).isPresent();
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

package se.sundsvall.contactsettings.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import static se.sundsvall.contactsettings.api.model.enums.Operator.EQUALS;

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
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

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
	private static final String DELEGATE_FILTER_ENTITY_ID = "4327dae1-a00b-462d-885a-417628ea3114";

	@Autowired
	private ContactSettingRepository contactSettingRepository;

	@Autowired
	private DelegateRepository delegateRepository;

	@Autowired
	private DelegateFilterRepository delegateFilterRepository;

	@Test
	void create() {

		// Arrange
		final var principal = contactSettingRepository.save(createContactSettingEntity());
		final var agent = contactSettingRepository.save(createContactSettingEntity());
		final var entity = DelegateEntity.create()
			.withAgent(agent)
			.withPrincipal(principal)
			.withFilters(List.of(DelegateFilterEntity.create()
				.withAlias("My filter")
				.withFilterRules(List.of(DelegateFilterRule.create()
					.withAttributeName("key")
					.withOperator(EQUALS.toString())
					.withAttributeValue("value")))));

		// Act
		final var result = delegateRepository.save(entity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(isValidUUID(result.getId())).isTrue();
		assertThat(result.getCreated()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getModified()).isNull();
		assertThat(result.getAgent()).isEqualTo(agent);
		assertThat(result.getPrincipal()).isEqualTo(principal);
		assertThat(result.getFilters())
			.isNotEmpty()
			.first()
			.matches(delegateFilter -> isValidUUID(delegateFilter.getId()))
			.matches(delegateFilter -> "My filter".equals(delegateFilter.getAlias()))
			.matches(delegateFilter -> delegateFilter.getFilterRules().stream()
				.allMatch(rule -> "key".equals(rule.getAttributeName()) && "value".equals(rule.getAttributeValue()) && rule.getOperator().equals(EQUALS.toString())));
	}

	@Test
	void findByAgentId() {

		// Act
		final var result = delegateRepository.findByAgentId(DELEGATE_ENTITY_AGENT_ID);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(DELEGATE_ENTITY_ID);
		assertThat(result.get(0).getAgent().getId()).isEqualTo(DELEGATE_ENTITY_AGENT_ID);
		assertThat(result.get(0).getAgent().getPartyId()).isEqualTo(DELEGATE_ENTITY_AGENT_PARTY_ID);
		assertThat(result.get(0).getPrincipal().getId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_ID);
		assertThat(result.get(0).getPrincipal().getPartyId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_PARTY_ID);
	}

	@Test
	void findByAgentIdNotFound() {

		// Act
		final var result = delegateRepository.findByAgentId("non-existing");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findByPrincipalId() {

		// Act
		final var result = delegateRepository.findByPrincipalId(DELEGATE_ENTITY_PRINCIPAL_ID);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(DELEGATE_ENTITY_ID);
		assertThat(result.get(0).getAgent().getId()).isEqualTo(DELEGATE_ENTITY_AGENT_ID);
		assertThat(result.get(0).getAgent().getPartyId()).isEqualTo(DELEGATE_ENTITY_AGENT_PARTY_ID);
		assertThat(result.get(0).getPrincipal().getId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_ID);
		assertThat(result.get(0).getPrincipal().getPartyId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_PARTY_ID);
	}

	@Test
	void findByPrincipalIdNotFound() {

		// Act
		final var result = delegateRepository.findByPrincipalId("non-existing");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void findByPrincipalIdAndAgentId() {

		// Act
		final var result = delegateRepository.findByPrincipalIdAndAgentId(DELEGATE_ENTITY_PRINCIPAL_ID, DELEGATE_ENTITY_AGENT_ID);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(DELEGATE_ENTITY_ID);
		assertThat(result.get(0).getAgent().getId()).isEqualTo(DELEGATE_ENTITY_AGENT_ID);
		assertThat(result.get(0).getAgent().getPartyId()).isEqualTo(DELEGATE_ENTITY_AGENT_PARTY_ID);
		assertThat(result.get(0).getPrincipal().getId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_ID);
		assertThat(result.get(0).getPrincipal().getPartyId()).isEqualTo(DELEGATE_ENTITY_PRINCIPAL_PARTY_ID);
	}

	@Test
	void findByPrincipalIdAndAgentIdNotFound() {

		// Act
		final var result = delegateRepository.findByPrincipalIdAndAgentId(DELEGATE_ENTITY_PRINCIPAL_ID, "non-existing");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void delete() {

		// Arrange
		assertThat(delegateRepository.findById(DELEGATE_ENTITY_ID)).isPresent();
		assertThat(delegateFilterRepository.findById(DELEGATE_FILTER_ENTITY_ID)).isPresent();
		assertThat(contactSettingRepository.findById(DELEGATE_ENTITY_PRINCIPAL_ID)).isPresent();
		assertThat(contactSettingRepository.findById(DELEGATE_ENTITY_AGENT_ID)).isPresent();

		// Act
		delegateRepository.deleteById(DELEGATE_ENTITY_ID);

		// Assert
		assertThat(delegateRepository.findById(DELEGATE_ENTITY_ID)).isNotPresent(); // Should be removed.
		assertThat(delegateFilterRepository.findById(DELEGATE_FILTER_ENTITY_ID)).isNotPresent(); // Should be removed.
		assertThat(contactSettingRepository.findById(DELEGATE_ENTITY_PRINCIPAL_ID)).isPresent(); // Should still be present.
		assertThat(contactSettingRepository.findById(DELEGATE_ENTITY_AGENT_ID)).isPresent(); // Should still be present.
	}

	private static ContactSettingEntity createContactSettingEntity() {
		return ContactSettingEntity.create()
			.withAlias("alias")
			.withChannels(List.of(Channel.create()
				.withAlias("Email")
				.withContactMethod("EMAIL")
				.withDestination("john.doe@host.com")))
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

package se.sundsvall.contactsettings.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import static se.sundsvall.contactsettings.api.model.enums.Operator.EQUALS;
import static se.sundsvall.contactsettings.api.model.enums.Operator.NOT_EQUALS;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

/**
 * DelegateFilterRepository tests
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
class DelegateFilterRepositoryTest {

	private static final String DELEGATE_FILTER_ENTITY_ID = "4327dae1-a00b-462d-885a-417628ea3114";

	@Autowired
	private DelegateFilterRepository delegateFilterRepository;

	@Test
	void create() {

		// Arrange
		final var entity = DelegateFilterEntity.create()
			.withAlias("My filter")
			.withFilterRules(List.of(
				DelegateFilterRule.create()
					.withAttributeName("key1")
					.withOperator(EQUALS.toString())
					.withAttributeValue("value1"),
				DelegateFilterRule.create()
					.withAttributeName("key2")
					.withOperator(NOT_EQUALS.toString())
					.withAttributeValue("value2")));

		// Act
		final var result = delegateFilterRepository.save(entity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(isValidUUID(result.getId())).isTrue();
		assertThat(result.getCreated()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getModified()).isNull();
		assertThat(result.getFilterRules())
			.isNotEmpty()
			.extracting(DelegateFilterRule::getAttributeName, DelegateFilterRule::getOperator, DelegateFilterRule::getAttributeValue)
			.containsExactly(
				tuple("key1", "EQUALS", "value1"),
				tuple("key2", "NOT_EQUALS", "value2"));
	}

	@Test
	void findById() {

		// Act
		final var result = delegateFilterRepository.findById(DELEGATE_FILTER_ENTITY_ID).orElseThrow();

		// Assert
		assertThat(result).isNotNull();
		assertThat(isValidUUID(result.getId())).isTrue();
		assertThat(result.getAlias()).isEqualTo("Jane will only see messages for summer house");
		assertThat(result.getFilterRules())
			.isNotEmpty()
			.extracting(DelegateFilterRule::getAttributeName, DelegateFilterRule::getOperator, DelegateFilterRule::getAttributeValue)
			.containsExactly(tuple("facilityId", "EQUALS", "12345678"));
	}

	@Test
	void findByIdNotFound() {

		// Act
		final var result = delegateFilterRepository.findById("non-existing");

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void update() {

		// Arrange
		final var entity = delegateFilterRepository.findById(DELEGATE_FILTER_ENTITY_ID).orElseThrow()
			.withAlias("My Updatedfilter")
			.withFilterRules(List.of(
				DelegateFilterRule.create()
					.withAttributeName("key3")
					.withOperator(NOT_EQUALS.toString())
					.withAttributeValue("value3"),
				DelegateFilterRule.create()
					.withAttributeName("key4")
					.withOperator(EQUALS.toString())
					.withAttributeValue("value4")));

		// Act
		final var result = delegateFilterRepository.save(entity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(isValidUUID(result.getId())).isTrue();
		assertThat(result.getModified()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getFilterRules())
			.isNotEmpty()
			.extracting(DelegateFilterRule::getAttributeName, DelegateFilterRule::getOperator, DelegateFilterRule::getAttributeValue)
			.containsExactly(
				tuple("key3", "NOT_EQUALS", "value3"),
				tuple("key4", "EQUALS", "value4"));
	}

	@Test
	void delete() {

		// Arrange
		assertThat(delegateFilterRepository.findById(DELEGATE_FILTER_ENTITY_ID)).isPresent();

		// Act
		delegateFilterRepository.deleteById(DELEGATE_FILTER_ENTITY_ID);

		// Assert
		assertThat(delegateFilterRepository.findById(DELEGATE_FILTER_ENTITY_ID)).isNotPresent();
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

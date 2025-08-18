package se.sundsvall.contactsettings.integration.db.model.listener;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;

class DelegateEntityListenerTest {

	@Test
	void prePersist() {

		// Arrange
		final var listener = new DelegateEntityListener();
		final var entity = new DelegateEntity();

		// Act
		listener.prePersist(entity);

		// Assert
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("created", "modified");
		assertThat(entity.getCreated()).isCloseTo(now(), within(2, SECONDS));
	}

	@Test
	void preUpdate() {

		// Arrange
		final var listener = new DelegateEntityListener();
		final var entity = new DelegateEntity();

		// Act
		listener.preUpdate(entity);

		// Assert
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("modified");
		assertThat(entity.getModified()).isCloseTo(now(), within(2, SECONDS));
	}
}

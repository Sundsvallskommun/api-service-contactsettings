package se.sundsvall.contactsettings.integration.db.model.listener;

import org.junit.jupiter.api.Test;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ContactSettingEntityListenerTest {

	@Test
	void prePersist() {

		// Arrange
		final var listener = new ContactSettingEntityListener();
		final var entity = new ContactSettingEntity();

		// Act
		listener.prePersist(entity);

		// Assert
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("created", "channels", "modified");
		assertThat(entity.getCreated()).isCloseTo(now(), within(2, SECONDS));
	}

	@Test
	void preUpdate() {

		// Arrange
		final var listener = new ContactSettingEntityListener();
		final var entity = new ContactSettingEntity();

		// Act
		listener.preUpdate(entity);

		// Assert
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("modified", "channels");
		assertThat(entity.getModified()).isCloseTo(now(), within(2, SECONDS));
	}
}

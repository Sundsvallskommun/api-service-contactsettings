package se.sundsvall.contactsettings.integration.db.model.listener;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.ZoneId;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

public class ContactSettingEntityListener {

	@PrePersist
	void prePersist(final ContactSettingEntity entity) {
		entity.setCreated(now(ZoneId.systemDefault()).truncatedTo(MILLIS));
	}

	@PreUpdate
	void preUpdate(final ContactSettingEntity entity) {
		entity.setModified(now(ZoneId.systemDefault()).truncatedTo(MILLIS));
	}
}

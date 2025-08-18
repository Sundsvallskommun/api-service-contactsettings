package se.sundsvall.contactsettings.integration.db.model.listener;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.ZoneId;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

public class ContactSettingEntityListener {

	@PrePersist
	void prePersist(final ContactSettingEntity entity) {
		var now = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
		entity.setCreated(now);
		entity.setModified(now);
	}

	@PreUpdate
	void preUpdate(final ContactSettingEntity entity) {
		entity.setModified(now(ZoneId.systemDefault()).truncatedTo(MILLIS));
	}
}

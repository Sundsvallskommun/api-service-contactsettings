package se.sundsvall.contactsettings.integration.db.model.listener;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.ZoneId;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;

public class DelegateEntityListener {

	@PrePersist
	void prePersist(final DelegateEntity entity) {
		var now = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
		entity.setCreated(now);
		entity.setModified(now);
	}

	@PreUpdate
	void preUpdate(final DelegateEntity entity) {
		entity.setModified(now(ZoneId.systemDefault()).truncatedTo(MILLIS));
	}
}

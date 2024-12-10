package se.sundsvall.contactsettings.integration.db.model.listener;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.ZoneId;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;

public class DelegateFilterEntityListener {

	@PrePersist
	void prePersist(final DelegateFilterEntity entity) {
		entity.setCreated(now(ZoneId.systemDefault()).truncatedTo(MILLIS));
	}

	@PreUpdate
	void preUpdate(final DelegateFilterEntity entity) {
		entity.setModified(now(ZoneId.systemDefault()).truncatedTo(MILLIS));
	}
}

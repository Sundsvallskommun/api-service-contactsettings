package se.sundsvall.contactsettings.integration.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

@CircuitBreaker(name = "contactSettingRepository")
public interface ContactSettingRepository extends CrudRepository<ContactSettingEntity, String>, JpaRepository<ContactSettingEntity, String>, JpaSpecificationExecutor<ContactSettingEntity> {

	/**
	 * Find by partyId.
	 *
	 * @param partyId of the ContactSetting owner.
	 * @return an Optional ContactSettingEntity.
	 */
	Optional<ContactSettingEntity> findByPartyId(String partyId);
}

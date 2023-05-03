package se.sundsvall.contactsettings.integration.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

@CircuitBreaker(name = "contactSettingRepository")
public interface ContactSettingRepository extends JpaRepository<ContactSettingEntity, String> {

	/**
	 * Find by partyId.
	 *
	 * @param partyId of the ContactSetting owner.
	 * @return an Optional ContactSettingEntity.
	 */
	Optional<ContactSettingEntity> findByPartyId(String partyId);

	/**
	 * Find by channel destination (SMS, EMAIL, etc.).
	 *
	 * @param destination channel-destination of the ContactSettings to find.
	 * @return an Optional ContactSettingEntity.
	 */
	List<ContactSettingEntity> findByChannelsDestination(String destination);
}

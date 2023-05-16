package se.sundsvall.contactsettings.integration.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

@CircuitBreaker(name = "contactSettingRepository")
public interface ContactSettingRepository extends JpaRepository<ContactSettingEntity, String> {

	/**
	 * Find by partyId.
	 *
	 * @param partyId of the ContactSetting owner.
	 * @return a List of ContactSettingEntity objects.
	 */
	List<ContactSettingEntity> findByPartyId(String partyId);

	/**
	 * Find by createdById.
	 *
	 * @param createdById the id of the ContactSetting that created the instance to find.
	 * @return a List of ContactSettingEntity objects.
	 */
	List<ContactSettingEntity> findByCreatedById(String createdById);

	/**
	 * Find by channel destination (SMS, EMAIL, etc.).
	 *
	 * @param destination channel-destination of the ContactSettings to find.
	 * @return a List of ContactSettingEntity objects.
	 */
	List<ContactSettingEntity> findByChannelsDestination(String destination);
}

package se.sundsvall.contactsettings.integration.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

@Transactional
@CircuitBreaker(name = "contactSettingRepository")
public interface ContactSettingRepository extends JpaRepository<ContactSettingEntity, String> {

	/**
	 * Find by municipalityId and idd.
	 *
	 * @param  municipalityId of the ContactSetting.
	 * @param  id             the ID of the ContactSetting.
	 * @return                an Optional of ContactSettingEntity object.
	 */
	Optional<ContactSettingEntity> findByMunicipalityIdAndId(String municipalityId, String id);

	/**
	 * Find by municipalityId and partyId.
	 *
	 * @param  municipalityId of the ContactSetting.
	 * @param  partyId        of the ContactSetting owner.
	 * @return                an Optional of ContactSettingEntity object.
	 */
	Optional<ContactSettingEntity> findByMunicipalityIdAndPartyId(String municipalityId, String partyId);

	/**
	 * Find by municipalityId and createdById.
	 *
	 * @param  municipalityId of the ContactSetting.
	 * @param  createdById    the id of the ContactSetting that created the instance to find.
	 * @return                a List of ContactSettingEntity objects.
	 */
	List<ContactSettingEntity> findByMunicipalityIdAndCreatedById(String municipalityId, String createdById);

	/**
	 * Returns whether an entity with the given municipalityId and id exists.
	 *
	 * @param  municipalityId of the ContactSetting.
	 * @param  id             the id of the ContactSetting.
	 * @return                a List of ContactSettingEntity objects.
	 */
	boolean existsByMunicipalityIdAndId(String municipalityId, String id);

	/**
	 * Find by channel destination (SMS, EMAIL, etc.).
	 *
	 * @param  municipalityId of the ContactSetting.
	 * @param  destination    channel-destination of the ContactSettings to find.
	 * @return                a List of ContactSettingEntity objects.
	 */
	List<ContactSettingEntity> findByMunicipalityIdAndChannelsDestination(String municipalityId, String destination);
}

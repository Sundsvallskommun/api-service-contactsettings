package se.sundsvall.contactsettings.integration.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;

@CircuitBreaker(name = "delegateRepository")
public interface DelegateRepository extends CrudRepository<DelegateEntity, String>, JpaRepository<DelegateEntity, String> {

	/**
	 * Find by agent partyId. I.e. the delegate agents partyId.
	 *
	 * @param agentPartyId the partyId of the agent.
	 * @return an Optional ContactSettingEntity.
	 */
	Optional<DelegateEntity> findByAgentPartyId(String agentPartyId);

	/**
	 * Find by principal partyId. I.e. the delegate principals partyId.
	 *
	 * @param principalPartyId the partyId of the principal.
	 * @return an Optional ContactSettingEntity.
	 */
	Optional<DelegateEntity> findByPrincipalPartyId(String principalPartyId);

	/**
	 * Find by agent partyId and principal partyId.
	 *
	 * @param principalPartyId the partyId of the principal.
	 * @param agentPartyId     the partyId of the agent.
	 * @return an Optional ContactSettingEntity.
	 */
	Optional<DelegateEntity> findByAgentPartyIdAndPrincipalPartyId(String agentPartyId, String principalPartyId);
}

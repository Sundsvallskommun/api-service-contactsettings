package se.sundsvall.contactsettings.integration.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;

@Transactional
@CircuitBreaker(name = "delegateRepository")
public interface DelegateRepository extends JpaRepository<DelegateEntity, String> {

	/**
	 * Find by agent contact settings ID. I.e. the delegate agents (contactSetting) ID.
	 *
	 * @param contactSettingsId the contactSettingsId.
	 * @return an Optional ContactSettingEntity.
	 */
	List<DelegateEntity> findByAgentId(String contactSettingsId);

	/**
	 * Find by principal contact settings ID. I.e. the delegate principal (contactSetting) ID.
	 *
	 * @param contactSettingsId the contactSettingsId.
	 * @return an Optional ContactSettingEntity.
	 */
	List<DelegateEntity> findByPrincipalId(String contactSettingsId);

	/**
	 * Find by principal contact settings ID. I.e. the delegate principal (contactSetting) ID.
	 *
	 * @param principalContactSettingsId the contactSettingsId of the principal (delegate owner).
	 * @param agentContactSettingsId     the contactSettingsId of the agent.
	 * @return an Optional ContactSettingEntity.
	 */
	List<DelegateEntity> findByPrincipalIdAndAgentId(String principalContactSettingsId, String agentContactSettingsId);
}

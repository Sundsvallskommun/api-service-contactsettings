package se.sundsvall.contactsettings.integration.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;

@CircuitBreaker(name = "delegateRepository")
public interface DelegateRepository extends CrudRepository<DelegateEntity, String>, JpaRepository<DelegateEntity, String> {

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
}

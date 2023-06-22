package se.sundsvall.contactsettings.integration.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;

@Transactional
@CircuitBreaker(name = "delegateFilterRepository")
public interface DelegateFilterRepository extends JpaRepository<DelegateFilterEntity, String> {

	/**
	 * Returns whether an entity with the given id and delegateId exists.
	 *
	 * @param  id         the DelegateFilterEntity id.
	 * @param  delegateId the delegateId.
	 * @return            true if an entity with the given id exists, false otherwise.
	 */
	boolean existsByIdAndDelegateId(String id, String delegateId);

	/**
	 * Count by delegateId.
	 *
	 * @param  delegateId the delegateId.
	 * @return            the number of DelegateFilterEntity objects with the provided delegateId.
	 */
	int countByDelegateId(String delegateId);

	/**
	 * Find by id and delegateId.
	 *
	 * @param  id         the DelegateFilterEntity id
	 * @param  delegateId the delegateId.
	 * @return            an Optional DelegateFilterEntity.
	 */
	Optional<DelegateFilterEntity> findByIdAndDelegateId(String id, String delegateId);
}

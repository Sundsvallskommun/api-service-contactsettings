package se.sundsvall.contactsettings.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;

@Transactional
@CircuitBreaker(name = "delegateFilterRepository")
public interface DelegateFilterRepository extends JpaRepository<DelegateFilterEntity, String> {
}

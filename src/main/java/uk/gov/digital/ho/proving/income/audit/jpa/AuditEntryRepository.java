package uk.gov.digital.ho.proving.income.audit.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEntryRepository extends CrudRepository<AuditEntry, Long> {
}

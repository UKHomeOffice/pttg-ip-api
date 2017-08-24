package uk.gov.digital.ho.proving.income.audit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEntryJpaRepository extends CrudRepository<AuditEntry, Long> {
}

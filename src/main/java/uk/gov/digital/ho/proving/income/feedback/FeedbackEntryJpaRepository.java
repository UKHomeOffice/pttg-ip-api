package uk.gov.digital.ho.proving.income.feedback;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackEntryJpaRepository extends CrudRepository<FeedbackEntry, Long>{
}

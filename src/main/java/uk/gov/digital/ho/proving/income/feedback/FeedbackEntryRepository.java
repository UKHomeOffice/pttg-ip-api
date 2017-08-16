package uk.gov.digital.ho.proving.income.feedback;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackEntryRepository extends CrudRepository<FeedbackEntry, Long>{
}

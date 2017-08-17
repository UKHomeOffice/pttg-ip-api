package uk.gov.digital.ho.proving.income.feedback;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.digital.ho.proving.income.api.RequestData;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class FeedbackService {

    private final FeedbackEntryJpaRepository repository;
    private final RequestData requestData;

    public FeedbackService(FeedbackEntryJpaRepository repository, RequestData requestData) {
        this.repository = repository;
        this.requestData = requestData;
    }

    @Transactional
    public void add(String feedback) {

        repository.save(new FeedbackEntry(UUID.randomUUID().toString(),
            LocalDateTime.now(),
            requestData.sessionId(),
            requestData.deploymentName(),
            requestData.deploymentNamespace(),
            requestData.userId(),
            feedback));
    }
}

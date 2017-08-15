package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions.AuditDataException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditRepository {

    private final ObjectMapper mapper;
    private final RequestData requestData;
    private final AuditEntryRepository repository;

    public AuditRepository(ObjectMapper mapper, RequestData requestData, AuditEntryRepository repository) {
        this.mapper = mapper;
        this.requestData = requestData;
        this.repository = repository;
    }

    @Transactional
    public void add(AuditEventType eventType, UUID eventId, Map<String, Object> auditData) {
        try {
            repository.save(new AuditEntry(eventId.toString(),
                LocalDateTime.now(),
                requestData.sessionId(),
                requestData.correlationId(),
                requestData.userId(),
                requestData.deploymentName(),
                requestData.deploymentNamespace(),
                eventType,
                mapper.writeValueAsString(auditData)));
        } catch (JsonProcessingException e) {
            throw new AuditDataException("unable to create audit record: ", e);
        }
    }

}

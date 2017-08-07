package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions.AuditDataException;
import uk.gov.digital.ho.proving.income.audit.AuditEntry;
import uk.gov.digital.ho.proving.income.audit.AuditEntryRepository;
import uk.gov.digital.ho.proving.income.audit.AuditEventType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static uk.gov.digital.ho.proving.income.logging.LoggingInterceptor.USER_ID_HEADER;

@Component
public class AuditRepository {

    private final ObjectMapper mapper;
    private final AuditEntryRepository repository;

    public AuditRepository(ObjectMapper mapper, AuditEntryRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Transactional
    public void add(AuditEventType eventType, UUID eventId, Map<String, Object> auditData) {
        try {
            repository.save(new AuditEntry(eventId.toString(),
                LocalDateTime.now(),
                "0",
                "0",
                getPrincipal(),
                "",
                "",
                eventType,
                mapper.writeValueAsString(auditData)));
        } catch (JsonProcessingException e) {
            throw new AuditDataException("unable to create audit record: ", e);
        }
    }

    public String getPrincipal() {
        if (StringUtils.isBlank(MDC.get(USER_ID_HEADER))) {
            return "anonymous";
        }
        return MDC.get(USER_ID_HEADER);
    }
}

package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.income.api.RequestData;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@Slf4j
public class AuditClient {

    private final Clock clock;
    private final RestTemplate restTemplate;
    private final String auditEndpoint;
    private final String auditHistoryEndpoint;
    private final String auditArchiveEndpoint;
    private final RequestData requestData;
    private final ObjectMapper mapper;

    public AuditClient(Clock clock,
                       RestTemplate restTemplate,
                       RequestData requestData,
                       @Value("${pttg.audit.endpoint}") String auditEndpoint,
                       @Value("${audit.history.endpoint}") String auditHistoryEndpoint,
                       @Value("${audit.archive.endpoint}") String auditArchiveEndpoint,
                       ObjectMapper mapper) {
        this.clock = clock;
        this.restTemplate = restTemplate;
        this.requestData = requestData;
        this.auditEndpoint = auditEndpoint;
        this.auditHistoryEndpoint = auditHistoryEndpoint;
        this.auditArchiveEndpoint = auditArchiveEndpoint;
        this.mapper = mapper;
    }

    public void add(AuditEventType eventType, UUID eventId, Map<String, Object> auditDetail) {

        log.info("POST data for {} to audit service", eventId);

        try {
            AuditableData auditableData = generateAuditableData(eventType, eventId, auditDetail);
            dispatchAuditableData(auditableData);
            log.info("data POSTed to audit service");
        } catch (JsonProcessingException e) {
            log.error("Failed to create json representation of audit data");
        }
    }

    @Retryable(
            value = { RestClientException.class },
            maxAttemptsExpression = "#{${audit.service.retry.attempts}}",
            backoff = @Backoff(delayExpression = "#{${audit.service.retry.delay}}"))
    private void dispatchAuditableData(AuditableData auditableData) {
        restTemplate.exchange(auditEndpoint, POST, toEntity(auditableData), Void.class);
    }

    List<AuditRecord> getAuditHistory(LocalDate toDate, List<AuditEventType> eventTypes) {
        URI uri = UriComponentsBuilder.fromHttpUrl(auditHistoryEndpoint)
            .queryParam("toDate", toDate.format(DateTimeFormatter.ISO_DATE))
            .queryParam("eventTypes", eventTypes)
            .build()
            .encode()
            .toUri();

        HttpEntity<Void> entity = new HttpEntity<>(generateRestHeaders());
        ResponseEntity<List<AuditRecord>> auditRecords = restTemplate.exchange(uri, GET, entity, new ParameterizedTypeReference<List<AuditRecord>>() {});
        return auditRecords.getBody();
    }

    List<AuditRecord> getAuditHistoryPaginated(List<AuditEventType> eventTypes, int page, int size) {
        URI uri = UriComponentsBuilder.fromHttpUrl(auditHistoryEndpoint)
            .queryParam("eventTypes", eventTypes)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()
            .encode()
            .toUri();

        HttpEntity<Void> entity = new HttpEntity<>(generateRestHeaders());
        ResponseEntity<List<AuditRecord>> response = restTemplate.exchange(uri, GET, entity, new ParameterizedTypeReference<List<AuditRecord>>() {});
        return response.getBody();
    }

    void archiveAudit(ArchiveAuditRequest request) {
        HttpEntity<ArchiveAuditRequest> entity = new HttpEntity<>(request, generateRestHeaders());
        ResponseEntity<ArchiveAuditResponse> response = restTemplate.exchange(auditArchiveEndpoint, POST, entity, new ParameterizedTypeReference<ArchiveAuditResponse>() {});
    }

    @Recover
    void addRetryFailureRecovery(RestClientException e, AuditEventType eventType) {
        log.error("Failed to audit {} after retries - {}", eventType, e.getMessage());
    }

    private AuditableData generateAuditableData(AuditEventType eventType, UUID eventId, Map<String, Object> auditDetail) throws JsonProcessingException {
        return new AuditableData(eventId.toString(),
                                    LocalDateTime.now(clock),
                                    requestData.sessionId(),
                                    requestData.correlationId(),
                                    requestData.userId(),
                                    requestData.deploymentName(),
                                    requestData.deploymentNamespace(),
                                    eventType,
                                    mapper.writeValueAsString(auditDetail));
    }

    private HttpEntity<AuditableData> toEntity(AuditableData auditableData) {
        return new HttpEntity<>(auditableData, generateRestHeaders());
    }

    private HttpHeaders generateRestHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.add(AUTHORIZATION, requestData.auditBasicAuth());
        headers.setContentType(APPLICATION_JSON);

        return headers;
    }
}

package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.audit.statistics.AuditClientEndpointProperties;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.gov.digital.ho.proving.income.application.LogEvent.*;

@Component
@Slf4j
public class AuditClient {

    private final Clock clock;
    private final RestTemplate restTemplate;
    private final String auditEndpoint;
    private final String auditHistoryEndpoint;
    private final String correlationIdsEndpoint;
    private final String historyByCorrelationIdEndpoint;
    private final String auditArchiveEndpoint;
    private final RequestData requestData;
    private final ObjectMapper mapper;
    private final RetryTemplate retryTemplate;
    private final int historyPageSize;

    AuditClient(Clock clock,
                RestTemplate restTemplate,
                RequestData requestData,
                AuditClientEndpointProperties endpointProperties,
                ObjectMapper mapper,
                @Qualifier("auditRetryTemplate") RetryTemplate retryTemplate) {
        this.clock = clock;
        this.restTemplate = restTemplate;
        this.requestData = requestData;
        this.auditEndpoint = endpointProperties.getAuditEndpoint();
        this.auditHistoryEndpoint = endpointProperties.getHistoryEndpoint();
        this.correlationIdsEndpoint = endpointProperties.getCorrelationIdsEndpoint();
        this.historyByCorrelationIdEndpoint = endpointProperties.getHistoryByCorrelationIdEndpoint();
        this.auditArchiveEndpoint = endpointProperties.getArchiveEndpoint();
        this.historyPageSize = endpointProperties.getArchiveHistoryPageSize();
        this.mapper = mapper;
        this.retryTemplate = retryTemplate;
    }

    public void add(AuditEventType eventType, UUID eventId, Map<String, Object> auditDetail) {
        try {
            retryTemplate.execute(context -> addAudit(eventType, eventId, auditDetail));
        } catch (RestClientException e) {
            log.error("Failed to audit {} after retries - {}", eventType, e.getMessage(), value(EVENT, INCOME_PROVING_AUDIT_FAILURE));
        }
    }

    private Object addAudit(AuditEventType eventType, UUID eventId, Map<String, Object> auditDetail) {
        log.info("POST data for {} to audit service", eventId, value(EVENT, INCOME_PROVING_AUDIT_REQUEST));

        try {
            AuditableData auditableData = generateAuditableData(eventType, eventId, auditDetail);
            dispatchAuditableData(auditableData);
            log.info("data POSTed to audit service", value(EVENT, INCOME_PROVING_AUDIT_SUCCESS));
        } catch (JsonProcessingException e) {
            log.error("Failed to create json representation of audit data", value(EVENT, INCOME_PROVING_AUDIT_FAILURE));
        }
        return null; // retry lambda requires a return value although we don't actually use it
    }

    private void dispatchAuditableData(AuditableData auditableData) {
        restTemplate.exchange(auditEndpoint, POST, toEntity(auditableData), Void.class);
    }

    List<AuditRecord> getAuditHistory(LocalDate toDate, List<AuditEventType> eventTypes) {
        int page = 0;
        List<AuditRecord> auditRecords = new ArrayList<>();
        List<AuditRecord> auditRecordsPage = getAuditHistoryPaginated(eventTypes, page++, historyPageSize, toDate);
        auditRecords.addAll(auditRecordsPage);
        while(auditRecordsPage.size() == historyPageSize) {
            auditRecordsPage = getAuditHistoryPaginated(eventTypes, page++, historyPageSize, toDate);
            auditRecords.addAll(auditRecordsPage);
        }
        return auditRecords;
    }

    private List<AuditRecord> getAuditHistoryPaginated(List<AuditEventType> eventTypes, int page, int size, LocalDate toDate) {
        URI uri = generateUri(eventTypes, page, size, toDate);

        HttpEntity<Void> entity = new HttpEntity<>(generateRestHeaders());
        ResponseEntity<List<AuditRecord>> response = restTemplate.exchange(uri, GET, entity, new ParameterizedTypeReference<List<AuditRecord>>() {});
        return response.getBody();
    }

    void archiveAudit(ArchiveAuditRequest request, LocalDate resultDate) {
        HttpEntity<ArchiveAuditRequest> entity = new HttpEntity<>(request, generateRestHeaders());
        String date = DateTimeFormatter.ISO_DATE.format(resultDate);
        try {
            restTemplate.exchange(auditArchiveEndpoint + "/" + date, POST, entity, Void.class);
        } catch(RestClientException ex) {
            log.error(String.format("Archive audit request for %s returned error %s", request, ex));
        }
    }

    public List<String> getAllCorrelationIdsForEventType(List<AuditEventType> eventTypes) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(generateRestHeaders());
        URI uri = generateCorrelationIdsUri(eventTypes);
        ResponseEntity<List<String>> response = restTemplate.exchange(uri, GET, requestEntity, new ParameterizedTypeReference<List<String>>() {});
        return response.getBody();
    }

    public List<String> getAllCorrelationIdsForEventType(List<AuditEventType> eventTypes, LocalDate toDate) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(generateRestHeaders());
        URI uri = generateCorrelationIdsUri(eventTypes, toDate);
        ResponseEntity<List<String>> response = restTemplate.exchange(uri, GET, requestEntity, new ParameterizedTypeReference<List<String>>() {});
        return response.getBody();
    }

    public List<AuditRecord> getHistoryByCorrelationId(String correlationId, List<AuditEventType> eventTypes) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(generateRestHeaders());
        URI uri = generateHistoryByCorrelationIdUri(correlationId, eventTypes);
        ResponseEntity<List<AuditRecord>> response = restTemplate.exchange(uri, GET, requestEntity, new ParameterizedTypeReference<List<AuditRecord>>() {});
        return response.getBody();
    }

    private URI generateCorrelationIdsUri(List<AuditEventType> eventTypes, LocalDate toDate) {
        return UriComponentsBuilder.fromHttpUrl(correlationIdsEndpoint)
                                   .queryParam("eventTypes", eventTypes.toArray(new AuditEventType[0]))
                                   .queryParam("toDate", toDate)
                                   .build()
                                   .encode()
                                   .toUri();
    }
    private URI generateCorrelationIdsUri(List<AuditEventType> eventTypes) {
        return UriComponentsBuilder.fromHttpUrl(correlationIdsEndpoint)
                                   .queryParam("eventTypes", eventTypes.toArray(new AuditEventType[0]))
                                   .build()
                                   .encode()
                                   .toUri();
    }

    private URI generateHistoryByCorrelationIdUri(String correlationId, List<AuditEventType> eventTypes) {
        return UriComponentsBuilder.fromHttpUrl(historyByCorrelationIdEndpoint)
                                   .queryParam("correlationId", correlationId)
                                   .queryParam("eventTypes", eventTypes.toArray(new AuditEventType[0]))
                                   .build()
                                   .encode()
                                   .toUri();
    }

    URI generateUri(List<AuditEventType> eventTypes, int page, int size, LocalDate toDate) {
        return UriComponentsBuilder.fromHttpUrl(auditHistoryEndpoint)
            .queryParam("eventTypes", eventTypes.toArray(new AuditEventType[0]))
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("toDate", toDate)
            .build()
            .encode()
            .toUri();
    }

    URI generateUri(List<AuditEventType> eventTypes, int page, int size) {
        return UriComponentsBuilder.fromHttpUrl(auditHistoryEndpoint)
            .queryParam("eventTypes", eventTypes.toArray(new AuditEventType[0]))
            .queryParam("page", page)
            .queryParam("size", size)
            .build()
            .encode()
            .toUri();
    }

    public List<ArchivedResult> getArchivedResults(LocalDate fromDate, LocalDate toDate) {
        URI uri = UriComponentsBuilder.fromHttpUrl(auditArchiveEndpoint)
            .queryParam("fromDate", fromDate)
            .queryParam("toDate", toDate)
            .build()
            .encode()
            .toUri();

        HttpEntity<Void> entity = new HttpEntity<>(generateRestHeaders());
        ResponseEntity<List<ArchivedResult>> response = restTemplate.exchange(uri, GET, entity, new ParameterizedTypeReference<List<ArchivedResult>>() {});
        return response.getBody();
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
        headers.add(RequestData.CORRELATION_ID_HEADER, requestData.correlationId());

        return headers;
    }
}

package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.RequestData;

import java.net.URI;
import java.time.*;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@RunWith(MockitoJUnitRunner.class)
public class AuditClientTest {

    private static TimeZone defaultTimeZone;
    private static ObjectMapper mapper = new ObjectMapper();

    @Mock private RestTemplate mockRestTemplate;
    @Mock private RequestData mockRequestData;

    @Captor private ArgumentCaptor<HttpEntity> captorHttpEntity;
    @Captor private ArgumentCaptor<URI> captorUri;

    private AuditClient auditClient;

    private static final String SOME_ENDPOINT = "http://some-endpoint";
    private static final String SOME_HISTORY_ENDPOINT = "http://some-history-endpoint";
    private static final String SOME_ARCHIVE_ENDPOINT = "http://some-archive-endpoint";

    @BeforeClass
    public static void beforeAllTests() {
        defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @AfterClass
    public static void afterAllTests() {
        TimeZone.setDefault(defaultTimeZone);
    }

    @Before
    public void setup() {
        auditClient = new AuditClient(Clock.fixed(Instant.parse("2017-08-29T08:00:00Z"), ZoneId.of("UTC")),
                                        mockRestTemplate,
                                        mockRequestData,
                                        SOME_ENDPOINT,
                                        SOME_HISTORY_ENDPOINT,
                                        SOME_ARCHIVE_ENDPOINT,
                                        mapper);
    }

    @Test
    public void shouldUseCollaborators() {
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.randomUUID(), null);

        verify(mockRestTemplate).exchange(eq(SOME_ENDPOINT), eq(POST), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    public void shouldSetHeaders() {

        when(mockRequestData.auditBasicAuth()).thenReturn("some basic auth header value");
        when(mockRequestData.correlationId()).thenReturn("some correlation id");
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.randomUUID(), null);

        verify(mockRestTemplate).exchange(eq(SOME_ENDPOINT), eq(POST), captorHttpEntity.capture(), eq(Void.class));

        HttpHeaders headers = captorHttpEntity.getValue().getHeaders();
        assertThat(headers.get("Authorization").get(0)).isEqualTo("some basic auth header value");
        assertThat(headers.get("Content-Type").get(0)).isEqualTo(APPLICATION_JSON_VALUE);
        assertThat(headers.get("x-correlation-id").get(0)).isEqualTo("some correlation id");
    }

    @Test
    public void shouldSetAuditableData() {

        when(mockRequestData.sessionId()).thenReturn("some session id");
        when(mockRequestData.correlationId()).thenReturn("some correlation id");
        when(mockRequestData.userId()).thenReturn("some user id");
        when(mockRequestData.deploymentName()).thenReturn("some deployment name");
        when(mockRequestData.deploymentNamespace()).thenReturn("some deployment namespace");

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.randomUUID(), Collections.emptyMap());

        verify(mockRestTemplate).exchange(eq(SOME_ENDPOINT), eq(POST), captorHttpEntity.capture(), eq(Void.class));

        AuditableData auditableData = (AuditableData) captorHttpEntity.getValue().getBody();
        assertThat(auditableData.getEventId()).isNotEmpty();
        assertThat(auditableData.getTimestamp()).isEqualTo(LocalDateTime.parse("2017-08-29T08:00:00"));
        assertThat(auditableData.getSessionId()).isEqualTo("some session id");
        assertThat(auditableData.getCorrelationId()).isEqualTo("some correlation id");
        assertThat(auditableData.getUserId()).isEqualTo("some user id");
        assertThat(auditableData.getDeploymentName()).isEqualTo("some deployment name");
        assertThat(auditableData.getDeploymentNamespace()).isEqualTo("some deployment namespace");
        assertThat(auditableData.getEventType()).isEqualTo(INCOME_PROVING_FINANCIAL_STATUS_REQUEST);
        assertThat(auditableData.getData()).isEqualTo("{}");
    }

    @Test
    public void shouldRetrieveAuditHistory() {
        List<AuditEventType> eventTypes = Arrays.asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> results = new ArrayList<>();
        ResponseEntity<List<AuditRecord>> resultsEntity = ResponseEntity.ok(results);
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}))).thenReturn(resultsEntity);

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        URI uri = captorUri.getValue();
        assertThat(uri).hasHost(SOME_HISTORY_ENDPOINT.replace("http://", ""));
        assertThat(uri).hasQuery(String.format("toDate=%s&eventTypes=%s", LocalDate.now().toString(), eventTypes.toString()));
        assertThat(auditRecords).isEqualTo(results);
    }

    @Test
    public void shouldRequestAuditArchive() {
        ArchiveAuditRequest request = new ArchiveAuditRequest("any_nino", LocalDate.now().minusMonths(6), Arrays.asList("corr1", "corr2"), "PASS", LocalDate.now());
        when(mockRestTemplate.exchange(eq(SOME_ARCHIVE_ENDPOINT), eq(POST), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<ArchiveAuditResponse>() {}))).thenReturn(ResponseEntity.ok(new ArchiveAuditResponse()));

        auditClient.archiveAudit(request);

        verify(mockRestTemplate).exchange(eq(SOME_ARCHIVE_ENDPOINT), eq(POST), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<ArchiveAuditResponse>() {}));
        ArchiveAuditRequest actual = (ArchiveAuditRequest) captorHttpEntity.getValue().getBody();
        assertThat(actual).isEqualTo(request);
    }

    @Test
    public void getAuditHistoryPaginated_givenParams_expectedUri() {
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {})))
            .thenReturn(ResponseEntity.ok(emptyList()));

        List<AuditEventType> eventTypes = Arrays.asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        int page = 23;
        int size = 8;
        auditClient.getAuditHistoryPaginated(eventTypes, page, size);

        URI uri = captorUri.getValue();
        assertThat(uri).hasHost(SOME_HISTORY_ENDPOINT.replace("http://", ""));
        assertThat(uri).hasQuery(String.format("eventTypes=%s&page=%s&size=%s", eventTypes.toString(), page, size));
    }

    @Test
    public void getAuditHistoryPaginated_givenResponse_returnRecords() {
        List<AuditRecord> results = emptyList();
        stubResponse(results);

        List<AuditEventType> someEventTypes = Arrays.asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        int somePage = 1;
        int someSize = 1;

        assertThat(auditClient.getAuditHistoryPaginated(someEventTypes, somePage, someSize))
            .isEqualTo(results);
    }

    private void stubResponse(List<AuditRecord> results) {
        ResponseEntity<List<AuditRecord>> response = ResponseEntity.ok(results);
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {})))
            .thenReturn(response);
    }
}

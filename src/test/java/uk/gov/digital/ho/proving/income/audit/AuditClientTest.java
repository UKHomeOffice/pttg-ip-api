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

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
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

    private AuditClient auditClient;

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
                                        "some endpoint",
                                        "some history endpoint",
                                        "some archive endpoint",
                                        mapper);
    }

    @Test
    public void shouldUseCollaborators() {
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.randomUUID(), null);

        verify(mockRestTemplate).exchange(eq("some endpoint"), eq(POST), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    public void shouldSetHeaders() {

        when(mockRequestData.auditBasicAuth()).thenReturn("some basic auth header value");
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.randomUUID(), null);

        verify(mockRestTemplate).exchange(eq("some endpoint"), eq(POST), captorHttpEntity.capture(), eq(Void.class));

        HttpHeaders headers = captorHttpEntity.getValue().getHeaders();
        assertThat(headers.get("Authorization").get(0)).isEqualTo("some basic auth header value");
        assertThat(headers.get("Content-Type").get(0)).isEqualTo(APPLICATION_JSON_VALUE);
    }

    @Test
    public void shouldSetAuditableData() {

        when(mockRequestData.sessionId()).thenReturn("some session id");
        when(mockRequestData.correlationId()).thenReturn("some correlation id");
        when(mockRequestData.userId()).thenReturn("some user id");
        when(mockRequestData.deploymentName()).thenReturn("some deployment name");
        when(mockRequestData.deploymentNamespace()).thenReturn("some deployment namespace");

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.randomUUID(), Collections.emptyMap());

        verify(mockRestTemplate).exchange(eq("some endpoint"), eq(POST), captorHttpEntity.capture(), eq(Void.class));

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
        when(mockRestTemplate.exchange(eq("some history endpoint"), eq(POST), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}))).thenReturn(resultsEntity);

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        verify(mockRestTemplate).exchange(eq("some history endpoint"), eq(POST), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}));
        AuditHistoryRequest request = (AuditHistoryRequest)captorHttpEntity.getValue().getBody();
        assertThat(request.toDate()).isEqualTo(LocalDate.now());
        assertThat(request.eventTypes().size()).isEqualTo(2);
        assertThat(request.eventTypes()).containsExactlyInAnyOrder(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        assertThat(auditRecords).isEqualTo(results);
    }

    @Test
    public void shouldRequestAuditArchive() {
        ArchiveAuditRequest request = new ArchiveAuditRequest("any_nino", LocalDate.now().minusMonths(6), Arrays.asList("corr1", "corr2"), "PASS", LocalDate.now());
        when(mockRestTemplate.exchange(eq("some archive endpoint"), eq(POST), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<ArchiveAuditResponse>() {}))).thenReturn(ResponseEntity.ok(new ArchiveAuditResponse()));

        auditClient.archiveAudit(request);

        verify(mockRestTemplate).exchange(eq("some archive endpoint"), eq(POST), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<ArchiveAuditResponse>() {}));
        ArchiveAuditRequest actual = (ArchiveAuditRequest) captorHttpEntity.getValue().getBody();
        assertThat(actual).isEqualTo(request);
    }
}

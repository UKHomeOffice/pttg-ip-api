package uk.gov.digital.ho.proving.income.audit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.RequestData;
import utils.LogCapturer;

import java.io.IOException;
import java.net.URI;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
    private static final int HISTORY_PAGE_SIZE = 2;

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
                                        HISTORY_PAGE_SIZE,
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
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> results = new ArrayList<>();
        ResponseEntity<List<AuditRecord>> resultsEntity = ResponseEntity.ok(results);
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}))).thenReturn(resultsEntity);

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        URI uri = captorUri.getValue();
        assertThat(uri).hasHost(SOME_HISTORY_ENDPOINT.replace("http://", ""));
        assertThat(auditRecords).isEqualTo(results);
        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=" + eventTypes,
            "page=0",
            "size=" + HISTORY_PAGE_SIZE,
            "toDate=" + LocalDate.now().toString()
        );
    }

    @Test
    public void shouldRetrieveAuditHistory_returnsAllRecords() throws IOException {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> results = mockMultiplePages();

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(auditRecords)
            .extracting("id")
            .containsExactly("0", "1", "2");
    }

    @Test
    public void shouldRetrieveAuditHistory_loadsFirstPage() throws IOException {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> results = mockMultiplePages();

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        URI uri = captorUri.getAllValues().get(0);
        assertThat(uri).hasHost(SOME_HISTORY_ENDPOINT.replace("http://", ""));
        assertThat(auditRecords).isEqualTo(results);
        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=" + eventTypes,
            "page=0",
            "size=" + HISTORY_PAGE_SIZE,
            "toDate=" + LocalDate.now().toString()
        );
    }

    @Test
    public void shouldRetrieveAuditHistory_loadSecondPage() throws IOException {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> results = mockMultiplePages();

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(captorUri.getAllValues()).hasSize(2);
        URI uri = captorUri.getAllValues().get(1);
        assertThat(uri).hasHost(SOME_HISTORY_ENDPOINT.replace("http://", ""));
        assertThat(auditRecords).isEqualTo(results);
        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=" + eventTypes,
            "page=1",
            "size=" + HISTORY_PAGE_SIZE,
            "toDate=" + LocalDate.now().toString()
        );
    }

    @Test
    public void shouldRetrieveAuditHistory_handlesEmptySecondPage() throws IOException {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> firstPage = getAuditRecords(2);
        List<AuditRecord> secondPage = Collections.emptyList();
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {})))
            .thenReturn(ResponseEntity.ok(firstPage), ResponseEntity.ok(secondPage));

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(captorUri.getAllValues()).hasSize(2);
        URI uri = captorUri.getAllValues().get(1);
        assertThat(uri).hasHost(SOME_HISTORY_ENDPOINT.replace("http://", ""));
        assertThat(auditRecords).isEqualTo(firstPage);
        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=" + eventTypes,
            "page=1",
            "size=" + HISTORY_PAGE_SIZE,
            "toDate=" + LocalDate.now().toString()
        );
    }

    private List<AuditRecord> mockMultiplePages() throws IOException {
        List<AuditRecord> results = getAuditRecords(3);
        List<AuditRecord> firstPage = asList(results.get(0), results.get(1));
        List<AuditRecord> secondPage = singletonList(results.get(2));
        ResponseEntity<List<AuditRecord>> resultsEntity1 = ResponseEntity.ok(firstPage);
        ResponseEntity<List<AuditRecord>> resultsEntity2 = ResponseEntity.ok(secondPage);
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}))).thenReturn(resultsEntity1, resultsEntity2);
        return results;
    }

    @Test
    public void shouldRequestAuditArchive() {
        ArchiveAuditRequest request = new ArchiveAuditRequest("any_nino", LocalDate.now().minusMonths(6), asList("corr1", "corr2"), "PASS", LocalDate.now());
        when(mockRestTemplate.exchange(eq(SOME_ARCHIVE_ENDPOINT), eq(POST), captorHttpEntity.capture(), eq(Void.class))).thenReturn(ResponseEntity.ok(null));

        auditClient.archiveAudit(request);

        verify(mockRestTemplate).exchange(eq(SOME_ARCHIVE_ENDPOINT), eq(POST), captorHttpEntity.capture(), eq(Void.class));
        ArchiveAuditRequest actual = (ArchiveAuditRequest) captorHttpEntity.getValue().getBody();
        assertThat(actual).isEqualTo(request);
    }

    @Test
    public void shouldLogAuditArchiveErrors() {
        ArchiveAuditRequest request = new ArchiveAuditRequest("any_nino", LocalDate.now().minusMonths(6), asList("corr1", "corr2"), "PASS", LocalDate.now());
        when(mockRestTemplate.exchange(eq(SOME_ARCHIVE_ENDPOINT), eq(POST), captorHttpEntity.capture(), eq(Void.class)))
            .thenThrow(new RestClientException("exception text"));
        LogCapturer<AuditClient> logCapturer = LogCapturer.forClass(AuditClient.class);
        logCapturer.start();

        auditClient.archiveAudit(request);

        List<ILoggingEvent> allLogEvents = logCapturer.getAllEvents();
        String errorMessage = "";
        for(ILoggingEvent loggingEvent: allLogEvents) {
            if (loggingEvent.getLevel().equals(Level.ERROR)) {
                errorMessage = loggingEvent.getFormattedMessage();
            }
        }
        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("corr1");
        assertThat(errorMessage).contains("corr2");
        assertThat(errorMessage).contains("PASS");
        assertThat(errorMessage).contains("exception text");
    }

    @Test
    public void getAuditHistoryPaginated_givenParams_expectedUri() {
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {})))
            .thenReturn(ResponseEntity.ok(emptyList()));

        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        int page = 23;
        int size = 8;
        auditClient.getAuditHistoryPaginated(eventTypes, page, size);

        URI uri = captorUri.getValue();
        assertThat(uri).hasHost(SOME_HISTORY_ENDPOINT.replace("http://", ""));

        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=" + eventTypes,
            "page=" + page,
            "size=" + size,
            "toDate=" + LocalDate.MAX.toString()
        );
    }

    @Test
    public void getAuditHistoryPaginated_givenResponse_returnRecords() {
        List<AuditRecord> results = emptyList();
        stubResponse(results);

        List<AuditEventType> someEventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
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

    private List<AuditRecord> getAuditRecords(int quantity) throws IOException {
        JsonNode detail = new ObjectMapper().readValue("{}", JsonNode.class);
        return IntStream.range(0, quantity)
            .mapToObj(count -> new AuditRecord(Integer.valueOf(count).toString(), LocalDateTime.now(), "any_email", INCOME_PROVING_FINANCIAL_STATUS_REQUEST, detail, "any_nino"))
            .collect(Collectors.toList());
    }
}

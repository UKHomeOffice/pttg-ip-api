package uk.gov.digital.ho.proving.income.audit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.application.LogEvent;
import uk.gov.digital.ho.proving.income.audit.statistics.AuditClientEndpointProperties;
import utils.LogCapturer;

import java.io.IOException;
import java.net.URI;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.qos.logback.classic.Level.ERROR;
import static ch.qos.logback.classic.Level.INFO;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.application.LogEvent.*;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@RunWith(MockitoJUnitRunner.class)
public class AuditClientTest {

    private static TimeZone defaultTimeZone;
    private static final UUID UUID = new UUID(1, 1);

    @Mock
    private static ObjectMapper mockObjectMapper;
    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private RequestData mockRequestData;
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor private ArgumentCaptor<HttpEntity> captorHttpEntity;
    @Captor private ArgumentCaptor<URI> captorUri;
    @Captor private ArgumentCaptor<String> captorUrl;

    private AuditClient auditClient;

    private static final String SOME_ENDPOINT = "http://some-endpoint";
    private static final String SOME_HISTORY_ENDPOINT = "http://some-history-endpoint";
    private static final String SOME_ARCHIVE_ENDPOINT = "http://some-archive-endpoint";
    private static final String SOME_CORRELATION_IDS_ENDPOINT = "http://some-correlation-ids-endpoint";
    private static final String SOME_HISTORY_BY_CORRELATION_ID_ENDPOINT = "http://some-history-by-correlation-id-endpoint";
    private static final int HISTORY_PAGE_SIZE = 2;

    private static final List<AuditEventType> ANY_EVENT_TYPES = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
    private static final LocalDate ANY_DATE = LocalDate.now();

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
        AuditClientEndpointProperties endpointProperties = new AuditClientEndpointProperties();
        endpointProperties.setAuditEndpoint(SOME_ENDPOINT);
        endpointProperties.setHistoryEndpoint(SOME_HISTORY_ENDPOINT);
        endpointProperties.setArchiveEndpoint(SOME_ARCHIVE_ENDPOINT);
        endpointProperties.setArchiveHistoryPageSize(HISTORY_PAGE_SIZE);
        endpointProperties.setCorrelationIdsEndpoint(SOME_CORRELATION_IDS_ENDPOINT);
        endpointProperties.setHistoryByCorrelationIdEndpoint(SOME_HISTORY_BY_CORRELATION_ID_ENDPOINT);

        auditClient = new AuditClient(Clock.fixed(Instant.parse("2017-08-29T08:00:00Z"), ZoneId.of("UTC")),
            mockRestTemplate,
            mockRequestData,
            endpointProperties,
            mockObjectMapper);

        Logger rootLogger = (Logger) LoggerFactory.getLogger(AuditClient.class);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(mockAppender);
    }

    @Test
    public void shouldUseCollaborators() {
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID, null);

        verify(mockRestTemplate).exchange(eq(SOME_ENDPOINT), eq(POST), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    public void shouldSetHeaders() {
        stubRequestData();
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID, null);

        verify(mockRestTemplate).exchange(eq(SOME_ENDPOINT), eq(POST), captorHttpEntity.capture(), eq(Void.class));

        HttpHeaders headers = captorHttpEntity.getValue().getHeaders();
        assertHeaders(headers);
    }

    @Test
    public void shouldSetAuditableData() throws JsonProcessingException {

        when(mockRequestData.sessionId()).thenReturn("some session id");
        when(mockRequestData.correlationId()).thenReturn("some correlation id");
        when(mockRequestData.userId()).thenReturn("some user id");
        when(mockRequestData.deploymentName()).thenReturn("some deployment name");
        when(mockRequestData.deploymentNamespace()).thenReturn("some deployment namespace");
        when(mockObjectMapper.writeValueAsString(any(Object.class))).thenReturn("{}");

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID, Collections.emptyMap());

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
    public void getAuditHistory_basicCall_correctHostUsed() {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        ResponseEntity<List<AuditRecord>> resultsEntity = ResponseEntity.ok(emptyList());
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}))).thenReturn(resultsEntity);

        auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        URI uri = captorUri.getValue();
        assertThat(uri).hasHost(SOME_HISTORY_ENDPOINT.replace("http://", ""));
    }

    @Test
    public void getAuditHistory_basicCall_expectedResultsReturned() {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> results = new ArrayList<>();
        ResponseEntity<List<AuditRecord>> resultsEntity = ResponseEntity.ok(results);
        when(mockRestTemplate.exchange(any(URI.class), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}))).thenReturn(resultsEntity);

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(auditRecords).isEqualTo(results);
    }

    @Test
    public void getAuditHistory_basicCall_queryParametersCorrect() {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        ResponseEntity<List<AuditRecord>> resultsEntity = ResponseEntity.ok(emptyList());
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}))).thenReturn(resultsEntity);

        auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        URI uri = captorUri.getValue();
        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=" + eventTypes.get(0),
            "eventTypes=" + eventTypes.get(1),
            "page=0",
            "size=" + HISTORY_PAGE_SIZE,
            "toDate=" + LocalDate.now().toString()
        );
    }

    @Test
    public void getAuditHistory_multiplePages_returnsAllRecords() throws IOException {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        mockMultiplePages();

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(auditRecords)
            .extracting("id")
            .containsExactly("0", "1", "2");
    }

    @Test
    public void getAuditHistory_multiplePages_requestsFirstPage() throws IOException {
        List<AuditEventType> eventTypes = singletonList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST);
        mockMultiplePages();

        auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        URI uri = captorUri.getAllValues().get(0);
        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST",
            "page=0",
            "size=" + HISTORY_PAGE_SIZE,
            "toDate=" + LocalDate.now().toString()
        );
    }

    @Test
    public void getAuditHistory_multiplePages_requestsSecondPage() throws IOException {
        List<AuditEventType> eventTypes = singletonList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST);
        mockMultiplePages();

        auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(captorUri.getAllValues()).hasSize(2);
        URI uri = captorUri.getAllValues().get(1);
        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST",
            "page=1",
            "size=" + HISTORY_PAGE_SIZE,
            "toDate=" + LocalDate.now().toString()
        );
    }

    @Test
    public void getAuditHistory_multiplePages_returnsResultsFromAllPages() throws IOException {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> results = mockMultiplePages();

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(auditRecords).isEqualTo(results);
    }

    @Test
    public void getAuditHistory_emptySecondPage_requestsSecondPage() throws IOException {
        List<AuditEventType> eventTypes = singletonList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST);
        List<AuditRecord> firstPage = getAuditRecords(2);
        List<AuditRecord> secondPage = Collections.emptyList();
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {})))
            .thenReturn(ResponseEntity.ok(firstPage), ResponseEntity.ok(secondPage));

        auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(captorUri.getAllValues()).hasSize(2);
        URI uri = captorUri.getAllValues().get(1);
        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST",
            "page=1",
            "size=" + HISTORY_PAGE_SIZE,
            "toDate=" + LocalDate.now().toString()
        );
    }

    @Test
    public void getAuditHistory_emptySecondPage_returnsResultsFromFirstPage() throws IOException {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        List<AuditRecord> firstPage = getAuditRecords(2);
        List<AuditRecord> secondPage = Collections.emptyList();
        when(mockRestTemplate.exchange(any(URI.class), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {})))
            .thenReturn(ResponseEntity.ok(firstPage), ResponseEntity.ok(secondPage));

        List<AuditRecord> auditRecords = auditClient.getAuditHistory(LocalDate.now(), eventTypes);

        assertThat(auditRecords).isEqualTo(firstPage);
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
    public void archiveAudit_shouldRequestAuditArchive() {
        ArchiveAuditRequest request = new ArchiveAuditRequest("any_nino", LocalDate.now().minusMonths(6), ImmutableSet.of("corr1", "corr2"), "PASS");
        when(mockRestTemplate.exchange(eq(SOME_ARCHIVE_ENDPOINT + "/2019-06-30"), eq(POST), captorHttpEntity.capture(), eq(Void.class))).thenReturn(ResponseEntity.ok(null));

        auditClient.archiveAudit(request, LocalDate.of(2019, 6, 30));

        verify(mockRestTemplate).exchange(eq(SOME_ARCHIVE_ENDPOINT + "/2019-06-30"), eq(POST), captorHttpEntity.capture(), eq(Void.class));
        ArchiveAuditRequest actual = (ArchiveAuditRequest) captorHttpEntity.getValue().getBody();
        assertThat(actual).isEqualTo(request);
    }

    @Test
    public void archiveAudit_shouldFormatResultDateOnUrl() {
        ArchiveAuditRequest request = new ArchiveAuditRequest("any_nino", LocalDate.now().minusMonths(6),  ImmutableSet.of("corr1", "corr2"), "PASS");
        when(mockRestTemplate.exchange(captorUrl.capture(), eq(POST), captorHttpEntity.capture(), eq(Void.class))).thenReturn(ResponseEntity.ok(null));

        auditClient.archiveAudit(request, LocalDate.of(2019, 6, 30));

        String url = captorUrl.getValue();
        assertThat(url).endsWith("/2019-06-30");
    }

    @Test
    public void archiveAudit_shouldLogAuditArchiveErrors() {
        ArchiveAuditRequest request = new ArchiveAuditRequest("any_nino", LocalDate.now().minusMonths(6),  ImmutableSet.of("corr1", "corr2"), "PASS");
        when(mockRestTemplate.exchange(eq(SOME_ARCHIVE_ENDPOINT + "/2019-06-30"), eq(POST), captorHttpEntity.capture(), eq(Void.class)))
            .thenThrow(new RestClientException("exception text"));
        LogCapturer<AuditClient> logCapturer = LogCapturer.forClass(AuditClient.class);
        logCapturer.start();

        auditClient.archiveAudit(request, LocalDate.of(2019, 6, 30));

        List<ILoggingEvent> allLogEvents = logCapturer.getAllEvents();
        String errorMessage = "";
        for (ILoggingEvent loggingEvent : allLogEvents) {
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
    public void shouldLogWhenAddToAuditServiceEvent() {
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID, Collections.emptyMap());

        verifyLogMessage("POST data for 00000000-0000-0001-0000-000000000001 to audit service", INCOME_PROVING_AUDIT_REQUEST, INFO);
    }

    @Test
    public void shouldLogAfterSuccessfulAuditServiceCall() {
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID, Collections.emptyMap());

        verifyLogMessage("data POSTed to audit service", INCOME_PROVING_AUDIT_SUCCESS, INFO);
    }

    @Test
    public void shouldLogAfterFailureToAudit() throws JsonProcessingException {
        when(mockObjectMapper.writeValueAsString(any(Object.class))).thenThrow(JsonProcessingException.class);

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID, Collections.emptyMap());

        verifyLogMessage("Failed to create json representation of audit data", INCOME_PROVING_AUDIT_FAILURE, ERROR);
    }

    private void verifyLogMessage(final String message, LogEvent event, Level logLevel) {
        verify(mockAppender).doAppend(argThat(argument -> {
            LoggingEvent loggingEvent = (LoggingEvent) argument;
            return loggingEvent.getLevel().equals(logLevel) &&
                loggingEvent.getFormattedMessage().equals(message) &&
                Arrays.asList(loggingEvent.getArgumentArray()).contains(new ObjectAppendingMarker("event_id", event));
        }));
    }

    @Test
    public void getArchivedResults_givenDates_expectedUri() {
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<ArchivedResult>>() {})))
            .thenReturn(ResponseEntity.ok(emptyList()));

        LocalDate fromDate = LocalDate.of(2018, 12, 1);
        LocalDate toDate = LocalDate.of(2018, 12, 31);
        auditClient.getArchivedResults(fromDate, toDate);

        URI uri = captorUri.getValue();
        assertThat(uri).hasHost(SOME_ARCHIVE_ENDPOINT.replace("http://", ""));

        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder(
            "fromDate=" + fromDate,
            "toDate=" + toDate
        );
    }

    @Test
    public void getArchivedResults_givenResponse_returnResults() {
        List<ArchivedResult> expectedResults = singletonList(new ArchivedResult(singletonMap("PASSED", 5)));
        when(mockRestTemplate.exchange(any(URI.class), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<ArchivedResult>>() {})))
            .thenReturn(ResponseEntity.ok(expectedResults));

        LocalDate someDate = LocalDate.now();
        List<ArchivedResult> actualResults = auditClient.getArchivedResults(someDate, someDate);
        assertThat(actualResults).isEqualTo(expectedResults);
    }

    @Test
    public void generateUri_withDate_singleEventType_correctlyFormatted() {
        URI uri = auditClient.generateUri(singletonList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST), 1, 1, LocalDate.of(2019, 6, 30));

        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).contains("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST");
    }

    @Test
    public void generateUri_withDate_multipleEventTypes_correctlyFormatted() {
        URI uri = auditClient.generateUri(asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE), 1, 1, LocalDate.of(2019, 6, 30));

        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).contains("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST");
        assertThat(queryStringComponents).contains("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE");
    }

    @Test
    public void generateUri_withoutDate_singleEventType_correctlyFormatted() {
        URI uri = auditClient.generateUri(singletonList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST), 1, 1);

        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).contains("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST");
    }

    @Test
    public void generateUri_withoutDate_multipleEventTypes_correctlyFormatted() {
        URI uri = auditClient.generateUri(asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE), 1, 1);

        String[] queryStringComponents = uri.getQuery().split("&");
        assertThat(queryStringComponents).contains("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST");
        assertThat(queryStringComponents).contains("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE");
    }

    @Test
    public void getAllCorrelationIdsForEventType_anyRequest_expectedUrlCalled() {
        stubGetAllCorrelationIds();

        auditClient.getAllCorrelationIdsForEventType(ANY_EVENT_TYPES);
        assertThat(captorUri.getValue())
            .hasHost(SOME_CORRELATION_IDS_ENDPOINT.replace("http://", ""));
    }

    @Test
    public void getAllCorrelationIdsForEventType_givenEventTypes_queryParametersCorrect() {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);

        stubGetAllCorrelationIds();

        auditClient.getAllCorrelationIdsForEventType(eventTypes);

        String[] queryStringComponents = captorUri.getValue().getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST", "eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE");
    }

    @Test
    public void getAllCorrelationIdsForEventType_anyRequest_shouldSetHeaders() {
        stubRequestData();
        stubGetAllCorrelationIds();

        auditClient.getAllCorrelationIdsForEventType(ANY_EVENT_TYPES);

        verify(mockRestTemplate).exchange(any(URI.class), eq(GET), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<List<String>>() {}));

        HttpHeaders headers = captorHttpEntity.getValue().getHeaders();
        assertHeaders(headers);
    }

    @Test
    public void getAllCorrelationIdsForEventType_givenResponse_returnCorrelationIds() {
        List<String> expectedCorrelationIds = asList("some correlation id", "some other correlation id");
        stubGetAllCorrelationIds(expectedCorrelationIds);

        List<String> actualCorrelationIds = auditClient.getAllCorrelationIdsForEventType(ANY_EVENT_TYPES);

        assertThat(actualCorrelationIds).isEqualTo(expectedCorrelationIds);
    }

    @Test
    public void getAllCorrelationIdsForEventType_withDate_expectedUrlCalled() {
        stubGetAllCorrelationIds(asList("any correlation id", "any other correlation id"));

        auditClient.getAllCorrelationIdsForEventType(ANY_EVENT_TYPES, ANY_DATE);

        assertThat(captorUri.getValue())
            .hasHost(SOME_CORRELATION_IDS_ENDPOINT.replace("http://", ""));
    }

    @Test
    public void getAllCorrelationIdsForEventType_withDate_queryParametersCorrect() {
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        LocalDate toDate = LocalDate.parse("2019-07-31");

        stubGetAllCorrelationIds();

        auditClient.getAllCorrelationIdsForEventType(eventTypes, toDate);
        String[] queryStringComponents = captorUri.getValue().getQuery().split("&");
        assertThat(queryStringComponents).containsExactlyInAnyOrder("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST",
                                                                    "eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE",
                                                                    "toDate=2019-07-31");
    }

    @Test
    public void getAllCorrelationIdsForEventType_withDate_shouldSetHeaders() {
        stubRequestData();
        stubGetAllCorrelationIds();

        auditClient.getAllCorrelationIdsForEventType(ANY_EVENT_TYPES, ANY_DATE);

        verify(mockRestTemplate).exchange(any(URI.class), eq(GET), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<List<String>>() {}));

        HttpHeaders headers = captorHttpEntity.getValue().getHeaders();
        assertHeaders(headers);
    }

    @Test
    public void getAllCorrelationIdsForEventType_withDate_returnCorrelationIds() {
        List<String> expectedCorrelationIds = asList("some correlation id", "some other correlation id");
        stubGetAllCorrelationIds(expectedCorrelationIds);

        List<String> actualCorrelationIds = auditClient.getAllCorrelationIdsForEventType(ANY_EVENT_TYPES, ANY_DATE);
        assertThat(actualCorrelationIds).isEqualTo(expectedCorrelationIds);
    }

    @Test
    public void getHistoryByCorrelationId_anyRequest_expectedUriCalled() {
        stubGetHistoryForCorrelationId();

        auditClient.getHistoryByCorrelationId("any correlation id", ANY_EVENT_TYPES);

        assertThat(captorUri.getValue())
            .hasHost(SOME_HISTORY_BY_CORRELATION_ID_ENDPOINT.replace("http://", ""));
    }

    @Test
    public void getHistoryByCorrelationId_givenParameters_expectedQueryString() {
        stubGetHistoryForCorrelationId();

        List<AuditEventType> someEventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        String someCorrelationId = "some-correlation-id";
        auditClient.getHistoryByCorrelationId(someCorrelationId, someEventTypes);

        String[] queryParams = captorUri.getValue().getQuery().split("&");
        assertThat(queryParams)
            .containsExactlyInAnyOrder("correlationId=some-correlation-id", "eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST",
                                       "eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE");
    }

    @Test
    public void getHistoryByCorrelationId_anyRequest_shouldSetHeaders() {
        stubRequestData();
        stubGetHistoryForCorrelationId();

        auditClient.getHistoryByCorrelationId("any correlation ID", ANY_EVENT_TYPES);

        verify(mockRestTemplate).exchange(any(URI.class), eq(GET), captorHttpEntity.capture(), eq(new ParameterizedTypeReference<List<AuditRecord>>() {}));

        HttpHeaders headers = captorHttpEntity.getValue().getHeaders();
        assertHeaders(headers);
    }

    @Test
    public void getHistoryByCorrelationId_givenResponse_returnAuditRecords() {
        AuditRecord someAuditRecord = new AuditRecord("some id", LocalDateTime.now(), "some email", INCOME_PROVING_FINANCIAL_STATUS_REQUEST, null, "some nino");
        stubGetHistoryForCorrelationId(someAuditRecord);

        List<AuditRecord> expectedAuditRecords = singletonList(someAuditRecord);

        List<AuditRecord> actualAuditRecords = auditClient.getHistoryByCorrelationId("any correlation ID", ANY_EVENT_TYPES);
        assertThat(actualAuditRecords).isEqualTo(expectedAuditRecords);
    }

    private void assertHeaders(HttpHeaders headers) {
        assertThat(headers.get("Authorization").get(0)).isEqualTo("some basic auth header value");
        assertThat(headers.get("Content-Type").get(0)).isEqualTo(APPLICATION_JSON_VALUE);
        assertThat(headers.get("x-correlation-id").get(0)).isEqualTo("some correlation id");
    }

    private void stubRequestData() {
        when(mockRequestData.auditBasicAuth()).thenReturn("some basic auth header value");
        when(mockRequestData.correlationId()).thenReturn("some correlation id");
    }

    private void stubGetAllCorrelationIds() {
        stubGetAllCorrelationIds(asList("any correlation id", "any other correlation id"));
    }

    private void stubGetAllCorrelationIds(List<String> correlationIds) {
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<String>>() {})))
            .thenReturn(ResponseEntity.ok(correlationIds));
    }

    private void stubGetHistoryForCorrelationId() {
        AuditRecord anyAuditRecord = new AuditRecord("some id", LocalDateTime.now(), "some email", INCOME_PROVING_FINANCIAL_STATUS_REQUEST, null, "some nino");
        stubGetHistoryForCorrelationId(anyAuditRecord);
    }

    private void stubGetHistoryForCorrelationId(AuditRecord anyAuditRecord) {
        when(mockRestTemplate.exchange(captorUri.capture(), eq(GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuditRecord>>() {})))
            .thenReturn(ResponseEntity.ok(singletonList(anyAuditRecord)));
    }

    private List<AuditRecord> getAuditRecords(int quantity) throws IOException {
        JsonNode detail = new ObjectMapper().readValue("{}", JsonNode.class);
        return IntStream.range(0, quantity)
            .mapToObj(count -> new AuditRecord(Integer.valueOf(count).toString(), LocalDateTime.now(), "any_email", INCOME_PROVING_FINANCIAL_STATUS_REQUEST, detail, "any_nino"))
            .collect(Collectors.toList());
    }
}

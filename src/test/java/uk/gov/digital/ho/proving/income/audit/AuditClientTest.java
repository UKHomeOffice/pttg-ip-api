package uk.gov.digital.ho.proving.income.audit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.application.LogEvent;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static ch.qos.logback.classic.Level.ERROR;
import static ch.qos.logback.classic.Level.INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_AUDIT_FAILURE;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_AUDIT_REQUEST;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_AUDIT_SUCCESS;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;

@RunWith(MockitoJUnitRunner.class)
public class AuditClientTest {

    private static TimeZone defaultTimeZone;
    private static final UUID  UUID = new UUID(1, 1);

    @Mock private static ObjectMapper mockObjectMapper;
    @Mock private RestTemplate mockRestTemplate;
    @Mock private RequestData mockRequestData;
    @Mock private Appender<ILoggingEvent> mockAppender;

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
                                        mockObjectMapper);

        Logger rootLogger = (Logger) LoggerFactory.getLogger(AuditClient.class);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(mockAppender);
    }

    @Test
    public void shouldUseCollaborators() {
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID, null);

        verify(mockRestTemplate).exchange(eq("some endpoint"), eq(POST), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    public void shouldSetHeaders() {

        when(mockRequestData.auditBasicAuth()).thenReturn("some basic auth header value");
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID, null);

        verify(mockRestTemplate).exchange(eq("some endpoint"), eq(POST), captorHttpEntity.capture(), eq(Void.class));

        HttpHeaders headers = captorHttpEntity.getValue().getHeaders();
        assertThat(headers.get("Authorization").get(0)).isEqualTo("some basic auth header value");
        assertThat(headers.get("Content-Type").get(0)).isEqualTo(APPLICATION_JSON_VALUE);
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
        ArgumentCaptor<ILoggingEvent> captor = ArgumentCaptor.forClass(ILoggingEvent.class);
        verify(mockAppender, atLeastOnce()).doAppend(captor.capture());

        List<ILoggingEvent> loggingEvents = captor.getAllValues();
        for (ILoggingEvent loggingEvent : loggingEvents) {
            LoggingEvent logEvent = (LoggingEvent) loggingEvent;
            if (logEvent.getFormattedMessage().equals(message) && logEvent.getLevel().equals(logLevel) &&
                loggingEvent.getArgumentArray()[loggingEvent.getArgumentArray().length - 1].equals(new ObjectAppendingMarker("event_id", event))) {
                return;
            }
        }
        fail(String.format("Failed to find log with message=\"%s\" and level=\"%s\"", message, logLevel));
    }
}

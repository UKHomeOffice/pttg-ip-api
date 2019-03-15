package uk.gov.digital.ho.proving.income.audit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
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

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;

@RunWith(MockitoJUnitRunner.class)
public class AuditClientTest {

    private static TimeZone defaultTimeZone;
    private static ObjectMapper mapper = new ObjectMapper();

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
    public void shouldLogEventType() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(AuditClient.class);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(mockAppender);

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.randomUUID(), Collections.emptyMap());

        verify(mockAppender).doAppend(argThat(argument -> {
            LoggingEvent loggingEvent = (LoggingEvent) argument;

            return loggingEvent.getMessage().equals("POST data for {} to audit service") &&
                ((ObjectAppendingMarker) loggingEvent.getArgumentArray()[1]).getFieldName().equals("event_id");
        }));
    }
}

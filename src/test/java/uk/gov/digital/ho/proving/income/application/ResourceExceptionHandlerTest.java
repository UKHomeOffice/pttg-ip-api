package uk.gov.digital.ho.proving.income.application;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import uk.gov.digital.ho.proving.income.api.NinoUtils;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import utils.LogCapturer;

import java.util.Arrays;
import java.util.List;

import static ch.qos.logback.classic.Level.ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_AUDIT_FAILURE;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_SERVICE_RESPONSE_ERROR;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_SERVICE_RESPONSE_NOT_FOUND;

@RunWith(MockitoJUnitRunner.class)
public class ResourceExceptionHandlerTest {

    @InjectMocks
    private ResourceExceptionHandler resourceExceptionHandler;
    @Mock
    private NinoUtils ninoUtils;
    @Mock
    private AuditClient auditClient;
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Before
    public void setUp() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(ResourceExceptionHandler.class);
        rootLogger.setLevel(ERROR);
        rootLogger.addAppender(mockAppender);
    }

    @Test
    public void thatNotFoundExceptionDoesNotLogFullNino() {
        LogCapturer<ResourceExceptionHandler> logCapturer = LogCapturer.forClass(ResourceExceptionHandler.class);
        logCapturer.start();

        String realNino = "RealNino";
        String redactedNino = "RedadactedNino";

        when(ninoUtils.redact(realNino)).thenReturn(redactedNino);

        resourceExceptionHandler.handle(new ApplicationExceptions.EarningsServiceNoUniqueMatchException(realNino));

        List<ILoggingEvent> allLogEvents = logCapturer.getAllEvents();
        for (final ILoggingEvent logEvent : allLogEvents) {
            final String logMessage = logEvent.getFormattedMessage();
            assertThat(logMessage).doesNotContain(realNino);
        }
    }

    @Test
    public void shouldLogAuditDataException() {
        resourceExceptionHandler.handle(new ApplicationExceptions.AuditDataException("some message"));

        verifyLogMessage("some message", INCOME_PROVING_AUDIT_FAILURE);
    }

    @Test
    public void shouldLogMissingServletRequestParameterException() {
        resourceExceptionHandler.handle(new MissingServletRequestParameterException("missing parameter", "parameter type"));

        verifyLogMessage("Required parameter type parameter 'missing parameter' is not present", INCOME_PROVING_SERVICE_RESPONSE_ERROR);
    }

    @Test
    public void shouldLogMissingNoHandlerfoundException() {
        resourceExceptionHandler.handle(new NoHandlerFoundException("POST", "some url", httpHeaders()));

        verifyLogMessage("No handler found for POST some url", INCOME_PROVING_SERVICE_RESPONSE_NOT_FOUND);
    }

    private void verifyLogMessage(final String message, LogEvent event) {
        verify(mockAppender).doAppend(argThat(argument -> {
            LoggingEvent loggingEvent = (LoggingEvent) argument;
            return loggingEvent.getLevel().equals(ERROR) &&
                loggingEvent.getFormattedMessage().equals(message) &&
                Arrays.asList(loggingEvent.getArgumentArray()).contains(new ObjectAppendingMarker("event_id", event));
        }));
    }

    private HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        return headers;
    }
}

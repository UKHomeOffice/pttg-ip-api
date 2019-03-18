package uk.gov.digital.ho.proving.income.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import uk.gov.digital.ho.proving.income.api.NinoUtils;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import utils.LogCapturer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void shouldLogEventWhenExceptionThrown() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(ResourceExceptionHandler.class);
        rootLogger.setLevel(Level.ERROR);
        rootLogger.addAppender(mockAppender);

        resourceExceptionHandler.handle(new ApplicationExceptions.AuditDataException("some message"));

        verify(mockAppender).doAppend(argThat(argument -> {
            LoggingEvent loggingEvent = (LoggingEvent) argument;

            return loggingEvent.getFormattedMessage().equals("some message") &&
                ((ObjectAppendingMarker) loggingEvent.getArgumentArray()[0]).getFieldName().equals("event_id") &&
                loggingEvent.getArgumentArray()[0].toString().equals("INCOME_PROVING_AUDIT_FAILURE");
        }));
    }



}

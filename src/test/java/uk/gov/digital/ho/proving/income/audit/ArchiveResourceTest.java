package uk.gov.digital.ho.proving.income.audit;

import ch.qos.logback.classic.Level;
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
import uk.gov.digital.ho.proving.income.api.RequestData;
import utils.LogCapturer;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ArchiveResourceTest {

    @InjectMocks private ArchiveResource archiveResource;
    @Mock private AuditArchiveService auditArchiveService;
    @Mock private RequestData mockRequestData;
    @Mock private Appender<ILoggingEvent> mockAppender;


    @Before
    public void setUp() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(ArchiveResource.class);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(mockAppender);
    }

    @Test
    public void archive_callsAuditArchiveService() {
        archiveResource.archive();

        verify(auditArchiveService).archiveAudit();
    }

    @Test
    public void archive_logsRequestReceived() {
        LogCapturer<ArchiveResource> logCapturer = LogCapturer.forClass(ArchiveResource.class);
        logCapturer.start();

        archiveResource.archive();

        verify(mockAppender).doAppend(argThat(arg -> {
                LoggingEvent loggingEvent = (LoggingEvent) arg;
                return loggingEvent.getFormattedMessage().toLowerCase().contains("request received")
                    && loggingEvent.getLevel().equals(Level.INFO);
        }));
    }

    @Test
    public void archive_logsResponseReturned() {
        LogCapturer<ArchiveResource> logCapturer = LogCapturer.forClass(ArchiveResource.class);
        logCapturer.start();

        archiveResource.archive();

        verify(mockAppender).doAppend(argThat(arg -> {
                LoggingEvent loggingEvent = (LoggingEvent) arg;
                return loggingEvent.getFormattedMessage().toLowerCase().contains("ok response returned")
                    && loggingEvent.getLevel().equals(Level.INFO);
        }));
    }

    @Test
    public void archive_logsRequestDuration() {
        LogCapturer<ArchiveResource> logCapturer = LogCapturer.forClass(ArchiveResource.class);
        logCapturer.start();

        archiveResource.archive();

        verify(mockAppender).doAppend(argThat(arg -> {
            LoggingEvent loggingEvent = (LoggingEvent) arg;
            Object[] arguments = loggingEvent.getArgumentArray();
            if (Objects.isNull(arguments) || arguments.length == 0) {
                return false;
            }
            return ((ObjectAppendingMarker)arguments[0]).getFieldName().toLowerCase().equals("request_duration_ms");
        }));
    }
}

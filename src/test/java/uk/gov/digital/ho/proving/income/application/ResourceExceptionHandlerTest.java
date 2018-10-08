package uk.gov.digital.ho.proving.income.application;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.NinoUtils;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import utils.LogCapturer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceExceptionHandlerTest {

    @InjectMocks
    private ResourceExceptionHandler resourceExceptionHandler;

    @Mock
    private NinoUtils ninoUtils;

    @Mock
    private AuditClient auditClient;

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

}

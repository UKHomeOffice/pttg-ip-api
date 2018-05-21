package utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

public class LogCapturer<T> {
    private final Logger logger;
    private List<ILoggingEvent> loggingEvents;
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    private LogCapturer(final Class<T> loggingClass) {
        MockitoAnnotations.initMocks(this);
        logger = (Logger) LoggerFactory.getLogger(loggingClass);
    }

    public static <T> LogCapturer<T> forClass(Class<T> loggingClass) {
        requireNonNull(loggingClass, "Logging class cannot be null.");
        return new LogCapturer<>(loggingClass);
    }

    public void start() {
        logger.addAppender(mockAppender);
    }

    public List<ILoggingEvent> getAllEvents() {
        captureLogEvents();
        return loggingEvents;
    }

    private void captureLogEvents() {
        final ArgumentCaptor<ILoggingEvent> logCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
        verify(mockAppender, atLeast(0)).doAppend(logCaptor.capture());

        loggingEvents = Collections.unmodifiableList(logCaptor.getAllValues());
    }
}

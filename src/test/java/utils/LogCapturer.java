package utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

public class LogCapturer<T> {
    @Captor
    private ArgumentCaptor<ILoggingEvent> logCaptor;

    @Mock
    private Appender<ILoggingEvent> mockAppender;
    private Logger logger;

    private LogCapturer(final Class<T> loggingClass) {
        this(loggingClass, Level.ALL);
    }

    private LogCapturer(final Class<T> loggingClass, final Level loggingLevel) {
        MockitoAnnotations.initMocks(this);
        logger = (Logger) LoggerFactory.getLogger(loggingClass);
        logger.setLevel(loggingLevel);
    }

    public static <T> LogCapturer<T> forClass(Class<T> loggingClass) {
        requireNonNull(loggingClass, "Logging class cannot be null.");
        return new LogCapturer<>(loggingClass);
    }

    public static <T> LogCapturer<T> forClass(Class<T> loggingClass, final Level loggingLevel) {
        requireNonNull(loggingClass, "Logging class cannot be null.");
        requireNonNull(loggingLevel, "Logging level cannot be null.");
        return new LogCapturer<>(loggingClass, loggingLevel);
    }

    public void start() {
        logger.addAppender(mockAppender);
    }

    public List<ILoggingEvent> getAllEvents() {
        captureLogEvents();
        return logCaptor.getAllValues();
    }

    public ILoggingEvent getLastEvent() {
        captureLogEvents();
        return logCaptor.getValue();
    }

    private void captureLogEvents() {
        verify(mockAppender, atLeast(0)).doAppend(logCaptor.capture());
    }
}

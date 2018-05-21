package utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class LogCapturerTest {
    private static final Logger log = LoggerFactory.getLogger(LogCapturerTest.class);

    @Test
    public void shouldBeEmptyOnInit() {
        // given
        final LogCapturer<LogCapturerTest> logCapturer = LogCapturer.forClass(LogCapturerTest.class);
        logCapturer.start();

        // when
        // no log statements

        // then
        // verify log events list is empty
        assertThat(logCapturer.getAllEvents()).isEmpty();
    }

    @Test
    public void shouldReturnEmptyListIfNotStarted() {
        // given
        final LogCapturer<LogCapturerTest> logCapturer = LogCapturer.forClass(LogCapturerTest.class);

        // when
        // not started

        // then
        // verify log events list is empty
        assertThat(logCapturer.getAllEvents()).isEmpty();
    }

    @Test
    public void shouldReturnUnmodifiableList() {
        // given
        final LogCapturer<LogCapturerTest> logCapturer = LogCapturer.forClass(LogCapturerTest.class);
        logCapturer.start();

        // when
        try {
            logCapturer.getAllEvents().add(null);
            fail("A `UnsupportedOperationException` should have been thrown");
        } catch (final UnsupportedOperationException e) {
            // success
        }
    }

    @Test
    public void shouldCaptureLogs() {
        final LogCapturer<LogCapturerTest> logCapturer = LogCapturer.forClass(LogCapturerTest.class);
        logCapturer.start();

        for (int i = 0; i < 10; i++) {
            final String logMessage = String.format("Log Message #%d", i);

            log.info(logMessage);

            final List<ILoggingEvent> logEvents = logCapturer.getAllEvents();
            assertThat(logEvents).hasSize(i + 1);

            final ILoggingEvent latestLog = logEvents.get(i);
            assertThat(latestLog.getFormattedMessage()).isEqualTo(logMessage);
        }
    }
}

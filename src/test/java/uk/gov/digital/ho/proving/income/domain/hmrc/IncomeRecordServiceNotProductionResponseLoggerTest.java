package uk.gov.digital.ho.proving.income.domain.hmrc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncomeRecordServiceNotProductionResponseLoggerTest {

    @Mock
    private ObjectMapper mockMapper;
    @Mock
    private Appender mockAppender;

    @InjectMocks
    private IncomeRecordServiceNotProductionResponseLogger incomeRecordServiceNotProductionResponseLogger;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Before
    public void setup() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(IncomeRecordServiceNotProductionResponseLogger.class);
        logger.addAppender(mockAppender);
    }

    @After
    public void tearDown() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).detachAppender(mockAppender);
    }

    @Test
    public void shouldUseCollaborators() throws JsonProcessingException {

        IncomeRecord incomeRecord = new IncomeRecord(emptyList(), emptyList());

        when(mockMapper.writeValueAsString(incomeRecord)).thenReturn("json version of Income Record");

        incomeRecordServiceNotProductionResponseLogger.record(incomeRecord);

        verify(mockMapper).writeValueAsString(incomeRecord);
        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        assertThat(captorLoggingEvent.getAllValues().size()).isEqualTo(1);
        assertThat(captorLoggingEvent.getValue().getLevel()).isEqualTo(Level.INFO);
        assertThat(captorLoggingEvent.getValue().getMessage()).isEqualTo("json version of Income Record");
    }

    @Test
    public void shouldSwallowJsonProcessingException() throws JsonProcessingException {

        IncomeRecord incomeRecord = new IncomeRecord(emptyList(), emptyList());

        when(mockMapper.writeValueAsString(incomeRecord)).thenThrow(JsonProcessingException.class);

        incomeRecordServiceNotProductionResponseLogger.record(incomeRecord);

        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        assertThat(captorLoggingEvent.getAllValues().size()).isEqualTo(1);
        assertThat(captorLoggingEvent.getValue().getLevel()).isEqualTo(Level.ERROR);
        assertThat(captorLoggingEvent.getValue().getMessage()).isEqualTo("Failed to turn IncomeRecord response data into JSON");
    }
}

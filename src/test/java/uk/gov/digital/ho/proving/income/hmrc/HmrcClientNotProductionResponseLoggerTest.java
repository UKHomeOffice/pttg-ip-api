package uk.gov.digital.ho.proving.income.hmrc;

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
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.time.LocalDate;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HmrcClientNotProductionResponseLoggerTest {

    @Mock private ObjectMapper mockMapper;
    @Mock private Appender mockAppender;

    @InjectMocks private IncomeRecordServiceNotProductionResponseLogger incomeRecordServiceNotProductionResponseLogger;

    @Captor private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    private Identity stubIdentity;
    private IncomeRecord stubIncomeRecord;

    @Before
    public void setup() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(IncomeRecordServiceNotProductionResponseLogger.class);
        logger.addAppender(mockAppender);

        stubIncomeRecord = new IncomeRecord(emptyList(), emptyList(), emptyList(), aIndividual());

        stubIdentity = new Identity("some firstname",
                                        "some lastname",
                                        LocalDate.now(),
                                        "some nino");
    }

    @After
    public void tearDown() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).detachAppender(mockAppender);
    }

    @Test
    public void shouldUseCollaborators() throws JsonProcessingException {

        when(mockMapper.writeValueAsString(any())).thenReturn("json version of Income Record");

        incomeRecordServiceNotProductionResponseLogger.record(stubIdentity, stubIncomeRecord);

        verify(mockMapper).writeValueAsString(
                            incomeRecordServiceNotProductionResponseLogger.produceLogEntry(stubIdentity, stubIncomeRecord));
        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        assertThat(captorLoggingEvent.getAllValues().size()).isEqualTo(1);
        assertThat(captorLoggingEvent.getValue().getLevel()).isEqualTo(Level.INFO);
        assertThat(captorLoggingEvent.getValue().getMessage()).isEqualTo("json version of Income Record");
    }

    @Test
    public void shouldSwallowJsonProcessingException() throws JsonProcessingException {

        when(mockMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        incomeRecordServiceNotProductionResponseLogger.record(stubIdentity, stubIncomeRecord);

        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        assertThat(captorLoggingEvent.getAllValues().size()).isEqualTo(1);
        assertThat(captorLoggingEvent.getValue().getLevel()).isEqualTo(Level.ERROR);
        assertThat(captorLoggingEvent.getValue().getMessage()).isEqualTo("Failed to turn IncomeRecord response data into JSON");
    }

    @Test
    public void shouldProduceLogEntry() {

        Map<String, Object> logEntry = incomeRecordServiceNotProductionResponseLogger.produceLogEntry(stubIdentity, stubIncomeRecord);

        assertThat(logEntry.size()).isEqualTo(2);
        assertThat(logEntry.containsKey("identity"));
        assertThat(logEntry.get("identity")).isEqualTo(stubIdentity);
        assertThat(logEntry.containsKey("incomeRecord"));
        assertThat(logEntry.get("incomeRecord")).isEqualTo(stubIncomeRecord);
    }

    private HmrcIndividual aIndividual() {
        return new HmrcIndividual("Joe", "Bloggs", "NE121212C", LocalDate.now());
    }

}

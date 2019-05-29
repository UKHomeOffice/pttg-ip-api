package uk.gov.digital.ho.proving.income.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.google.common.collect.ImmutableList;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import uk.gov.digital.ho.proving.income.api.domain.*;
import uk.gov.digital.ho.proving.income.application.LogEvent;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;
import utils.LogCapturer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static ch.qos.logback.classic.Level.INFO;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_SERVICE_REQUEST_RECEIVED;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_SERVICE_RESPONSE_SUCCESS;

@RunWith(MockitoJUnitRunner.class)
public class FinancialStatusResourceTest {

    @InjectMocks
    private FinancialStatusResource service;
    @Mock
    private FinancialStatusService mockHelper;
    @Mock
    private AuditClient mockAuditClient;
    @Mock
    private NinoUtils mockNinoUtils;
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    private final String realNino = "RealNino";
    private final String redactedNino = "RedactedNino";
    private final String sanitisedNino = "SanitisedNino";

    private final List<Applicant> applicants = Arrays.asList(new Applicant("forename",
        "surname",
        LocalDate.of(2000,01,01),
        realNino));

    @Before
    public void setUp() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(FinancialStatusResource.class);
        rootLogger.setLevel(INFO);
        rootLogger.addAppender(mockAppender);
    }

    @Test
    public void shouldNeverLogSuppliedNino() {
        // given
        FinancialStatusRequest mockFinancialStatusRequest = mock(FinancialStatusRequest.class);
        Applicant applicant = new Applicant("forename", "surname", LocalDate.now(), realNino);
        List<Applicant> applicants = singletonList(applicant);
        when(mockFinancialStatusRequest.applicants()).thenReturn(applicants);

        when(mockNinoUtils.sanitise(realNino)).thenReturn(sanitisedNino);
        when(mockNinoUtils.redact(sanitisedNino)).thenReturn(redactedNino);

        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
        when(mockFinancialStatusRequest.applicationRaisedDate()).thenReturn(fiveDaysAgo);
        when(mockHelper.getIncomeRecords(any(), any(), any())).thenReturn(getIncomeRecords());
        when(mockHelper.calculateResponse(any(), any(), any())).thenReturn(getResponse());

        LogCapturer<FinancialStatusResource> logCapturer = LogCapturer.forClass(FinancialStatusResource.class);
        logCapturer.start();

        // when
        service.getFinancialStatus(mockFinancialStatusRequest);

        // then
        verify(mockNinoUtils, atLeastOnce()).redact(sanitisedNino);

        // verify log outputs never contain the `real` nino
        List<ILoggingEvent> allLogEvents = logCapturer.getAllEvents();
        for (final ILoggingEvent logEvent : allLogEvents) {
            final String logMessage = logEvent.getFormattedMessage();
            assertThat(logMessage).doesNotContain(realNino);
        }
    }

    private IncomeRecord getApplicantIncomeRecord() {
        Income income = new Income(BigDecimal.ONE, LocalDate.now(), 1, null, "E1");
        return new IncomeRecord(ImmutableList.of(income), new ArrayList<>(), new ArrayList<>(), null);
    }

    private Individual getApplicantIndividual() {
        return new Individual("applicant", "surname", "A");
    }

    private IncomeRecord getPartnerIncomeRecord() {
        Income income = new Income(BigDecimal.ONE, LocalDate.now(), 1, null, "E2");
        return new IncomeRecord(ImmutableList.of(income), new ArrayList<>(), new ArrayList<>(), null);
    }

    private Individual getPartnerIndividual() {
        return new Individual("partner", "surname", "B");
    }

    private LinkedHashMap<Individual, IncomeRecord> getIncomeRecords() {
        LinkedHashMap<Individual, IncomeRecord> incomeRecords = new LinkedHashMap<>();
        incomeRecords.put(getApplicantIndividual(), getApplicantIncomeRecord());
        incomeRecords.put(getPartnerIndividual(), getPartnerIncomeRecord());
        return incomeRecords;
    }

    private FinancialStatusCheckResponse getResponse() {

        List<CheckedIndividual> checkedIndividuals = new ArrayList<>();
        CheckedIndividual applicant = new CheckedIndividual("A", Collections.unmodifiableList(singletonList("E1")));
        checkedIndividuals.add(applicant);
        CategoryCheck categoryCheck = new CategoryCheck("B", "Test", false, LocalDate.now(), LocalDate.now(), IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD, BigDecimal.TEN, checkedIndividuals);
        List<CategoryCheck> categoryChecks = Collections.unmodifiableList(singletonList(categoryCheck));

        List<Individual> individuals = Collections.unmodifiableList(singletonList(getApplicantIndividual()));

        return new FinancialStatusCheckResponse(new ResponseStatus("100", "OK"), individuals, categoryChecks);
    }

    @Test
    public void shouldLogWhenRequestReceived() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(FinancialStatusResource.class);
        rootLogger.setLevel(INFO);
        rootLogger.addAppender(mockAppender);

        when(mockNinoUtils.sanitise(realNino)).thenReturn(sanitisedNino);
        when(mockNinoUtils.redact(sanitisedNino)).thenReturn(redactedNino);
        when(mockHelper.calculateResponse(any(), any(), any())).thenReturn(getResponse());

        service.getFinancialStatus(new FinancialStatusRequest(applicants, LocalDate.of(2019,01,01), 0 ));

        verifyLogMessage("Financial status check request received for RedactedNino - applicationRaisedDate = 2019-01-01, dependents = 0",
            INCOME_PROVING_SERVICE_REQUEST_RECEIVED);

    }

    @Test
    public void shouldLogWhenResponseReceived() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(FinancialStatusResource.class);
        rootLogger.setLevel(INFO);
        rootLogger.addAppender(mockAppender);

        when(mockNinoUtils.sanitise(realNino)).thenReturn(sanitisedNino);
        when(mockNinoUtils.redact(sanitisedNino)).thenReturn(redactedNino);
        when(mockHelper.calculateResponse(any(), any(), any())).thenReturn(getResponse());

        service.getFinancialStatus(new FinancialStatusRequest(applicants, LocalDate.of(2019,01,01), 0 ));

        verifyLogMessage("Financial status check passed for RedactedNino is: false", INCOME_PROVING_SERVICE_RESPONSE_SUCCESS);

    }

    private void verifyLogMessage(final String message, LogEvent event) {
        verify(mockAppender).doAppend(argThat(argument -> {
            LoggingEvent loggingEvent = (LoggingEvent) argument;
            return loggingEvent.getLevel().equals(INFO) &&
                loggingEvent.getFormattedMessage().equals(message) &&
                Arrays.asList(loggingEvent.getArgumentArray()).contains(new ObjectAppendingMarker("event_id", event));
        }));
    }
}

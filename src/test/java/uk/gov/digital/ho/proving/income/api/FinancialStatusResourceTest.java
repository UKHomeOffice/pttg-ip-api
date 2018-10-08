package uk.gov.digital.ho.proving.income.api;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.*;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;
import utils.LogCapturer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    public void shouldNeverLogSuppliedNino() {
        // given
        String realNino = "RealNino";
        String redactedNino = "RedactedNino";
        String sanitisedNino = "SanitisedNino";
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

}

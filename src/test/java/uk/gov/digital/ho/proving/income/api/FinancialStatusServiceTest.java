package uk.gov.digital.ho.proving.income.api;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.domain.hmrc.*;
import utils.LogCapturer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FinancialStatusServiceTest {
    private static final String ANY_EMPLOYER_PAYE_REF = "any employer PAYE ref";
    private static final LocalDate MIDDLE_OF_CURRENT_MONTH = LocalDate.now().withDayOfMonth(14);

    @Mock
    private HmrcClient mockHmrcClient;

    @Mock
    private AuditClient mockAuditClient;

    @Mock
    private NinoUtils mockNinoUtils;

    private FinancialStatusService service;

    private List<Employments> employments;
    private List<Employments> employmentsWithDuplicates;
    private List<AnnualSelfAssessmentTaxReturn> taxReturns;
    private List<Employments> multipleEmployments;
    private List<Income> incomeWithoutDuplicates;
    private List<Income> incomeWithDuplicates;

    @Before
    public void setup() {

        service = new FinancialStatusService(mockHmrcClient, mockAuditClient, mockNinoUtils);

        Income incomeA = incomeFromMonthsAgo(6);
        Income incomeB = incomeFromMonthsAgo(5);
        Income incomeC = incomeFromMonthsAgo(4);
        Income incomeD = incomeFromMonthsAgo(3);
        Income incomeE = incomeFromMonthsAgo(2);
        Income incomeF = incomeFromMonthsAgo(1);
        Income incomeG = incomeFromMonthsAgo(0);

        incomeWithoutDuplicates = new ArrayList<>();

        incomeWithoutDuplicates.add(incomeA);
        incomeWithoutDuplicates.add(incomeB);
        incomeWithoutDuplicates.add(incomeC);
        incomeWithoutDuplicates.add(incomeD);
        incomeWithoutDuplicates.add(incomeE);
        incomeWithoutDuplicates.add(incomeF);
        incomeWithoutDuplicates.add(incomeG);

        incomeWithDuplicates = new ArrayList<>();

        incomeWithDuplicates.add(incomeA);
        incomeWithDuplicates.add(incomeB);
        incomeWithDuplicates.add(incomeB);
        incomeWithDuplicates.add(incomeB);
        incomeWithDuplicates.add(incomeC);
        incomeWithDuplicates.add(incomeC);
        incomeWithDuplicates.add(incomeD);
        incomeWithDuplicates.add(incomeD);
        incomeWithDuplicates.add(incomeD);
        incomeWithDuplicates.add(incomeE);
        incomeWithDuplicates.add(incomeE);
        incomeWithDuplicates.add(incomeF);
        incomeWithDuplicates.add(incomeF);
        incomeWithDuplicates.add(incomeF);
        incomeWithDuplicates.add(incomeG);
        incomeWithDuplicates.add(incomeG);
        incomeWithDuplicates.add(incomeG);

        employments = new ArrayList<>();
        employments.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));

        taxReturns = new ArrayList<>();

        multipleEmployments = new ArrayList<>();
        multipleEmployments.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
        multipleEmployments.add(new Employments(new Employer("another employer", ANY_EMPLOYER_PAYE_REF)));


        employmentsWithDuplicates = new ArrayList<>();
        employmentsWithDuplicates.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
        employmentsWithDuplicates.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
    }

    private Income incomeFromMonthsAgo(int offset) {
        return new Income(new BigDecimal("1600"),
            MIDDLE_OF_CURRENT_MONTH.minusMonths(offset),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentEqualToCurrentDayOfMonth() {

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, taxReturns, employments, aIndividual());

        LocalDate applicationRaisedDate = this.MIDDLE_OF_CURRENT_MONTH;

        FinancialStatusCheckResponse response = service.monthlyCheck(applicationRaisedDate,
            0,
            applicationRaisedDate.minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentBeforeCurrentDayOfMonth() {

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, taxReturns, employments, aIndividual());

        LocalDate applicationRaisedDate = MIDDLE_OF_CURRENT_MONTH.plusDays(1);

        FinancialStatusCheckResponse response = service.monthlyCheck(applicationRaisedDate,
            0,
            applicationRaisedDate.minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentAfterCurrentDayOfMonth() {

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, taxReturns, employments, aIndividual());

        LocalDate applicationRaisedDate = MIDDLE_OF_CURRENT_MONTH.minusDays(1);

        FinancialStatusCheckResponse response = service.monthlyCheck(applicationRaisedDate,
            0,
            applicationRaisedDate.minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithDuplicates() {

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithDuplicates, taxReturns, employments, aIndividual());

        FinancialStatusCheckResponse response = service.monthlyCheck(LocalDate.now(),
            0,
            LocalDate.now().minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().isPassed()).isTrue();
    }

    @Test
    public void shouldFilterOutDuplicateEmployers() {

        List<Income> incomes = new ArrayList<>();

        IncomeRecord incomeRecord = new IncomeRecord(incomes, taxReturns, employmentsWithDuplicates, aIndividual());

        FinancialStatusCheckResponse response = service.monthlyCheck(LocalDate.now(),
            0,
            LocalDate.now().minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().getEmployers().size()).isEqualTo(1);
    }

    @Test
    public void duplicateEmployerFilterShouldAllowMultipleEmployersWithDifferentNames() {

        List<Income> incomes = new ArrayList<>();

        IncomeRecord incomeRecord = new IncomeRecord(incomes, taxReturns, multipleEmployments, aIndividual());

        FinancialStatusCheckResponse response = service.monthlyCheck(LocalDate.now(),
            0,
            LocalDate.now().minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().getEmployers().size()).isEqualTo(2);
    }

    @Test
    public void shouldNeverLogSuppliedNino() {
        // given
        String realNino = "RealNino";
        FinancialStatusRequest mockFinancialStatusRequest = mock(FinancialStatusRequest.class);
        Applicant applicant = new Applicant("forename", "surname", LocalDate.now(), realNino);
        List<Applicant> applicants = Arrays.asList(applicant);
        when(mockFinancialStatusRequest.getApplicants()).thenReturn(applicants);

        when(mockNinoUtils.redact(realNino)).thenReturn("RedactedNino");

        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
        when(mockFinancialStatusRequest.getApplicationRaisedDate()).thenReturn(fiveDaysAgo);
        when(mockHmrcClient.getIncomeRecord(any(), any(), any())).thenReturn(mock(IncomeRecord.class));

        LogCapturer<FinancialStatusService> logCapturer = LogCapturer.forClass(FinancialStatusService.class);
        logCapturer.start();

        // when
        service.getFinancialStatus(mockFinancialStatusRequest);

        // then
        verify(mockNinoUtils, atLeastOnce()).redact(realNino);

        // verify log outputs never contain the `real` nino
        List<ILoggingEvent> allLogEvents = logCapturer.getAllEvents();
        for (final ILoggingEvent logEvent : allLogEvents) {
            final String logMessage = logEvent.getFormattedMessage();
            assertThat(logMessage).doesNotContain(realNino);
        }
    }

    private Individual aIndividual() {
        return new Individual("Joe", "Bloggs", "NE121212C", LocalDate.now());
    }
}

package uk.gov.digital.ho.proving.income.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditService;
import uk.gov.digital.ho.proving.income.domain.hmrc.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FinancialStatusServiceTest {

    private static final String ANY_EMPLOYER_PAYE_REF = "any employer PAYE ref";

    @Mock IncomeRecordService mockIncomeRecordService;
    @Mock AuditService mockAuditService;

    @InjectMocks
    private FinancialStatusService service;

    private List<Employments> employments;
    private List<Employments> employmentsWithDuplicates;
    private List<Employments> multipleEmployments;
    private List<Income> incomeWithoutDuplicates;
    private List<Income> incomeWithDuplicates;

    private LocalDate middleOfCurrentMonth = LocalDate.now().withDayOfMonth(14);

    @Before
    public void setup() {

        Income incomeA = new Income(new BigDecimal("1600"),
            middleOfCurrentMonth.minusMonths(6),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);

        Income incomeB = new Income(new BigDecimal("1600"),
            middleOfCurrentMonth.minusMonths(5),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);

        Income incomeC = new Income(new BigDecimal("1600"),
            middleOfCurrentMonth.minusMonths(4),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);

        Income incomeD = new Income(new BigDecimal("1600"),
            middleOfCurrentMonth.minusMonths(3),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);

        Income incomeE = new Income(new BigDecimal("1600"),
            middleOfCurrentMonth.minusMonths(2),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);

        Income incomeF = new Income(new BigDecimal("1600"),
            middleOfCurrentMonth.minusMonths(1),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);

        Income incomeG = new Income(new BigDecimal("1600"),
            middleOfCurrentMonth.minusMonths(0),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);

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

        multipleEmployments = new ArrayList<>();
        multipleEmployments.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
        multipleEmployments.add(new Employments(new Employer("another employer", ANY_EMPLOYER_PAYE_REF)));


        employmentsWithDuplicates = new ArrayList<>();
        employmentsWithDuplicates.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
        employmentsWithDuplicates.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentEqualToCurrentDayOfMonth() {

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, employments, aIndividual());

        LocalDate applicationRaisedDate = this.middleOfCurrentMonth;

        FinancialStatusCheckResponse response = service.monthlyCheck(applicationRaisedDate,
            0,
            applicationRaisedDate.minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentBeforeCurrentDayOfMonth() {

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, employments, aIndividual());

        LocalDate applicationRaisedDate = middleOfCurrentMonth.plusDays(1);

        FinancialStatusCheckResponse response = service.monthlyCheck(applicationRaisedDate,
            0,
            applicationRaisedDate.minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentAfterCurrentDayOfMonth() {

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, employments, aIndividual());

        LocalDate applicationRaisedDate = middleOfCurrentMonth.minusDays(1);

        FinancialStatusCheckResponse response = service.monthlyCheck(applicationRaisedDate,
            0,
            applicationRaisedDate.minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithDuplicates() {

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithDuplicates, employments, aIndividual());

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

        IncomeRecord incomeRecord = new IncomeRecord(incomes, employmentsWithDuplicates, aIndividual());

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

        IncomeRecord incomeRecord = new IncomeRecord(incomes, multipleEmployments, aIndividual());

        FinancialStatusCheckResponse response = service.monthlyCheck(LocalDate.now(),
            0,
            LocalDate.now().minusMonths(6),
            incomeRecord,
            null);

        assertThat(response.getCategoryCheck().getEmployers().size()).isEqualTo(2);
    }

    private Individual aIndividual() {
        return new Individual("Joe", "Bloggs", "NE121212C", LocalDate.now());
    }

}

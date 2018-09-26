package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.hmrc.domain.AnnualSelfAssessmentTaxReturn;
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.amount;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.*;

public class CatANonSalariedIncomeValidatorTest {

    private static final LocalDate APPLICATION_RAISED_DATE = LocalDate.now();
    private static final LocalDate ANY_DOB = APPLICATION_RAISED_DATE.minusYears(20);
    private static final Applicant ANY_APPLICANT = new Applicant("any forename", "any surname", ANY_DOB, "any nino");
    private static final HmrcIndividual ANY_HMRC_INDIVIDUAL = new HmrcIndividual("any forename", "any surname", "any nino", ANY_DOB);
    private static final List<ApplicantIncome> ANY_APPLICANT_INCOME = singletonList(new ApplicantIncome(ANY_APPLICANT, new IncomeRecord(emptyList(), emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL)));

    private CatANonSalariedIncomeValidator validator = new CatANonSalariedIncomeValidator();

    @Test
    public void shouldReturnNotEnoughRecordsWhenNoIncomeRecords() {
        IncomeValidationResult result = validator.validate(new IncomeValidationRequest(ANY_APPLICANT_INCOME, APPLICATION_RAISED_DATE, 0));

        assertThat(result.status()).isEqualTo(NOT_ENOUGH_RECORDS);
    }

    @Test
    public void checkedIndividualShouldHaveSameNinoAsInRequest() {
        String givenNino = "a given nino";
        Applicant applicant = new Applicant("any forename", "any surname", ANY_DOB, givenNino);
        HmrcIndividual hmrcIndividual = new HmrcIndividual("any forename", "any surname", givenNino, ANY_DOB);

        List<ApplicantIncome> incomes = singletonList(new ApplicantIncome(applicant, new IncomeRecord(emptyList(), emptyList(), emptyList(), hmrcIndividual)));

        IncomeValidationResult result = validator.validate(new IncomeValidationRequest(incomes, APPLICATION_RAISED_DATE, 0));
        assertThat(result.individuals()).hasSize(1);
        assertThat(result.individuals().get(0).nino()).isEqualTo(givenNino);
    }

    @Test
    public void resultShouldBeCategoryA() {
        IncomeValidationResult result = validator.validate(new IncomeValidationRequest(ANY_APPLICANT_INCOME, APPLICATION_RAISED_DATE, 0));

        assertThat(result.category()).isEqualTo("A");
    }

    @Test
    public void calculationTypeShouldBeCategoryANonSalaried() {
        String expectedCalculationType = "Category A non salaried";

        IncomeValidationResult result = validator.validate(new IncomeValidationRequest(ANY_APPLICANT_INCOME, APPLICATION_RAISED_DATE, 0));

        assertThat(result.calculationType()).isEqualTo(expectedCalculationType);
    }

    @Test
    public void assessmentStartDateShouldBe6MonthsBeforeApplicationDate() {
        LocalDate applicationDate = LocalDate.of(2018, Month.AUGUST, 23);
        LocalDate expectedAssessmentStartDate = LocalDate.of(2018, Month.FEBRUARY, 23);

        IncomeValidationResult result = validator.validate(new IncomeValidationRequest(ANY_APPLICANT_INCOME, applicationDate, 0));

        assertThat(result.assessmentStartDate()).isEqualTo(expectedAssessmentStartDate);
    }

    @Test
    public void annualThresholdForDependantsShouldBeCorrect() {
        Map<Integer, Integer> dependantsAndExpectedThreshold = new HashMap<>();
        dependantsAndExpectedThreshold.put(0, 18_600);
        dependantsAndExpectedThreshold.put(1, 22_400);
        dependantsAndExpectedThreshold.put(2, 24_800);
        dependantsAndExpectedThreshold.put(3, 27_200);
        dependantsAndExpectedThreshold.put(4, 29_600);
        dependantsAndExpectedThreshold.put(5, 32_000);

        for (int dependants = 0; dependants <= 5; dependants++) {
            IncomeValidationResult result = validator.validate(new IncomeValidationRequest(ANY_APPLICANT_INCOME, APPLICATION_RAISED_DATE, dependants));

            Integer expectedThreshold = dependantsAndExpectedThreshold.get(dependants);
            assertThat(result.threshold()).isEqualTo(BigDecimal.valueOf(expectedThreshold));
        }
    }

    @Test
    public void shouldPassWhenOverThresholdSingleMonth() {
        List<Income> incomes = singletonList(new Income(BigDecimal.valueOf(18_600 / 2), APPLICATION_RAISED_DATE.minusDays(1), null, null, "any employer ref"));

        Applicant applicant = ANY_APPLICANT;
        IncomeRecord incomeRecord = new IncomeRecord(incomes, emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(applicant, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), CATA_NON_SALARIED_PASSED);
    }

    @Test
    public void shouldFailWhenBelowThresholdSingleMonth() {
        List<Income> incomes = singletonList(new Income(BigDecimal.valueOf(18_600 / 2).subtract(BigDecimal.ONE), APPLICATION_RAISED_DATE.minusDays(1), null, null, "any employer ref"));

        Applicant applicant = ANY_APPLICANT;
        IncomeRecord incomeRecord = new IncomeRecord(incomes, emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(applicant, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), CATA_NON_SALARIED_BELOW_THRESHOLD);
    }

    @Test
    public void shouldFailWhenOnlyIncomeNotPaye() {
        Applicant applicant = ANY_APPLICANT;
        List<AnnualSelfAssessmentTaxReturn> selfAssessmentIncome = singletonList(new AnnualSelfAssessmentTaxReturn(String.valueOf(APPLICATION_RAISED_DATE.getYear()), BigDecimal.valueOf(33_000)));

        IncomeRecord incomeRecord = new IncomeRecord(emptyList(), selfAssessmentIncome, emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(applicant, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), NOT_ENOUGH_RECORDS);
    }

    @Test
    public void shouldPassWhenOverThreshold2MonthsSummed() {
        List<Income> incomes = Arrays.asList(
            new Income(BigDecimal.valueOf(18_600 / 4), APPLICATION_RAISED_DATE.minusDays(1), null, null, "any employer ref"),
            new Income(BigDecimal.valueOf(18_600 / 4), APPLICATION_RAISED_DATE.minusMonths(5), null, null, "any employer ref")
        );

        IncomeRecord incomeRecord = new IncomeRecord(incomes, emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(ANY_APPLICANT, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), CATA_NON_SALARIED_PASSED);
    }

    @Test
    public void shouldFailWhenOverThresholdMonthOutOfRange() {
        List<Income> incomes = singletonList(new Income(BigDecimal.valueOf(18_600 / 2), APPLICATION_RAISED_DATE.minusMonths(7), null, null, "any employer ref"));

        IncomeRecord incomeRecord = new IncomeRecord(incomes, emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(ANY_APPLICANT, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), NOT_ENOUGH_RECORDS);
    }

    @Test
    public void shouldFailWhenOverThreshold2MonthsSummed1MonthOutOfRange() {
        List<Income> incomes = Arrays.asList(
            new Income(BigDecimal.valueOf(18_600 / 4), APPLICATION_RAISED_DATE.minusDays(1), null, null, "any employer ref"),
            new Income(BigDecimal.valueOf(18_600 / 4), APPLICATION_RAISED_DATE.minusMonths(7), null, null, "any employer ref")
        );

        IncomeRecord incomeRecord = new IncomeRecord(incomes, emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(ANY_APPLICANT, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), CATA_NON_SALARIED_BELOW_THRESHOLD);
    }

    @Test
    public void shouldFailWhenPaymentAfterRaisedDate() {
        List<Income> incomes = singletonList(new Income(BigDecimal.valueOf(18_600 / 2), APPLICATION_RAISED_DATE.plusDays(1), null, null, "any employer ref"));

        IncomeRecord incomeRecord = new IncomeRecord(incomes, emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(ANY_APPLICANT, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), NOT_ENOUGH_RECORDS);
    }

    @Test
    public void shouldPassWhenOverThresholdVariableAmounts() {
        List<Income> incomes = Arrays.asList(
            new Income(amount("18599.99"), APPLICATION_RAISED_DATE.minusDays(1), null, null, "any employer ref"),
            new Income(amount("0.01"), APPLICATION_RAISED_DATE.minusDays(2), null, null, "any employer ref")
        );

        IncomeRecord incomeRecord = new IncomeRecord(incomes, emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(ANY_APPLICANT, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), CATA_NON_SALARIED_PASSED);

    }

    @Test
    public void shouldFilterOutDuplicateIncomeEntries() {
        Income income = new Income(BigDecimal.valueOf(18_600 / 4), APPLICATION_RAISED_DATE.minusDays(1), null, null, "any employer ref");
        List<Income> incomes = Arrays.asList(income, income);

        IncomeRecord incomeRecord = new IncomeRecord(incomes, emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);
        List<ApplicantIncome> applicantIncomes = singletonList(new ApplicantIncome(ANY_APPLICANT, incomeRecord));

        assertExpectedResult(new IncomeValidationRequest(applicantIncomes, APPLICATION_RAISED_DATE, 0), CATA_NON_SALARIED_BELOW_THRESHOLD);
    }
    // TODO OJR 2018/09/26 accept only one employer, Partner Only, Combined

    private void assertExpectedResult(IncomeValidationRequest request, IncomeValidationStatus expectedStatus) {
        IncomeValidationResult result = validator.validate(request);
        assertThat(result.status()).isEqualTo(expectedStatus);
    }
}

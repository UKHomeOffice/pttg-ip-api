package uk.gov.digital.ho.proving.income.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.*;

@RunWith(MockitoJUnitRunner.class)
public class CatAMonthlyIncomeValidatorTest {

    @Mock
    private IncomeThresholdCalculator incomeThresholdCalculator;
    @InjectMocks
    private CatASalariedMonthlyIncomeValidator validator;

    @Before
    public void setUp() {
        when(incomeThresholdCalculator.monthlyThreshold(0)).thenReturn(BigDecimal.valueOf(18600).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
    }

    @Test
    public void thatNonContiguousPaymentsInsufficientMonthsFails() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientQuantity(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void thatMultipleNonContiguousMonthlyPaymentsFails() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonth(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void thatMultipleMonthlyContiguousPaymentInsufficientMonthsFails() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = contiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientRangeOfMonths(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void thatSufficientContiguousMonthlyPaymentsPasses() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = contiguousMonthlyPayments(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

    @Test
    public void thatValidCategoryIndividualAccepted() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getConsecutiveIncomes2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

    @Test
    public void thatCalculationTypeIsOfRequiredFormatForStepAssertor() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getConsecutiveIncomes2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.calculationType()).startsWith("Category ");
    }

    @Test
    public void thatInvalidCategoryAIndividualRejectedNonConsecutive() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getNoneConsecutiveIncomes2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void thatInvalidCategoryAIndividualRejectedNotEnoughRecords() {
        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getNotEnoughConsecutiveIncomes2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void thatInvalidCategoryAIndividualRejectedMultipleEmployers() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getConsecutiveIncomesButDifferentEmployers2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MULTIPLE_EMPLOYERS);
        assertThat(categoryAIndividual.individuals().get(0).employers()).contains(BURGER_KING);
        assertThat(categoryAIndividual.individuals().get(0).employers()).contains(PIZZA_HUT);
    }

    @Test
    public void thatInvalidCateogryAIndividualRejectedInsufficientEarnings() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getConsecutiveIncomesButLowAmounts2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_VALUE_BELOW_THRESHOLD);
    }

    @Test
    public void thatValidCategoryAIndividualAcceptedDifferentPayday() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getConsecutiveIncomesWithDifferentMonthlyPayDay2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate.withDayOfMonth(16), 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

    @Test
    public void thatValidCategoryAIndividualAcceptedThresholdBoundary() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getConsecutiveIncomesWithExactlyTheAmount2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate.withDayOfMonth(17), 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

    @Test
    public void shouldPassWhenMultiplePaymentsSameMonth() {
        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = twoPaymentsSameMonth(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

    @Test
    public void shouldExcludeExactDuplicatePayments() {
        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = incomeWithDuplicateMonthlyPayments(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.MONTHLY_VALUE_BELOW_THRESHOLD);
    }

}

package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.TestData.*;

public class CatAMonthlyIncomeValidatorTest {

    private int days = 182;
    private CatASalariedMonthlyIncomeValidator validator = new CatASalariedMonthlyIncomeValidator();

    @Test
    public void thatNonContiguousPaymentsInsufficientMonthsFails() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientQuantity(raisedDate);
        LocalDate pastDate = raisedDate.minusMonths(6);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void thatMultipleNonContiguousMonthlyPaymentsFails() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonth(raisedDate);
        LocalDate pastDate = raisedDate.minusMonths(6);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void thatMultipleMonthlyContiguousPaymentInsufficientMonthsFails() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = contiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientRangeOfMonths(raisedDate);
        LocalDate pastDate = raisedDate.minusMonths(6);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void thatMultiplePaymentsInEarliestMonthFails() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = contiguousMonthlyPaymentsWithMultiplePaymentsInEarliestMonth(raisedDate);
        LocalDate pastDate = raisedDate.minusMonths(6);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void thatMultiplePaymentInMiddleMonthFails() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = contiguousMonthlyPaymentsWithMultiplePaymentsInMiddleMonth(raisedDate);
        LocalDate pastDate = raisedDate.minusMonths(6);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void thatSufficientContiguousMonthlyPaymentsPasses() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = contiguousMonthlyPayments(raisedDate);
        LocalDate pastDate = raisedDate.minusMonths(6);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

    @Test
    public void thatValidCategoryIndividualAccepted() {

        List<ApplicantIncome> incomes = getConsecutiveIncomes2();
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

    @Test
    public void thatInvalidCategoryAIndividualRejectedNonConsecutive() {

        List<ApplicantIncome> incomes = getNoneConsecutiveIncomes2();
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void thatInvalidCategoryAIndividualRejectedNotEnoughRecords() {

        List<ApplicantIncome> incomes = getNotEnoughConsecutiveIncomes2();
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void thatInvalidCategoryAIndividualRejectedMultipleEmployers() {

        List<ApplicantIncome> incomes = getConsecutiveIncomesButDifferentEmployers2();
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MULTIPLE_EMPLOYERS);
        assertThat(categoryAIndividual.individuals().get(0).employers()).contains(BURGER_KING);
        assertThat(categoryAIndividual.individuals().get(0).employers()).contains(PIZZA_HUT);
    }

    @Test
    public void thatInvalidCateogryAIndividualRejectedInsufficientEarnings() {

        List<ApplicantIncome> incomes = getConsecutiveIncomesButLowAmounts2();
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_VALUE_BELOW_THRESHOLD);
    }

    @Test
    public void thatValidCategoryAIndividualAcceptedDifferentPayday() {

        List<ApplicantIncome> incomes = getConsecutiveIncomesWithDifferentMonthlyPayDay2();
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

    @Test
    public void thatValidCategoryAIndividualAcceptedThresholdBoundary() {

        List<ApplicantIncome> incomes = getConsecutiveIncomesWithExactlyTheAmount2();
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, pastDate, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED);
    }

}

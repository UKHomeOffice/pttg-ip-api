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
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.*;

public class CatAWeeklyIncomeValidatorTest {

    private CatASalariedWeeklyIncomeValidator validator = new CatASalariedWeeklyIncomeValidator();

    @Test
    public void validCategoryAIndividualAccepted() {

        List<ApplicantIncome> incomes = getIncomesAboveThreshold2();
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_SALARIED_PASSED);
    }

    @Test
    public void thatCalculationTypeIsOfRequiredFormatForStepAssertor() {

        List<ApplicantIncome> incomes = getIncomesAboveThreshold2();
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.calculationType()).startsWith("Category ");
    }

    @Test
    public void thatExactly26WeeksAccepted() {

        List<ApplicantIncome> incomes = getIncomesExactly26AboveThreshold2();
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_SALARIED_PASSED);
    }

    @Test
    public void thatExactly26WeeksRejectedIfRaisedBeforeLastPayday() {

        List<ApplicantIncome> incomes = getIncomesExactly26AboveThreshold2();
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 10);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void thatInsufficientWeeklyDataFails() {

        List<ApplicantIncome> incomes = getIncomesNotEnoughWeeks2();
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void thatSomeWeeksBelowThresholdFails() {

        List<ApplicantIncome> incomes = getIncomesSomeBelowThreshold2();
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_VALUE_BELOW_THRESHOLD);
    }

    @Test
    public void shouldPassIfMultiplePaymentsSameWeekWhichWhenCombinedAreOverThreshold() {
        List<ApplicantIncome> incomes = getIncomesAboveThresholdMultiplePaymentsOneWeek();

        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_SALARIED_PASSED);
    }

    @Test
    public void shouldPassIfMultiplePaymentsSameDayWhichWhenCombinedAreOverThreshold() {
        List<ApplicantIncome> incomes = getIncomesAboveThresholdMultiplePaymentsSameDay();

        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_SALARIED_PASSED);
    }

    @Test
    public void shouldFailIfMultiplePaymentsSameWeekWhichWhenCombinedAreStillBelowThreshold() {
        List<ApplicantIncome> incomes = getIncomesMultiplePaymentsSameWeekBelowThreshold();

        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_VALUE_BELOW_THRESHOLD);
    }

    @Test
    public void shouldFailIfMultiplePaymentsSameWeekDifferentEmployersEvenOverThreshold() {
        List<ApplicantIncome> incomes = getIncomesMultiplePaymentsSameWeekDifferentEmployers();

        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MULTIPLE_EMPLOYERS);
    }

    @Test
    public void shouldExcludeExactDuplicatePayments() {
        List<ApplicantIncome> incomes = incomeWithDuplicateWeeklyPayments();
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_VALUE_BELOW_THRESHOLD);
    }
}

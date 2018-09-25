package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.*;

public class CatAWeeklyIncomeValidatorTest {

    private CatASalariedWeeklyIncomeValidator validator = new CatASalariedWeeklyIncomeValidator();

    @Test
    public void validCategoryAIndividualAccepted() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesAboveThreshold2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_SALARIED_PASSED);
    }

    @Test
    public void thatCalculationTypeIsOfRequiredFormatForStepAssertor() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesAboveThreshold2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.calculationType()).startsWith("Category ");
    }

    @Test
    public void thatExactly26WeeksAccepted() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesExactly26AboveThreshold2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_SALARIED_PASSED);
    }

    @Test
    public void thatExactly26WeeksRejectedIfRaisedBeforeLastPayday() {

        LocalDate raisedDate = LocalDate.now().minusDays(6);
        List<ApplicantIncome> incomes = getIncomesExactly26AboveThreshold2(LocalDate.now());

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void thatInsufficientWeeklyDataFails() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesNotEnoughWeeks2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void thatSomeWeeksBelowThresholdFails() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesSomeBelowThreshold2(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_VALUE_BELOW_THRESHOLD);
    }

    @Test
    public void shouldPassIfMultiplePaymentsSameWeekWhichWhenCombinedAreOverThreshold() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesAboveThresholdMultiplePaymentsOneWeek(raisedDate);


        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_SALARIED_PASSED);
    }

    @Test
    public void shouldPassIfMultiplePaymentsSameDayWhichWhenCombinedAreOverThreshold() {
        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesAboveThresholdMultiplePaymentsSameDay(raisedDate);


        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_SALARIED_PASSED);
    }

    @Test
    public void shouldFailIfMultiplePaymentsSameWeekWhichWhenCombinedAreStillBelowThreshold() {
        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesMultiplePaymentsSameWeekBelowThreshold(raisedDate);


        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_VALUE_BELOW_THRESHOLD);
    }

    @Test
    public void shouldFailIfMultiplePaymentsSameWeekDifferentEmployersEvenOverThreshold() {
        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesMultiplePaymentsSameWeekDifferentEmployers(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.MULTIPLE_EMPLOYERS);
    }

    @Test
    public void shouldExcludeExactDuplicatePayments() {
        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = incomeWithDuplicateWeeklyPayments(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.WEEKLY_VALUE_BELOW_THRESHOLD);
    }
}

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


public class CatAUnsupportedIncomeValidatorTest {

    private CatAUnsupportedIncomeValidator validator = new CatAUnsupportedIncomeValidator();

    @Test
    public void thatChangedFrequencyHasCorrectStatus() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = changedFrequencyPayments(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.PAY_FREQUENCY_CHANGE);

    }

    @Test
    public void thatUnsupportedFrequencyHasCorrectStatus() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = fortnightlyPayment(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.status()).isEqualTo(IncomeValidationStatus.UNKNOWN_PAY_FREQUENCY);

    }

    @Test
    public void thatCalculationTypeIsOfRequiredFormatForStepAssertor() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = changedFrequencyPayments(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult categoryAIndividual = validator.validate(request);

        assertThat(categoryAIndividual.calculationType()).startsWith("Category ");

    }

}

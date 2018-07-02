package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.getDate;
import static uk.gov.digital.ho.proving.income.validator.CatBNonSalariedTestData.*;

public class CatBNonSalariedIncomeValidatorTest {

    private CatBNonSalariedIncomeValidator validator = new CatBNonSalariedIncomeValidator();

    @Test
    public void thatResultDetailsAreReturned() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentAboveNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.calculationType()).isEqualTo(CatBNonSalariedIncomeValidator.CALCULATION_TYPE)
            .withFailMessage("The correct calculation should be returned");
        assertThat(result.assessmentStartDate()).isEqualTo(raisedDate.minusYears(1))
            .withFailMessage("The assessment start date should be 1 year before the raised date");
        assertThat(result.threshold()).isEqualTo(new SalariedThresholdCalculator(0).yearlyThreshold())
            .withFailMessage("The yearly threshold should be returned");
    }

    @Test
    public void thatSingleApplicantDetailsAreReturned() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentAboveNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.individuals().size()).isEqualTo(1)
            .withFailMessage("A single checked individual should be returned");
        CheckedIndividual individual = result.individuals().get(0);
        assertThat(individual.nino()).isEqualTo(CatBNonSalariedTestData.NINO)
            .withFailMessage("The checked individual should have the correct nino");
        assertThat(individual.employers().size()).isEqualTo(1)
            .withFailMessage("The checked individual should have a single employer");
        assertThat(individual.employers().get(0)).isEqualTo(CatBNonSalariedTestData.PIZZA_HUT)
            .withFailMessage("The checked individual should have the correct employer");
    }

    @Test
    public void thatSingleApplicantSingleMonthAboveThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentAboveNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantSingleMonthBelowThresholdFails() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentBelowNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatSingleApplicantSingleMonthEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantMultipleMonthsBelowThresholdFails() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleMonthlyPaymentsBelowNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatSingleApplicantMultipleMonthsEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleMonthlyPaymentsEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantSingleDependantEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentEqualsSingleDependantThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 1);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantSingleDependantBelowThresholdFails() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentBelowSingleDependantThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 1);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatSingleApplicantThreeDependantsEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentEqualsThreeDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 3);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantThreeDependantsBelowThresholdFails() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentBelowThreeDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 3);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatSingleApplicantMultiplePaymentsSameMonthEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleMonthlyPaymentSameMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatTwoApplicantsDetailsAreReturned() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantSingleMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.individuals().size()).isEqualTo(2)
            .withFailMessage("Two checked individuals should be returned");

        CheckedIndividual applicant = result.individuals().get(0);
        assertThat(applicant.nino()).isEqualTo(CatBNonSalariedTestData.NINO)
            .withFailMessage("The checked individual should have the correct nino");
        assertThat(applicant.employers().size()).isEqualTo(1)
            .withFailMessage("The checked individual should have a single employer");
        assertThat(applicant.employers().get(0)).isEqualTo(CatBNonSalariedTestData.PIZZA_HUT)
            .withFailMessage("The checked individual should have the correct employer");

        CheckedIndividual partner = result.individuals().get(1);
        assertThat(partner.nino()).isEqualTo(CatBNonSalariedTestData.NINO_PARTNER)
            .withFailMessage("The checked individual should have the correct nino");
        assertThat(partner.employers().size()).isEqualTo(1)
            .withFailMessage("The checked individual should have a single employer");
        assertThat(partner.employers().get(0)).isEqualTo(CatBNonSalariedTestData.BURGER_KING)
            .withFailMessage("The checked individual should have the correct employer");
    }

    @Test
    public void thatTwoApplicantsCombinedEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantBothSingleMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatTwoApplicantsCombinedBelowThresholdFails() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantBothSingleMonthBelowNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatTwoApplicantsMultipleMonthsCombinedEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantMultipleMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatTwoApplicantsOnlyApplicantEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantsOnlyApplicantEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
        assertThat(result.individuals().size()).isEqualTo(1)
            .withFailMessage("There should be only one applicant returned");
        assertThat(result.individuals().get(0).nino()).isEqualTo(CatBNonSalariedTestData.NINO)
            .withFailMessage("The individual returned should be the first applicant");
    }

    @Test
    public void thatTwoApplicantsOnlyPartnerEqualsThresholdPasses() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantsOnlyPartnerEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
        assertThat(result.individuals().size()).isEqualTo(1)
            .withFailMessage("There should be only one applicant returned");
        assertThat(result.individuals().get(0).nino()).isEqualTo(CatBNonSalariedTestData.NINO_PARTNER)
            .withFailMessage("The individual returned should be the partner");
    }

    @Test
    public void thatCalculationTypeIsOfRequiredFormatForStepAssertor() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentAboveNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.calculationType()).startsWith("Category ");
    }


}

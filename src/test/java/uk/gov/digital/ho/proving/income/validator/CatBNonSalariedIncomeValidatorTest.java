package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.getDate;
import static uk.gov.digital.ho.proving.income.validator.CatBNonSalariedTestData.*;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.EMPLOYMENT_CHECK_FAILED;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.EMPLOYMENT_CHECK_PASSED;

@RunWith(MockitoJUnitRunner.class)
public class CatBNonSalariedIncomeValidatorTest {

    @Mock
    private EmploymentCheckIncomeValidator employmentCheckIncomeValidator;

    @InjectMocks
    private CatBNonSalariedIncomeValidator validator;

    public void employmentCheckPasses() {
        IncomeValidationResult incomeValidationResult = mock(IncomeValidationResult.class);
        when(incomeValidationResult.status()).thenReturn(EMPLOYMENT_CHECK_PASSED);

        when(employmentCheckIncomeValidator.validate(any())).thenReturn(incomeValidationResult);
    }

    public void employmentCheckFails() {
        IncomeValidationResult incomeValidationResult = mock(IncomeValidationResult.class);
        when(incomeValidationResult.status()).thenReturn(EMPLOYMENT_CHECK_FAILED);

        when(employmentCheckIncomeValidator.validate(any())).thenReturn(incomeValidationResult);
    }

    @Test
    public void thatResultDetailsAreReturned() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentAboveNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.calculationType()).isEqualTo("Category B non salaried")
            .withFailMessage("The correct calculation should be returned");
        assertThat(result.assessmentStartDate()).isEqualTo(raisedDate.minusYears(1))
            .withFailMessage("The assessment start date should be 1 year before the raised date");
        assertThat(result.threshold()).isEqualTo(new IncomeThresholdCalculator(0).yearlyThreshold())
            .withFailMessage("The yearly threshold should be returned");
    }

    @Test
    public void thatSingleApplicantDetailsAreReturned() {
        employmentCheckPasses();

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
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentAboveNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantSingleMonthBelowThresholdFails() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentBelowNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatSingleApplicantSingleMonthEqualsThresholdPasses() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantMultipleMonthsBelowThresholdFails() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleMonthlyPaymentsBelowNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatSingleApplicantMultipleMonthsEqualsThresholdPasses() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleMonthlyPaymentsEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantSingleDependantEqualsThresholdPasses() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentEqualsSingleDependantThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 1);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantSingleDependantBelowThresholdFails() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentBelowSingleDependantThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 1);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatSingleApplicantThreeDependantsEqualsThresholdPasses() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentEqualsThreeDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 3);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatSingleApplicantThreeDependantsBelowThresholdFails() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentBelowThreeDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 3);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatSingleApplicantMultiplePaymentsSameMonthEqualsThresholdPasses() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleMonthlyPaymentSameMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatTwoApplicantsDetailsAreReturned() {
        employmentCheckPasses();

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
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantBothSingleMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatTwoApplicantsCombinedBelowThresholdFails() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantBothSingleMonthBelowNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD)
            .withFailMessage("The income validation should be below the threshold");
    }

    @Test
    public void thatTwoApplicantsMultipleMonthsCombinedEqualsThresholdPasses() {
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantMultipleMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.status()).isEqualTo(IncomeValidationStatus.CATB_NON_SALARIED_PASSED)
            .withFailMessage("The income validation should have passed");
    }

    @Test
    public void thatTwoApplicantsOnlyApplicantEqualsThresholdPasses() {
        employmentCheckPasses();

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
        employmentCheckPasses();

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
        employmentCheckPasses();

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentAboveNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        IncomeValidationResult result = validator.validate(request);

        assertThat(result.calculationType()).startsWith("Category ");
    }

    @Test
    public void shouldReturnFailedEmploymentCheckWhenEmploymentCheckFails() {
        // given
        employmentCheckFails();

        LocalDate raisedDate = LocalDate.now();
        IncomeValidationRequest request = new IncomeValidationRequest(Collections.emptyList(), raisedDate, 0);

        // when
        IncomeValidationResult validate = validator.validate(request);

        // then
        assertThat(validate.status()).isEqualTo(EMPLOYMENT_CHECK_FAILED);
    }
}

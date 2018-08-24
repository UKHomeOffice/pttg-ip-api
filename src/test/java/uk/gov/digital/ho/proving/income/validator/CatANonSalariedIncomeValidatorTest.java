package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static uk.gov.digital.ho.proving.income.validator.CatANonSalariedTestData.*;


public class CatANonSalariedIncomeValidatorTest {

    private final IncomeValidator validator = new CatANonSalariedIncomeValidator();
    private LocalDate applicationRaisedDate = LocalDate.of(2018, 8, 23);

    @Test
    public void sufficientNonSalariedIncomeOverSixMonthsPasses() {
        List<ApplicantIncome> applicantIncomes = sixMonthsOfSufficientNonSalariedIncome(applicationRaisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.CATA_NON_SALARIED_PASSED);
    }

    @Test
    public void insufficientNonSalariedIncomeOverSixMonthsFails() {
        List<ApplicantIncome> applicantIncomes = sixMonthsOfInsufficientNonSalariedIncome(applicationRaisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.CATA_NON_SALARIED_BELOW_THRESHOLD);
    }

    @Test
    public void sufficientNonSalariedIncomeOverWithMonthGapsPasses() {
        List<ApplicantIncome> applicantIncomes = sixMonthsOfSufficientNonSalariedIncomeWithGaps(applicationRaisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.CATA_NON_SALARIED_PASSED);
    }

    @Test
    public void sufficientNonSalariedIncomeOnArdPasses() {
        List<ApplicantIncome> applicantIncomes = sufficientNonSalariedSingleIncome(applicationRaisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.CATA_NON_SALARIED_PASSED);
    }

    @Test
    public void sufficientNonSalariedIncome183DaysBeforeArdPasses() {
        List<ApplicantIncome> applicantIncomes = sufficientNonSalariedSingleIncome(applicationRaisedDate.minusDays(183));
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.CATA_NON_SALARIED_PASSED);
    }

    @Test
    public void sufficientNonSalariedIncome184DaysBeforeArdFails() {
        List<ApplicantIncome> applicantIncomes = sufficientNonSalariedSingleIncome(applicationRaisedDate.minusDays(184));
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.CATA_NON_SALARIED_BELOW_THRESHOLD);
    }

    @Test
    public void sufficientNonSalariedIncomeOnlyWith2EmployersFails() {
        List<ApplicantIncome> applicantIncomes = sufficientNonSalariedButOnlyFor2Employers(applicationRaisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.MULTIPLE_EMPLOYERS);
    }

    @Test
    public void sufficientNonSalariedIncomeFor1OutOf2EmployersPasses() {
        List<ApplicantIncome> applicantIncomes = sufficientNonSalariedFor1Of2Employers(applicationRaisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.CATA_NON_SALARIED_PASSED);
    }

    @Test
    public void sufficientNonSalariedIncomeWithPartnerPasses() {
        List<ApplicantIncome> applicantIncomes = sufficientNonSalariedWithPartner(applicationRaisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, 0);

        IncomeValidationResult validationResult = validator.validate(request);

        assertThat(validationResult.status()).isEqualTo(IncomeValidationStatus.CATA_NON_SALARIED_PASSED);
    }

    @Test
    public void insufficientNonSalariedIncomeEvenWithPartnerFails() {
        fail("Not yet implemented");
    }

    @Test
    public void sufficientSalariedWithPartnerFor1Of2EmployersEachPasses() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor1DependantPasses() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor1DependantFails() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor2DependantPasses() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor2DependantFails() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor3DependantPasses() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor3DependantFails() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor4DependantPasses() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor4DependantFails() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor5DependantPasses() {
        fail("Not yet implemented");
    }

    @Test
    public void insufficientNonSalariedIncomeFor5DependantFails() {
        fail("Not yet implemented");
    }
}

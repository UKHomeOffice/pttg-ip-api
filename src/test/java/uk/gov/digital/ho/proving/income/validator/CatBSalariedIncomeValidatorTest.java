package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
import static uk.gov.digital.ho.proving.income.validator.CatBSalariedTestData.*;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.*;

@RunWith(MockitoJUnitRunner.class)
public class CatBSalariedIncomeValidatorTest {

    @Mock private EmploymentCheckIncomeValidator employmentCheckIncomeValidator;

    @InjectMocks private CatBSalariedIncomeValidator catBSalariedIncomeValidator;

    private final LocalDate applicationDate = LocalDate.of(2018, Month.AUGUST, 24);

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
    public void checkPassesIf12MonthsOverThresholdApplicantOnly() {
        employmentCheckPasses();
        assertStatus(twelveMonthsOverThreshold(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkPassesMixedFrequencyButOverThreshold() {
        employmentCheckPasses();
        assertStatus(mixedFrequencyButOverThreshold(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkPassesUnsortedIncomeData() {

    }

    @Test
    public void checkPassesIf12MonthsOverThresholdMultipleEmployersSolo() {
        employmentCheckPasses();
        assertStatus(overThresholdMultipleEmployers(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkFailsIfMonthMissingButStillEnoughPayments() {
        employmentCheckPasses();
        assertStatus(monthMissingButEnoughPayments(applicationDate), NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void checkFailsIfMonthMissingTooFewPayments() {
        employmentCheckPasses();
        assertStatus(monthMissingTooFewPayments(applicationDate), IncomeValidationStatus.NOT_ENOUGH_RECORDS);
    }

    @Test
    public void checkFailsIfMonthBelowThreshold() {
        employmentCheckPasses();
        assertStatus(monthBelowThreshold(applicationDate), CATB_SALARIED_BELOW_THRESHOLD);
    }

    @Test
    public void checkPassesIf12MonthsOverThresholdForApplicantJointApplication() {
        employmentCheckPasses();
        assertStatus(twelveMonthsOverThresholdJointApplication(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkFailsIfMonthMissingBothApplicantsJointApplication() {
        employmentCheckPasses();
        assertStatus(jointApplicationMonthMissingBothApplicants(applicationDate), NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void checkFailsIfMonthUnderThresholdForBothApplicantsInJoint() {
        employmentCheckPasses();
        assertStatus(jointApplicationMonthUnderThreshold(applicationDate), CATB_SALARIED_BELOW_THRESHOLD);
    }

    @Test
    public void checkPassesFor1DependantIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 1;

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor1DependantIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 1;

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor1DependantIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 1;

        assertStatus(twelveMonthsOverThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor1DependantIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 1;

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor2DependantsIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 2;

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor2DependantsIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 2;

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor2DependantsIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 2;

        assertStatus(twelveMonthsOverThresholdApplicantOnlyInJoint(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor2DependantsIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 2;

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor3DependantsIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 3;

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor3DependantsIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 3;

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor3DependantsIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 3;

        assertStatus(twelveMonthsOverThresholdApplicantOnlyInJoint(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor3DependantsIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 3;

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor4DependantsIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 4;

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor4DependantsIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 4;

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor4DependantsIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 4;

        assertStatus(twelveMonthsOverThresholdApplicantOnlyInJoint(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor4DependantsIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 4;

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }


    @Test
    public void checkPassesFor5DependantsIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 5;

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor5DependantsIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 5;

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor5DependantsIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 5;

        assertStatus(twelveMonthsOverThresholdApplicantOnlyInJoint(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor5DependantsIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 5;

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void catBSalariedCheckFailsIfEmploymentCheckFails() {
        employmentCheckFails();

        assertStatus(Collections.emptyList(), EMPLOYMENT_CHECK_FAILED);
    }

    private void assertStatus(List<ApplicantIncome> applicantIncomes, IncomeValidationStatus status) {
        assertStatus(applicantIncomes, status, 0);
    }

    private void assertStatus(List<ApplicantIncome> applicantIncomes, IncomeValidationStatus status, int dependants) {
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationDate, dependants);

        IncomeValidationResult result = catBSalariedIncomeValidator.validate(request);

        assertThat(result.status()).isEqualTo(status);
    }
}

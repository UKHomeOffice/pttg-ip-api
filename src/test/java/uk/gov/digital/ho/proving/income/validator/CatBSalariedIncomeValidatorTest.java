package uk.gov.digital.ho.proving.income.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.digital.ho.proving.income.validator.CatBSalariedTestData.*;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.*;

@RunWith(MockitoJUnitRunner.class)
public class CatBSalariedIncomeValidatorTest {

    @Mock private EmploymentCheckIncomeValidator employmentCheckIncomeValidator;
    @Mock private IncomeThresholdCalculator incomeThresholdCalculator;

    private CatBSalariedIncomeValidator catBSalariedIncomeValidator;

    private final LocalDate applicationDate = LocalDate.of(2018, Month.AUGUST, 24);


    @Before
    public void setUp() {
        catBSalariedIncomeValidator = new CatBSalariedIncomeValidator(employmentCheckIncomeValidator, incomeThresholdCalculator);
    }

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

    private void expectDependants(int dependants) {
        BigDecimal yearlyThreshold = BigDecimal.valueOf(18600);
        if (dependants == 1) {
            yearlyThreshold = BigDecimal.valueOf(22400);
        }
        else if(dependants > 1) {
            yearlyThreshold = BigDecimal.valueOf(22400 + (dependants - 1) * 2400);
        }
        when(incomeThresholdCalculator.yearlyThreshold(dependants)).thenReturn(yearlyThreshold);
        when(incomeThresholdCalculator.monthlyThreshold(dependants)).thenReturn(yearlyThreshold.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
    }

    @Test
    public void checkPassesIf12MonthsOverThresholdApplicantOnly() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(twelveMonthsOverThreshold(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkPassesMixedFrequencyButOverThreshold() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(mixedFrequencyButOverThreshold(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkPassesUnsortedIncomeData() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(twelveMonthsOverThresholdUnsorted(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkPassesIf12MonthsOverThresholdMultipleEmployers() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(overThresholdMultipleEmployers(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkFailsIfMonthMissingButStillEnoughPayments() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(monthMissingButEnoughPayments(applicationDate), NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void checkFailsIfMonthMissingTooFewPayments() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(monthMissingTooFewPayments(applicationDate), NOT_ENOUGH_RECORDS);
    }

    @Test
    public void checkFailsIfMonthBelowThreshold() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(monthBelowThreshold(applicationDate), CATB_SALARIED_BELOW_THRESHOLD);
    }

    @Test
    public void checkPassesIf12MonthsOverThresholdForJointApplication() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(twelveMonthsOverThresholdJointApplication(applicationDate), CATB_SALARIED_PASSED);
    }

    @Test
    public void checkFailsIfMonthMissingBothApplicantsJointApplication() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(jointApplicationMonthMissingBothApplicants(applicationDate), NON_CONSECUTIVE_MONTHS);
    }

    @Test
    public void checkFailsIfMonthUnderThresholdForBothApplicantsInJoint() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(jointApplicationMonthUnderThreshold(applicationDate), CATB_SALARIED_BELOW_THRESHOLD);
    }

    @Test
    public void checkPassesFor1DependantIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 1;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor1DependantIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 1;
        expectDependants(dependants);

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor1DependantIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 1;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor1DependantIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 1;
        expectDependants(dependants);

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor2DependantsIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 2;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor2DependantsIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 2;
        expectDependants(dependants);

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor2DependantsIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 2;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThresholdApplicantOnlyInJoint(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor2DependantsIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 2;
        expectDependants(dependants);

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor3DependantsIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 3;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor3DependantsIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 3;
        expectDependants(dependants);

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor3DependantsIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 3;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThresholdApplicantOnlyInJoint(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor3DependantsIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 3;
        expectDependants(dependants);

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor4DependantsIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 4;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor4DependantsIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 4;
        expectDependants(dependants);

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor4DependantsIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 4;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThresholdApplicantOnlyInJoint(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor4DependantsIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 4;
        expectDependants(dependants);

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }


    @Test
    public void checkPassesFor5DependantsIfOverThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 5;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThreshold(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor5DependantsIfUnderThresholdSoloApplication() {
        employmentCheckPasses();
        final int dependants = 5;
        expectDependants(dependants);

        assertStatus(monthBelowThreshold(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void checkPassesFor5DependantsIfOverThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 5;
        expectDependants(dependants);

        assertStatus(twelveMonthsOverThresholdApplicantOnlyInJoint(applicationDate, dependants), CATB_SALARIED_PASSED, dependants);
    }

    @Test
    public void checkFailsFor5DependantsIfUnderThresholdJointApplication() {
        employmentCheckPasses();
        final int dependants = 5;
        expectDependants(dependants);

        assertStatus(twelveMonthsUnderThresholdJointApplication(applicationDate, dependants), CATB_SALARIED_BELOW_THRESHOLD, dependants);
    }

    @Test
    public void catBSalariedCheckFailsIfEmploymentCheckFails() {
        employmentCheckFails();
        expectDependants(0);

        assertStatus(Collections.emptyList(), EMPLOYMENT_CHECK_FAILED);
    }

    @Test
    public void checkPassesWhenNoPaymentThisCalendarMonthButOtherwiseFine() {
        employmentCheckPasses();
        expectDependants(0);

        LocalDate earlyInMonthApplicationDate = LocalDate.of(2018, Month.JUNE, 2);
        assertStatus(twelveMonthsOverThresholdNoPaymentThisMonth(earlyInMonthApplicationDate), CATB_SALARIED_PASSED, earlyInMonthApplicationDate);
    }

    @Test
    public void checkFailsWhenIncomeNotPaye() {
        employmentCheckPasses();
        expectDependants(0);

        LocalDate earlyInMonthApplicationDate = LocalDate.of(2018, Month.JUNE, 2);
        assertStatus(twelveMonthsOverThresholdNotPaye(earlyInMonthApplicationDate), NOT_ENOUGH_RECORDS);
    }

    @Test
    public void checkFailsWhenNotEnoughMonthsAreBeforeApplicationDate() {
        employmentCheckPasses();
        expectDependants(0);

        assertStatus(twelveMonthsOverThresholdButNotAllBeforeArd(applicationDate), NOT_ENOUGH_RECORDS);
    }

    private void assertStatus(List<ApplicantIncome> applicantIncomes, IncomeValidationStatus status) {
        assertStatus(applicantIncomes, status, 0);
    }

    private void assertStatus(List<ApplicantIncome> applicantIncomes, IncomeValidationStatus status, int dependants) {
        assertStatus(applicantIncomes, status, dependants, applicationDate);
    }

    private void assertStatus(List<ApplicantIncome> applicantIncomes, IncomeValidationStatus status, LocalDate applicationDate) {
        assertStatus(applicantIncomes, status, 0, applicationDate);
    }

    private void assertStatus(List<ApplicantIncome> applicantIncomes, IncomeValidationStatus status, int dependants, LocalDate applicationDate) {
        IncomeValidationRequest request = new IncomeValidationRequest(applicantIncomes, applicationDate, dependants);

        IncomeValidationResult result = catBSalariedIncomeValidator.validate(request);

        assertThat(result.status()).isEqualTo(status);
    }
}

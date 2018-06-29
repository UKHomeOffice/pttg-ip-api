package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.EMPLOYMENT_CHECK_FAILED;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.EMPLOYMENT_CHECK_PASSED;

@Service
public class EmploymentCheckIncomeValidator implements IncomeValidator {


    public static final String CALCULATION_TYPE = "Employment Check";
    public static final Integer ASSESSMENT_START_DAYS_PREVIOUS = 32;

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        if (!incomeValidationRequest.isJointRequest()) {
            IncomeValidationResult result = doValidation(incomeValidationRequest);
            return result;
        }

        IncomeValidationRequest applicantOnlyRequest = incomeValidationRequest.toApplicantOnly();
        IncomeValidationResult applicantOnlyResult = doValidation(applicantOnlyRequest);
        if (applicantOnlyResult.status().isPassed()) {
            return applicantOnlyResult;
        }


        IncomeValidationRequest partnerOnlyRequest = incomeValidationRequest.toPartnerOnly();
        IncomeValidationResult partnerOnlyResult = doValidation(partnerOnlyRequest);
        if (partnerOnlyResult.status().isPassed()) {
            return partnerOnlyResult;
        }

        IncomeValidationResult jointResult = doValidation(incomeValidationRequest);
        return jointResult;
    }


    private IncomeValidationResult doValidation(IncomeValidationRequest incomeValidationRequest) {

        LocalDate assessmentStartDate = incomeValidationRequest.applicationRaisedDate().minusDays(ASSESSMENT_START_DAYS_PREVIOUS);

        BigDecimal monthlyThreshold = new SalariedThresholdCalculator(incomeValidationRequest.dependants()).getMonthlyThreshold();

        BigDecimal earningsSinceAssessmentStart =
            incomeValidationRequest.applicantIncomes().stream()
                .flatMap(applicantIncome -> applicantIncome.incomeRecord().paye().stream())
                .filter(income -> ! income.paymentDate().isBefore(assessmentStartDate))
                .map(Income::payment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        IncomeValidationStatus result = earningsSinceAssessmentStart.compareTo(monthlyThreshold) >= 0 ? EMPLOYMENT_CHECK_PASSED : EMPLOYMENT_CHECK_FAILED;

        return new IncomeValidationResult(
            result,
            monthlyThreshold,
            getCheckedIndividuals(incomeValidationRequest),
            assessmentStartDate,
            CALCULATION_TYPE);
    }

    private List<CheckedIndividual> getCheckedIndividuals(IncomeValidationRequest incomeValidationRequest) {
        return incomeValidationRequest.applicantIncomes()
            .stream()
            .map(applicantIncome ->
                new CheckedIndividual(
                    applicantIncome.applicant().nino(),
                    IncomeValidationHelper.toEmployerNames(applicantIncome.incomeRecord().employments())
                ))
            .collect(Collectors.toList());
    }

}

package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.CATB_NON_SALARIED_PASSED;

@Service
public class CatBNonSalariedIncomeValidator implements CategoryIncomeValidator {

    private static final String CALCULATION_TYPE = "Category B non salaried";
    private static final Integer ASSESSMENT_START_YEARS_BEFORE = 1;
    private static final String CATEGORY = "B";

    private final EmploymentCheckIncomeValidator employmentCheckIncomeValidator;

    public CatBNonSalariedIncomeValidator(EmploymentCheckIncomeValidator employmentCheckIncomeValidator) {
        this.employmentCheckIncomeValidator = employmentCheckIncomeValidator;
    }

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        IncomeValidationResult employmentCheckResult = employmentCheckIncomeValidator.validate(incomeValidationRequest);

        boolean passedEmploymentCheck = employmentCheckResult.status().isPassed();
        if (passedEmploymentCheck) {
            return validateNonSalariedIncome(incomeValidationRequest);
        } else {
            return employmentCheckResult;
        }
    }

    private IncomeValidationResult validateNonSalariedIncome(IncomeValidationRequest incomeValidationRequest) {
        IncomeValidationResult result = doValidation(incomeValidationRequest);
        if (result.status().isPassed()) {
            return result;
        }

        if (!incomeValidationRequest.isJointRequest()) {
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

        return result;
    }

    private IncomeValidationResult doValidation(IncomeValidationRequest incomeValidationRequest) {

        LocalDate assessmentStartDate = incomeValidationRequest.applicationRaisedDate().minusYears(ASSESSMENT_START_YEARS_BEFORE);

        BigDecimal projectedAnnualIncome = getProjectedAnnualIncome(incomeValidationRequest);

        BigDecimal yearlyThreshold = new SalariedThresholdCalculator(incomeValidationRequest.dependants()).yearlyThreshold();

        IncomeValidationStatus result = projectedAnnualIncome.compareTo(yearlyThreshold) >= 0 ? CATB_NON_SALARIED_PASSED : CATB_NON_SALARIED_BELOW_THRESHOLD;

        return new IncomeValidationResult(
            result,
            yearlyThreshold,
            IncomeValidationHelper.getCheckedIndividuals(incomeValidationRequest.allIncome()),
            assessmentStartDate,
            CATEGORY,
            CALCULATION_TYPE);
    }

    private BigDecimal getProjectedAnnualIncome(IncomeValidationRequest incomeValidationRequest) {
        List<Income> incomes =
            incomeValidationRequest.allIncome()
                .stream()
                .flatMap(applicantIncome -> applicantIncome.incomeRecord().paye().stream())
                .collect(Collectors.toList());
        Map<Integer, BigDecimal> monthlyIncomes = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);
        return ProjectedAnnualIncomeCalculator.calculate(monthlyIncomes);
    }

}

package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.getAllPayeIncomes;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.CATB_NON_SALARIED_PASSED;

@Service
public class CatBNonSalariedIncomeValidator implements ActiveIncomeValidator {

    private static final String CALCULATION_TYPE = "Category B non salaried";
    private static final Integer ASSESSMENT_START_YEARS_BEFORE = 1;
    private static final String CATEGORY = "B";

    private final EmploymentCheckIncomeValidator employmentCheckIncomeValidator;

    private final IncomeThresholdCalculator incomeThresholdCalculator;

    public CatBNonSalariedIncomeValidator(
        EmploymentCheckIncomeValidator employmentCheckIncomeValidator,
        IncomeThresholdCalculator incomeThresholdCalculator
    ) {
        this.employmentCheckIncomeValidator = employmentCheckIncomeValidator;
        this.incomeThresholdCalculator = incomeThresholdCalculator;
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

        BigDecimal yearlyThreshold = incomeThresholdCalculator.yearlyThreshold(incomeValidationRequest.dependants());

        IncomeValidationStatus result = projectedAnnualIncome.compareTo(yearlyThreshold) >= 0 ? CATB_NON_SALARIED_PASSED : CATB_NON_SALARIED_BELOW_THRESHOLD;

        return IncomeValidationResult.builder()
            .status(result)
            .threshold(yearlyThreshold)
            .individuals(incomeValidationRequest.getCheckedIndividuals())
            .assessmentStartDate(assessmentStartDate)
            .category(CATEGORY)
            .calculationType(CALCULATION_TYPE)
            .build();
    }

    private BigDecimal getProjectedAnnualIncome(IncomeValidationRequest incomeValidationRequest) {
        List<Income> incomes = getAllPayeIncomes(incomeValidationRequest);
        Map<Integer, BigDecimal> monthlyIncomes = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);
        return ProjectedAnnualIncomeCalculator.calculate(monthlyIncomes);
    }

}

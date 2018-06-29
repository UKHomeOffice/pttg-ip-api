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
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.CATB_NON_SALARIED_PASSED;

@Service
public class CatBNonSalariedIncomeValidator implements IncomeValidator {

    public static final String CALCULATION_TYPE = "Category B non salaried";
    public static final Integer ASSESSMENT_START_YEARS_BEFORE = 1;

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        IncomeValidationResult result = doValidation(incomeValidationRequest);
        if (result.status().isPassed()) {
            return result;
        }

        if(!incomeValidationRequest.isJointRequest()) {
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
            getCheckedIndividuals(incomeValidationRequest),
            assessmentStartDate,
            CALCULATION_TYPE);
    }

    private BigDecimal getProjectedAnnualIncome(IncomeValidationRequest incomeValidationRequest) {
        List<Income> incomes =
            incomeValidationRequest.applicantIncomes()
                .stream()
                .flatMap(applicantIncome -> applicantIncome.incomeRecord().paye().stream())
                .collect(Collectors.toList());
        Map<Integer, BigDecimal> monthlyIncomes = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);
        return ProjectedAnnualIncomeCalculator.calculate(monthlyIncomes);
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

package uk.gov.digital.ho.proving.income.calculation;

import lombok.extern.slf4j.Slf4j;
import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.api.domain.FinancialCheckValues;
import uk.gov.digital.ho.proving.income.domain.FinancialCheckResult;
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments;
import uk.gov.digital.ho.proving.income.domain.hmrc.Income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.digital.ho.proving.income.calculation.CalculationHelper.*;

@Slf4j
public class CatASalariedMonthlyCalculator implements Calculator {

    private static final Integer NUMBER_OF_MONTHS = 6;

    @Override
    public CalculationResult calculate(CalculationRequest calculationRequest) {

        ApplicantIncome applicantIncome = calculationRequest.applicantIncomes().get(0);

        FinancialCheckResult financialCheckResult =
            validateCategoryAMonthlySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                calculationRequest.lower(),
                calculationRequest.upper(),
                calculationRequest.dependants(),
                applicantIncome.employments(),
                applicantIncome.applicant().nino());

        CalculationResult calculationResult;
        List<String> employments = applicantIncome.employments().stream().map(e -> e.employer().name()).collect(Collectors.toList());
        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);
        boolean passed = financialCheckResult.financialCheckValue().equals(FinancialCheckValues.MONTHLY_SALARIED_PASSED);
        calculationResult = new CalculationResult(financialCheckResult.financialCheckValue(), financialCheckResult.threshold(), Arrays.asList(checkedIndividual), passed);
        return calculationResult;

    }

    private FinancialCheckResult validateCategoryAMonthlySalaried(List<Income> income, LocalDate lower, LocalDate upper, Integer dependants, List<Employments> employments, String nino) {
        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(dependants);
        BigDecimal monthlyThreshold = thresholdCalculator.getMonthlyThreshold();
        CheckedIndividual checkedIndividual = new CheckedIndividual(nino, toEmployerNames(employments));
        return new FinancialCheckResult(financialCheckForMonthlySalaried(income, NUMBER_OF_MONTHS, monthlyThreshold, lower, upper), monthlyThreshold, Arrays.asList(checkedIndividual));
    }

    private FinancialCheckValues financialCheckForMonthlySalaried(List<Income> incomes, int numOfMonths, BigDecimal threshold, LocalDate lower, LocalDate upper) {
        Stream<Income> individualIncome = filterIncomesByDates(incomes, lower, upper);
        List<Income> lastXMonths = individualIncome.limit(numOfMonths).collect(Collectors.toList());
        if (lastXMonths.size() >= numOfMonths) {

            // Do we have NUMBER_OF_MONTHS consecutive months with the same employer
            for (int i = 0; i < numOfMonths - 1; i++) {
                if (!isSuccessiveMonths(lastXMonths.get(i), lastXMonths.get(i + 1))) {
                    log.debug("FAILED: Months not consecutive");
                    return FinancialCheckValues.NON_CONSECUTIVE_MONTHS;
                }
            }

            EmploymentCheck employmentCheck = checkIncomesPassThresholdWithSameEmployer(lastXMonths, threshold);
            if (employmentCheck.equals(EmploymentCheck.PASS)) {
                return FinancialCheckValues.MONTHLY_SALARIED_PASSED;
            } else {
                return employmentCheck.equals(EmploymentCheck.FAILED_THRESHOLD) ? FinancialCheckValues.MONTHLY_VALUE_BELOW_THRESHOLD : FinancialCheckValues.MULTIPLE_EMPLOYERS;
            }

        } else {
            return FinancialCheckValues.NOT_ENOUGH_RECORDS;
        }
    }

}

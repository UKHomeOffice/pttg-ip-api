package uk.gov.digital.ho.proving.income.calculation;

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

public class CatASalariedWeeklyCalculator implements Calculator {

    private final static Integer NUMBER_OF_WEEKS = 26;

    @Override
    public CalculationResult calculate(CalculationRequest calculationRequest) {

        ApplicantIncome applicantIncome = calculationRequest.applicantIncomes().get(0);

        FinancialCheckResult financialCheckResult =
            validateCategoryAWeeklySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                calculationRequest.lower(),
                calculationRequest.upper(),
                calculationRequest.dependants(),
                applicantIncome.employments(),
                applicantIncome.applicant().nino());

        CalculationResult calculationResult;
        List<String> employments = applicantIncome.employments().stream().map(e -> e.employer().name()).collect(Collectors.toList());
        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);
        boolean passed = financialCheckResult.financialCheckValue().equals(FinancialCheckValues.WEEKLY_SALARIED_PASSED);
        calculationResult = new CalculationResult(financialCheckResult.financialCheckValue(), financialCheckResult.threshold(), Arrays.asList(checkedIndividual), passed);
        return calculationResult;
    }

    private FinancialCheckResult validateCategoryAWeeklySalaried(List<Income> income, LocalDate lower, LocalDate upper, Integer dependants, List<Employments> employments, String nino) {
        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(dependants);
        BigDecimal weeklyThreshold = thresholdCalculator.getWeeklyThreshold();
        CheckedIndividual checkedIndividual = new CheckedIndividual(nino, toEmployerNames(employments));
        return new FinancialCheckResult(financialCheckForWeeklySalaried(income, NUMBER_OF_WEEKS, weeklyThreshold, lower, upper), weeklyThreshold, Arrays.asList(checkedIndividual));
    }

    private static FinancialCheckValues financialCheckForWeeklySalaried(List<Income> incomes, int numOfWeeks, BigDecimal threshold, LocalDate lower, LocalDate upper) {
        Stream<Income> individualIncome = filterIncomesByDates(incomes, lower, upper);
        List<Income> lastXWeeks = individualIncome.collect(Collectors.toList());

        if (lastXWeeks.size() >= numOfWeeks) {
            EmploymentCheck employmentCheck = checkIncomesPassThresholdWithSameEmployer(lastXWeeks, threshold);
            if (employmentCheck.equals(EmploymentCheck.PASS)) {
                return FinancialCheckValues.WEEKLY_SALARIED_PASSED;
            } else {
                return employmentCheck.equals(EmploymentCheck.FAILED_THRESHOLD) ? FinancialCheckValues.WEEKLY_VALUE_BELOW_THRESHOLD : FinancialCheckValues.MULTIPLE_EMPLOYERS;
            }
        } else {
            return FinancialCheckValues.NOT_ENOUGH_RECORDS;
        }

    }

}

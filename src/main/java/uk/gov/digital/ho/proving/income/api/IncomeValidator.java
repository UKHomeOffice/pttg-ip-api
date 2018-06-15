package uk.gov.digital.ho.proving.income.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.domain.FinancialCheckResult;
import uk.gov.digital.ho.proving.income.api.domain.FinancialCheckValues;
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments;
import uk.gov.digital.ho.proving.income.domain.hmrc.Income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncomeValidator {

    private static final int NUMBER_OF_MONTHS = 6;
    private static final int NUMBER_OF_WEEKS = 26;

    private static final Logger LOGGER = LoggerFactory.getLogger(IncomeValidator.class);

    private IncomeValidator() {
    }

    public static FinancialCheckResult validateCategoryAMonthlySalaried(List<Income> income, LocalDate lower, LocalDate upper, Integer dependants, List<Employments> employments, String nino) {
        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(dependants);
        BigDecimal monthlyThreshold = thresholdCalculator.getMonthlyThreshold();
        CheckedIndividual checkedIndividual = new CheckedIndividual(nino, toEmployerNames(employments));
        return new FinancialCheckResult(financialCheckForMonthlySalaried(income, NUMBER_OF_MONTHS, monthlyThreshold, lower, upper), monthlyThreshold, Arrays.asList(checkedIndividual));
    }

    public static FinancialCheckResult validateCategoryAWeeklySalaried(List<Income> income, LocalDate lower, LocalDate upper, Integer dependants, List<Employments> employments, String nino) {
        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(dependants);
        BigDecimal weeklyThreshold = thresholdCalculator.getWeeklyThreshold();
        CheckedIndividual checkedIndividual = new CheckedIndividual(nino, toEmployerNames(employments));
        return new FinancialCheckResult(financialCheckForWeeklySalaried(income, NUMBER_OF_WEEKS, weeklyThreshold, lower, upper), weeklyThreshold, Arrays.asList(checkedIndividual));
    }

    private static List<String> toEmployerNames(List<Employments> employments) {
        return employments.stream().map(employment -> employment.employer().name()).distinct().collect(Collectors.toList());
    }

    private static FinancialCheckValues financialCheckForMonthlySalaried(List<Income> incomes, int numOfMonths, BigDecimal threshold, LocalDate lower, LocalDate upper) {
        Stream<Income> individualIncome = filterIncomesByDates(incomes, lower, upper);
        List<Income> lastXMonths = individualIncome.limit(numOfMonths).collect(Collectors.toList());
        if (lastXMonths.size() >= numOfMonths) {

            // Do we have NUMBER_OF_MONTHS consecutive months with the same employer
            for (int i = 0; i < numOfMonths - 1; i++) {
                if (!isSuccessiveMonths(lastXMonths.get(i), lastXMonths.get(i + 1))) {
                    LOGGER.debug("FAILED: Months not consecutive");
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

    private static EmploymentCheck checkIncomesPassThresholdWithSameEmployer(List<Income> incomes, BigDecimal threshold) {
        String employerPayeReference = incomes.get(0).employerPayeReference();
        for (Income income : incomes) {
            if (!checkValuePassesThreshold(income.payment(), threshold)) {
                LOGGER.debug("FAILED: Income value = " + income.payment() + " is below threshold: " + threshold);
                return EmploymentCheck.FAILED_THRESHOLD;
            }

            if (!employerPayeReference.equalsIgnoreCase(income.employerPayeReference())) {
                LOGGER.debug("FAILED: Different employerPayeReference = " + employerPayeReference + " is not the same as " + income.employerPayeReference());
                return EmploymentCheck.FAILED_EMPLOYER;
            }
        }
        return EmploymentCheck.PASS;
    }


    private static boolean checkValuePassesThreshold(BigDecimal value, BigDecimal threshold) {
        return (value.compareTo(threshold) >= 0);
    }


    private static Stream<Income> filterIncomesByDates(List<Income> incomes, LocalDate lower, LocalDate upper) {
        return incomes.stream()
            .sorted((income1, income2) -> income2.paymentDate().compareTo(income1.paymentDate()))
            .filter(income -> isDateInRange(income.paymentDate(), lower, upper));
    }

    private static boolean isSuccessiveMonths(Income first, Income second) {
        return getDifferenceInMonthsBetweenDates(first.paymentDate(), second.paymentDate()) == 1;
    }


    public static long getDifferenceInMonthsBetweenDates(LocalDate date1, LocalDate date2) {

        // Period.toTotalMonths() only returns integer month differences so for 14/07/2015 and 17/06/2015 it returns 0
        // We need it to return 1, so we set both dates to the first of the month

        LocalDate toDate = date1.withDayOfMonth(1);
        LocalDate fromDate = date2.withDayOfMonth(1);

        Period period = fromDate.until(toDate);
        return period.toTotalMonths();

    }

    private static boolean isDateInRange(LocalDate date, LocalDate lower, LocalDate upper) {
        boolean inRange = !(date.isBefore(lower) || date.isAfter(upper));
        LOGGER.debug(String.format("%s: %s in range of %s & %s", inRange, date, lower, upper));
        return inRange;
    }

    private enum EmploymentCheck {
        PASS, FAILED_THRESHOLD, FAILED_EMPLOYER
    }

}

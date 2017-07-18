package uk.gov.digital.ho.proving.income.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.digital.ho.proving.income.domain.Income;
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncomeValidator {

    private static final int NUMBER_OF_MONTHS = 6;
    private static final int NUMBER_OF_WEEKS = 26;

    private static final Logger LOGGER = LoggerFactory.getLogger(IncomeValidator.class);

    private IncomeValidator() {
    }

    public static FinancialCheckResult validateCategoryAMonthlySalaried(List<Income> incomes, LocalDate lower, LocalDate upper, int dependants, List<String> employers) {
        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(dependants);
        BigDecimal monthlyThreshold = thresholdCalculator.getMonthlyThreshold();
        return new FinancialCheckResult(financialCheckForMonthlySalaried(incomes, NUMBER_OF_MONTHS, monthlyThreshold, lower, upper), monthlyThreshold,employers);
    }

    public static FinancialCheckResult validateCategoryAWeeklySalaried(List<Income> incomes, LocalDate lower, LocalDate upper, int dependants, List<String> employers) {
        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(dependants);
        BigDecimal weeklyThreshold = thresholdCalculator.getWeeklyThreshold();
        return new FinancialCheckResult(financialCheckForWeeklySalaried(incomes, NUMBER_OF_WEEKS, weeklyThreshold, lower, upper), weeklyThreshold, employers);
    }

    public static FinancialCheckResult validateCategoryAMonthlySalaried2(List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> income, LocalDate lower, LocalDate upper, Integer dependants, List<Employments> employments) {
        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(dependants);
        BigDecimal monthlyThreshold = thresholdCalculator.getMonthlyThreshold();
        return new FinancialCheckResult(financialCheckForMonthlySalaried2(income, NUMBER_OF_MONTHS, monthlyThreshold, lower, upper), monthlyThreshold, toEmployerNames(employments));
    }

    public static FinancialCheckResult validateCategoryAWeeklySalaried2(List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> income, LocalDate lower, LocalDate upper, Integer dependants, List<Employments> employments) {
        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(dependants);
        BigDecimal weeklyThreshold = thresholdCalculator.getWeeklyThreshold();
        return new FinancialCheckResult(financialCheckForWeeklySalaried2(income, NUMBER_OF_WEEKS, weeklyThreshold, lower, upper), weeklyThreshold, toEmployerNames(employments));
    }

    private static List<String> toEmployerNames(List<Employments> employments) {
        return employments.stream().map(employment -> employment.getEmployer().getName()).collect(Collectors.toList());
    }

    //TODO Refactor date handling once we know more about the back end and test env
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
                return employmentCheck.equals(EmploymentCheck.FAILED_THRESHOLD) ? FinancialCheckValues.MONTHLY_VALUE_BELOW_THRESHOLD : FinancialCheckValues.NON_CONSECUTIVE_MONTHS;
            }

        } else {
            return FinancialCheckValues.NOT_ENOUGH_RECORDS;
        }
    }


    private static FinancialCheckValues financialCheckForMonthlySalaried2(List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> incomes, int numOfMonths, BigDecimal threshold, LocalDate lower, LocalDate upper) {
        Stream<uk.gov.digital.ho.proving.income.domain.hmrc.Income> individualIncome = filterIncomesByDates2(incomes, lower, upper);
        List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> lastXMonths = individualIncome.limit(numOfMonths).collect(Collectors.toList());
        if (lastXMonths.size() >= numOfMonths) {

            // Do we have NUMBER_OF_MONTHS consecutive months with the same employer
            for (int i = 0; i < numOfMonths - 1; i++) {
                if (!isSuccessiveMonths(lastXMonths.get(i), lastXMonths.get(i + 1))) {
                    LOGGER.debug("FAILED: Months not consecutive");
                    return FinancialCheckValues.NON_CONSECUTIVE_MONTHS;
                }
            }

            EmploymentCheck employmentCheck = checkIncomesPassThresholdWithSameEmployer2(lastXMonths, threshold);
            if (employmentCheck.equals(EmploymentCheck.PASS)) {
                return FinancialCheckValues.MONTHLY_SALARIED_PASSED;
            } else {
                return employmentCheck.equals(EmploymentCheck.FAILED_THRESHOLD) ? FinancialCheckValues.MONTHLY_VALUE_BELOW_THRESHOLD : FinancialCheckValues.NON_CONSECUTIVE_MONTHS;
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
                return employmentCheck.equals(EmploymentCheck.FAILED_THRESHOLD) ? FinancialCheckValues.WEEKLY_VALUE_BELOW_THRESHOLD : FinancialCheckValues.NON_CONSECUTIVE_MONTHS;
            }
        } else {
            return FinancialCheckValues.NOT_ENOUGH_RECORDS;
        }

    }

    private static FinancialCheckValues financialCheckForWeeklySalaried2(List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> incomes, int numOfWeeks, BigDecimal threshold, LocalDate lower, LocalDate upper) {
        Stream<uk.gov.digital.ho.proving.income.domain.hmrc.Income> individualIncome = filterIncomesByDates2(incomes, lower, upper);
        List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> lastXWeeks = individualIncome.collect(Collectors.toList());

        if (lastXWeeks.size() >= numOfWeeks) {
            EmploymentCheck employmentCheck = checkIncomesPassThresholdWithSameEmployer2(lastXWeeks, threshold);
            if (employmentCheck.equals(EmploymentCheck.PASS)) {
                return FinancialCheckValues.WEEKLY_SALARIED_PASSED;
            } else {
                return employmentCheck.equals(EmploymentCheck.FAILED_THRESHOLD) ? FinancialCheckValues.WEEKLY_VALUE_BELOW_THRESHOLD : FinancialCheckValues.NON_CONSECUTIVE_MONTHS;
            }
        } else {
            return FinancialCheckValues.NOT_ENOUGH_RECORDS;
        }

    }

    private static EmploymentCheck checkIncomesPassThresholdWithSameEmployer(List<Income> incomes, BigDecimal threshold) {
        String employer = incomes.get(0).getEmployer();
        for (Income income : incomes) {
            if (!checkValuePassesThreshold(new BigDecimal(income.getIncome()), threshold)) {
                LOGGER.debug("FAILED: Income value = " + new BigDecimal(income.getIncome()) + " is below threshold: " + threshold);
                return EmploymentCheck.FAILED_THRESHOLD;
            }

            if (!employer.equalsIgnoreCase(income.getEmployer())) {
                LOGGER.debug("FAILED: Different employers = " + employer + " is not the same as " + income.getEmployer());
                return EmploymentCheck.FAILED_EMPLOYER;
            }
        }
        return EmploymentCheck.PASS;
    }

    private static EmploymentCheck checkIncomesPassThresholdWithSameEmployer2(List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> incomes, BigDecimal threshold) {
        String employerPayeReference = incomes.get(0).getEmployerPayeReference();
        for (uk.gov.digital.ho.proving.income.domain.hmrc.Income income : incomes) {
            if (!checkValuePassesThreshold(income.getPayment(), threshold)) {
                LOGGER.debug("FAILED: Income value = " + income.getPayment() + " is below threshold: " + threshold);
                return EmploymentCheck.FAILED_THRESHOLD;
            }

            if (!employerPayeReference.equalsIgnoreCase(income.getEmployerPayeReference())) {
                LOGGER.debug("FAILED: Different employerPayeReference = " + employerPayeReference + " is not the same as " + income.getEmployerPayeReference());
                return EmploymentCheck.FAILED_EMPLOYER;
            }
        }
        return EmploymentCheck.PASS;
    }


    private static boolean checkValuePassesThreshold(BigDecimal value, BigDecimal threshold) {
        return (value.compareTo(threshold) >= 0);
    }


    private static Stream<uk.gov.digital.ho.proving.income.domain.hmrc.Income> filterIncomesByDates2(List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> incomes, LocalDate lower, LocalDate upper) {
        return incomes.stream()
            .sorted((income1, income2) -> income2.getPaymentDate().compareTo(income1.getPaymentDate()))
            .filter(income -> isDateInRange(income.getPaymentDate(), lower, upper));
    }

    private static Stream<Income> filterIncomesByDates(List<Income> incomes, LocalDate lower, LocalDate upper) {
        return incomes.stream()
            .sorted((income1, income2) -> income2.getPayDate().compareTo(income1.getPayDate()))
            .filter(income -> isDateInRange(income.getPayDate(), lower, upper));
    }

    private static boolean isSuccessiveMonths(Income first, Income second) {
        return getDifferenceInMonthsBetweenDates(first.getPayDate(), second.getPayDate()) == 1;
    }

    private static boolean isSuccessiveMonths(uk.gov.digital.ho.proving.income.domain.hmrc.Income first, uk.gov.digital.ho.proving.income.domain.hmrc.Income second) {
        return getDifferenceInMonthsBetweenDates(first.getPaymentDate(), second.getPaymentDate()) == 1;
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

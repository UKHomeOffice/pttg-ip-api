package uk.gov.digital.ho.proving.income.validator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.digital.ho.proving.income.hmrc.domain.Employments;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.EmploymentCheck;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class IncomeValidationHelper {

    static boolean isSuccessiveMonths(Income first, Income second) {
        return getDifferenceInMonthsBetweenDates(first.paymentDate(), second.paymentDate()) == 1;
    }

    static long getDifferenceInMonthsBetweenDates(LocalDate date1, LocalDate date2) {

        // Period.toTotalMonths() only returns integer month differences so for 14/07/2015 and 17/06/2015 it returns 0
        // We need it to return 1, so we set both dates to the first of the month

        LocalDate toDate = date1.withDayOfMonth(1);
        LocalDate fromDate = date2.withDayOfMonth(1);

        Period period = fromDate.until(toDate);
        return period.toTotalMonths();

    }

    static List<String> toEmployerNames(List<Employments> employments) {
        return employments.stream()
            .map(employment -> employment.employer().name())
            .distinct()
            .collect(Collectors.toList());
    }

    static EmploymentCheck checkIncomesPassThresholdWithSameEmployer(List<Income> incomes, BigDecimal threshold) {
        String employerPayeReference = incomes.get(0).employerPayeReference();
        for (Income income : incomes) {
            if (!checkValuePassesThreshold(income.payment(), threshold)) {
                log.debug("FAILED: Income value = " + income.payment() + " is below threshold: " + threshold);
                return EmploymentCheck.FAILED_THRESHOLD;
            }

            if (!employerPayeReference.equalsIgnoreCase(income.employerPayeReference())) {
                log.debug("FAILED: Different employerPayeReference = " + employerPayeReference + " is not the same as " + income.employerPayeReference());
                return EmploymentCheck.FAILED_EMPLOYER;
            }
        }
        return EmploymentCheck.PASS;
    }

    static List<Income> filterIncomesByDates(List<Income> incomes, LocalDate lower, LocalDate upper) {
        return incomes.stream()
            .filter(income -> isDateInRange(income.paymentDate(), lower, upper))
            .collect(Collectors.toList());
    }

    static List<Income> orderByPaymentDate(List<Income> incomeStream) {
        return incomeStream.stream()
            .sorted((income1, income2) -> income2.paymentDate().compareTo(income1.paymentDate()))
            .collect(Collectors.toList());
    }

    private static boolean isDateInRange(LocalDate date, LocalDate lower, LocalDate upper) {
        boolean inRange = !(date.isBefore(lower) || date.isAfter(upper));
        log.debug(String.format("%s: %s in range of %s & %s", inRange, date, lower, upper));
        return inRange;
    }

    static boolean checkValuePassesThreshold(BigDecimal value, BigDecimal threshold) {
        return (value.compareTo(threshold) >= 0);
    }

    static List<Income> removeDuplicates(List<Income> incomes) {
        return incomes.stream().distinct().collect(Collectors.toList());
    }

    static List<Income> getAllPayeIncomes(IncomeValidationRequest incomeValidationRequest) {
        return incomeValidationRequest.allIncome()
            .stream()
            .flatMap(applicantIncome -> applicantIncome.incomeRecord().paye().stream())
            .collect(Collectors.toList());
    }

    static List<Income> getAllPayeInDateRange(IncomeValidationRequest incomeValidationRequest, LocalDate applicationStartDate) {
        List<Income> paye = getAllPayeIncomes(incomeValidationRequest);
        LocalDate applicationRaisedDate = incomeValidationRequest.applicationRaisedDate();
        return filterIncomesByDates(paye, applicationStartDate, applicationRaisedDate);
    }

    static List<Income> combineIncomesForSameMonth(List<Income> incomes) {
        Map<Integer, List<Income>> groupedByMonth = incomes.stream()
            .collect(Collectors.groupingBy(Income::yearMonthAndEmployer));
        return sumGroupedIncomes(groupedByMonth.values());
    }

    static List<Income> combineIncomesForSameWeek(List<Income> incomes) {
        Map<Integer, List<Income>> groupedByWeek = incomes.stream()
            .collect(Collectors.groupingBy(Income::weekNumberAndEmployer));
        return sumGroupedIncomes(groupedByWeek.values());
    }

    private static List<Income> sumGroupedIncomes(Collection<List<Income>> groupedIncomes) {
        List<Income> summedIncomes = new ArrayList<>();
        for (List<Income> samePeriodIncomes : groupedIncomes) {
            Income summedIncome = samePeriodIncomes.get(0);
            for (int i = 1; i < samePeriodIncomes.size(); i++) {
                summedIncome = summedIncome.add(samePeriodIncomes.get(i));
            }
            summedIncomes.add(summedIncome);
        }
        return summedIncomes;
    }

    static BigDecimal totalPayment(List<Income> incomes) {
        return incomes.stream()
            .map(Income::payment)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static BigDecimal largestSingleEmployerIncome(List<Income> incomes) {
        return groupIncomesByEmployers(incomes).stream()
            .map(IncomeValidationHelper::totalPayment)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    private static Collection<List<Income>> groupIncomesByEmployers(List<Income> paye) {
        return paye.stream()
            .collect(Collectors.groupingBy(Income::employerPayeReference))
            .values();
    }
}

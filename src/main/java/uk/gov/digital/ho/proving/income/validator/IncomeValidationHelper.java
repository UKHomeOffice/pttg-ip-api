package uk.gov.digital.ho.proving.income.validator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Employments;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.EmploymentCheck;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return employments.stream().map(employment -> employment.employer().name()).distinct().collect(Collectors.toList());
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

    static Stream<Income> filterIncomesByDates(List<Income> incomes, LocalDate lower, LocalDate upper) {
        return incomes.stream()
            .sorted((income1, income2) -> income2.paymentDate().compareTo(income1.paymentDate()))
            .filter(income -> isDateInRange(income.paymentDate(), lower, upper));
    }

    static List<CheckedIndividual> getCheckedIndividuals(List<ApplicantIncome> applicantIncomes) {
        return applicantIncomes
            .stream()
            .map(applicantIncome ->
                new CheckedIndividual(
                    applicantIncome.applicant().nino(),
                    IncomeValidationHelper.toEmployerNames(applicantIncome.incomeRecord().employments())
                ))
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

}

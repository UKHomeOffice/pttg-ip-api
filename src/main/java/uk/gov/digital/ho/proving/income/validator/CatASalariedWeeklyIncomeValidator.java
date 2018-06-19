package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.*;

public class CatASalariedWeeklyIncomeValidator implements IncomeValidator {

    private final static Integer NUMBER_OF_WEEKS = 26;

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncomes().get(0);

        List<String> employments =
            applicantIncome.employments()
                .stream()
                .map(e -> e.employer().name())
                .collect(Collectors.toList());

        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);

        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(incomeValidationRequest.dependants());
        BigDecimal weeklyThreshold = thresholdCalculator.getWeeklyThreshold();

        IncomeValidationStatus status =
            financialCheckForWeeklySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                NUMBER_OF_WEEKS,
                weeklyThreshold,
                incomeValidationRequest.lower(),
                incomeValidationRequest.upper());

        return new IncomeValidationResult(status, weeklyThreshold, Arrays.asList(checkedIndividual));
    }

    private static IncomeValidationStatus financialCheckForWeeklySalaried(List<Income> incomes, int numOfWeeks, BigDecimal threshold, LocalDate lower, LocalDate upper) {
        Stream<Income> individualIncome = filterIncomesByDates(incomes, lower, upper);
        List<Income> lastXWeeks = individualIncome.collect(Collectors.toList());

        if (lastXWeeks.size() >= numOfWeeks) {
            EmploymentCheck employmentCheck = checkIncomesPassThresholdWithSameEmployer(lastXWeeks, threshold);
            if (employmentCheck.equals(EmploymentCheck.PASS)) {
                return IncomeValidationStatus.WEEKLY_SALARIED_PASSED;
            } else {
                return employmentCheck.equals(EmploymentCheck.FAILED_THRESHOLD) ? IncomeValidationStatus.WEEKLY_VALUE_BELOW_THRESHOLD : IncomeValidationStatus.MULTIPLE_EMPLOYERS;
            }
        } else {
            return IncomeValidationStatus.NOT_ENOUGH_RECORDS;
        }

    }

}

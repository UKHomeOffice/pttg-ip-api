package uk.gov.digital.ho.proving.income.validator;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class CatASalariedMonthlyIncomeValidator implements IncomeValidator {

    private static final Integer NUMBER_OF_MONTHS = 6;

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
        BigDecimal monthlyThreshold = thresholdCalculator.getMonthlyThreshold();

        IncomeValidationStatus status =
            financialCheckForMonthlySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                NUMBER_OF_MONTHS,
                monthlyThreshold,
                incomeValidationRequest.lower(),
                incomeValidationRequest.upper());

        return new IncomeValidationResult(status, monthlyThreshold, Arrays.asList(checkedIndividual));
    }

    private IncomeValidationStatus financialCheckForMonthlySalaried(List<Income> incomes, int numOfMonths, BigDecimal threshold, LocalDate lower, LocalDate upper) {
        Stream<Income> individualIncome = filterIncomesByDates(incomes, lower, upper);
        List<Income> lastXMonths = individualIncome.limit(numOfMonths).collect(Collectors.toList());
        if (lastXMonths.size() >= numOfMonths) {

            // Do we have NUMBER_OF_MONTHS consecutive months with the same employer
            for (int i = 0; i < numOfMonths - 1; i++) {
                if (!isSuccessiveMonths(lastXMonths.get(i), lastXMonths.get(i + 1))) {
                    log.debug("FAILED: Months not consecutive");
                    return IncomeValidationStatus.NON_CONSECUTIVE_MONTHS;
                }
            }

            EmploymentCheck employmentCheck = checkIncomesPassThresholdWithSameEmployer(lastXMonths, threshold);
            if (employmentCheck.equals(EmploymentCheck.PASS)) {
                return IncomeValidationStatus.MONTHLY_SALARIED_PASSED;
            } else {
                return employmentCheck.equals(EmploymentCheck.FAILED_THRESHOLD) ? IncomeValidationStatus.MONTHLY_VALUE_BELOW_THRESHOLD : IncomeValidationStatus.MULTIPLE_EMPLOYERS;
            }

        } else {
            return IncomeValidationStatus.NOT_ENOUGH_RECORDS;
        }
    }

}

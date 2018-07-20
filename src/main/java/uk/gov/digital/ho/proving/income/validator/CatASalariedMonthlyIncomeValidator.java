package uk.gov.digital.ho.proving.income.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
@Service
public class CatASalariedMonthlyIncomeValidator implements IncomeValidator {

    private static final Integer ASSESSMENT_START_DAYS_PREVIOUS = 182;
    private static final Integer NUMBER_OF_MONTHS = 6;
    private static final String CALCULATION_TYPE = "Category A Monthly Salary";
    private static final String CATEGORY = "A";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncome();

        List<String> employments = toEmployerNames(applicantIncome.employments());

        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);

        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(incomeValidationRequest.dependants());
        BigDecimal monthlyThreshold = thresholdCalculator.getMonthlyThreshold();

        LocalDate assessmentStartDate = incomeValidationRequest.applicationRaisedDate().minusDays(ASSESSMENT_START_DAYS_PREVIOUS);

        IncomeValidationStatus status =
            financialCheckForMonthlySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                NUMBER_OF_MONTHS,
                monthlyThreshold,
                assessmentStartDate,
                incomeValidationRequest.applicationRaisedDate());

        return new IncomeValidationResult(status, monthlyThreshold, Arrays.asList(checkedIndividual), assessmentStartDate, CATEGORY, CALCULATION_TYPE);
    }

    private IncomeValidationStatus financialCheckForMonthlySalaried(List<Income> incomes, int numOfMonths, BigDecimal threshold, LocalDate assessmentStartDate, LocalDate applicationRaisedDate) {
        Stream<Income> individualIncome = filterIncomesByDates(incomes, assessmentStartDate, applicationRaisedDate);
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

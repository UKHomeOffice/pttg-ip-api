package uk.gov.digital.ho.proving.income.validator;

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

@Service
public class CatASalariedWeeklyIncomeValidator implements IncomeValidator {

    private static final Integer ASSESSMENT_START_DAYS_PREVIOUS = 182;
    private final static Integer NUMBER_OF_WEEKS = 26;
    private static final String CALCULATION_TYPE = "Category A Weekly single applicant";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncomes().get(0);

        List<String> employments = toEmployerNames(applicantIncome.employments());

        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);

        SalariedThresholdCalculator thresholdCalculator = new SalariedThresholdCalculator(incomeValidationRequest.dependants());
        BigDecimal weeklyThreshold = thresholdCalculator.getWeeklyThreshold();

        LocalDate assessmentStartDate = incomeValidationRequest.applicationRaisedDate().minusDays(ASSESSMENT_START_DAYS_PREVIOUS);

        IncomeValidationStatus status =
            financialCheckForWeeklySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                NUMBER_OF_WEEKS,
                weeklyThreshold,
                assessmentStartDate,
                incomeValidationRequest.applicationRaisedDate());

        return new IncomeValidationResult(status, weeklyThreshold, Arrays.asList(checkedIndividual), assessmentStartDate, CALCULATION_TYPE);
    }

    private static IncomeValidationStatus financialCheckForWeeklySalaried(List<Income> incomes, int numOfWeeks, BigDecimal threshold, LocalDate assessmentStartDate, LocalDate applicationRaisedDate) {
        Stream<Income> individualIncome = filterIncomesByDates(incomes, assessmentStartDate, applicationRaisedDate);
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

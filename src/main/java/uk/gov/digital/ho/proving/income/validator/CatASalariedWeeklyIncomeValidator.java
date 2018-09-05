package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.getAssessmentStartDate;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.*;

@Service
public class CatASalariedWeeklyIncomeValidator implements IncomeValidator {

    private final static Integer NUMBER_OF_WEEKS = 26;
    private static final String CALCULATION_TYPE = "Category A Weekly Salary";
    private static final String CATEGORY = "A";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncome();

        List<String> employments = toEmployerNames(applicantIncome.employments());

        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(incomeValidationRequest.dependants());
        BigDecimal weeklyThreshold = thresholdCalculator.getWeeklyThreshold();

        LocalDate assessmentStartDate = getAssessmentStartDate(incomeValidationRequest.applicationRaisedDate());

        IncomeValidationStatus status =
            financialCheckForWeeklySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                NUMBER_OF_WEEKS,
                weeklyThreshold,
                assessmentStartDate,
                incomeValidationRequest.applicationRaisedDate());

        return new IncomeValidationResult(status, weeklyThreshold, Arrays.asList(checkedIndividual), assessmentStartDate, CATEGORY, CALCULATION_TYPE);
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

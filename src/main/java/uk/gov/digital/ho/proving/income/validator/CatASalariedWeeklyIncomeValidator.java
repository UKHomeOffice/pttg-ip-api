package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.getAssessmentStartDate;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.*;

@Service
public class CatASalariedWeeklyIncomeValidator implements IncomeValidator {

    private static final Integer WEEKS_OF_INCOME = 26;
    private static final String CALCULATION_TYPE = "Category A Weekly Salary";
    private static final String CATEGORY = "A";

    private final IncomeThresholdCalculator incomeThresholdCalculator;

    public CatASalariedWeeklyIncomeValidator(IncomeThresholdCalculator incomeThresholdCalculator) {
        this.incomeThresholdCalculator = incomeThresholdCalculator;
    }

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncome();

        List<String> employments = toEmployerNames(applicantIncome.employments());

        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);

        BigDecimal weeklyThreshold = incomeThresholdCalculator.weeklyThreshold(incomeValidationRequest.dependants());

        LocalDate assessmentStartDate = getAssessmentStartDate(incomeValidationRequest.applicationRaisedDate());

        IncomeValidationStatus status =
            financialCheckForWeeklySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                weeklyThreshold,
                assessmentStartDate,
                incomeValidationRequest.applicationRaisedDate());

        return IncomeValidationResult.builder()
            .status(status)
            .threshold(weeklyThreshold)
            .individuals(Collections.singletonList(checkedIndividual))
            .assessmentStartDate(assessmentStartDate)
            .category(CATEGORY)
            .calculationType(CALCULATION_TYPE)
            .build();
    }

    private static IncomeValidationStatus financialCheckForWeeklySalaried(List<Income> incomes, BigDecimal threshold, LocalDate assessmentStartDate, LocalDate applicationRaisedDate) {
        List<Income> individualIncome = filterIncomesByDates(incomes, assessmentStartDate, applicationRaisedDate);
        List<Income> lastXWeeks = orderByPaymentDate(combineIncomesForSameWeek(individualIncome));

        if (lastXWeeks.size() >= WEEKS_OF_INCOME) {
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

package uk.gov.digital.ho.proving.income.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.NUMBER_OF_MONTHS;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.getAssessmentStartDate;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.*;

@Slf4j
@Service
public class CatASalariedMonthlyIncomeValidator implements IncomeValidator {

    private static final String CALCULATION_TYPE = "Category A Monthly Salary";
    private static final String CATEGORY = "A";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncome();

        List<String> employments = toEmployerNames(applicantIncome.employments());

        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(incomeValidationRequest.dependants());
        BigDecimal monthlyThreshold = thresholdCalculator.getMonthlyThreshold();

        LocalDate assessmentStartDate = getAssessmentStartDate(incomeValidationRequest.applicationRaisedDate());

        IncomeValidationStatus status =
            financialCheckForMonthlySalaried(
                removeDuplicates(applicantIncome.incomeRecord().paye()),
                monthlyThreshold,
                assessmentStartDate,
                incomeValidationRequest.applicationRaisedDate());

        return new IncomeValidationResult(status, monthlyThreshold, Collections.singletonList(checkedIndividual), assessmentStartDate, CATEGORY, CALCULATION_TYPE);
    }

    private IncomeValidationStatus financialCheckForMonthlySalaried(List<Income> incomes, BigDecimal threshold, LocalDate assessmentStartDate, LocalDate applicationRaisedDate) {
        Stream<Income> individualIncome = filterIncomesByDates(incomes, assessmentStartDate, applicationRaisedDate);
        List<Income> lastXMonths = individualIncome.limit(NUMBER_OF_MONTHS).collect(Collectors.toList());
        if (lastXMonths.size() >= NUMBER_OF_MONTHS) {

            // Do we have NUMBER_OF_MONTHS consecutive months with the same employer
            for (int i = 0; i < NUMBER_OF_MONTHS - 1; i++) {
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

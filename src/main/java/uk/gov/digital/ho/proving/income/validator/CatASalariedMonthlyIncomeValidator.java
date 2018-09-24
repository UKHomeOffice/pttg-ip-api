package uk.gov.digital.ho.proving.income.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.MONTHS_OF_INCOME;
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
                applicantIncome.incomeRecord().paye(),
                monthlyThreshold,
                assessmentStartDate,
                incomeValidationRequest.applicationRaisedDate());

        return IncomeValidationResult.builder()
            .status(status)
            .threshold(monthlyThreshold)
            .individuals(Collections.singletonList(checkedIndividual))
            .assessmentStartDate(assessmentStartDate)
            .category(CATEGORY)
            .calculationType(CALCULATION_TYPE)
            .build();
    }

    private IncomeValidationStatus financialCheckForMonthlySalaried(List<Income> incomes, BigDecimal threshold, LocalDate assessmentStartDate, LocalDate applicationRaisedDate) {
        List<Income> individualIncome = filterIncomesByDates(incomes, assessmentStartDate, applicationRaisedDate);
        if (individualIncome.size() < MONTHS_OF_INCOME) {
            return IncomeValidationStatus.NOT_ENOUGH_RECORDS;
        }

        List<Income> lastXMonths = orderByPaymentDate(combineIncomesForSameMonth(individualIncome)).stream()
            .limit(MONTHS_OF_INCOME)
            .collect(Collectors.toList());
        if (lastXMonths.size() < MONTHS_OF_INCOME) {
            return IncomeValidationStatus.NON_CONSECUTIVE_MONTHS;
        }

        // Do we have MONTHS_OF_INCOME consecutive months with the same employer
        for (int i = 0; i < MONTHS_OF_INCOME - 1; i++) {
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
    }
}

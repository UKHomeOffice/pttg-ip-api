package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.getAssessmentStartDate;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.toEmployerNames;

@Service
public class CatAUnsupportedIncomeValidator implements IncomeValidator {

    private static final String CALCULATION_TYPE = "Category A Unsupported Salary Frequency";
    private static final String CATEGORY = "A";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncome();
        FrequencyCalculator.Frequency frequency = FrequencyCalculator.calculate(applicantIncome.incomeRecord());
        List<String> employments = toEmployerNames(applicantIncome.employments());
        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);
        return IncomeValidationResult.builder()
            .status(getStatus(frequency))
            .threshold(BigDecimal.ZERO)
            .individuals(Collections.singletonList(checkedIndividual))
            .assessmentStartDate(getAssessmentStartDate(incomeValidationRequest.applicationRaisedDate()))
            .category(CATEGORY)
            .calculationType(CALCULATION_TYPE)
            .build();
    }

    private IncomeValidationStatus getStatus(FrequencyCalculator.Frequency frequency) {
        if(frequency.equals(FrequencyCalculator.Frequency.CHANGED)) {
            return IncomeValidationStatus.PAY_FREQUENCY_CHANGE;
        }

        return IncomeValidationStatus.UNKNOWN_PAY_FREQUENCY;
    }
}

package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.toEmployerNames;

@Service
public class CatAUnsupportedIncomeValidator implements IncomeValidator {

    private static final Integer ASSESSMENT_START_DAYS_PREVIOUS = 182;
    private static final String CALCULATION_TYPE = "Category A unsupported pay frequency single applicant";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        FrequencyCalculator.Frequency frequency = FrequencyCalculator.calculate(incomeValidationRequest.applicantIncomes().get(0).incomeRecord());
        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncomes().get(0);
        List<String> employments = toEmployerNames(applicantIncome.employments());
        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);
        return new IncomeValidationResult(getStatus(frequency), BigDecimal.ZERO, Arrays.asList(checkedIndividual), incomeValidationRequest.applicationRaisedDate().minusDays(ASSESSMENT_START_DAYS_PREVIOUS), CALCULATION_TYPE);
    }

    private IncomeValidationStatus getStatus(FrequencyCalculator.Frequency frequency) {
        if(frequency.equals(FrequencyCalculator.Frequency.CHANGED)) {
            return IncomeValidationStatus.PAY_FREQUENCY_CHANGE;
        }

        return IncomeValidationStatus.UNKNOWN_PAY_FREQUENCY;
    }
}

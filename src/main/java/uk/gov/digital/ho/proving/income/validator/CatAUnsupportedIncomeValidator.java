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

@Service
public class CatAUnsupportedIncomeValidator implements IncomeValidator {

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        FrequencyCalculator.Frequency frequency = FrequencyCalculator.calculate(incomeValidationRequest.applicantIncomes().get(0).incomeRecord());
        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncomes().get(0);
        List<String> employments = applicantIncome.employments().stream().map(e -> e.employer().name()).collect(Collectors.toList());
        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);
        return new IncomeValidationResult(getStatus(frequency), BigDecimal.ZERO, Arrays.asList(checkedIndividual));
    }

    private IncomeValidationStatus getStatus(FrequencyCalculator.Frequency frequency) {
        if(frequency.equals(FrequencyCalculator.Frequency.CHANGED)) {
            return IncomeValidationStatus.PAY_FREQUENCY_CHANGE;
        }

        return IncomeValidationStatus.UNKNOWN_PAY_FREQUENCY;
    }
}

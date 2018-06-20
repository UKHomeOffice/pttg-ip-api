package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.util.ArrayList;

@Service
public class CatBNonSalariedIncomeValidator implements IncomeValidator {

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        return new IncomeValidationResult(IncomeValidationStatus.CATB_NON_SALARIED_PASSED, new SalariedThresholdCalculator(incomeValidationRequest.dependants()).yearlyThreshold(), new ArrayList());
    }
}

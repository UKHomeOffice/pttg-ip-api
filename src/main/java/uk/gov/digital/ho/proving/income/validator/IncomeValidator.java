package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;

public interface IncomeValidator {

    IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest);
}

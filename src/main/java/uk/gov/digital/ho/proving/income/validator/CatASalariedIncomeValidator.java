package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

public class CatASalariedIncomeValidator implements IncomeValidator {

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        switch (FrequencyCalculator.calculate(incomeValidationRequest.applicantIncomes().get(0).incomeRecord())) {
            case CALENDAR_MONTHLY:
                return new CatASalariedMonthlyIncomeValidator().validate(incomeValidationRequest);
            case WEEKLY:
                return new CatASalariedWeeklyIncomeValidator().validate(incomeValidationRequest);
            case CHANGED:
                return new CatAUnsupportedIncomeValidator(IncomeValidationStatus.PAY_FREQUENCY_CHANGE).validate(incomeValidationRequest);
            default:
                return new CatAUnsupportedIncomeValidator(IncomeValidationStatus.UNKNOWN_PAY_FREQUENCY).validate(incomeValidationRequest);
        }

    }
}

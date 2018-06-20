package uk.gov.digital.ho.proving.income.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;

@Service
public class CatASalariedIncomeValidator implements IncomeValidator {

    @Autowired
    @Qualifier("catASalariedMonthlyIncomeValidator")
    private IncomeValidator catASalariedMonthlyIncomeValidator;

    @Autowired
    @Qualifier("catASalariedWeeklyIncomeValidator")
    private IncomeValidator catASalariedWeeklyIncomeValidator;

    @Autowired
    @Qualifier("catAUnsupportedIncomeValidator")
    private IncomeValidator catAUnsupportedIncomeValidator;

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        switch (FrequencyCalculator.calculate(incomeValidationRequest.applicantIncomes().get(0).incomeRecord())) {
            case CALENDAR_MONTHLY:
                return catASalariedMonthlyIncomeValidator.validate(incomeValidationRequest);
            case WEEKLY:
                return catASalariedWeeklyIncomeValidator.validate(incomeValidationRequest);
            default:
                return catAUnsupportedIncomeValidator.validate(incomeValidationRequest);
        }

    }
}

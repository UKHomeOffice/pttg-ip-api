package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;

import java.time.LocalDate;

@Service
public class CatASalariedIncomeValidator implements ActiveIncomeValidator {

    static final Integer MONTHS_OF_INCOME = 6;

    private IncomeValidator catASalariedMonthlyIncomeValidator;
    private IncomeValidator catAUnsupportedIncomeValidator;

    public CatASalariedIncomeValidator(
        IncomeValidator catASalariedMonthlyIncomeValidator,
        IncomeValidator catAUnsupportedIncomeValidator
    ) {
        this.catASalariedMonthlyIncomeValidator = catASalariedMonthlyIncomeValidator;
        this.catAUnsupportedIncomeValidator = catAUnsupportedIncomeValidator;
    }

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        switch (FrequencyCalculator.calculate(incomeValidationRequest.applicantIncome().incomeRecord())) {
            case CALENDAR_MONTHLY:
            case WEEKLY:
                return catASalariedMonthlyIncomeValidator.validate(incomeValidationRequest);
            default:
                return catAUnsupportedIncomeValidator.validate(incomeValidationRequest);
        }

    }

    static LocalDate getAssessmentStartDate(final LocalDate applicationRaisedDate) {
        return applicationRaisedDate.minusMonths(MONTHS_OF_INCOME);
    }
}

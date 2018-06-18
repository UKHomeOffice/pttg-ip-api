package uk.gov.digital.ho.proving.income.calculation;

import uk.gov.digital.ho.proving.income.api.domain.FinancialCheckValues;

public class CatASalariedCalculator implements Calculator {

    @Override
    public CalculationResult calculate(CalculationRequest calculationRequest) {

        switch (FrequencyCalculator.calculate(calculationRequest.applicantIncomes().get(0).incomeRecord())) {
            case CALENDAR_MONTHLY:
                return new CatASalariedMonthlyCalculator().calculate(calculationRequest);
            case WEEKLY:
                return new CatASalariedWeeklyCalculator().calculate(calculationRequest);
            case CHANGED:
                return new CatAUnsupportedCalculator(FinancialCheckValues.PAY_FREQUENCY_CHANGE).calculate(calculationRequest);
            default:
                return new CatAUnsupportedCalculator(FinancialCheckValues.UNKNOWN_PAY_FREQUENCY).calculate(calculationRequest);
        }

    }
}

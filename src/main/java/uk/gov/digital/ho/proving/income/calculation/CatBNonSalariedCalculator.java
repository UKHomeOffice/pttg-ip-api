package uk.gov.digital.ho.proving.income.calculation;

import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.FinancialCheckValues;

import java.util.ArrayList;

public class    CatBNonSalariedCalculator implements Calculator {

    @Override
    public CalculationResult calculate(CalculationRequest calculationRequest) {
        return new CalculationResult(FinancialCheckValues.CATB_NON_SALARIED_PASSED, new SalariedThresholdCalculator(calculationRequest.dependants()).yearlyThreshold(), new ArrayList(), true);
    }
}

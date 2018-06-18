package uk.gov.digital.ho.proving.income.calculation;

public enum CalculationType {
    CATEGORY_B_NON_SALARIED(new CatBNonSalariedCalculator());

    private Calculator calculator;

    CalculationType(Calculator calculator) {
        this.calculator = calculator;
    }

    public Calculator getCalculator() {
        return calculator;
    }
}

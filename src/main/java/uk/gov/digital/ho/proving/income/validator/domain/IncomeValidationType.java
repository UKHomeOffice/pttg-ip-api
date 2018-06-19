package uk.gov.digital.ho.proving.income.validator.domain;

import uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator;
import uk.gov.digital.ho.proving.income.validator.CatBNonSalariedIncomeValidator;
import uk.gov.digital.ho.proving.income.validator.IncomeValidator;

public enum IncomeValidationType {

    CATEGORY_A_SALARIED(new CatASalariedIncomeValidator()),
    CATEGORY_B_NON_SALARIED(new CatBNonSalariedIncomeValidator());

    private IncomeValidator incomeValidator;

    IncomeValidationType(IncomeValidator incomeValidator) {
        this.incomeValidator = incomeValidator;
    }

    public IncomeValidator calculator() {
        return incomeValidator;
    }
}

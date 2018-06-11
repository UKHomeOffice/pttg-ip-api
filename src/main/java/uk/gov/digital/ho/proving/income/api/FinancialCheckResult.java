package uk.gov.digital.ho.proving.income.api;


import java.math.BigDecimal;
import java.util.List;

public class FinancialCheckResult {

    private FinancialCheckValues financialCheckValue;
    private BigDecimal threshold;
    private List<CheckedIndividual> individuals;

    public FinancialCheckResult(FinancialCheckValues financialCheckValue, BigDecimal threshold, List<CheckedIndividual> individuals) {
        this.financialCheckValue = financialCheckValue;
        this.threshold = threshold;
        this.individuals = individuals;
    }

    public FinancialCheckValues getFinancialCheckValue() {
        return financialCheckValue;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public List<CheckedIndividual> getIndividuals() {
        return individuals;
    }

    @Override
    public String toString() {
        return "FinancialCheckResult{" +
            "financialCheckValue=" + financialCheckValue +
            ", threshold=" + threshold +
            ", individuals=" + individuals +
            '}';
    }
}

package uk.gov.digital.ho.proving.income.api;


import java.math.BigDecimal;

public class FinancialCheckResult {

    private FinancialCheckValues financialCheckValue;
    private BigDecimal threshold;

    public FinancialCheckResult(FinancialCheckValues financialCheckValue, BigDecimal threshold) {
        this.financialCheckValue = financialCheckValue;
        this.threshold = threshold;
    }

    public FinancialCheckValues getFinancialCheckValue() {
        return financialCheckValue;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "FinancialCheckResult{" +
            "financialCheckValue=" + financialCheckValue +
            ", threshold=" + threshold +
            '}';
    }
}

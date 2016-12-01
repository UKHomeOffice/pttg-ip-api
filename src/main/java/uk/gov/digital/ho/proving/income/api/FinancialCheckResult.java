package uk.gov.digital.ho.proving.income.api;


import java.math.BigDecimal;
import java.util.List;

public class FinancialCheckResult {

    private FinancialCheckValues financialCheckValue;
    private BigDecimal threshold;
    private List<String> employers;

    public FinancialCheckResult(FinancialCheckValues financialCheckValue, BigDecimal threshold, List<String> employers) {
        this.financialCheckValue = financialCheckValue;
        this.threshold = threshold;
        this.employers = employers;
    }

    public FinancialCheckValues getFinancialCheckValue() {
        return financialCheckValue;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public List<String> getEmployers() {
        return employers;
    }

    @Override
    public String toString() {
        return "FinancialCheckResult{" +
            "financialCheckValue=" + financialCheckValue +
            ", threshold=" + threshold +
            ", employers=" + employers +
            '}';
    }
}

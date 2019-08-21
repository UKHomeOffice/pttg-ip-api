package uk.gov.digital.ho.proving.income.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class IncomeThresholdCalculator {

    private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);
    private static final BigDecimal WEEKS_PER_YEAR = BigDecimal.valueOf(52);

    private final BigDecimal baseThreshold;
    private final BigDecimal oneDependantThreshold;
    private final BigDecimal remainingDependantsIncrement;

    public IncomeThresholdCalculator(
        @Value("${threshold.yearly.base}") BigDecimal baseThreshold,
        @Value("${threshold.yearly.oneDependant}") BigDecimal oneDependantThreshold,
        @Value("${threshold.yearly.remainingDependantsIncrement}") BigDecimal remainingDependantsIncrement
    ) {
        this.baseThreshold = baseThreshold;
        this.oneDependantThreshold = oneDependantThreshold;
        this.remainingDependantsIncrement = remainingDependantsIncrement;
    }

    public BigDecimal yearlyThreshold(int dependants) {
        if (dependants == 0) {
            return baseThreshold;
        }
       return oneDependantThreshold.add(remainingDependantsIncrement.multiply(BigDecimal.valueOf(dependants - 1)));
    }

    public BigDecimal monthlyThreshold(int dependants) {
        return yearlyThreshold(dependants).divide(MONTHS_PER_YEAR, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal weeklyThreshold(int dependants) {
        return yearlyThreshold(dependants).divide(WEEKS_PER_YEAR, 2, RoundingMode.HALF_UP);
    }
}

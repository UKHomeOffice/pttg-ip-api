package uk.gov.digital.ho.proving.income.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class AnnualisedAverageCalculator {

    private static final Integer MONTHS_PER_YEAR = 12;

    public static BigDecimal calculate(Map<Integer, BigDecimal> aggregatedMonthlyIncome) {
        if(aggregatedMonthlyIncome.size() == 0) {
            return new BigDecimal("0.00");
        }

        BigDecimal total = aggregatedMonthlyIncome.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return total
            .multiply(BigDecimal.valueOf(MONTHS_PER_YEAR))
            .divide(BigDecimal.valueOf(aggregatedMonthlyIncome.size()), 2, RoundingMode.HALF_UP);
    }
}

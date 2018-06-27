package uk.gov.digital.ho.proving.income.validator;

import java.math.BigDecimal;
import java.util.Map;

public class AnnualisedAverageCalculator {

    public static final int MONTHS_PER_YEAR = 12;

    public static BigDecimal calculate(Map<Integer, BigDecimal> aggregatedMonthlyIncome) {
        if(aggregatedMonthlyIncome.size() == 0) {
            return new BigDecimal("0.00");
        }

        BigDecimal total = aggregatedMonthlyIncome.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthlyAverage = total.divide(BigDecimal.valueOf(aggregatedMonthlyIncome.size()));
        return monthlyAverage.multiply(BigDecimal.valueOf(MONTHS_PER_YEAR));
    }
}

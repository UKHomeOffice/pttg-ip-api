package uk.gov.digital.ho.proving.income.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectedAnnualIncomeCalculator {

    private static final Integer MONTHS_PER_YEAR = 12;

    public static BigDecimal calculate(Map<Integer, BigDecimal> aggregatedMonthlyIncome) {
        if(aggregatedMonthlyIncome.size() == 0) {
            return new BigDecimal("0.00");
        }

        Map<Integer, BigDecimal> sanitisedMonthlyIncomes =
            aggregatedMonthlyIncome.entrySet().stream()
                .filter(monthlyIncome -> isNonZeroIncome(monthlyIncome.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        BigDecimal total =
            sanitisedMonthlyIncomes.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total
            .multiply(BigDecimal.valueOf(MONTHS_PER_YEAR))
            .divide(BigDecimal.valueOf(sanitisedMonthlyIncomes.size()), 2, RoundingMode.HALF_UP);
    }

    private static boolean isNonZeroIncome(BigDecimal income) {
        return income.compareTo(BigDecimal.ZERO) != 0;
    }
}

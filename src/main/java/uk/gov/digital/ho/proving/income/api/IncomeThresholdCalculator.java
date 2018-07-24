package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class IncomeThresholdCalculator {
    private static final BigDecimal TWELVE = new BigDecimal(12);
    private static final BigDecimal FIFTY_TWO = new BigDecimal(52);

    private static final BigDecimal BASE_THRESHOLD = BigDecimal.valueOf(18_600);
    private static final BigDecimal ONE_DEPENDANT_THRESHOLD = BigDecimal.valueOf(22_400);
    private static final BigDecimal REMAINING_DEPENDANT_INCREMENT = BigDecimal.valueOf(2_400);

    private BigDecimal monthlyThreshold;
    private BigDecimal weeklyThreshold;
    private BigDecimal yearlyThreshold;

    public IncomeThresholdCalculator(Integer dependants) {
        if (dependants < 0) {
            throw new IllegalArgumentException(String.format("Number of dependants cannot be less than zero, [%d] given", dependants));
        }

        this.yearlyThreshold = calculateYearlyThreshold(dependants);
        this.monthlyThreshold = yearlyThreshold.divide(TWELVE, 2, BigDecimal.ROUND_HALF_UP);
        this.weeklyThreshold = yearlyThreshold.divide(FIFTY_TWO, 2, BigDecimal.ROUND_HALF_UP);

        log.debug("yearlyThreshold: {}", yearlyThreshold);
        log.debug("monthlyThreshold: {}", monthlyThreshold);
        log.debug("weeklyThreshold: {}", weeklyThreshold);
    }

    private BigDecimal calculateYearlyThreshold(Integer dependants) {
        if (dependants == null || dependants == 0) {
            return BASE_THRESHOLD;
        }

        if (dependants == 1) {
            return ONE_DEPENDANT_THRESHOLD;
        }

        BigDecimal remainingDependants = BigDecimal.valueOf(dependants - 1);
        BigDecimal thresholdIncrement = REMAINING_DEPENDANT_INCREMENT.multiply(remainingDependants);

        return ONE_DEPENDANT_THRESHOLD.add(thresholdIncrement);
    }

    public BigDecimal getMonthlyThreshold() {
        return monthlyThreshold;
    }

    public BigDecimal getWeeklyThreshold() {
        return weeklyThreshold;
    }

    public BigDecimal yearlyThreshold() {
        return yearlyThreshold;
    }
}

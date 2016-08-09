package uk.gov.digital.ho.proving.income.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class SalariedThresholdCalculator {

    private BigDecimal dependants = BigDecimal.ZERO;
    private BigDecimal subsequentDependants = BigDecimal.ZERO;

    private final BigDecimal BASE_THRESHOLD_VALUE = new BigDecimal(18600);
    private final BigDecimal FIRST_DEPENDANT_VALUE = new BigDecimal(3800);
    private final BigDecimal SUBSEQUENT_DEPENDANT_VALUE = new BigDecimal(2400);
    private final BigDecimal MONTHS = new BigDecimal(12);
    private final BigDecimal WEEKS = new BigDecimal(52);

    private BigDecimal monthlyThreshold = BigDecimal.ZERO;
    private BigDecimal weeklyThreshold = BigDecimal.ZERO;
    private BigDecimal yearlyThreshold = BigDecimal.ZERO;

    private static final Logger LOGGER = LoggerFactory.getLogger(SalariedThresholdCalculator.class);

    public SalariedThresholdCalculator(int dependants) {

        if (dependants < 0) {
            throw new IllegalArgumentException("Number of dependants cannot be less than zero.");
        } else {
            this.dependants = new BigDecimal(dependants);
            this.subsequentDependants = new BigDecimal(dependants - 1);
            yearlyThreshold =calcThreshold();
            monthlyThreshold = yearlyThreshold.divide(MONTHS, 2, BigDecimal.ROUND_HALF_UP);
            weeklyThreshold = yearlyThreshold.divide(WEEKS, 2, BigDecimal.ROUND_HALF_UP);
            LOGGER.debug("yearlyThreshold: {}", yearlyThreshold);
            LOGGER.debug("monthlyThreshold: {}", monthlyThreshold);
            LOGGER.debug("weeklyThreshold: {}", weeklyThreshold);
        }
    }

    private BigDecimal calcThreshold() {
        BigDecimal thresholdValue;
        if (dependants.compareTo(BigDecimal.ZERO) == 0) {
            thresholdValue = BASE_THRESHOLD_VALUE;
        } else {
            thresholdValue = (BASE_THRESHOLD_VALUE.add(FIRST_DEPENDANT_VALUE).add(SUBSEQUENT_DEPENDANT_VALUE.multiply(subsequentDependants)));
        }
        return thresholdValue;
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

package uk.gov.digital.ho.proving.income.api;

import java.math.BigDecimal;

public class SelfAssessmentThresholdCalculator {
    private static final BigDecimal BASE_THRESHOLD = BigDecimal.valueOf(18_600);
    private static final BigDecimal ONE_DEPENDANT_THRESHOLD = BigDecimal.valueOf(22_400);
    private static final BigDecimal REMAINING_DEPENDANT_INCREMENT = BigDecimal.valueOf(2_400);

    private Integer dependants;

    public SelfAssessmentThresholdCalculator(Integer dependants) {
        if (dependants < 0) {
            throw new IllegalArgumentException(String.format("Number of dependants cannot be less than zero, %d give", dependants));
        }

        this.dependants = dependants;
    }

    public BigDecimal threshold() {
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
}

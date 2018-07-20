package uk.gov.digital.ho.proving.income.api;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SelfAssessmentThresholdCalculatorTest {

    private static final int[] EXPECTED_THRESHOLDS = {18_600, 22_400, 24_800, 27_200, 29_600, 32_000};

    @Test
    public void shouldReturnExpectedThresholds() {
        for (int i = 0; i < EXPECTED_THRESHOLDS.length; i++) {
            SelfAssessmentThresholdCalculator thresholdCalculator = new SelfAssessmentThresholdCalculator(i);
            BigDecimal actualThreshold = thresholdCalculator.threshold();

            BigDecimal expectedThreshold = BigDecimal.valueOf(EXPECTED_THRESHOLDS[i]);
            assertThat(actualThreshold)
                .withFailMessage("Expected threshold of [%s] for [%d] dependants, got [%s]", expectedThreshold, i, actualThreshold)
                .isEqualTo(expectedThreshold);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDependantsLessThanZero() {
        new SelfAssessmentThresholdCalculator(-1);
    }
}

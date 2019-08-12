package uk.gov.digital.ho.proving.income.api;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IncomeThresholdCalculatorNewTest {

    private static final BigDecimal SOME_BASE_THRESHOLD = BigDecimal.valueOf(18_600);
    private static final BigDecimal SOME_ONE_DEPENDANT_THRESHOLD = BigDecimal.valueOf(22_400);
    private static final BigDecimal SOME_REMAINING_DEPENDANT_INCREMENT = BigDecimal.valueOf(2_400);

    private static final BigDecimal EXPECTED_YEARLY_ZERO_DEPENDANTS = BigDecimal.valueOf(18_600);
    private static final BigDecimal EXPECTED_YEARLY_ONE_DEPENDANTS = BigDecimal.valueOf(22_400);
    private static final BigDecimal EXPECTED_YEARLY_TWO_DEPENDANTS = BigDecimal.valueOf(24_800);
    private static final BigDecimal EXPECTED_YEARLY_FIVE_DEPENDANTS = BigDecimal.valueOf(32_000);

    private final IncomeThresholdCalculatorNew incomeThresholdCalculator =
        new IncomeThresholdCalculatorNew(SOME_BASE_THRESHOLD, SOME_ONE_DEPENDANT_THRESHOLD, SOME_REMAINING_DEPENDANT_INCREMENT);

    @Test
    public void yearlyThreshold_noDependants_baseThreshold() {
        assertThat(incomeThresholdCalculator.yearlyThreshold(0)).isEqualTo(EXPECTED_YEARLY_ZERO_DEPENDANTS);
    }

    @Test
    public void yearlyThreshold_oneDependants_oneDependantThreshold() {
        assertThat(incomeThresholdCalculator.yearlyThreshold(1)).isEqualTo(EXPECTED_YEARLY_ONE_DEPENDANTS);
    }

    @Test
    public void yearlyThreshold_twoDependants_addsIncrement() {
        assertThat(incomeThresholdCalculator.yearlyThreshold(2)).isEqualTo(EXPECTED_YEARLY_TWO_DEPENDANTS);
    }

    @Test
    public void yearlyThreshold_fiveDependants_addsFourIncrements() {
        assertThat(incomeThresholdCalculator.yearlyThreshold(5)).isEqualTo(EXPECTED_YEARLY_FIVE_DEPENDANTS);
    }

    @Test
    public void monthlyThreshold_noDependants_baseThreshold() {
        BigDecimal someMonthlyBaseThreshold = EXPECTED_YEARLY_ZERO_DEPENDANTS.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        assertThat(incomeThresholdCalculator.monthlyThreshold(0)).isEqualTo(someMonthlyBaseThreshold);
    }

    @Test
    public void monthlyThreshold_oneDependants_oneDependantThreshold() {
        BigDecimal someMonthlyDependantThreshold = EXPECTED_YEARLY_ONE_DEPENDANTS.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        assertThat(incomeThresholdCalculator.monthlyThreshold(1)).isEqualTo(someMonthlyDependantThreshold);
    }

    @Test
    public void monthlyThreshold_twoDependants_addsIncrement() {
        BigDecimal someMonthlyDependantThreshold = EXPECTED_YEARLY_TWO_DEPENDANTS.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        assertThat(incomeThresholdCalculator.monthlyThreshold(2)).isEqualTo(someMonthlyDependantThreshold);
    }

    @Test
    public void monthlyThreshold_fiveDependants_addsFourIncrements() {
        BigDecimal someMonthlyDependantThreshold =
            SOME_ONE_DEPENDANT_THRESHOLD
                .add(SOME_REMAINING_DEPENDANT_INCREMENT.multiply(BigDecimal.valueOf(4)))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        assertThat(incomeThresholdCalculator.monthlyThreshold(5)).isEqualTo(someMonthlyDependantThreshold);
    }

    @Test
    public void weeklyThreshold_noDependants_baseThreshold() {
        BigDecimal someWeeklyBaseThreshold = EXPECTED_YEARLY_ZERO_DEPENDANTS.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
        assertThat(incomeThresholdCalculator.weeklyThreshold(0)).isEqualTo(someWeeklyBaseThreshold);
    }

    @Test
    public void weeklyThreshold_oneDependants_oneDependantThreshold() {
        BigDecimal someWeeklyDependantThreshold = EXPECTED_YEARLY_ONE_DEPENDANTS.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
        assertThat(incomeThresholdCalculator.weeklyThreshold(1)).isEqualTo(someWeeklyDependantThreshold);
    }

    @Test
    public void weeklyThreshold_twoDependants_addsIncrement() {
        BigDecimal someWeeklyDependantThreshold = EXPECTED_YEARLY_TWO_DEPENDANTS.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
        assertThat(incomeThresholdCalculator.weeklyThreshold(2)).isEqualTo(someWeeklyDependantThreshold);
    }

    @Test
    public void weeklyThreshold_fiveDependants_addsFourIncrements() {
        BigDecimal someWeeklyDependantThreshold = EXPECTED_YEARLY_FIVE_DEPENDANTS.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
        assertThat(incomeThresholdCalculator.weeklyThreshold(5)).isEqualTo(someWeeklyDependantThreshold);
    }

}

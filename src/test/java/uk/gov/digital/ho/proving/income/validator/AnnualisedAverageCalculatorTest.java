package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnualisedAverageCalculatorTest {

    @Test
    public void thatNoMonthlyIncomeReturnsZeroAnnualIncome() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();

        BigDecimal annualisedAverage = AnnualisedAverageCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(annualisedAverage).isEqualTo(new BigDecimal("0.00"));
        assertThat(annualisedAverage.scale()).isEqualTo(2);
    }

    @Test
    public void thatSingleMonthlyIncomeCalculatesAnnualisedAverage() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("1.00"));

        BigDecimal annualisedAverage = AnnualisedAverageCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(annualisedAverage).isEqualTo(new BigDecimal("12.00"));
        assertThat(annualisedAverage.scale()).isEqualTo(2);
    }

    @Test
    public void thatTwoMonthlyIncomesCalculatesAnnualisedAverage() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201805, new BigDecimal("2.00"));

        BigDecimal annualisedAverage = AnnualisedAverageCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(annualisedAverage).isEqualTo(new BigDecimal("18.00"));
        assertThat(annualisedAverage.scale()).isEqualTo(2);
    }

    @Test
    public void thatSeveralMonthlyIncomesCalculatesAnnualisedAverage() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201809, new BigDecimal("12.00"));
        aggregatedMonthlyIncome.put(201901, new BigDecimal("11.60"));

        BigDecimal annualisedAverage = AnnualisedAverageCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(annualisedAverage).isEqualTo(new BigDecimal("98.40"));
        assertThat(annualisedAverage.scale()).isEqualTo(2);
    }

    @Test
    public void thatNegativeIncomesAreHandled() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("-1.00"));
        aggregatedMonthlyIncome.put(201811, new BigDecimal("1.50"));

        BigDecimal annualisedAverage = AnnualisedAverageCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(annualisedAverage).isEqualTo(new BigDecimal("3.00"));
        assertThat(annualisedAverage.scale()).isEqualTo(2);
    }


}

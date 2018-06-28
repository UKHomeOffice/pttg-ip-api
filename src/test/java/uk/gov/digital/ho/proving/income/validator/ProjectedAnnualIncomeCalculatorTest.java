package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectedAnnualIncomeCalculatorTest {

    @Test
    public void thatNoMonthlyIncomeReturnsZeroAnnualIncome() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();

        BigDecimal projectedYearlyIncome = ProjectedAnnualIncomeCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(projectedYearlyIncome).isEqualTo(new BigDecimal("0.00"));
        assertThat(projectedYearlyIncome.scale()).isEqualTo(2);
    }

    @Test
    public void thatSingleMonthlyIncomeCalculatesProjectedYearlyIncome() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("1.00"));

        BigDecimal projectedYearlyIncome = ProjectedAnnualIncomeCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(projectedYearlyIncome).isEqualTo(new BigDecimal("12.00"));
        assertThat(projectedYearlyIncome.scale()).isEqualTo(2);
    }

    @Test
    public void thatTwoMonthlyIncomesCalculatesProjectedYearlyIncome() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201805, new BigDecimal("2.00"));

        BigDecimal projectedYearlyIncome = ProjectedAnnualIncomeCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(projectedYearlyIncome).isEqualTo(new BigDecimal("18.00"));
        assertThat(projectedYearlyIncome.scale()).isEqualTo(2);
    }

    @Test
    public void thatSeveralMonthlyIncomesCalculatesProjectedYearlyIncome() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201809, new BigDecimal("12.00"));
        aggregatedMonthlyIncome.put(201901, new BigDecimal("11.60"));

        BigDecimal projectedYearlyIncome = ProjectedAnnualIncomeCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(projectedYearlyIncome).isEqualTo(new BigDecimal("98.40"));
        assertThat(projectedYearlyIncome.scale()).isEqualTo(2);
    }

    @Test
    public void thatNegativeIncomesAreHandled() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("-1.00"));
        aggregatedMonthlyIncome.put(201811, new BigDecimal("1.50"));

        BigDecimal projectedYearlyIncome = ProjectedAnnualIncomeCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(projectedYearlyIncome).isEqualTo(new BigDecimal("3.00"));
        assertThat(projectedYearlyIncome.scale()).isEqualTo(2);
    }

    @Test
    public void thatRecurringDecimalIsHandled() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        aggregatedMonthlyIncome.put(201804, new BigDecimal("18589.99"));
        aggregatedMonthlyIncome.put(201803, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201802, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201801, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201712, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201711, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201710, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201709, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201708, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201707, new BigDecimal("1.00"));
        aggregatedMonthlyIncome.put(201706, new BigDecimal("1.00"));

        BigDecimal projectedYearlyIncome = ProjectedAnnualIncomeCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(projectedYearlyIncome).isEqualTo(new BigDecimal("20290.90"));
        assertThat(projectedYearlyIncome.scale()).isEqualTo(2);
    }

    @Test
    public void thatRoundingErrorsAreHandled() {
        Map<Integer, BigDecimal> aggregatedMonthlyIncome = new LinkedHashMap<>();
        IntStream.range(1, 11).forEach(i -> {
            aggregatedMonthlyIncome.put(i, new BigDecimal("1866.67"));
        });
        aggregatedMonthlyIncome.put(12, new BigDecimal("1866.62"));

        BigDecimal projectedYearlyIncome = ProjectedAnnualIncomeCalculator.calculate(aggregatedMonthlyIncome);

        assertThat(projectedYearlyIncome).isEqualTo(new BigDecimal("22399.99"));
        assertThat(projectedYearlyIncome.scale()).isEqualTo(2);
    }

}

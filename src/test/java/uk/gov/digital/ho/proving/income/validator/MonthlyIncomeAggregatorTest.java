package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MonthlyIncomeAggregatorTest {

    @Test
    public void thatNoDataReturnsEmptyList() {
        Map<Integer, BigDecimal> aggregatedIncome = MonthlyIncomeAggregator.aggregateMonthlyIncome(new ArrayList<>());

        assertThat(aggregatedIncome).isNotNull()
            .withFailMessage("The aggregated income should not be null");
        assertThat(aggregatedIncome.size()).isEqualTo(0)
            .withFailMessage("The aggregated income should have zero size");
    }

    @Test
    public void thatSingleIncomeReturnsSingleMonth() {
        ArrayList<Income> incomes = new ArrayList<>();
        incomes.add(getIncome());

        Map<Integer, BigDecimal> aggregatedIncome = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);

        assertThat(aggregatedIncome.size()).isEqualTo(1)
            .withFailMessage("The aggregated income should contain 1 month");
        assertThat(aggregatedIncome.entrySet().contains(getYearAndMonth(LocalDate.now())))
            .withFailMessage("The single income should be the current month");
        assertThat(aggregatedIncome.get(getYearAndMonth(LocalDate.now()))).isEqualTo(new BigDecimal("1.00"))
            .withFailMessage("The income for the current month should contain the correct income value");
    }

    @Test
    public void thatTwoMonthIncomesReturnsTwoMonths() {
        ArrayList<Income> incomes = new ArrayList<>();
        final LocalDate now = LocalDate.now();
        incomes.add(getIncome(now, new BigDecimal("1.00")));
        incomes.add(getIncome(now.minusMonths(1), new BigDecimal("2.00")));

        Map<Integer, BigDecimal> aggregatedIncome = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);

        final Integer thisMonth = getYearAndMonth(now);
        final Integer prevMonth = getYearAndMonth(now.minusMonths(1));

        assertThat(aggregatedIncome.size()).isEqualTo(2)
            .withFailMessage("The aggregated income should contain 2 months");
        assertThat(aggregatedIncome.entrySet().contains(thisMonth))
            .withFailMessage("The incomes should contain the current month");
        assertThat(aggregatedIncome.get(thisMonth)).isEqualTo(new BigDecimal("1.00"))
            .withFailMessage("The incomes for the current month should contain the correct income value");
        assertThat(aggregatedIncome.entrySet().contains(prevMonth))
            .withFailMessage("The incomes should contain the previous month");
        assertThat(aggregatedIncome.get(prevMonth)).isEqualTo(new BigDecimal("2.00"))
            .withFailMessage("The income for the previous month should contain the correct income value");
    }

    @Test
    public void thatTwoMonthIncomesReverseOrderReturnsTwoMonths() {
        ArrayList<Income> incomes = new ArrayList<>();
        final LocalDate now = LocalDate.now();
        incomes.add(getIncome(now.minusMonths(1), new BigDecimal("2.00")));
        incomes.add(getIncome(now, new BigDecimal("1.00")));

        Map<Integer, BigDecimal> aggregatedIncome = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);

        final Integer thisMonth = getYearAndMonth(now);
        final Integer prevMonth = getYearAndMonth(now.minusMonths(1));

        assertThat(aggregatedIncome.size()).isEqualTo(2)
            .withFailMessage("The aggregated income should contain 2 months");
        assertThat(aggregatedIncome.entrySet().contains(thisMonth))
            .withFailMessage("The incomes should contain the current month");
        assertThat(aggregatedIncome.get(thisMonth)).isEqualTo(new BigDecimal("1.00"))
            .withFailMessage("The incomes for the current month should contain the correct income value");
        assertThat(aggregatedIncome.entrySet().contains(prevMonth))
            .withFailMessage("The incomes should contain the previous month");
        assertThat(aggregatedIncome.get(prevMonth)).isEqualTo(new BigDecimal("2.00"))
            .withFailMessage("The income for the previous month should contain the correct income value");
    }

    @Test
    public void thatTwoMonthNonContinuousIncomeReturnsTwoMonths() {
        ArrayList<Income> incomes = new ArrayList<>();
        final LocalDate now = LocalDate.now();
        incomes.add(getIncome(now, new BigDecimal("1.00")));
        incomes.add(getIncome(now.minusMonths(2), new BigDecimal("2.00")));

        Map<Integer, BigDecimal> aggregatedIncome = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);

        final Integer thisMonth = getYearAndMonth(now);
        final Integer prevTwoMonth = getYearAndMonth(now.minusMonths(2));

        assertThat(aggregatedIncome.size()).isEqualTo(2)
            .withFailMessage("The aggregated income should contain 2 months");
        assertThat(aggregatedIncome.entrySet().contains(thisMonth))
            .withFailMessage("The incomes should contain the current month");
        assertThat(aggregatedIncome.get(thisMonth)).isEqualTo(new BigDecimal("1.00"))
            .withFailMessage("The incomes for the current month should contain the correct income value");
        assertThat(aggregatedIncome.entrySet().contains(prevTwoMonth))
            .withFailMessage("The incomes should contain 2 months ago");
        assertThat(aggregatedIncome.get(prevTwoMonth)).isEqualTo(new BigDecimal("2.00"))
            .withFailMessage("The income for 2 months ago should contain the correct income value");
    }

    @Test
    public void thatSameMonthsAreAggregated() {
        ArrayList<Income> incomes = new ArrayList<>();
        final LocalDate now = LocalDate.now();
        incomes.add(getIncome(now, new BigDecimal("1.00")));
        incomes.add(getIncome(now.minusMonths(1), new BigDecimal("2.00")));
        incomes.add(getIncome(now, new BigDecimal("3.00")));

        Map<Integer, BigDecimal> aggregatedIncome = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);

        final Integer thisMonth = getYearAndMonth(now);
        final Integer prevMonth = getYearAndMonth(now.minusMonths(1));

        assertThat(aggregatedIncome.size()).isEqualTo(2)
            .withFailMessage("The aggregated income should contain 2 months");
        assertThat(aggregatedIncome.entrySet().contains(thisMonth))
            .withFailMessage("The incomes should contain the current month");
        assertThat(aggregatedIncome.get(thisMonth)).isEqualTo(new BigDecimal("4.00"))
            .withFailMessage("The incomes for the current month should sum both payments in that month");
        assertThat(aggregatedIncome.entrySet().contains(prevMonth))
            .withFailMessage("The incomes should contain the previous month");
        assertThat(aggregatedIncome.get(prevMonth)).isEqualTo(new BigDecimal("2.00"))
            .withFailMessage("The income for the previous month should contain the correct income value");

    }

    @Test
    public void thatIncomesAreAggregatedRegardlessOfPayeRef() {
        ArrayList<Income> incomes = new ArrayList<>();
        incomes.add(getIncomeWithRef("payeRef1"));
        incomes.add(getIncomeWithRef("payeRef2"));

        Map<Integer, BigDecimal> aggregatedIncome = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);

        final Integer thisMonth = getYearAndMonth(LocalDate.now());

        assertThat(aggregatedIncome.size()).isEqualTo(1)
            .withFailMessage("The aggregated income should contain 1 month");
        assertThat(aggregatedIncome.get(thisMonth)).isEqualTo(new BigDecimal("2.00"))
            .withFailMessage("The aggregated income should include all incomes regardless of paye reference");

    }

    @Test
    public void thatIncomesAreAggregatedRegardlessOfWeekAndMonthNumbers() {
        ArrayList<Income> incomes = new ArrayList<>();
        incomes.add(getIncomeWithWeekNumber(1));
        incomes.add(getIncomeWithWeekNumber(27));
        incomes.add(getIncomeWithMonthNumber(3));
        incomes.add(getIncomeWithMonthNumber(9));

        Map<Integer, BigDecimal> aggregatedIncome = MonthlyIncomeAggregator.aggregateMonthlyIncome(incomes);

        final Integer thisMonth = getYearAndMonth(LocalDate.now());

        assertThat(aggregatedIncome.size()).isEqualTo(1)
            .withFailMessage("The aggregated income should contain 1 month");
        assertThat(aggregatedIncome.get(thisMonth)).isEqualTo(new BigDecimal("4.00"))
            .withFailMessage("The aggregated income should include all incomes regardless of week and month number");

    }

    private Income getIncome() {
        return getIncome(LocalDate.now(), new BigDecimal("1.00"));
    }

    private Income getIncome(LocalDate payDate, BigDecimal payAmount) {
        return new Income(payAmount, payDate, 0, 0, "ref");
    }

    private Income getIncomeWithRef(String ref) {
        return new Income(new BigDecimal("1.00"), LocalDate.now(), 0, 0, ref);
    }

    private Income getIncomeWithWeekNumber(Integer weekNumber) {
        return new Income(new BigDecimal("1.00"), LocalDate.now(), 0, weekNumber, "ref");
    }

    private Income getIncomeWithMonthNumber(Integer monthNumber) {
        return new Income(new BigDecimal("1.00"), LocalDate.now(), 0, monthNumber, "ref");
    }

    private Integer getYearAndMonth(LocalDate date) {
        return date.getYear() * 100 + date.getMonthValue();
    }
}

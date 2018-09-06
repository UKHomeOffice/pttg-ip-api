package uk.gov.digital.ho.proving.income.validator;

import org.assertj.core.util.Lists;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.FrequencyCalculator.Frequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.FrequencyCalculator.Frequency.*;
import static uk.gov.digital.ho.proving.income.validator.FrequencyCalculator.calculate;

public class FrequencyCalculatorTest {

    private final LocalDate someDate = LocalDate.of(2018, 1, 24);

    @Test
    public void shouldReturnMonthlyWhenMultiplePaymentsForOneMonth() {
        List<Income> paye = incomesForMonthNumber(1, 1, 2, 3, 4, 5, 6);
        IncomeRecord incomeRecord = new IncomeRecord(paye, null, null, null);
        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnWeeklyWhenMultiplePaymentsForOneWeek() {
        List<Income> paye = incomesForWeekNumber(50, 51, 51, 52, 53);
        IncomeRecord incomeRecord = new IncomeRecord(paye, null, null, null);
        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }
    // TODO OJR 2018/09/06 We probably need to test similarly that when there are no week/month numbers, having two payments on same date doesn't affect calculation.

    /*
     When monthly number present.
     */

    @Test
    public void shouldReturnMonthlyWhen6SameDateConsecutiveWeeksButWithMonthNumberPresent() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDateWithMonthPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyWhen2SameDateConsectutiveMonthsWithMonthNumberPresent() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(2)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDateWithMonthPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyWhen1MonthWithMonthNumberPresent() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(1)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDateWithMonthPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    /*
    When weekly number present and consecutive up to 56 (yes can be above 52)
     */

    @Test
    public void shouldReturnWeeklyWhen26DifferentDayConsecutiveWeeksButConsecutiveWeekNumbers() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.JANUARY, 2))
            .limit(26)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeeklyWhen52DuplicatedDifferentDayConsecutiveWeeksButConsecutiveWeekNumbers() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.JANUARY, 2))
            .limit(52)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeekly2DifferentDayConsecutiveWeeksWithConsecutiveWeekNumbers() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.JANUARY, 2))
            .limit(2)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeekly1DayWithWeekNumber() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.JANUARY, 2))
            .limit(1)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnFortnightlyWhen13RandomDaysButWeekNumbersForEveryOtherWeek() {
        List<LocalDate> dates = generateFortnightlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(13)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(FORTNIGHTLY);
    }

    @Test
    public void shouldReturn4WeeklyWhen6RandomDaysButWeekNumbersEvery4thWeeks() {
        List<LocalDate> dates = generateFourWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(FOUR_WEEKLY);
    }

    @Test
    public void shouldReturnChangedWhenAMixtureOfWeekAndMonthNumbers() {
        Income monthlyPayment = new Income(BigDecimal.ONE, LocalDate.of(2017, Month.DECEMBER, 1), 9, null, "some employer ref");
        Income weeklyPayment = new Income(BigDecimal.ONE, LocalDate.of(2017, Month.DECEMBER, 7), null, 41, "some employer ref");

        IncomeRecord incomeRecord = new IncomeRecord(Arrays.asList(monthlyPayment, weeklyPayment), Collections.emptyList(), Collections.emptyList(), null);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CHANGED);

    }

    /*
    When no weekly or monthly number present.
     */

    @Test
    public void shouldReturnMonthlyWhen6SameDateConsecutiveMonths() {
        List<LocalDate> dates = generateCalendarMonthlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDate(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyWhen6SimilarDateConsecutiveMonths() {
        List<LocalDate> dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), date -> date.minusMonths(1).plusDays(randomBetween(-1, 1)))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDate(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnWeeklyWhen26SameDayConsecutiveWeeks() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(26)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDate(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeeklyWhen26SimilarDayConsecutiveWeeks() {
        List<LocalDate> dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), date -> date.minusDays(7).plusDays(randomBetween(-1, 1)))
            .limit(26)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDate(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnMonthlyWhen3SameDateConsecutiveMonths() {
        List<LocalDate> dates = generateCalendarMonthlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(3)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDate(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnWeeklyWhen13SameDayConsecutiveWeeks() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(13)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDate(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnFortnightlyWhen13SameDayEveryTwoWeeks() {
        List<LocalDate> dates = generateFortnightlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(13)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDate(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(FORTNIGHTLY);
    }

    @Test
    public void shouldReturn4WeeklyWhen6SameDayEveryFourWeeks() {
        List<LocalDate> dates = generateFourWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDate(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(FOUR_WEEKLY);
    }

    @Test
    public void shouldReturnMonthlyWhenNoPayments() {
        IncomeRecord incomeRecord = incomeRecordForDate(Lists.emptyList());

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyWHenOnlyOnePayment() {
        IncomeRecord incomeRecord = incomeRecordForDate(Collections.singletonList(LocalDate.of(2017, Month.DECEMBER, 1)));

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    private List<Income> incomesForMonthNumber(int... monthNumbers) {
        final List<Income> incomes = new ArrayList<>();
        for (int monthNumber : monthNumbers) {
            incomes.add(new Income(BigDecimal.TEN, someDate.plusMonths(monthNumber), monthNumber, null, "any employer ref"));
        }
        return incomes;
    }

    private List<Income> incomesForWeekNumber(int... weekNumbers) {
        final List<Income> incomes = new ArrayList<>();
        for (int weekNumber : weekNumbers) {
            incomes.add(new Income(BigDecimal.TEN, someDate.plusWeeks(weekNumber), null, weekNumber, "any employer ref"));
        }
        return incomes;
    }

    private Stream<LocalDate> generateWeeklyDatesFrom(LocalDate startDate) {
        return Stream.iterate(startDate, date -> date.minusDays(7));
    }

    private Stream<LocalDate> generateFortnightlyDatesFrom(LocalDate startDate) {
        return Stream.iterate(startDate, date -> date.minusDays(14));
    }

    private Stream<LocalDate> generateFourWeeklyDatesFrom(LocalDate startDate) {
        return Stream.iterate(startDate, date -> date.minusDays(28));
    }

    private Stream<LocalDate> generateCalendarMonthlyDatesFrom(LocalDate startDate) {
        return Stream.iterate(startDate, date -> date.minusMonths(1));
    }

    private IncomeRecord incomeRecordForDate(List<LocalDate> dates) {
        List<Income> paye = dates.stream()
            .map(date -> new Income(BigDecimal.ONE, date, null, null, "some employer ref"))
            .collect(Collectors.toList());

        return new IncomeRecord(paye, Lists.emptyList(), Lists.emptyList(), null);
    }

    private IncomeRecord incomeRecordForDateWithMonthPayNumber(List<LocalDate> dates) {
        List<Income> paye = dates.stream()
            .map(date -> new Income(BigDecimal.ONE, date, mapToMonthNumber(date), null, "some employer ref"))
            .collect(Collectors.toList());
        return new IncomeRecord(paye, Lists.emptyList(), Lists.emptyList(), null);
    }

    private IncomeRecord incomeRecordForRandomisedDateWithWeekPayNumber(List<LocalDate> dates) {
        List<Income> paye = dates.stream()
            .map(date -> new Income(BigDecimal.ONE, date.plusDays(randomBetween(-7, 7)), null, mapWeekToNumber(date), "ref")).collect(Collectors.toList());
        return new IncomeRecord(paye, Lists.emptyList(), Lists.emptyList(), null);
    }

    private Integer mapToMonthNumber(LocalDate date) {
        int taxMonth = date.getMonthValue() - 3;
        return taxMonth > 0 ? taxMonth : 12 + taxMonth;
    }

    private Integer mapWeekToNumber(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear()) - 13;

        return weekNumber > 0 ? weekNumber : 52 + weekNumber;
    }

    private long randomBetween(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}

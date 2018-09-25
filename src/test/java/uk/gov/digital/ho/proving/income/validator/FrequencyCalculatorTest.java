package uk.gov.digital.ho.proving.income.validator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.frequencycalculator.Frequency;
import uk.gov.digital.ho.proving.income.validator.frequencycalculator.FrequencyCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static uk.gov.digital.ho.proving.income.validator.frequencycalculator.Frequency.*;
import static uk.gov.digital.ho.proving.income.validator.frequencycalculator.FrequencyCalculator.calculate;
import static uk.gov.digital.ho.proving.income.validator.frequencycalculator.FrequencyCalculator.calculateByPaymentNumbers;


@RunWith(MockitoJUnitRunner.class)
public class FrequencyCalculatorTest {

    private final LocalDate someDate = LocalDate.of(2018, 1, 24);
    private final BigDecimal someAmount= BigDecimal.TEN;

    @Mock private Appender<ILoggingEvent> mockAppender;

    @Before
    public void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(FrequencyCalculator.class);
        logger.setLevel(Level.INFO);
        logger.addAppender(mockAppender);
    }

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

    @Test
    public void shouldLogWhenCalculateByPaymentNumbersCalled() {
        calculateByPaymentNumbers(mock(IncomeRecord.class));

        verifyLogMessage("Calculating frequency by payment numbers");
    }

    @Test
    public void shouldLogResultWhenCalculateByPaymentNumbersReturnsMonthly0payments() {
        IncomeRecord incomeRecord = new IncomeRecord(emptyList(), null, null, null);

        calculateByPaymentNumbers(incomeRecord);

        verifyLogMessage("Frequency calculated by payment numbers as CALENDAR_MONTHLY");
    }

    @Test
    public void shouldLogResultWhenCalculateByPaymentNumbersReturnsMonthly1Payment() {
        Income someIncome = new Income(someAmount, someDate, null, null, "some employer ref");
        IncomeRecord incomeRecord = new IncomeRecord(singletonList(someIncome), emptyList(), emptyList(), null);

        calculateByPaymentNumbers(incomeRecord);

        verifyLogMessage("Frequency calculated by payment numbers as CALENDAR_MONTHLY");
    }

    @Test
    public void shouldLogResultWhenCalculateByPaymentNumbersReturnsUnknownBecauseAverageIntervalIs5Days() {
        Income income1 = new Income(someAmount, someDate, null, null, "some employer ref");
        Income income2 = new Income(someAmount, someDate.plusDays(5), null, null, "some employer ref");

        IncomeRecord incomeRecord = new IncomeRecord(asList(income1, income2), emptyList(), emptyList(), null);

        calculateByPaymentNumbers(incomeRecord);

        verifyLogMessage("Frequency calculated by payment numbers as UNKNOWN");
    }

    @Test
    public void shouldLogResultWhenCalculateByPaymentNumbersReturnsWeeklyBecauseAverageIntervalIs7Days() {
        Income income1 = new Income(someAmount, someDate, null, null, "some employer ref");
        Income income2 = new Income(someAmount, someDate.plusDays(7), null, null, "some employer ref");

        IncomeRecord incomeRecord = new IncomeRecord(asList(income1, income2), emptyList(), emptyList(), null);

        calculateByPaymentNumbers(incomeRecord);

        verifyLogMessage("Frequency calculated by payment numbers as WEEKLY");
    }

    @Test
    public void shouldLogResultWhenCalculateByPaymentNumbersReturnsFortnightlyBecauseAverageIntervalIs14Days() {
        Income income1 = new Income(someAmount, someDate, null, null, "some employer ref");
        Income income2 = new Income(someAmount, someDate.plusDays(14), null, null, "some employer ref");

        IncomeRecord incomeRecord = new IncomeRecord(asList(income1, income2), emptyList(), emptyList(), null);

        calculateByPaymentNumbers(incomeRecord);

        verifyLogMessage("Frequency calculated by payment numbers as FORTNIGHTLY");
    }

    @Test
    public void shouldLogResultWhenCalculateByPaymentNumbersReturnsFourWeeklyBecauseAverageIntervalIs28Days() {
        Income income1 = new Income(someAmount, someDate, null, null, "some employer ref");
        Income income2 = new Income(someAmount, someDate.plusDays(28), null, null, "some employer ref");

        IncomeRecord incomeRecord = new IncomeRecord(asList(income1, income2), emptyList(), emptyList(), null);

        calculateByPaymentNumbers(incomeRecord);

        verifyLogMessage("Frequency calculated by payment numbers as FOUR_WEEKLY");
    }

    @Test
    public void shouldLogResultWhenCalculateByPaymentNumbersReturnsCalendarMonthlyBecauseAverageIntervalIs31Days() {
        Income income1 = new Income(someAmount, someDate, null, null, "some employer ref");
        Income income2 = new Income(someAmount, someDate.plusDays(31), null, null, "some employer ref");

        IncomeRecord incomeRecord = new IncomeRecord(asList(income1, income2), emptyList(), emptyList(), null);

        calculateByPaymentNumbers(incomeRecord);

        verifyLogMessage("Frequency calculated by payment numbers as CALENDAR_MONTHLY");
    }

    @Test
    public void shouldLogResultWhenCalculateByPaymentNumbersReturnsUnknownBecauseAverageIntervalIs32Days() {
        Income income1 = new Income(someAmount, someDate, null, null, "some employer ref");
        Income income2 = new Income(someAmount, someDate.plusDays(32), null, null, "some employer ref");

        IncomeRecord incomeRecord = new IncomeRecord(asList(income1, income2), emptyList(), emptyList(), null);

        calculateByPaymentNumbers(incomeRecord);

        verifyLogMessage("Frequency calculated by payment numbers as UNKNOWN");
    }

    /*
     When monthly number present.
     */

    @Test
    public void shouldReturnMonthlyWhen6SameDateConsecutiveWeeksButWithMonthNumberPresent() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDatesWithMonthPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyWhen2SameDateConsectutiveMonthsWithMonthNumberPresent() {
        List<LocalDate> dates = generateCalendarMonthlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(2)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDatesWithMonthPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyWhen1MonthWithMonthNumberPresent() {
        List<LocalDate> dates = generateCalendarMonthlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(1)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDatesWithMonthPayNumber(dates);

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

        IncomeRecord incomeRecord = incomeRecordForRandomisedDatesWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeeklyWhen52DuplicatedDifferentDayConsecutiveWeeksButConsecutiveWeekNumbers() {
        List<LocalDate> dates = Stream.iterate(LocalDate.of(2017, Month.JANUARY, 2), date -> date.minusDays(2))
            .limit(52)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDatesWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeekly2DifferentDayConsecutiveWeeksWithConsecutiveWeekNumbers() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.JANUARY, 2))
            .limit(2)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDatesWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeekly1DayWithWeekNumber() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.JANUARY, 2))
            .limit(1)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDatesWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnFortnightlyWhen13RandomDaysButWeekNumbersForEveryOtherWeek() {
        List<LocalDate> dates = generateFortnightlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(13)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDatesWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(FORTNIGHTLY);
    }

    @Test
    public void shouldReturn4WeeklyWhen6RandomDaysButWeekNumbersEvery4thWeeks() {
        List<LocalDate> dates = generateFourWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForRandomisedDatesWithWeekPayNumber(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(FOUR_WEEKLY);
    }

    @Test
    public void shouldReturnChangedWhenAMixtureOfWeekAndMonthNumbers() {
        Income monthlyPayment = new Income(BigDecimal.ONE, LocalDate.of(2017, Month.DECEMBER, 1), 9, null, "some employer ref");
        Income weeklyPayment = new Income(BigDecimal.ONE, LocalDate.of(2017, Month.DECEMBER, 7), null, 41, "some employer ref");

        IncomeRecord incomeRecord = new IncomeRecord(asList(monthlyPayment, weeklyPayment), emptyList(), emptyList(), null);

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

        IncomeRecord incomeRecord = incomeRecordForDates(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyWhen6SimilarDateConsecutiveMonths() {
        List<LocalDate> dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), date -> date.minusMonths(1).plusDays(randomBetween(-1, 1)))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDates(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnWeeklyWhen26SameDayConsecutiveWeeks() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(26)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDates(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeeklyWhen26SimilarDayConsecutiveWeeks() {
        List<LocalDate> dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), date -> date.minusDays(7).plusDays(randomBetween(-1, 1)))
            .limit(26)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDates(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnMonthlyWhen3SameDateConsecutiveMonths() {
        List<LocalDate> dates = generateCalendarMonthlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(3)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDates(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnWeeklyWhen13SameDayConsecutiveWeeks() {
        List<LocalDate> dates = generateWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(13)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDates(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnFortnightlyWhen13SameDayEveryTwoWeeks() {
        List<LocalDate> dates = generateFortnightlyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(13)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDates(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(FORTNIGHTLY);
    }

    @Test
    public void shouldReturn4WeeklyWhen6SameDayEveryFourWeeks() {
        List<LocalDate> dates = generateFourWeeklyDatesFrom(LocalDate.of(2017, Month.DECEMBER, 1))
            .limit(6)
            .collect(Collectors.toList());

        IncomeRecord incomeRecord = incomeRecordForDates(dates);

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(FOUR_WEEKLY);
    }

    @Test
    public void shouldReturnMonthlyWhenNoPayments() {
        IncomeRecord incomeRecord = incomeRecordForDates(emptyList());

        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyWhenOnlyOnePayment() {
        IncomeRecord incomeRecord = incomeRecordForDates(singletonList(LocalDate.of(2017, Month.DECEMBER, 1)));

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

    private IncomeRecord incomeRecordForDates(List<LocalDate> dates) {
        List<Income> paye = dates.stream()
            .map(date -> new Income(BigDecimal.ONE, date, null, null, "some employer ref"))
            .collect(Collectors.toList());

        return new IncomeRecord(paye, emptyList(), emptyList(), null);
    }

    private IncomeRecord incomeRecordForDatesWithMonthPayNumber(List<LocalDate> dates) {
        List<Income> paye = dates.stream()
            .map(date -> new Income(BigDecimal.ONE, date, mapToMonthNumber(date), null, "some employer ref"))
            .collect(Collectors.toList());
        return new IncomeRecord(paye, emptyList(), emptyList(), null);
    }

    private IncomeRecord incomeRecordForRandomisedDatesWithWeekPayNumber(List<LocalDate> dates) {
        List<Income> paye = dates.stream()
            .map(date -> new Income(BigDecimal.ONE, date.plusDays(randomBetween(-7, 7)), null, mapWeekToNumber(date), "ref")).collect(Collectors.toList());
        return new IncomeRecord(paye, emptyList(), emptyList(), null);
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

    private void createConsecutiveDuplicates(List<Income> list) {
        list.add(3, list.get(3));
    }

    private void createNonConsecutiveDuplicates(List<Income> list) {
        list.add(0, list.get(4));
    }

    private void verifyLogMessage(final String message) {
        ArgumentCaptor<ILoggingEvent> captor = ArgumentCaptor.forClass(ILoggingEvent.class);
        verify(mockAppender, times(2)).doAppend(captor.capture());

        List<ILoggingEvent> loggingEvents = captor.getAllValues();
        for (ILoggingEvent loggingEvent : loggingEvents) {
            LoggingEvent logEvent = (LoggingEvent) loggingEvent;
            if (logEvent.getMessage().equals(message) && logEvent.getLevel().equals(Level.INFO)) {
                return;
            }
        }
        fail(String.format("Failed to find log with message=\"%s\" and level=INFO", message));
    }
}

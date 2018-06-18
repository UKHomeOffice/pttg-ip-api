package uk.gov.digital.ho.proving.income.api

import spock.lang.Specification
import uk.gov.digital.ho.proving.income.domain.hmrc.Income
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord

import java.time.LocalDate
import java.time.Month
import java.time.temporal.WeekFields
import java.util.function.UnaryOperator
import java.util.stream.Stream

import static java.util.Collections.emptyList
import static java.util.Collections.singletonList
import static uk.gov.digital.ho.proving.income.calculation.FrequencyCalculator.Frequency.*
import static uk.gov.digital.ho.proving.income.calculation.FrequencyCalculator.calculate

/**
 *
 */
class FrequencyCalculatorSpec extends Specification {
    // when monthly number present
    def "should return monthly when 6 same date consecutive weeks but with month number present"() {

        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusDays(-7)} as UnaryOperator<LocalDate>).
            limit(6).
            collect()

        def incomeRecord = incomeRecordForDateWithMonthPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CALENDAR_MONTHLY
    }

    def "should return monthly when 2 same date consecutive months but with month number present"() {

        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusMonths(-1)} as UnaryOperator<LocalDate>).
            limit(2).
            collect()

        def incomeRecord = incomeRecordForDateWithMonthPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CALENDAR_MONTHLY
    }

    def "should return monthly when 1 month but with month number present"() {

        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusMonths(-1)} as UnaryOperator<LocalDate>).
            limit(1).
            collect()

        def incomeRecord = incomeRecordForDateWithMonthPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CALENDAR_MONTHLY
    }

    // when weekly number present and consecutive up to 56 (yes can be 4 above 52)
    def "should return weekly when 26 different day consecutive weeks but with consecutive week number numbers"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.JANUARY, 2), { date -> date.plusDays(-7)} as UnaryOperator<LocalDate>).
            limit(26).
            collect()

        def incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == WEEKLY
    }

    def "should return weekly when 52 duplicated different day consecutive weeks but with consecutive week number numbers"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.JANUARY, 2), { date -> date.plusDays(-2)} as UnaryOperator<LocalDate>).
            limit(52).
            collect()

        def incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == WEEKLY
    }

    def "should return weekly when 2 different day consecutive weeks but with consecutive week number numbers"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.JANUARY, 2), { date -> date.plusDays(-7)} as UnaryOperator<LocalDate>).
            limit(2).
            collect()

        def incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == WEEKLY
    }

    def "should return weekly when 1 day but with a week number number"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.JANUARY, 2), { date -> date.plusDays(-7)} as UnaryOperator<LocalDate>).
            limit(1).
            collect()

        def incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == WEEKLY
    }


    def "should return fortnightly when 13 random days but week numbers for every other week"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusDays(-14)} as UnaryOperator<LocalDate>).
            limit(13).
            collect()

        def incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == FORTNIGHTLY
    }

    def "should return 4 weekly when 6 random days but week numbers for every 4th weeks"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusDays(-28)} as UnaryOperator<LocalDate>).
            limit(6).
            collect()

        def incomeRecord = incomeRecordForRandomisedDateWithWeekPayNumber(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == FOUR_WEEKLY
    }

    def "should return changed when mixture of week and month numbers"() {
        given:
        def monthlyPayment = new Income(BigDecimal.ONE, LocalDate.of(2017, Month.DECEMBER, 1), 9, null, "ref")
        def weeklyPayment = new Income(BigDecimal.ONE, LocalDate.of(2017, Month.DECEMBER, 7), null, 41, "ref")

        def incomeRecord = new IncomeRecord(
            Arrays.asList(monthlyPayment, weeklyPayment),
            emptyList(),
            emptyList(),
            null)


        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CHANGED
    }


    // when no weekly or monthly number present
    def "should return monthly when 6 same date consecutive months"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusMonths(-1)} as UnaryOperator<LocalDate>).
            limit(6).
            collect()

        def incomeRecord = incomeRecordForDate(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CALENDAR_MONTHLY
    }

    def "should return monthly when 6 similar date consecutive months"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusMonths(-1).plusDays(randomBetween(-1, 1))} as UnaryOperator<LocalDate>).
            limit(6).
            collect()

        def incomeRecord = incomeRecordForDate(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CALENDAR_MONTHLY
    }

    def randomBetween(int min, int max) {
        new Random().nextInt(max-min+1)+min
    }

    def "should return weekly when 26 same day consecutive weeks"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusDays(-7)} as UnaryOperator<LocalDate>).
            limit(26).
            collect()

        def incomeRecord = incomeRecordForDate(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == WEEKLY
    }

    def "should return weekly when 26 similar day consecutive weeks"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusDays(-7).plusDays(randomBetween(-1, 1))} as UnaryOperator<LocalDate>).
            limit(26).
            collect()

        def incomeRecord = incomeRecordForDate(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == WEEKLY
    }

    def "should return monthly when 3 same date consecutive months"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusMonths(-1)} as UnaryOperator<LocalDate>).
            limit(3).
            collect()

        def incomeRecord = incomeRecordForDate(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CALENDAR_MONTHLY
    }

    def "should return weekly when 13 same day consecutive weeks"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusDays(-7)} as UnaryOperator<LocalDate>).
            limit(13).
            collect()

        def incomeRecord = incomeRecordForDate(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == WEEKLY
    }

    def "should return fortnightly when 13 same day every two weeks"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusDays(-14)} as UnaryOperator<LocalDate>).
            limit(13).
            collect()

        def incomeRecord = incomeRecordForDate(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == FORTNIGHTLY
    }

    def "should return 4 weekly when 6 same day every four weeks"() {
        given:
        def dates = Stream.iterate(LocalDate.of(2017, Month.DECEMBER, 1), { date -> date.plusDays(-28)} as UnaryOperator<LocalDate>).
            limit(6).
            collect()

        def incomeRecord = incomeRecordForDate(dates)

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == FOUR_WEEKLY
    }

    def "should return monthly when no payments"() {
        given:
        def incomeRecord = incomeRecordForDate(emptyList())

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CALENDAR_MONTHLY
    }

    def "should return unknown when only one payment"() {
        given:
        def incomeRecord = incomeRecordForDate(singletonList(LocalDate.of(2017, Month.DECEMBER, 1)))

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == CALENDAR_MONTHLY
    }

    def incomeRecordForDate(dates) {
        new IncomeRecord(
            dates.stream().map({ date -> new Income(BigDecimal.ONE, date, null, null, "ref")}).collect(),
            emptyList(),
            emptyList(),
            null)
    }

    def incomeRecordForDateWithMonthPayNumber(dates) {
        new IncomeRecord(
            dates.stream().map({ date -> new Income(BigDecimal.ONE, date, mapMonthToNumber(date), null, "ref")}).collect(),
            emptyList(),
            emptyList(),
            null)
    }

    def mapMonthToNumber(LocalDate date) {
        int taxMonth = date.getMonthValue() - 3;
        if (taxMonth < 1) {
            taxMonth = 12 + taxMonth;
        }

        return taxMonth;
    }
    def incomeRecordForRandomisedDateWithWeekPayNumber(dates) {
        new IncomeRecord(
            dates.stream().map({ date -> new Income(BigDecimal.ONE, date.plusDays(randomBetween(-7, 7)), null, mapWeekToNumber(date), "ref")}).collect(),
            emptyList(),
            emptyList(),
            null)
    }

    def mapWeekToNumber(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear()) - 13;

        if (weekNumber < 1) {
            weekNumber = 52 + weekNumber;
        }

        return weekNumber;
    }
}

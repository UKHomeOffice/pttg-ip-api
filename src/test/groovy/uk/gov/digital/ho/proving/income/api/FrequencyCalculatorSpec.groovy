package uk.gov.digital.ho.proving.income.api

import spock.lang.Specification
import uk.gov.digital.ho.proving.income.domain.hmrc.Income
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord

import java.time.LocalDate
import java.time.Month
import java.util.function.UnaryOperator
import java.util.stream.Stream

import static java.util.Collections.emptyList
import static java.util.Collections.singletonList
import static uk.gov.digital.ho.proving.income.api.FrequencyCalculator.Frequency.*
import static uk.gov.digital.ho.proving.income.api.FrequencyCalculator.calculate

/**
 *
 */
class FrequencyCalculatorSpec extends Specification {
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

    def "should return unknown when no payments"() {
        given:
        def incomeRecord = incomeRecordForDate(emptyList())

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == UNKNOWN
    }

    def "should return unknown when only one payment"() {
        given:
        def incomeRecord = incomeRecordForDate(singletonList(LocalDate.of(2017, Month.DECEMBER, 1)))

        when:
        def frequency = calculate(incomeRecord)

        then:
        frequency == UNKNOWN
    }

    def incomeRecordForDate(dates) {
        new IncomeRecord(
            dates.stream().map({ date -> new Income(BigDecimal.ONE, date, null, null, "ref")}).collect(),
            emptyList())
    }
}

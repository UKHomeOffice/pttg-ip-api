package uk.gov.digital.ho.proving.income.validator.domain;

import org.junit.Test;

import java.time.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class TaxYearTest {
    private static final ZoneId UTC = ZoneId.of("UTC");

    private static void assertStartDateIs(TaxYear taxYear, LocalDate expectedStart, Clock clock) {
        assertThat(taxYear.start())
            .withFailMessage("Given date [%s], expected start of tax year to be [%s] but was [%s]", LocalDateTime.now(clock), expectedStart, taxYear.start())
            .isEqualTo(expectedStart);
    }

    private static void assertEndDateIs(TaxYear taxYear, LocalDate expectedEnd, Clock clock) {
        assertThat(taxYear.end())
            .withFailMessage("Given date [%s], expected end of tax year to be [%s] but was [%s]", LocalDateTime.now(clock), expectedEnd, taxYear.end())
            .isEqualTo(expectedEnd);
    }

    private static void assertStartDateIs(TaxYear taxYear, LocalDate expectedStart, String rawTaxYear) {
        assertThat(taxYear.start())
            .withFailMessage("Given raw input is [%s], expected start of tax year to be [%s] but was [%s]", rawTaxYear, expectedStart, taxYear.start())
            .isEqualTo(expectedStart);
    }

    private static void assertEndDateIs(TaxYear taxYear, LocalDate expectedEnd, String rawTaxYear) {
        assertThat(taxYear.end())
            .withFailMessage("Given raw input is [%s], expected end of tax year to be [%s] but was [%s]", rawTaxYear, expectedEnd, taxYear.end())
            .isEqualTo(expectedEnd);
    }

    private static long getEpochSecondsAtMidday(LocalDate localDate) {
        return localDate.atTime(12, 0).toEpochSecond(ZoneOffset.UTC);
    }

    @Test
    public void shouldReturnCorrectTaxYearWhen6thApril2018() {
        // given
        long epochSeconds = getEpochSecondsAtMidday(LocalDate.of(2018, Month.APRIL, 6));
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        Clock clock = Clock.fixed(instant, UTC);

        // when
        TaxYear taxYear = TaxYear.from(clock);

        // then
        assertStartDateIs(taxYear, LocalDate.of(2018, Month.APRIL, 6), clock);
        assertEndDateIs(taxYear, LocalDate.of(2019, Month.APRIL, 5), clock);
    }

    @Test
    public void shouldReturnCorrectTaxYearWhen5thApril2018() {
        // given
        long epochSeconds = getEpochSecondsAtMidday(LocalDate.of(2018, Month.APRIL, 5));
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        Clock clock = Clock.fixed(instant, UTC);

        // when
        TaxYear taxYear = TaxYear.from(clock);

        // then
        assertStartDateIs(taxYear, LocalDate.of(2017, Month.APRIL, 6), clock);
        assertEndDateIs(taxYear, LocalDate.of(2018, Month.APRIL, 5), clock);
    }

    @Test
    public void shouldReturnCorrectTaxYearWhen1stJanuary2016() {
        // given
        long epochSeconds = getEpochSecondsAtMidday(LocalDate.of(2016, Month.JANUARY, 1));
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        Clock clock = Clock.fixed(instant, UTC);

        // when
        TaxYear taxYear = TaxYear.from(clock);

        // then
        assertStartDateIs(taxYear, LocalDate.of(2015, Month.APRIL, 6), clock);
        assertEndDateIs(taxYear, LocalDate.of(2016, Month.APRIL, 5), clock);
    }

    @Test
    public void shouldReturnCorrectTaxYearWhen1stNovember2017() {
        // given
        long epochSeconds = getEpochSecondsAtMidday(LocalDate.of(2017, Month.NOVEMBER, 1));
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        Clock clock = Clock.fixed(instant, UTC);

        // when
        TaxYear taxYear = TaxYear.from(clock);

        // then
        assertStartDateIs(taxYear, LocalDate.of(2017, Month.APRIL, 6), clock);
        assertEndDateIs(taxYear, LocalDate.of(2018, Month.APRIL, 5), clock);
    }

    @Test
    public void shouldCorrectlyParseTaxYearString201718() {
        // given
        String rawTaxYear = "2017-18";

        // when
        TaxYear taxYear = TaxYear.of(rawTaxYear);

        // then
        assertStartDateIs(taxYear, LocalDate.of(2017, Month.APRIL, 6), rawTaxYear);
        assertEndDateIs(taxYear, LocalDate.of(2018, Month.APRIL, 5), rawTaxYear);
    }

    @Test
    public void shouldCorrectlyParseTaxYearString201516() {
        // given
        String rawTaxYear = "2015-16";

        // when
        TaxYear taxYear = TaxYear.of(rawTaxYear);

        // then
        assertStartDateIs(taxYear, LocalDate.of(2015, Month.APRIL, 6), rawTaxYear);
        assertEndDateIs(taxYear, LocalDate.of(2016, Month.APRIL, 5), rawTaxYear);
    }

    @Test
    public void shouldCalculatePreviousTaxYearCorrectly() {
        // given
        long epochSeconds = getEpochSecondsAtMidday(LocalDate.of(2018, Month.APRIL, 6));
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        Clock clock = Clock.fixed(instant, UTC);

        TaxYear taxYear = TaxYear.from(clock);

        // when
        TaxYear previousTaxYear = taxYear.previousTaxYear();

        // then
        assertThat(taxYear)
            .withFailMessage("Expected method `previousTaxYear` to create new instance")
            .isNotSameAs(previousTaxYear);

        assertThat(previousTaxYear.start()).isEqualTo(LocalDate.of(2017, Month.APRIL, 6));
        assertThat(previousTaxYear.end()).isEqualTo(LocalDate.of(2018, Month.APRIL, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenInvalidTaxYearFormat() {
        TaxYear.of("apples");
    }
}

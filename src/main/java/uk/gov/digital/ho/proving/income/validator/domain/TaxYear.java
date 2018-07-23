package uk.gov.digital.ho.proving.income.validator.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
public class TaxYear {
    private static final Pattern TAX_YEAR_PATTERN = Pattern.compile("^\\d{4}\\s*?-\\d{2,4}$");

    private static final MonthDay TAX_YEAR_START_MONTH_DAY = MonthDay.of(Month.APRIL, 6);
    private static final MonthDay TAX_YEAR_END_MONTH_DAY = MonthDay.of(Month.APRIL, 5);

    private final LocalDate start;
    private final LocalDate end;

    private TaxYear(Year startYear) {
        this.start = startYear.atMonthDay(TAX_YEAR_START_MONTH_DAY);
        this.end = startYear.plusYears(1).atMonthDay(TAX_YEAR_END_MONTH_DAY);
    }

    public static TaxYear from(Clock clock) {
        Year startYear = startYear(clock);
        return new TaxYear(startYear);
    }

    public static TaxYear of(String taxYear) {
        validate(taxYear);

        Year startOfTaxYear = startYear(taxYear);
        return new TaxYear(startOfTaxYear);
    }

    public static void validate(String taxYear) {
        if (isInvalidTaxYear(taxYear)) {
            throw new IllegalArgumentException(String.format("Invalid Tax Year format [%s], expected format [YYYY-YY] or [YYYY-YYYY]", taxYear));
        }
    }

    private static boolean isInvalidTaxYear(String taxYear) {
        Matcher matcher = TAX_YEAR_PATTERN.matcher(taxYear);
        return !matcher.matches();
    }

    private static Year startYear(String taxYear) {
        int startYear = Integer.parseInt(taxYear.substring(0, 4));
        return Year.of(startYear);
    }

    private static Year startYear(Clock clock) {
        LocalDate startOfTaxYear = Year.now(clock).atMonthDay(TAX_YEAR_START_MONTH_DAY);

        LocalDate now = LocalDate.now(clock);
        if (now.isBefore(startOfTaxYear)) {
            startOfTaxYear = startOfTaxYear.minusYears(1);
        }

        return Year.from(startOfTaxYear);
    }

    public TaxYear previousTaxYear() {
        return new TaxYear(start.minusYears(1), end.minusYears(1));
    }
}

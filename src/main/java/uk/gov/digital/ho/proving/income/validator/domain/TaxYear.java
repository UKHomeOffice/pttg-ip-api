package uk.gov.digital.ho.proving.income.validator.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
public class TaxYear {
    private static final Pattern TAX_YEAR_PATTERN = Pattern.compile("^\\d{4}\\s*?-\\d{2,4}$");

    private static final int END_DAY_OF_MONTH = 5;
    private static final int START_DAY_OF_MONTH = 6;

    private final LocalDate start;
    private final LocalDate end;

    private TaxYear(Year startYear) {
        this.start = startOfCurrentTaxYearInCurrentYear(startYear);
        this.end = start.plusYears(1).withDayOfMonth(END_DAY_OF_MONTH);
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
        Matcher matcher = TAX_YEAR_PATTERN.matcher(taxYear);
        if (matcher.matches()) {
            return;
        }
        throw new IllegalArgumentException(String.format("Invalid Tax Year format [%s], expected format [YYYY-YY] or [YYYY-YYYY]", taxYear));
    }

    private static Year startYear(String taxYear) {
        int startYear = Integer.parseInt(taxYear.substring(0, 4));
        return Year.of(startYear);
    }

    private static Year startYear(Clock clock) {
        LocalDate startOfTaxYear = startOfTaxYearInCurrentYear(clock);

        LocalDate now = LocalDate.now(clock);
        if (now.isBefore(startOfTaxYear)) {
            startOfTaxYear = startOfTaxYear.minusYears(1);
        }

        return Year.from(startOfTaxYear);
    }

    private static LocalDate startOfTaxYearInCurrentYear(Clock clock) {
        return startOfCurrentTaxYearInCurrentYear(Year.now(clock));
    }

    private static LocalDate startOfCurrentTaxYearInCurrentYear(Year year) {
        return year.atMonth(Month.APRIL).atDay(START_DAY_OF_MONTH);
    }

    public TaxYear previousTaxYear() {
        return new TaxYear(start.minusYears(1), end.minusYears(1));
    }
}

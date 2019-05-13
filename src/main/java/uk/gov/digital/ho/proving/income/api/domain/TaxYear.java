package uk.gov.digital.ho.proving.income.api.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Accessors(fluent = true)
public class TaxYear {

    private static final Pattern TAX_YEAR_FORMAT_REGEX = Pattern.compile("(?<startYear>\\d{4})/(?<endYear>\\d{4})");

    private final LocalDate startDate;
    private final LocalDate endDate;

    public static TaxYear valueOf(String taxYear) {
        Year[] years = validate(taxYear);

        return new TaxYear(
            years[0].atMonth(Month.APRIL).atDay(6),
            years[1].atMonth(Month.APRIL).atDay(5)
        );
    }

    private static Year[] validate(String taxYear) {
        if (taxYear == null) {
            throw new IllegalArgumentException("Can't create taxYear from null");
        }

        Matcher matcher = TAX_YEAR_FORMAT_REGEX.matcher(taxYear);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("TaxYear must be in format YYYY/YYYY");
        }

        int startYear = Integer.valueOf(matcher.group("startYear"));
        int endYear = Integer.valueOf(matcher.group("endYear"));

        if (endYear - startYear != 1) {
            throw new IllegalArgumentException("Years must be consecutive");
        }

        return new Year[]{Year.of(startYear), Year.of(endYear)};
    }
}

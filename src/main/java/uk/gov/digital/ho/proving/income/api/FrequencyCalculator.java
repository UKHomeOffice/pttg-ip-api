package uk.gov.digital.ho.proving.income.api;

import uk.gov.digital.ho.proving.income.domain.hmrc.Income;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

public class FrequencyCalculator {
    enum Frequency {
        WEEKLY,
        FORTNIGHTLY,
        FOUR_WEEKLY,
        CALENDAR_MONTHLY,
        UNKNOWN
    }
    public static Frequency calculate(IncomeRecord incomeRecord) {
        Optional<LocalDate> max = incomeRecord.getIncome().stream().map(Income::getPaymentDate).max(Comparator.naturalOrder());
        Optional<LocalDate> min = incomeRecord.getIncome().stream().map(Income::getPaymentDate).min(Comparator.naturalOrder());

        if (!max.isPresent() || !min.isPresent()) {
            return Frequency.UNKNOWN;
        }

        long daysInRange =  DAYS.between(min.get(), max.get());
        long numberOfPayments = incomeRecord.getIncome().size();

        if (numberOfPayments < 2) {
            return Frequency.UNKNOWN;
        }

        int averageDaysBetweenPayments = Math.round((float)daysInRange / (float)(numberOfPayments - 1));

        if (averageDaysBetweenPayments < 6) {
            return Frequency.UNKNOWN;
        }
        if (averageDaysBetweenPayments < 8) {
            return Frequency.WEEKLY;
        }
        if (averageDaysBetweenPayments < 15) {
            return Frequency.FORTNIGHTLY;
        }
        if (averageDaysBetweenPayments < 29) {
            return Frequency.FOUR_WEEKLY;
        }
        if (averageDaysBetweenPayments < 32) {
            return Frequency.CALENDAR_MONTHLY;
        }
        return Frequency.UNKNOWN;
    }

}

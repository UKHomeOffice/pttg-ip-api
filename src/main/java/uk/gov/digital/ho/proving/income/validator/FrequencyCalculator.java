package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class FrequencyCalculator {
    enum Frequency {
        WEEKLY,
        FORTNIGHTLY,
        FOUR_WEEKLY,
        CALENDAR_MONTHLY,
        UNKNOWN,
        CHANGED
    }

    private enum NUMBER_TYPE {
        HAS_WEEKLY_NUMBER,
        HAS_MONTHLY_NUMBER,
        HAS_NONE
    }
    public static Frequency calculate(IncomeRecord incomeRecord) {
        if (hasDifferentFrequencies(incomeRecord)) {
            return Frequency.CHANGED;
        }
        Frequency monthFrequency = calculateByMonthNumbers(incomeRecord);

        if (monthFrequency != Frequency.UNKNOWN) {
            return monthFrequency;
        }

        Frequency weekFrequency = calculateByWeekNumbers(incomeRecord);

        if (weekFrequency != Frequency.UNKNOWN) {
            return weekFrequency;
        }
        return calculateByPaymentNumbers(incomeRecord);
    }

    private static boolean hasDifferentFrequencies(IncomeRecord incomeRecord) {
        return numberOfDifferentFrequencyTypes(incomeRecord) > 1;
    }

    private static int numberOfDifferentFrequencyTypes(IncomeRecord incomeRecord) {
        return incomeRecord.paye().stream().map(
            income ->
                hasMonthlyNumber(income) ?
                    NUMBER_TYPE.HAS_MONTHLY_NUMBER :
                    hasWeeklyNumber(income) ?
                        NUMBER_TYPE.HAS_WEEKLY_NUMBER :
                        NUMBER_TYPE.HAS_NONE).
            collect(Collectors.toSet()).size();
    }

    private static boolean hasWeeklyNumber(Income income) {
        return income.weekPayNumber() != null;
    }


    private static Frequency calculateByMonthNumbers(IncomeRecord incomeRecord) {
        if (incomeRecord.paye().stream().allMatch(FrequencyCalculator::hasMonthlyNumber)) {
            return Frequency.CALENDAR_MONTHLY;
        }
        return Frequency.UNKNOWN;
    }

    private static boolean hasMonthlyNumber(Income income) {
        return income.monthPayNumber() != null;
    }

    private static Frequency calculateByWeekNumbers(IncomeRecord incomeRecord) {
        if (incomeRecord.paye().stream().allMatch(FrequencyCalculator::hasWeeklyNumber)) {
            if (hasConsecutiveWeekNumbers(incomeRecord)) {
                return Frequency.WEEKLY;
            }
            if (hasConsecutive2WeekNumbers(incomeRecord)) {
                return Frequency.FORTNIGHTLY;
            }
            if (hasConsecutive4WeekNumbers(incomeRecord)) {
                return Frequency.FOUR_WEEKLY;
            }
        }
        return Frequency.UNKNOWN;
    }

    private static boolean hasConsecutive4WeekNumbers(IncomeRecord incomeRecord) {
        return isDifferenceAlways(uniqueWeekNumbersSorted(incomeRecord), 4);
    }

    private static boolean hasConsecutive2WeekNumbers(IncomeRecord incomeRecord) {
        return isDifferenceAlways(uniqueWeekNumbersSorted(incomeRecord), 2);
    }

    private static boolean hasConsecutiveWeekNumbers(IncomeRecord incomeRecord) {
        return isDifferenceAlways(uniqueWeekNumbersSorted(incomeRecord), 1);
    }

    private static List<Integer> uniqueWeekNumbersSorted(IncomeRecord incomeRecord) {
        return new ArrayList<>(
            incomeRecord.paye().
                stream().
                map(Income::weekPayNumber).
                collect(Collectors.toSet())).
            stream().
            sorted().
            collect(Collectors.toList());
    }

    private static boolean isDifferenceAlways(List<Integer> weekNumbers, int differenceAmount) {
        for (int i = 0; i < weekNumbers.size() - 1; i++) {
            if (weekNumbers.get(i+1) -  weekNumbers.get(i) != differenceAmount) {
                return false;
            }
        }

        return true;
    }

    private static Frequency calculateByPaymentNumbers(IncomeRecord incomeRecord) {
        Optional<LocalDate> max = incomeRecord.paye().stream().map(Income::paymentDate).max(Comparator.naturalOrder());
        Optional<LocalDate> min = incomeRecord.paye().stream().map(Income::paymentDate).min(Comparator.naturalOrder());

        if (!max.isPresent() || !min.isPresent()) {
            return Frequency.CALENDAR_MONTHLY;
        }

        long daysInRange =  DAYS.between(min.get(), max.get());
        long numberOfPayments = incomeRecord.paye().size();

        if (numberOfPayments < 2) {
            return Frequency.CALENDAR_MONTHLY;
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

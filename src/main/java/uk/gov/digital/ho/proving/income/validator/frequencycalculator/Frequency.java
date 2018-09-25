package uk.gov.digital.ho.proving.income.validator.frequencycalculator;

import java.util.Arrays;

public enum Frequency {
    WEEKLY(6, 7),
    FORTNIGHTLY(8, 14),
    FOUR_WEEKLY(15, 28),
    CALENDAR_MONTHLY(29, 31),
    UNKNOWN(Integer.MAX_VALUE, Integer.MAX_VALUE),
    CHANGED(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int minimumNumberOfDaysBetweenPayments;
    private final int maximumNumberOfDaysBetweenPayments;

    Frequency(int minimumNumberOfDaysBetweenPayments, int maximumNumberOfDaysBetweenPayments) {
        this.minimumNumberOfDaysBetweenPayments = minimumNumberOfDaysBetweenPayments;
        this.maximumNumberOfDaysBetweenPayments = maximumNumberOfDaysBetweenPayments;
    }

    public static Frequency frequencyForAverageNumberOfDaysBetweenPayments(int numberOfDaysBetweenPayments) {
        return Arrays.stream(Frequency.values())
            .filter(frequency -> frequency.isPaymentIntervalInRange(numberOfDaysBetweenPayments))
            .findFirst()
            .orElse(UNKNOWN);
    }

    private boolean isPaymentIntervalInRange(int numberOfDaysBetweenPayments) {
        return numberOfDaysBetweenPayments >= minimumNumberOfDaysBetweenPayments && numberOfDaysBetweenPayments <= maximumNumberOfDaysBetweenPayments;
    }
}

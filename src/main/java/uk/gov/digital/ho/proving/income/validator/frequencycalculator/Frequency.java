package uk.gov.digital.ho.proving.income.validator.frequencycalculator;

public enum Frequency {
    WEEKLY,
    FORTNIGHTLY,
    FOUR_WEEKLY,
    CALENDAR_MONTHLY,
    UNKNOWN,
    CHANGED;

    public static Frequency frequencyForAverageNumberOfDaysBetweenPayments(int numberOfDaysBetweenPayments) {
        if (numberOfDaysBetweenPayments < 6) {
            return UNKNOWN;
        }
        if (numberOfDaysBetweenPayments < 8) {
            return WEEKLY;
        }
        if (numberOfDaysBetweenPayments < 15) {
            return FORTNIGHTLY;
        }
        if (numberOfDaysBetweenPayments < 29) {
            return FOUR_WEEKLY;
        }
        if (numberOfDaysBetweenPayments <32) {
            return CALENDAR_MONTHLY;
        }
        return UNKNOWN;
    }
}

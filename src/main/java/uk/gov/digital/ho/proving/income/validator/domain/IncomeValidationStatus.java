package uk.gov.digital.ho.proving.income.validator.domain;

public enum IncomeValidationStatus {
    NON_CONSECUTIVE_MONTHS(false),
    MONTHLY_VALUE_BELOW_THRESHOLD(false),
    MONTHLY_SALARIED_PASSED(true),
    NOT_ENOUGH_RECORDS(false),
    WEEKLY_SALARIED_PASSED(true),
    WEEKLY_VALUE_BELOW_THRESHOLD(false),
    PAY_FREQUENCY_CHANGE(false),
    MULTIPLE_EMPLOYERS(false),
    UNKNOWN_PAY_FREQUENCY(false),
    CATA_NON_SALARIED_PASSED(true),
    CATA_NON_SALARIED_BELOW_THRESHOLD(false),
    CATB_NON_SALARIED_PASSED(true),
    CATB_NON_SALARIED_BELOW_THRESHOLD(false),
    CATB_SALARIED_PASSED(true),
    CATB_SALARIED_BELOW_THRESHOLD(false),
    EMPLOYMENT_CHECK_PASSED(true),
    EMPLOYMENT_CHECK_FAILED(false),
    SELF_ASSESSMENT_ONE_YEAR_PASSED(true),
    SELF_ASSESSMENT_ONE_YEAR_FAILED(false), ;

    private boolean passed;

    IncomeValidationStatus(boolean passed) {
        this.passed = passed;
    }

    public boolean isPassed() {
        return passed;
    }
}

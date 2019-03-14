package uk.gov.digital.ho.proving.income.application;

public enum LogEvent {
    INCOME_PROVING_SERVICE_REQUEST_RECEIVED,
    INCOME_PROVING_SERVICE_RESPONSE_SUCCESS,
    INCOME_PROVING_SERVICE_RESPONSE_ERROR,
    INCOME_PROVING_SERVICE_RESPONSE_NOT_FOUND,
    INCOME_PROVING_AUDIT_FAILURE;

    public static final String EVENT = "event_id";
}

package uk.gov.digital.ho.proving.income.application;

public interface ApplicationExceptions {

    class AuditDataException extends RuntimeException {
        public AuditDataException(Throwable cause) {
            super(cause);
        }

        public AuditDataException(String message) {
            super(message);
        }

        public AuditDataException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}

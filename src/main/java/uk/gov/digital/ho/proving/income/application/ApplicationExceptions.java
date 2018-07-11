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

    class EarningsServiceNoUniqueMatchException extends RuntimeException {
        private String nino;
        public EarningsServiceNoUniqueMatchException(String nino) {
            this.nino = nino;
        }
        public String nino() {
            return nino;
        }
    }

    class InvalidNationalInsuranceNumber extends IllegalArgumentException {
        public InvalidNationalInsuranceNumber(final String message) {
            super(message);
        }
    }
}

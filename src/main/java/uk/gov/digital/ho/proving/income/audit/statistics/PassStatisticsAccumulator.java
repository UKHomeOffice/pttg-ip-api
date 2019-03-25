package uk.gov.digital.ho.proving.income.audit.statistics;

import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;

import java.time.LocalDate;
import java.util.List;

import static uk.gov.digital.ho.proving.income.audit.AuditResultType.PASS;

class PassStatisticsAccumulator {

    private final LocalDate fromDate;
    private final LocalDate toDate;

    private int totalRequests;
    private int passes;
    private int failures;
    private int notFound;
    private int errors;

    PassStatisticsAccumulator(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    void accumulate(List<AuditResultByNino> records) {
        totalRequests++;
        switch (records.get(0).resultType()){

            case PASS:
                passes++;
                break;
            case FAIL:
                failures++;
                break;
            case NOTFOUND:
                notFound++;
                break;
            case ERROR:
                errors++;
                break;
        }

    }

    PassRateStatistics result() {
        return new PassRateStatistics(fromDate, toDate, totalRequests, passes, failures, notFound, errors);
    }
}

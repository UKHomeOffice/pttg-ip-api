package uk.gov.digital.ho.proving.income.audit.statistics;

import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        // TODO OJR EE-16843 Because if any date in range counts as included, filtering here won't work going forward. - filter in result method
        List<AuditResultByNino> recordsInRange = records.stream()
            .filter(this::isInDateRange)
            .collect(Collectors.toList());

        for (AuditResultByNino record : recordsInRange) {
            totalRequests++;
            switch (record.resultType()) {
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
    }

    PassRateStatistics result() {
        return new PassRateStatistics(fromDate, toDate, totalRequests, passes, failures, notFound, errors);
    }

    private boolean isInDateRange(AuditResultByNino result) {
        return !result.date().isBefore(fromDate) && !result.date().isAfter(toDate);
    }
}

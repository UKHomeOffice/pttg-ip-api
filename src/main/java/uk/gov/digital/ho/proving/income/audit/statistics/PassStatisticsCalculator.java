package uk.gov.digital.ho.proving.income.audit.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PassStatisticsCalculator {

    private final LocalDate fromDate;
    private final LocalDate toDate;

    PassStatisticsCalculator(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    PassRateStatistics result(List<AuditResultByNino> records) {
        List<AuditResultByNino> resultsInRange = records.stream()
            .filter(this::isInDateRange)
            .collect(Collectors.toList());

        int totalRequests = resultsInRange.size();

        Map<AuditResultType, Long> countsByResult = resultsInRange.stream()
            .collect(Collectors.groupingBy(AuditResultByNino::resultType, Collectors.counting()));

        long passes = countsByResult.getOrDefault(AuditResultType.PASS, 0L);
        long failures = countsByResult.getOrDefault(AuditResultType.FAIL, 0L);
        long notFound = countsByResult.getOrDefault(AuditResultType.NOTFOUND, 0L);
        long errors = countsByResult.getOrDefault(AuditResultType.ERROR, 0L);

        return new PassRateStatistics(fromDate, toDate, totalRequests, passes, failures, notFound, errors);
    }

    private boolean isInDateRange(AuditResultByNino auditResult) {
        return !auditResult.date().isBefore(fromDate) && !auditResult.date().isAfter(toDate);
    }
}

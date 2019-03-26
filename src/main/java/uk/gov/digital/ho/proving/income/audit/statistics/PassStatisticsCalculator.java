package uk.gov.digital.ho.proving.income.audit.statistics;

import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatistics.PassRateStatisticsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

class PassStatisticsCalculator {

    private final LocalDate fromDate;
    private final LocalDate toDate;

    PassStatisticsCalculator(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    PassRateStatistics result(List<AuditResultByNino> records) {
        PassRateStatisticsBuilder statisticsBuilder = PassRateStatistics.builder()
            .fromDate(fromDate)
            .toDate(toDate);

        List<AuditResultByNino> resultsInRange = filterInDateRange(records);
        statisticsBuilder.totalRequests(resultsInRange.size());

        Map<AuditResultType, Long> countsByResult = countByResultType(resultsInRange);

        return statisticsBuilder
            .passes(countsByResult.getOrDefault(PASS, 0L))
            .failures(countsByResult.getOrDefault(FAIL, 0L))
            .notFound(countsByResult.getOrDefault(NOTFOUND, 0L))
            .errors(countsByResult.getOrDefault(ERROR, 0L))
            .build();
    }

    private List<AuditResultByNino> filterInDateRange(List<AuditResultByNino> records) {
        return records.stream()
            .filter(this::isInDateRange)
            .collect(toList());
    }

    private boolean isInDateRange(AuditResultByNino auditResult) {
        return !auditResult.date().isBefore(fromDate) && !auditResult.date().isAfter(toDate);
    }

    private Map<AuditResultType, Long> countByResultType(List<AuditResultByNino> resultsInRange) {
        return resultsInRange.stream()
            .collect(groupingBy(AuditResultByNino::resultType, Collectors.counting()));
    }
}

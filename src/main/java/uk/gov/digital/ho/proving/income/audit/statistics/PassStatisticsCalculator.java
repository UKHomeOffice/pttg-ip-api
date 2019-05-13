package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.ArchivedResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

@Component
class PassStatisticsCalculator {

    PassRateStatistics result(List<AuditResultByNino> records, List<ArchivedResult> archivedResults, LocalDate fromDate, LocalDate toDate) {

        List<AuditResultByNino> resultsInRange = filterInDateRange(records, fromDate, toDate);
        Map<AuditResultType, Long> countsByResult = countByResultType(resultsInRange);

        long passes = combineCounts(PASS, countsByResult, archivedResults);
        long failures = combineCounts(FAIL, countsByResult, archivedResults);
        long notFound = combineCounts(NOTFOUND, countsByResult, archivedResults);
        long errors = combineCounts(ERROR, countsByResult, archivedResults);
        long totalRequests = passes + failures + notFound + errors;

        return PassRateStatistics.builder()
            .fromDate(fromDate)
            .toDate(toDate)
            .passes(passes)
            .failures(failures)
            .notFound(notFound)
            .errors(errors)
            .totalRequests(totalRequests)
            .build();
    }

    private List<AuditResultByNino> filterInDateRange(List<AuditResultByNino> records, LocalDate fromDate, LocalDate toDate) {
        return records.stream()
            .filter(auditResult -> isInDateRange(auditResult, fromDate, toDate))
            .collect(toList());
    }

    private boolean isInDateRange(AuditResultByNino auditResult, LocalDate fromDate, LocalDate toDate) {
        return !auditResult.date().isBefore(fromDate) && !auditResult.date().isAfter(toDate);
    }

    private Map<AuditResultType, Long> countByResultType(List<AuditResultByNino> resultsInRange) {
        return resultsInRange.stream()
            .collect(groupingBy(AuditResultByNino::resultType, Collectors.counting()));
    }

    private long combineCounts(AuditResultType resultType, Map<AuditResultType, Long> countsByResult, List<ArchivedResult> archivedResults) {

        long resultCount = countsByResult.getOrDefault(resultType, 0L);

        long archivedResultCount = archivedResults.stream()
            .map(ArchivedResult::getResults)
            .mapToLong(result -> result.getOrDefault(String.valueOf(resultType), 0))
            .sum();

        return resultCount + archivedResultCount;
    }
}

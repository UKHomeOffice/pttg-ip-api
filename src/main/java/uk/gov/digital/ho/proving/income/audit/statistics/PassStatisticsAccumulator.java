package uk.gov.digital.ho.proving.income.audit.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;
import uk.gov.digital.ho.proving.income.audit.AuditResultTypeComparator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

class PassStatisticsAccumulator {

    private final LocalDate fromDate;
    private final LocalDate toDate;

    private final Map<String, BestResult> bestResultByNino;


    PassStatisticsAccumulator(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        bestResultByNino = new HashMap<>(); 
    }

    void accumulate(List<AuditResultByNino> records) {
        for (AuditResultByNino record : records) {
            BestResult currentBestResult = bestResultByNino.get(record.nino());

            if (isNull(currentBestResult)
                || betterThanCurrentBest(record, currentBestResult)
                || sameResultButNewer(record, currentBestResult)) {
                bestResultByNino.put(record.nino(), new BestResult(record.date(), record.resultType()));
            }
        }
    }

    private boolean betterThanCurrentBest(AuditResultByNino record, BestResult currentBestResult) {
        return new AuditResultTypeComparator().compare(record.resultType(), currentBestResult.resultType) > 0;

    }

    private boolean sameResultButNewer(AuditResultByNino record, BestResult currentBestResult) {
        return record.resultType() == currentBestResult.resultType && record.date().isAfter(currentBestResult.dateOfBestResult);
    }

    PassRateStatistics result() {
        List<BestResult> resultsInRange = bestResultByNino.values().stream()
            .filter(this::isInDateRange)
            .collect(Collectors.toList());

        int totalRequests = resultsInRange.size();

        Map<AuditResultType, Long> countsByResult = resultsInRange.stream()
            .collect(Collectors.groupingBy(BestResult::resultType, Collectors.counting()));

        long passes = countsByResult.getOrDefault(AuditResultType.PASS, 0L);
        long failures = countsByResult.getOrDefault(AuditResultType.FAIL, 0L);
        long notFound = countsByResult.getOrDefault(AuditResultType.NOTFOUND, 0L);
        long errors = countsByResult.getOrDefault(AuditResultType.ERROR, 0L);

        return new PassRateStatistics(fromDate, toDate, totalRequests, passes, failures, notFound, errors);
    }

    private boolean isInDateRange(BestResult bestResult) {
        LocalDate resultDate = bestResult.dateOfBestResult();
        return !resultDate.isBefore(fromDate) && !resultDate.isAfter(toDate);
    }

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    private class BestResult {
        private final LocalDate dateOfBestResult;
        private final AuditResultType resultType;
    }
}

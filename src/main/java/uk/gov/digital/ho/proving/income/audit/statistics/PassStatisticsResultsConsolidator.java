package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PassStatisticsResultsConsolidator {

    private final AuditResultComparator resultComparator;
    private final int cutoffDays;

    PassStatisticsResultsConsolidator(AuditResultComparator resultComparator, @Value("${audit.history.cutoff.days}") int cutoffDays) {
        this.resultComparator = resultComparator;
        this.cutoffDays = cutoffDays;
    }

    List<AuditResult> consolidateResults(List<AuditResultsGroupedByNino> resultsGroupedByNino) {
        List<List<AuditResult>> separatedByCutoff = resultsGroupedByNino.stream()
                                                                        .map(AuditResultsGroupedByNino::results)
                                                                        .map(this::separateResultsByCutoff)
                                                                        .flatMap(Collection::stream)
                                                                        .collect(Collectors.toList());

        return separatedByCutoff.stream()
                                .map(this::earliestBestResult)
                                .collect(Collectors.toList());
    }

    List<List<AuditResult>> separateResultsByCutoff(List<AuditResult> results) {
        List<AuditResult> sortedByDate = sortByDate(results);
        List<List<AuditResult>> groupedByCutoff = new ArrayList<>();

        List<AuditResult> sameRequestResults = startNewGroup(groupedByCutoff);

        for (AuditResult auditResult : sortedByDate) {
            long dayOfResult = auditResult.date().toEpochDay();

            if (!sameRequestResults.isEmpty()) {
                long dayOfPreviousResult = getLast(sameRequestResults).date().toEpochDay();

                if (afterCutoff(dayOfResult, dayOfPreviousResult)) {
                    sameRequestResults = startNewGroup(groupedByCutoff);
                }
            }
            sameRequestResults.add(auditResult);
        }
        return filterEmpty(groupedByCutoff);
    }

    private AuditResult earliestBestResult(List<AuditResult> auditResults) {
        return auditResults.stream()
                           .max(resultComparator)
                           .orElse(null);
    }

    private List<AuditResult> sortByDate(List<AuditResult> results) {
        return results.stream()
                      .sorted(Comparator.comparing(AuditResult::date))
                      .collect(Collectors.toList());
    }

    private List<AuditResult> startNewGroup(List<List<AuditResult>> groupedByCutoff) {
        List<AuditResult> newGroup = new ArrayList<>();
        groupedByCutoff.add(newGroup);
        return newGroup;
    }

    private List<List<AuditResult>> filterEmpty(List<List<AuditResult>> groupedByCutoff) {
        return groupedByCutoff.stream()
                              .filter(result -> !result.isEmpty())
                              .collect(Collectors.toList());
    }

    private AuditResult getLast(List<AuditResult> auditResults) {
        return auditResults.get(auditResults.size() - 1);
    }

    private boolean afterCutoff(long dayOfResult, long dayOfPreviousResult) {
        return dayOfResult - dayOfPreviousResult >= cutoffDays;
    }
}

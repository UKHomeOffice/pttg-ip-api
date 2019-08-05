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

import static java.util.stream.Collectors.toCollection;

@Component
public class PassStatisticsResultsConsolidator {

    private final AuditResultComparator resultComparator;
    private final int cutoffDays;

    PassStatisticsResultsConsolidator(AuditResultComparator resultComparator, @Value("${audit.history.cutoff.days}") int cutoffDays) {
        this.resultComparator = resultComparator;
        this.cutoffDays = cutoffDays;
    }

    List<AuditResult> consolidateResults(List<AuditResultsGroupedByNino> resultsGroupedByNino) {
        List<AuditResultsGroupedByNino> separatedByCutoff = resultsGroupedByNino.stream()
                                                                                .map(this::separateResultsByCutoff)
                                                                                .flatMap(Collection::stream)
                                                                                .collect(Collectors.toList());

        return separatedByCutoff.stream()
                                .map(this::earliestBestResult)
                                .collect(Collectors.toList());
    }

    List<AuditResultsGroupedByNino> separateResultsByCutoff(AuditResultsGroupedByNino results) {
        AuditResultsGroupedByNino sortedByDate = sortByDate(results);
        List<AuditResultsGroupedByNino> groupedByCutoff = new ArrayList<>();

        AuditResultsGroupedByNino sameRequestResults = startNewGroup(groupedByCutoff);

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

    private AuditResult earliestBestResult(AuditResultsGroupedByNino auditResults) {
        return auditResults.stream()
                           .max(resultComparator)
                           .orElse(null);
    }

    private AuditResultsGroupedByNino sortByDate(AuditResultsGroupedByNino results) {
        return results.stream()
                      .sorted(Comparator.comparing(AuditResult::date))
                      .collect(toCollection(AuditResultsGroupedByNino::new));
    }

    private AuditResultsGroupedByNino startNewGroup(List<AuditResultsGroupedByNino> groupedByCutoff) {
        AuditResultsGroupedByNino newGroup = new AuditResultsGroupedByNino();
        groupedByCutoff.add(newGroup);
        return newGroup;
    }

    private List<AuditResultsGroupedByNino> filterEmpty(List<AuditResultsGroupedByNino> groupedByCutoff) {
        return groupedByCutoff.stream()
                              .filter(result -> !result.isEmpty())
                              .collect(Collectors.toList());
    }

    private AuditResult getLast(AuditResultsGroupedByNino auditResults) {
        return auditResults.get(auditResults.size() - 1);
    }

    private boolean afterCutoff(long dayOfResult, long dayOfPreviousResult) {
        return dayOfResult - dayOfPreviousResult >= cutoffDays;
    }
}

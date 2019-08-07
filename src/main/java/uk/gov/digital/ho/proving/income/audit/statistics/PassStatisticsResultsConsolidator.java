package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultComparator;
import uk.gov.digital.ho.proving.income.audit.ResultCutoffSeparator;

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
    private final ResultCutoffSeparator resultCutoffSeparator;

    PassStatisticsResultsConsolidator(AuditResultComparator resultComparator, @Value("${audit.history.cutoff.days}") int cutoffDays,
                                      ResultCutoffSeparator resultCutoffSeparator) {
        this.resultComparator = resultComparator;
        this.cutoffDays = cutoffDays;
        this.resultCutoffSeparator = resultCutoffSeparator;
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
        // TODO OJR EE-21001 Migrating to ResultCutoffSeparator
        AuditResultsGroupedByNino sortedByDate = sortByDate(results);
        List<AuditResultsGroupedByNino> groupedByCutoff = new ArrayList<>();

        AuditResultsGroupedByNino sameRequestResults = startNewGroup(groupedByCutoff);

        for (AuditResult auditResult : sortedByDate) {

            if (sameRequestResults.resultAfterCutoff(cutoffDays, auditResult)) {
                sameRequestResults = startNewGroup(groupedByCutoff);
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
}

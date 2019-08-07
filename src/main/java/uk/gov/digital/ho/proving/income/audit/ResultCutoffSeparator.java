package uk.gov.digital.ho.proving.income.audit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.statistics.AuditResultsGroupedByNino;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Component
public class ResultCutoffSeparator {


    private int cutoffDays;

    public ResultCutoffSeparator(@Value("${audit.history.cutoff.days}") int cutoffDays) {
        this.cutoffDays = cutoffDays;
    }

    public List<AuditResultsGroupedByNino> separateResultsByCutoff(AuditResultsGroupedByNino results) {
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

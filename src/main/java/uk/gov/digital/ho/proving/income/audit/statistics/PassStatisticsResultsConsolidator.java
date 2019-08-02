package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultComparator;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PassStatisticsResultsConsolidator {

    private final AuditResultComparator resultComparator;
    private final int cutoffDays;

    PassStatisticsResultsConsolidator(AuditResultComparator resultComparator, @Value("${audit.history.cutoff.days}") int cutoffDays) {
        this.resultComparator = resultComparator;
        this.cutoffDays = cutoffDays;
    }

    List<AuditResult> consolidateResults(List<List<AuditResult>> resultsGroupedByNino) {
        Map<String, List<AuditResult>> resultsByNino = resultsGroupedByNino.stream()
                                                                           .collect(Collectors.toMap(resultGroupedByNino -> resultGroupedByNino.get(0).nino(),
                                                                                                     resultGroupedByNino -> resultGroupedByNino,
                                                                                                     (a, b) -> b));
        return consolidateResults(resultsByNino);
    }

    List<AuditResult> consolidateResults(Map<String, List<AuditResult>> resultsByNino) {
        List<AuditResult> consolidatedResults = new ArrayList<>();
        for (List<AuditResult> results : resultsByNino.values()) {
            List<List<AuditResult>> separateResults = separateResultsByCutoff(results);
            for (List<AuditResult> groupedResult : separateResults) {
                AuditResult bestResult = groupedResult.stream()
                                                      .max(resultComparator)
                                                      .orElse(null);
                consolidatedResults.add(bestResult);
            }
        }
        return consolidatedResults;
    }

    List<List<AuditResult>> separateResultsByCutoff(List<AuditResult> results) {
        if (results.isEmpty()) {
            return Collections.emptyList();
        }


        List<AuditResult> sortedByDate = results.stream()
                                                .sorted(Comparator.comparing(AuditResult::date))
                                                .collect(Collectors.toList());

        List<AuditResult> sameRequestResults = new ArrayList<>();
        List<List<AuditResult>> groupedByCutoff = new ArrayList<>(Collections.singletonList(sameRequestResults));
        for (AuditResult auditResult : sortedByDate) {
            LocalDate dateOfResult = auditResult.date();
            LocalDate dateOfLastResult = sameRequestResults.isEmpty() ? LocalDate.MAX : sameRequestResults.get(sameRequestResults.size() - 1).date();
            if (dateOfResult.toEpochDay() - dateOfLastResult.toEpochDay() < cutoffDays) {
                sameRequestResults.add(auditResult);
            } else {
                sameRequestResults = new ArrayList<>();
                groupedByCutoff.add(sameRequestResults);
                sameRequestResults.add(auditResult);
            }
        }
        return groupedByCutoff;

    }
}

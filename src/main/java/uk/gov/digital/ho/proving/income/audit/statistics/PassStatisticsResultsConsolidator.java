package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultComparator;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

@Component
public class PassStatisticsResultsConsolidator {

    private final AuditResultComparator resultComparator;
    private final int cutoffDays;

    PassStatisticsResultsConsolidator(AuditResultComparator resultComparator, @Value("${audit.history.cutoff.days}") int cutoffDays) {
        this.resultComparator = resultComparator;
        this.cutoffDays = cutoffDays;
    }

    List<AuditResult> consolidateResults(List<List<AuditResult>> resultsGroupedByNino) {
        List<List<AuditResult>> separatedByCutoff = resultsGroupedByNino.stream()
                                                                        .map(this::separateResultsByCutoff)
                                                                        .flatMap(Collection::stream)
                                                                        .collect(Collectors.toList());

        return separatedByCutoff.stream()
                                .map(this::earliestBestResult)
                                .collect(Collectors.toList());
    }

    List<List<AuditResult>> separateResultsByCutoff(List<AuditResult> results) {
        List<AuditResult> sortedByDate = results.stream()
                                                .sorted(Comparator.comparing(AuditResult::date))
                                                .collect(Collectors.toList());

        List<AuditResult> sameRequestResults = new ArrayList<>();
        List<List<AuditResult>> groupedByCutoff = new ArrayList<>(singletonList(sameRequestResults));

        for (AuditResult auditResult : sortedByDate) {
            LocalDate dateOfResult = auditResult.date();
            LocalDate dateOfLastResult = sameRequestResults.isEmpty() ? LocalDate.MAX : sameRequestResults.get(sameRequestResults.size() - 1).date();

            if (dateOfResult.toEpochDay() - dateOfLastResult.toEpochDay() >= cutoffDays) {
                sameRequestResults = new ArrayList<>();
                groupedByCutoff.add(sameRequestResults);
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

    private List<List<AuditResult>> filterEmpty(List<List<AuditResult>> groupedByCutoff) {
        return groupedByCutoff.stream()
                              .filter(result -> !result.isEmpty())
                              .collect(Collectors.toList());
    }
}

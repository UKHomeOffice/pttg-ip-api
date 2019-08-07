package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultComparator;
import uk.gov.digital.ho.proving.income.audit.ResultCutoffSeparator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
                                                                                .map(resultCutoffSeparator::separateResultsByCutoff)
                                                                                .flatMap(Collection::stream)
                                                                                .collect(Collectors.toList());

        return separatedByCutoff.stream()
                                .map(this::earliestBestResult)
                                .collect(Collectors.toList());
    }

    private AuditResult earliestBestResult(AuditResultsGroupedByNino auditResults) {
        return auditResults.stream()
                           .max(resultComparator)
                           .orElse(null);
    }
}

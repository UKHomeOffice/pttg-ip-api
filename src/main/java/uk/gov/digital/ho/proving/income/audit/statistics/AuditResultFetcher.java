package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsService.AUDIT_EVENTS_TO_RETRIEVE;

@Component
public class AuditResultFetcher {

    private final AuditClient auditClient;
    private final AuditResultConsolidator resultConsolidator;
    private final AuditResultComparator resultComparator;

    public AuditResultFetcher(AuditClient auditClient, AuditResultConsolidator resultConsolidator,  AuditResultComparator resultComparator) {
        this.auditClient = auditClient;
        this.resultConsolidator = resultConsolidator;
        this.resultComparator = resultComparator;
    }

    public List<AuditResult> getAuditResults(List<String> correlationIds) {
        Map<String, AuditResult> bestResultsByNino = new HashMap<>();
        for (String correlationId : correlationIds) {
            AuditResult auditResult = getAuditResultForCorrelationId(correlationId);
            updateBestResults(bestResultsByNino, auditResult);
        }
        return new ArrayList<>(bestResultsByNino.values());
//
//        // TODO EE-21001 - probable new routine:
//        // Build up a map where each nino is the key and all the query results for that nino are stored in a list as the value
//        // Map<String, AuditResultsGroupedByNino> resultsByNino = new HashMap<>();
//        // for (String correlationId : allCorrelationIds) {
//        // AuditResult auditResult = getAuditResultForCorrelationId(correlationId);
//        //     if(!resultsByNino.hasKey(auditResult.nino()) {
//        //         resultByNino.put(auditResult.nino(), AuditResultsGroupedByNino(auditResult)));
//        //     } else {
//        //         resultByNino.get(auditResult.nino()).add(auditResult)
//        //     }
//        // }
//        //
//        // Consolidate the results into a single list - for each nino, sort results by date, split list if any 10 day gaps,
//        // for each split list of results - calculate best earliest result and put into the final results list.
//        // return PassRateStatisticsConsolidator.consolidateResults(resultByNino.values())
    }

    private AuditResult getAuditResultForCorrelationId(String correlationId) {
        List<AuditRecord> auditRecordsForCorrelationId = auditClient.getHistoryByCorrelationId(correlationId, AUDIT_EVENTS_TO_RETRIEVE);
        return resultConsolidator.getAuditResult(auditRecordsForCorrelationId);
    }

    private void updateBestResults(Map<String, AuditResult> bestResultsByNino, AuditResult newResult) {
        String nino = newResult.nino();

        if (!bestResultsByNino.containsKey(nino) || isBetterResult(bestResultsByNino.get(nino), newResult)) {
            bestResultsByNino.put(nino, newResult);
        }
    }

    private boolean isBetterResult(AuditResult currentResult, AuditResult newResult) {
        return resultComparator.compare(currentResult, newResult) < 0;
    }
}

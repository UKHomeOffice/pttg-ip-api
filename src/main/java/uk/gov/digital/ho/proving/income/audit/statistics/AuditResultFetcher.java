package uk.gov.digital.ho.proving.income.audit.statistics;

import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.audit.AuditRecord;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultConsolidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsService.AUDIT_EVENTS_TO_RETRIEVE;

@Component
public class AuditResultFetcher {

    private final AuditClient auditClient;
    private final AuditResultConsolidator resultConsolidator;
    private final PassStatisticsResultsConsolidator statisticsResultsConsolidator;

    public AuditResultFetcher(AuditClient auditClient,
                              AuditResultConsolidator resultConsolidator,
                              PassStatisticsResultsConsolidator statisticsResultsConsolidator) {
        this.auditClient = auditClient;
        this.resultConsolidator = resultConsolidator;
        this.statisticsResultsConsolidator = statisticsResultsConsolidator;
    }

    public List<AuditResult> getAuditResults(List<String> correlationIds) {
        List<AuditResultsGroupedByNino> resultsGroupedByNino = getResultsByNino(correlationIds);
        return statisticsResultsConsolidator.consolidateResults(resultsGroupedByNino);
    }

    private List<AuditResultsGroupedByNino> getResultsByNino(List<String> correlationIds) {
        Map<String, AuditResultsGroupedByNino> resultsByNino = new HashMap<>();

        for (String correlationId : correlationIds) {
            AuditResult auditResult = getAuditResultForCorrelationId(correlationId);
            addResultForNino(resultsByNino, auditResult);
        }

        return asGroupedResultsList(resultsByNino);
    }

    private AuditResult getAuditResultForCorrelationId(String correlationId) {
        List<AuditRecord> auditRecordsForCorrelationId = auditClient.getHistoryByCorrelationId(correlationId, AUDIT_EVENTS_TO_RETRIEVE);
        return resultConsolidator.getAuditResult(auditRecordsForCorrelationId);
    }

    private void addResultForNino(Map<String, AuditResultsGroupedByNino> resultsByNino, AuditResult auditResult) {
        AuditResultsGroupedByNino resultsForNino = groupedResultsFor(resultsByNino, auditResult.nino());
        resultsForNino.add(auditResult);
    }

    private AuditResultsGroupedByNino groupedResultsFor(Map<String, AuditResultsGroupedByNino> resultsByNino, String nino) {
        if (!resultsByNino.containsKey(nino)) {
            resultsByNino.put(nino, new AuditResultsGroupedByNino());
        }
        return resultsByNino.get(nino);
    }

    private List<AuditResultsGroupedByNino> asGroupedResultsList(Map<String, AuditResultsGroupedByNino> resultsByNino) {
        return ImmutableList.copyOf(resultsByNino.values());
    }
}

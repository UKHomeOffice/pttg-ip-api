package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.audit.AuditRecord;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultConsolidator;

import java.util.ArrayList;
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
        Map<String, AuditResultsGroupedByNino> resultsByNino = new HashMap<>();
        for (String correlationId : correlationIds) {
            AuditResult auditResult = getAuditResultForCorrelationId(correlationId);
            if (!resultsByNino.containsKey(auditResult.nino())) {
                resultsByNino.put(auditResult.nino(), new AuditResultsGroupedByNino(auditResult));
            } else {
                resultsByNino.get(auditResult.nino()).add(auditResult);
            }
        }
        return statisticsResultsConsolidator.consolidateResults(new ArrayList<>(resultsByNino.values()));
    }

    private AuditResult getAuditResultForCorrelationId(String correlationId) {
        List<AuditRecord> auditRecordsForCorrelationId = auditClient.getHistoryByCorrelationId(correlationId, AUDIT_EVENTS_TO_RETRIEVE);
        return resultConsolidator.getAuditResult(auditRecordsForCorrelationId);
    }

}

package uk.gov.digital.ho.proving.income.audit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@Component
class AuditArchiveService {

    private AuditClient auditClient;
    private AuditResultConsolidator auditResultConsolidator;
    private int retainAuditHistoryMonths;

    static final List<AuditEventType> AUDIT_EVENTS_TO_ARCHIVE =
        Arrays.asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);

    public AuditArchiveService(
        AuditClient auditClient,
        AuditResultConsolidator auditResultConsolidator,
        @Value("${audit.history.months}") int retainAuditHistoryMonths) {
        this.auditClient = auditClient;
        this.auditResultConsolidator = auditResultConsolidator;
        this.retainAuditHistoryMonths = retainAuditHistoryMonths;
    }

    void archiveAudit() {
        AuditArchiveConfig config = new AuditArchiveConfig(getLastDayToBeArchived(), AUDIT_EVENTS_TO_ARCHIVE);

        List<AuditRecord> auditHistory = auditClient.getAuditHistory(getLastDayToBeArchived(), AUDIT_EVENTS_TO_ARCHIVE);
        List<AuditResult> byCorrelationId = auditResultConsolidator.auditResultsByCorrelationId(auditHistory);
        List<ConsolidatedAuditResult> consolidatedByNino = auditResultConsolidator.consolidatedAuditResultsByNino(byCorrelationId);

        for (ConsolidatedAuditResult auditResult : consolidatedByNino) {
            auditClient.archiveAudit(generateAuditHistoryRequest(auditResult, config), auditResult.date());
        }
    }

    private LocalDate getLastDayToBeArchived() {
        return LocalDate.now().minusMonths(retainAuditHistoryMonths).minusDays(1);
    }

    private ArchiveAuditRequest generateAuditHistoryRequest(ConsolidatedAuditResult auditResult, AuditArchiveConfig config) {
        return ArchiveAuditRequest.builder()
            .nino(auditResult.nino())
            .correlationIds(auditResult.correlationIds())
            .lastArchiveDate(config.lastArchiveDate())
            .result(auditResult.resultType().name())
            .build();
    }

}

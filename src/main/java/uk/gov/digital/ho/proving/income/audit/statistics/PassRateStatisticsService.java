package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@Component
public class PassRateStatisticsService {

    private static final List<AuditEventType> AUDIT_EVENTS_TO_RETRIEVE = asList(
        INCOME_PROVING_FINANCIAL_STATUS_REQUEST,
        INCOME_PROVING_FINANCIAL_STATUS_RESPONSE
    );
    private final AuditClient auditClient;
    private final PassStatisticsCalculator calculator;
    private final AuditResultConsolidator consolidator;
    private final int requestPageSize;

    public PassRateStatisticsService(AuditClient auditClient,
                                     PassStatisticsCalculator calculator,
                                     AuditResultConsolidator consolidator,
                                     @Value("${audit.history.passratestats.pagesize}") int requestPageSize) {

        this.auditClient = auditClient;
        this.calculator = calculator;
        this.consolidator = consolidator;
        this.requestPageSize = requestPageSize;
    }

    public PassRateStatistics generatePassRateStatistics(LocalDate fromDate, LocalDate toDate) {
        List<AuditRecord> allAuditRecords = getAllAuditRecords();
        List<AuditResultByNino> resultsByNino = consolidateRecords(allAuditRecords);
        return calculator.result(resultsByNino, emptyList(), fromDate, toDate);
    }

    private List<AuditResultByNino> consolidateRecords(List<AuditRecord> allAuditRecords) {
        List<AuditResult> byCorrelationId = consolidator.auditResultsByCorrelationId(allAuditRecords);
        return consolidator.consolidatedAuditResultsByNino(byCorrelationId);
    }

    private List<AuditRecord> getAllAuditRecords() {
        List<AuditRecord> allAuditRecords = new ArrayList<>();

        int page = 0;
        while (addAuditRecords(allAuditRecords, page)) {
            page++;
        }
        return allAuditRecords;
    }

    private boolean addAuditRecords(List<AuditRecord> allAuditRecords, int page) {
        List<AuditRecord> auditRecords = auditClient.getAuditHistoryPaginated(AUDIT_EVENTS_TO_RETRIEVE, page, requestPageSize);
        allAuditRecords.addAll(auditRecords);

        return isFullPage(auditRecords);
    }

    private boolean isFullPage(List<AuditRecord> auditRecords) {
        return auditRecords.size() == requestPageSize;
    }
}

package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
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

    public PassRateStatistics generatePassRateStatistics(YearMonth calendarMonth) {
        return generatePassRateStatistics(calendarMonth.atDay(1), calendarMonth.atEndOfMonth());
    }

    public PassRateStatistics generatePassRateStatistics(TaxYear taxYear) {
        return generatePassRateStatistics(taxYear.startDate(), taxYear.endDate());
    }

    public PassRateStatistics generatePassRateStatistics(LocalDate fromDate, LocalDate toDate) {
        List<String> allCorrelationIds = auditClient.getAllCorrelationIdsForEventType(AUDIT_EVENTS_TO_RETRIEVE);

        List<AuditResultByNino> resultsByNino = getAuditResultByNinos(allCorrelationIds);

        List<ArchivedResult> archivedResults = auditClient.getArchivedResults(fromDate, toDate);
        return calculator.result(resultsByNino, archivedResults, fromDate, toDate);
    }

    private List<AuditResultByNino> getAuditResultByNinos(List<String> allCorrelationIds) {
        List<AuditResultByNino> resultsByNino = new ArrayList<>();
        for (String correlationId : allCorrelationIds) {
            List<AuditRecord> auditRecordsForCorrelationId = auditClient.getHistoryByCorrelationId(correlationId, AUDIT_EVENTS_TO_RETRIEVE);
            resultsByNino.addAll(consolidateRecords(auditRecordsForCorrelationId));
        }
        return resultsByNino;
    }

    private List<AuditResultByNino> consolidateRecords(List<AuditRecord> allAuditRecords) {
        List<AuditResult> byCorrelationId = consolidator.auditResultsByCorrelationId(allAuditRecords);
        return consolidator.consolidatedAuditResultsByNino(byCorrelationId);
    }
}

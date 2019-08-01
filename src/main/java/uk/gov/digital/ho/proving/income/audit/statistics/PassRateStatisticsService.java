package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final AuditResultComparator resultComparator;
    private final int cutoffDays;

    public PassRateStatisticsService(AuditClient auditClient,
                                     PassStatisticsCalculator calculator,
                                     AuditResultConsolidator consolidator,
                                     AuditResultComparator resultComparator,
                                     @Value("${audit.history.cutoff.days}") int cutoffDays) {
        this.auditClient = auditClient;
        this.calculator = calculator;
        this.consolidator = consolidator;
        this.resultComparator = resultComparator;
        this.cutoffDays = cutoffDays;
    }

    public PassRateStatistics generatePassRateStatistics(YearMonth calendarMonth) {
        return generatePassRateStatistics(calendarMonth.atDay(1), calendarMonth.atEndOfMonth());
    }

    public PassRateStatistics generatePassRateStatistics(TaxYear taxYear) {
        return generatePassRateStatistics(taxYear.startDate(), taxYear.endDate());
    }

    public PassRateStatistics generatePassRateStatistics(LocalDate fromDate, LocalDate toDate) {
        LocalDate cutOffDate = toDate.plusDays(cutoffDays);
        List<String> allCorrelationIds = auditClient.getAllCorrelationIdsForEventType(AUDIT_EVENTS_TO_RETRIEVE, cutOffDate);

        List<AuditResult> results = getAuditResults(allCorrelationIds);

        List<ArchivedResult> archivedResults = auditClient.getArchivedResults(fromDate, toDate);
        return calculator.result(results, archivedResults, fromDate, toDate);
    }

    private List<AuditResult> getAuditResults(List<String> allCorrelationIds) {
        Map<String, AuditResult> bestResultsByNino = new HashMap<>();
        for (String correlationId : allCorrelationIds) {
            AuditResult auditResult = getAuditResultForCorrelationId(correlationId);
            updateBestResults(bestResultsByNino, auditResult);
        }
        return new ArrayList<>(bestResultsByNino.values());
    }

    private AuditResult getAuditResultForCorrelationId(String correlationId) {
        List<AuditRecord> auditRecordsForCorrelationId = auditClient.getHistoryByCorrelationId(correlationId, AUDIT_EVENTS_TO_RETRIEVE);
        return consolidator.getAuditResult(auditRecordsForCorrelationId);
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

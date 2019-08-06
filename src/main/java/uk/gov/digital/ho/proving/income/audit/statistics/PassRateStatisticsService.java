package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@Component
public class PassRateStatisticsService {

    static final List<AuditEventType> AUDIT_EVENTS_TO_RETRIEVE = asList(
        INCOME_PROVING_FINANCIAL_STATUS_REQUEST,
        INCOME_PROVING_FINANCIAL_STATUS_RESPONSE
    );
    private final AuditClient auditClient;
    private final PassStatisticsCalculator calculator;
    private final AuditResultFetcher auditResultFetcher;
    private final int cutoffDays;

    public PassRateStatisticsService(AuditClient auditClient,
                                     PassStatisticsCalculator calculator,
                                     AuditResultFetcher auditResultFetcher,
                                     @Value("${audit.history.cutoff.days}") int cutoffDays) {
        this.auditClient = auditClient;
        this.calculator = calculator;
        this.auditResultFetcher = auditResultFetcher;
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

        List<AuditResult> results = auditResultFetcher.getAuditResults(allCorrelationIds);

        List<ArchivedResult> archivedResults = auditClient.getArchivedResults(fromDate, toDate);
        return calculator.result(results, archivedResults, fromDate, toDate);
    }
}

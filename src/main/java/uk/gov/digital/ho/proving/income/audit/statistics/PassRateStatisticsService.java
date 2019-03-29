package uk.gov.digital.ho.proving.income.audit.statistics;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditClient;

import java.time.LocalDate;

@Component
public class PassRateStatisticsService {

    private final AuditClient auditClient;
    private final PassStatisticsCalculator calculator;

    public PassRateStatisticsService(AuditClient auditClient, PassStatisticsCalculator calculator) {
        this.auditClient = auditClient;
        this.calculator = calculator;
    }

    public PassRateStatistics generatePassRateStatistics(LocalDate fromDate, LocalDate toDate) {
        return null;
    }
}

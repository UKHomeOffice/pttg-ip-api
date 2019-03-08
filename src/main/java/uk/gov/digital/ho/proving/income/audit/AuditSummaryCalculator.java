package uk.gov.digital.ho.proving.income.audit;

import java.util.List;

public class AuditSummaryCalculator {

    public static AuditSummary summarise(List<AuditableData> auditData) {

        return AuditSummary.builder()
            .passed(0)
            .notPassed(0)
            .notFound(0)
            .failed(0)
            .build();
    }

}

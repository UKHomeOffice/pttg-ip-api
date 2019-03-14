package uk.gov.digital.ho.proving.income.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.ERROR;

@Component
class AuditResultConsolidation {

    private AuditResultParser auditResultParser;
    private AuditResultTypeComparator auditResultTypeComparator;

    public AuditResultConsolidation(
        @Autowired AuditResultParser auditResultParser,
        @Autowired AuditResultTypeComparator auditResultTypeComparator
    ) {
        this.auditResultParser = auditResultParser;
        this.auditResultTypeComparator = auditResultTypeComparator;
    }

    List<AuditResult> auditResultsByCorrelationId(List<AuditRecord> auditRecords) {
        Map<String, List<AuditRecord>> recordsByCorrelationId =
            auditRecords.stream().collect(Collectors.groupingBy(AuditRecord::getId));

        return recordsByCorrelationId.values().stream()
            .map(this::getAuditResult)
            .collect(Collectors.toList());
    }

    AuditResult getAuditResult(List<AuditRecord> auditRecords) {
        String correlationId = auditRecords.get(0).getId();
        String nino = findingNino(auditRecords);
        LocalDate date = auditRecords.get(0).getDate().toLocalDate();

        AuditResultType resultType =
            auditRecords.stream()
                .map(record -> auditResultParser.from(record))
                .map(AuditResult::resultType)
                .max(auditResultTypeComparator)
                .orElse(ERROR);

        return new AuditResult(correlationId, date, nino, resultType);
    }

    private String findingNino(List<AuditRecord> auditRecords) {
        String nino = auditRecords.stream()
            .filter(record -> record.getRef().equals(INCOME_PROVING_FINANCIAL_STATUS_REQUEST))
            .findFirst()
            .map(record -> record.getNino())
            .orElse("");

        if (nino.isEmpty()) {
            nino = auditRecords.stream()
                .filter(record -> record.getRef().equals(INCOME_PROVING_FINANCIAL_STATUS_RESPONSE))
                .findFirst()
                .map(record -> record.getDetail())
                .map(detail -> auditResultParser.getResultNino(detail))
                .orElse("");
        }
        return nino;
    }

}

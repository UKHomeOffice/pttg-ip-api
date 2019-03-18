package uk.gov.digital.ho.proving.income.audit;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.ERROR;

@Component
class AuditResultConsolidator {

    private AuditResultParser auditResultParser;
    private AuditResultTypeComparator auditResultTypeComparator;
    private AuditResultComparator auditResultComparator;

    public AuditResultConsolidator(
        AuditResultParser auditResultParser,
        AuditResultTypeComparator auditResultTypeComparator,
        AuditResultComparator auditResultComparator
    ) {
        this.auditResultParser = auditResultParser;
        this.auditResultTypeComparator = auditResultTypeComparator;
        this.auditResultComparator = auditResultComparator;
    }

    List<AuditResult> auditResultsByCorrelationId(List<AuditRecord> auditRecords) {
        Map<String, List<AuditRecord>> recordsByCorrelationId =
            auditRecords.stream().collect(Collectors.groupingBy(AuditRecord::getId));

        return recordsByCorrelationId.values().stream()
            .map(this::getAuditResult)
            .collect(Collectors.toList());
    }

    List<AuditResultByNino> auditResultsByNino(List<AuditResult> results) {
        Map<String, List<AuditResult>> resultsByNino =
            results.stream().collect(Collectors.groupingBy(AuditResult::nino));

        return resultsByNino.values().stream()
            .map(this::consolidate)
            .collect(Collectors.toList());
    }

    private AuditResultByNino consolidate(List<AuditResult> results) {
        AuditResult consolidatedResult = results.stream()
            .max(auditResultComparator)
            .orElse(null);
        List<String> allCorrelationIds = results.stream()
            .map(AuditResult::correlationId)
            .collect(Collectors.toList());
        return new AuditResultByNino(consolidatedResult.nino(), allCorrelationIds, consolidatedResult.date(), consolidatedResult.resultType());
    }

    private AuditResult getAuditResult(List<AuditRecord> auditRecords) {
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
            .map(AuditRecord::getNino)
            .orElse("");

        if (nino.isEmpty()) {
            nino = auditRecords.stream()
                .filter(record -> record.getRef().equals(INCOME_PROVING_FINANCIAL_STATUS_RESPONSE))
                .findFirst()
                .map(AuditRecord::getDetail)
                .map(detail -> auditResultParser.getResultNino(detail))
                .orElse("");
        }
        return nino;
    }

}

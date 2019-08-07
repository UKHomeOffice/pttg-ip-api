package uk.gov.digital.ho.proving.income.audit;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.statistics.AuditResultsGroupedByNino;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.ERROR;

@Component
public class AuditResultConsolidator {

    private AuditResultParser auditResultParser;
    private AuditResultTypeComparator auditResultTypeComparator;
    private AuditResultComparator auditResultComparator;
    private ResultCutoffSeparator resultCutoffSeparator;

    public AuditResultConsolidator(
        AuditResultParser auditResultParser,
        AuditResultTypeComparator auditResultTypeComparator,
        AuditResultComparator auditResultComparator,
        ResultCutoffSeparator resultCutoffSeparator) {
        this.auditResultParser = auditResultParser;
        this.auditResultTypeComparator = auditResultTypeComparator;
        this.auditResultComparator = auditResultComparator;
        this.resultCutoffSeparator = resultCutoffSeparator;
    }

    public List<AuditResult> auditResultsByCorrelationId(List<AuditRecord> auditRecords) {
        Map<String, List<AuditRecord>> recordsByCorrelationId =
            auditRecords.stream().collect(Collectors.groupingBy(AuditRecord::getId));

        return recordsByCorrelationId.values().stream()
            .map(this::getAuditResult)
            .collect(Collectors.toList());
    }

    public List<ConsolidatedAuditResult> consolidatedAuditResults(List<AuditResult> results) {
        Map<String, List<AuditResult>> resultsByNino =
            results.stream().collect(Collectors.groupingBy(AuditResult::nino));

        return resultsByNino.values().stream()
                            .map(AuditResultsGroupedByNino::of)
                            .map(resultCutoffSeparator::separateResultsByCutoff)
                            .flatMap(Collection::stream)
                            .map(this::consolidateFirstBestResult)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
    }

    private ConsolidatedAuditResult consolidateFirstBestResult(AuditResultsGroupedByNino results) {
        AuditResult consolidatedResult = results.stream()
            .max(auditResultComparator)
            .orElse(null);
        if (consolidatedResult == null) {
            return null;
        }
        List<String> allCorrelationIds = results.stream()
            .map(AuditResult::correlationId)
            .collect(Collectors.toList());
        return new ConsolidatedAuditResult(consolidatedResult.nino(), allCorrelationIds, consolidatedResult.date(), consolidatedResult.resultType());
    }

    public AuditResult getAuditResult(List<AuditRecord> auditRecords) {
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

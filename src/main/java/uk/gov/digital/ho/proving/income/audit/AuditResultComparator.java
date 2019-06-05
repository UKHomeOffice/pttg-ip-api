package uk.gov.digital.ho.proving.income.audit;

import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class AuditResultComparator implements Comparator<AuditResult> {

    private AuditResultTypeComparator auditResultTypeComparator;

    public AuditResultComparator(AuditResultTypeComparator auditResultTypeComparator) {
        this.auditResultTypeComparator = auditResultTypeComparator;
    }

    @Override
    public int compare(AuditResult first, AuditResult second) {
        int result = auditResultTypeComparator.compare(first.resultType(), second.resultType());
        if (result == 0) {
            result = second.date().compareTo(first.date());
        }
        return result;
    }
}

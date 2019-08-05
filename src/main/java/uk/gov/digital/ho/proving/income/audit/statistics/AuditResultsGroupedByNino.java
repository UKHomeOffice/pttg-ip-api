package uk.gov.digital.ho.proving.income.audit.statistics;

import jersey.repackaged.com.google.common.collect.ForwardingList;
import lombok.Getter;
import uk.gov.digital.ho.proving.income.audit.AuditResult;

import java.util.List;

import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;

@Getter
public class AuditResultsGroupedByNino extends ForwardingList<AuditResult> {

    private final List<AuditResult> results;

    public AuditResultsGroupedByNino() {
        results = newArrayList();
    }

    public AuditResultsGroupedByNino(AuditResult result) {
        results = newArrayList(result);
    }

    @Override
    protected List<AuditResult> delegate() {
        return results;
    }
}

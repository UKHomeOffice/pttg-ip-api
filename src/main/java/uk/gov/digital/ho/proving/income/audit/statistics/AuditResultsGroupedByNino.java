package uk.gov.digital.ho.proving.income.audit.statistics;

import jersey.repackaged.com.google.common.collect.ForwardingList;
import uk.gov.digital.ho.proving.income.audit.AuditResult;

import java.time.LocalDate;
import java.util.List;

import static java.util.Comparator.naturalOrder;
import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;

public class AuditResultsGroupedByNino extends ForwardingList<AuditResult> {

    private final List<AuditResult> results;

    public AuditResultsGroupedByNino() {
        results = newArrayList();
    }

    public AuditResultsGroupedByNino(AuditResult result) {
        // TODO OJR EE-21001 Only used in tests - delete.
        results = newArrayList(result);
    }

    @Override
    protected List<AuditResult> delegate() {
        return results;
    }

    public LocalDate latestDate() {
        return stream().map(AuditResult::date)
                       .max(naturalOrder())
                       .orElse(null);
    }

    public boolean resultAfterCutoff(int cutoffDays, AuditResult auditResult) {
        if (latestDate() == null) {
            return false;
        }
        return latestDate().plusDays(cutoffDays).isBefore(auditResult.date());
    }
}

package uk.gov.digital.ho.proving.income.audit.statistics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.audit.AuditResult;

import java.util.List;
import java.util.stream.Stream;

import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
public class AuditResultsGroupedByNino {

    private final List<AuditResult> results;

    public AuditResultsGroupedByNino() {
        results = newArrayList();
    }

    public AuditResultsGroupedByNino(AuditResult result) {
        results = newArrayList(result);
    }

    public void add(AuditResult result) {
        results.add(result);
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    public Stream<AuditResult> stream() {
        return results.stream();
    }

    public AuditResult get(int i) {
        return results.get(i);
    }

    public int size() {
        return results.size();
    }
}

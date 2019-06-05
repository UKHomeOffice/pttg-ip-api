package uk.gov.digital.ho.proving.income.audit;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

@Component
public
class AuditResultTypeComparator implements Comparator<AuditResultType> {
    private static final List<AuditResultType> naturalOrder
        = Arrays.asList(ERROR, NOTFOUND, FAIL, PASS);

    @Override
    public int compare(AuditResultType first, AuditResultType second) {
        int firstIndex = naturalOrder.indexOf(first);
        int secondIndex = naturalOrder.indexOf(second);
        return Integer.compare(firstIndex, secondIndex);
    }
}

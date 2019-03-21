package uk.gov.digital.ho.proving.income.audit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

public class AuditResultTypeComparatorTest {

    private AuditResultTypeComparator comparator = new AuditResultTypeComparator();

    @Test
    public void compare_equals() {
        assertThat(comparator.compare(PASS, PASS)).isEqualTo(0);
        assertThat(comparator.compare(FAIL, FAIL)).isEqualTo(0);
    }

    @Test
    public void compare_greaterThan() {
        assertThat(comparator.compare(PASS, FAIL)).isEqualTo(1);
        assertThat(comparator.compare(FAIL, NOTFOUND)).isEqualTo(1);
        assertThat(comparator.compare(NOTFOUND, ERROR)).isEqualTo(1);
        assertThat(comparator.compare(PASS, ERROR)).isEqualTo(1);
    }

    @Test
    public void compare_lessThan() {
        assertThat(comparator.compare(FAIL, PASS)).isEqualTo(-1);
        assertThat(comparator.compare(NOTFOUND, FAIL)).isEqualTo(-1);
        assertThat(comparator.compare(ERROR, NOTFOUND)).isEqualTo(-1);
        assertThat(comparator.compare(ERROR, PASS)).isEqualTo(-1);
    }

    @Test
    public void sort() {
        List<AuditResultType> results = Arrays.asList(ERROR, PASS, FAIL, ERROR, PASS, NOTFOUND);
        Collections.sort(results, comparator);
        assertThat(results).containsExactly(ERROR, ERROR, NOTFOUND, FAIL, PASS, PASS);
    }

    @Test
    public void max() {
        List<AuditResultType> results = Arrays.asList(ERROR, PASS, FAIL, ERROR, PASS, NOTFOUND);
        AuditResultType max = results.stream().max(comparator).get();
        assertThat(max).isEqualTo(PASS);
    }
}

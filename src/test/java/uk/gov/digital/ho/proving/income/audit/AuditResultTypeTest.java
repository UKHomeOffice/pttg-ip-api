package uk.gov.digital.ho.proving.income.audit;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

public class AuditResultTypeTest {

    @Test
    public void sort_naturalOrdering_hasExpectedOrdering() {
        List<AuditResultType> auditResultTypes = asList(
            PASS,
            ERROR,
            FAIL,
            NOTFOUND,
            NOTFOUND,
            PASS,
            FAIL,
            ERROR
        );

        List<AuditResultType> expectedSorted = asList(
            PASS,
            PASS,
            FAIL,
            FAIL,
            NOTFOUND,
            NOTFOUND,
            ERROR,
            ERROR
        );

        assertThat(auditResultTypes.stream().sorted().collect(Collectors.toList()))
            .isEqualTo(expectedSorted);
    }
}

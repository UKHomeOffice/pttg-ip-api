package uk.gov.digital.ho.proving.income.audit;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

public class AuditResultComparatorTest {

    private AuditResultComparator auditResultComparator = new AuditResultComparator(new AuditResultTypeComparator());

    @Test
    public void compareTo_same_equals() {
        AuditResult auditResult1 = new AuditResult("any_correlation_id", now(), "any_nino", PASS);
        AuditResult auditResult2 = new AuditResult("any_correlation_id", now(), "any_nino", PASS);

        assertThat(auditResultComparator.compare(auditResult1, auditResult2)).isEqualTo(0);
    }

    @Test
    public void compareTo_betterResult_greater() {
        AuditResult auditResult1 = new AuditResult("any_correlation_id", now(), "any_nino", PASS);
        AuditResult auditResult2 = new AuditResult("any_correlation_id", now(), "any_nino", FAIL);

        assertThat(auditResultComparator.compare(auditResult1, auditResult2)).isEqualTo(1);
    }

    @Test
    public void compareTo_worseResult_lesser() {
        AuditResult auditResult1 = new AuditResult("any_correlation_id", now(), "any_nino", ERROR);
        AuditResult auditResult2 = new AuditResult("any_correlation_id", now(), "any_nino", FAIL);

        assertThat(auditResultComparator.compare(auditResult1, auditResult2)).isEqualTo(-1);
    }

    @Test
    public void compareTo_sameStatus_moreRecent_greater() {
        AuditResult auditResult1 = new AuditResult("any_correlation_id", now().plusDays(1), "any_nino", FAIL);
        AuditResult auditResult2 = new AuditResult("any_correlation_id", now(), "any_nino", FAIL);

        assertThat(auditResultComparator.compare(auditResult1, auditResult2)).isEqualTo(1);
    }

    @Test
    public void compareTo_sameStatus_older_lesser() {
        AuditResult auditResult1 = new AuditResult("any_correlation_id", now(), "any_nino", FAIL);
        AuditResult auditResult2 = new AuditResult("any_correlation_id", now().plusDays(1), "any_nino", FAIL);

        assertThat(auditResultComparator.compare(auditResult1, auditResult2)).isEqualTo(-1);
    }

    @Test
    public void sort() {
        AuditResult auditResult1 = new AuditResult("any_correlation_id_1", now(), "any_nino", PASS);
        AuditResult auditResult2 = new AuditResult("any_correlation_id_2", now().plusDays(1), "any_nino", FAIL);
        AuditResult auditResult3 = new AuditResult("any_correlation_id_3", now(), "any_nino", FAIL);

        List<AuditResult> results = Arrays.asList(auditResult1, auditResult2,  auditResult3);
        results.sort(auditResultComparator);

        assertThat(results.get(0).correlationId()).isEqualTo("any_correlation_id_3");
        assertThat(results.get(1).correlationId()).isEqualTo("any_correlation_id_2");
        assertThat(results.get(2).correlationId()).isEqualTo("any_correlation_id_1");
    }

    @Test
    public void max() {
        AuditResult auditResult1 = new AuditResult("any_correlation_id_1", now(), "any_nino", PASS);
        AuditResult auditResult2 = new AuditResult("any_correlation_id_2", now().plusDays(1), "any_nino", FAIL);
        AuditResult auditResult3 = new AuditResult("any_correlation_id_3", now(), "any_nino", FAIL);

        List<AuditResult> results = Arrays.asList(auditResult1, auditResult2,  auditResult3);
        AuditResult max = results.stream().max(auditResultComparator).get();

        assertThat(max.correlationId()).isEqualTo("any_correlation_id_1");
    }

}

package uk.gov.digital.ho.proving.income.audit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditSummaryCalculator.summarise;

public class AuditSummaryCalculatorTest {

    @Test
    public void summarise_nothing_zeroResults() {
        AuditSummary summary = summarise(new ArrayList<>());
        assertThat(summary.passed()).isEqualTo(0);
        assertThat(summary.notPassed()).isEqualTo(0);
        assertThat(summary.notFound()).isEqualTo(0);
        assertThat(summary.failed()).isEqualTo(0);
    }

}

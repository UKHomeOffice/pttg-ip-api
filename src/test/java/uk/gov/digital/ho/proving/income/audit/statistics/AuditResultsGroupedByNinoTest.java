package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditResultsGroupedByNinoTest {


    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final AuditResultType ANY_RESULT_TYPE = AuditResultType.PASS;
    private static final String ANY_NINO = "BB112233A";
    private static final AuditResult ANY_RESULT = new AuditResult("any correlation ID", ANY_DATE, ANY_NINO, ANY_RESULT_TYPE);

    @Test
    public void constructor_someNino_hasNino() {
        String someNino = "AA112233A";
        AuditResult someResult = new AuditResult("any correlation ID", ANY_DATE, someNino, ANY_RESULT_TYPE);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(someResult);
        assertThat(groupedResults.nino()).isEqualTo(someNino);
    }

    @Test
    public void constructor_someResult_setAsResults() {
        AuditResult someResult = new AuditResult("any correlation ID", ANY_DATE, ANY_NINO, ANY_RESULT_TYPE);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(someResult);
        assertThat(groupedResults.results()).containsExactly(someResult);
    }

    @Test
    public void add_someResult_addedToResults() {
        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(ANY_RESULT);

        AuditResult someResult = new AuditResult("some correlation ID", LocalDate.now(), "AA112233A", AuditResultType.FAIL);
        groupedResults.add(someResult);
        assertThat(groupedResults.results())
            .hasSize(2)
            .contains(someResult);
    }
}

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
    public void constructor_noArgs_emptyResults() {
        assertThat(new AuditResultsGroupedByNino().results()).isEmpty();
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

    @Test
    public void isEmpty_empty_returnTrue() {
        AuditResultsGroupedByNino emptyGroupedResults = new AuditResultsGroupedByNino();
        assertThat(emptyGroupedResults.isEmpty()).isTrue();
    }

    @Test
    public void isEmpty_nonEmpty_returnFalse() {
        AuditResultsGroupedByNino nonEmptyGroupedResults = new AuditResultsGroupedByNino(ANY_RESULT);
        assertThat(nonEmptyGroupedResults.isEmpty()).isFalse();
    }

    @Test
    public void stream_someResults_streamResults() {
        AuditResult someResult = new AuditResult("some correlation ID", LocalDate.now(), "AA112233A", AuditResultType.PASS);
        AuditResult someOtherResult = new AuditResult("some other correlation ID", LocalDate.now(), "AA112233A", AuditResultType.FAIL);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(someResult);
        groupedResults.add(someOtherResult);

        assertThat(groupedResults.stream()).containsExactlyInAnyOrder(someResult, someOtherResult);
    }

    @Test
    public void get_zero_returnFirstElement() {
        AuditResult someResult = new AuditResult("some correlation ID", LocalDate.now(), "AA112233A", AuditResultType.PASS);
        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(someResult);

        assertThat(groupedResults.get(0)).isEqualTo(someResult);
    }

    @Test
    public void get_one_returnSecondElement() {
        AuditResult someResult = new AuditResult("some correlation ID", LocalDate.now(), "AA112233A", AuditResultType.PASS);
        AuditResult someOtherResult = new AuditResult("some other correlation ID", LocalDate.now(), "AA112233A", AuditResultType.FAIL);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(someResult);
        groupedResults.add(someOtherResult);

        assertThat(groupedResults.get(1)).isEqualTo(someOtherResult);
    }

    @Test
    public void size_empty_returnZero() {
        AuditResultsGroupedByNino emptyResults = new AuditResultsGroupedByNino();
        assertThat(emptyResults.size()).isEqualTo(0);
    }

    @Test
    public void size_singleElement_returnOne() {
        AuditResultsGroupedByNino emptyResults = new AuditResultsGroupedByNino(ANY_RESULT);
        assertThat(emptyResults.size()).isEqualTo(1);
    }

    @Test
    public void size_twoElements_returnTwo() {
        AuditResultsGroupedByNino emptyResults = new AuditResultsGroupedByNino(ANY_RESULT);
        emptyResults.add(ANY_RESULT);
        assertThat(emptyResults.size()).isEqualTo(2);
    }
}

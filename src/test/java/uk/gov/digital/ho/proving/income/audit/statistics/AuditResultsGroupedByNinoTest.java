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
    private static final int ANY_INT = 9;


    @Test
    public void constructor_noArgs_empty() {
        assertThat(new AuditResultsGroupedByNino()).isEmpty();
    }

    @Test
    public void constructor_someResult_setAsResults() {
        AuditResult someResult = new AuditResult("any correlation ID", ANY_DATE, ANY_NINO, ANY_RESULT_TYPE);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(someResult);
        assertThat(groupedResults).containsExactly(someResult);
    }

    @Test
    public void latestDate_noDates_returnNull() {
        AuditResultsGroupedByNino emptyResult = new AuditResultsGroupedByNino();
        assertThat(emptyResult.latestDate()).isNull();
    }

    @Test
    public void latestDate_oneDate_returnDate() {
        LocalDate someDate = LocalDate.now();
        AuditResult someResult = resultFor(someDate);

        AuditResultsGroupedByNino singleResult = new AuditResultsGroupedByNino(someResult);
        assertThat(singleResult.latestDate()).isEqualTo(someDate);
    }

    @Test
    public void latestDate_multipleDates_returnLatest() {
        LocalDate earlierDate = LocalDate.now();
        LocalDate middleDate = earlierDate.plusDays(1);
        LocalDate laterDate = middleDate.plusDays(1);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(resultFor(earlierDate));
        groupedResults.add(resultFor(laterDate));
        groupedResults.add(resultFor(middleDate));

        assertThat(groupedResults.latestDate()).isEqualTo(laterDate);
    }

    @Test
    public void resultAfterCutoff_empty_alwaysFalse() {
        AuditResultsGroupedByNino emptyResults = new AuditResultsGroupedByNino();
        assertThat(emptyResults.resultAfterCutoff(ANY_INT, ANY_RESULT)).isFalse();
    }

    @Test
    public void resultAfterCutoff_oneDayBeforeCutoff_false() {
        LocalDate someDate = LocalDate.now();
        int someCutoffDays = 5;
        LocalDate beforeCutoff = someDate.plusDays(someCutoffDays - 1);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(resultFor(someDate));

        AuditResult resultBeforeCutoff = resultFor(beforeCutoff);
        assertThat(groupedResults.resultAfterCutoff(someCutoffDays, resultBeforeCutoff)).isFalse();
    }

    @Test
    public void resultAfterCutoff_cutOffDay_false() {
        LocalDate someDate = LocalDate.now();
        int someCutoffDays = 5;
        LocalDate onCutoff = someDate.plusDays(someCutoffDays);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(resultFor(someDate));

        AuditResult resultOnCutoff = resultFor(onCutoff);
        assertThat(groupedResults.resultAfterCutoff(someCutoffDays, resultOnCutoff)).isFalse();
    }

    @Test
    public void resultAfterCutoff_afterCutOffDay_true() {
        LocalDate someDate = LocalDate.now();
        int someCutoffDays = 5;
        LocalDate afterCutoff = someDate.plusDays(someCutoffDays + 1);

        AuditResultsGroupedByNino groupedResults = new AuditResultsGroupedByNino(resultFor(someDate));

        AuditResult resultAfterCutoff = resultFor(afterCutoff);
        assertThat(groupedResults.resultAfterCutoff(someCutoffDays, resultAfterCutoff)).isTrue();
    }

    private AuditResult resultFor(LocalDate date) {
        return new AuditResult("any correlation ID", date, ANY_NINO, ANY_RESULT_TYPE);
    }
}

package uk.gov.digital.ho.proving.income.audit.statistics;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.audit.ArchivedResult;
import uk.gov.digital.ho.proving.income.audit.AuditResult;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

public class PassStatisticsCalculatorTest {

    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final LocalDate FROM_DATE = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TO_DATE = LocalDate.of(2019, Month.JANUARY, 31);
    private static final LocalDate IN_RANGE = FROM_DATE.plusDays(2);

    private static final List<AuditResult> ANY_AUDIT_RESULTS = emptyList();
    private static final List<ArchivedResult> ANY_ARCHIVED_RESULTS = emptyList();

    private PassStatisticsCalculator accumulator = new PassStatisticsCalculator();

    @Test
    public void result_givenFromDate_inResult() {
        LocalDate fromDate = LocalDate.of(2019, 2, 3);

        assertThat(accumulator.result(ANY_AUDIT_RESULTS, ANY_ARCHIVED_RESULTS, fromDate, ANY_DATE).getFromDate())
            .isEqualTo(fromDate);
    }

    @Test
    public void result_givenToDate_inResult() {
        LocalDate toDate = LocalDate.of(2019, 3, 4);

        assertThat(accumulator.result(ANY_AUDIT_RESULTS, ANY_ARCHIVED_RESULTS, ANY_DATE, toDate).getToDate())
            .isEqualTo(toDate);
    }

    @Test
    public void result_emptyList_emptyResult() {
        assertThat(accumulator.result(emptyList(), emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_onePassInRange_onePassInStatistics() {
        List<AuditResult> singlePassInRange = singletonList(new AuditResult("any correlation id", IN_RANGE, "any nino", PASS));

        assertThat(accumulator.result(singlePassInRange, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(1, 0, 0, 0));
    }

    @Test
    public void result_oneFailInRange_oneFailInStatistics() {
        List<AuditResult> singleFailInRange = singletonList(new AuditResult("any correlation id", IN_RANGE, "any nino", FAIL));

        assertThat(accumulator.result(singleFailInRange, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void result_oneNotFoundInRange_oneNotFoundInStatistics() {
        List<AuditResult> singleNotFoundInRange = singletonList(new AuditResult("any correlation id", IN_RANGE, "any nino", NOTFOUND));

        assertThat(accumulator.result(singleNotFoundInRange, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void result_oneErrorInRange_oneErrorInStatistics() {
        List<AuditResult> singleErrorInRange = singletonList(new AuditResult("any correlation id", IN_RANGE, "any nino", ERROR));

        assertThat(accumulator.result(singleErrorInRange, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 1));
    }

    @Test
    public void result_tooEarly_notCounted() {
        List<AuditResult> tooEarlyResult = singletonList(new AuditResult("any correlation id", FROM_DATE.minusDays(1), "any nino", PASS));

        assertThat(accumulator.result(tooEarlyResult, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_firstDay_counted() {
        List<AuditResult> resultOnFirstDay = singletonList(new AuditResult("any correlation id", FROM_DATE, "any nino", FAIL));

        assertThat(accumulator.result(resultOnFirstDay, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void result_lastDay_notCounted() {
        List<AuditResult> resultByLastDay = singletonList(new AuditResult("any correlation id", TO_DATE, "any nino", NOTFOUND));

        assertThat(accumulator.result(resultByLastDay, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void result_tooLate_counted() {
        List<AuditResult> tooLateResult = singletonList(new AuditResult("any correlation id", TO_DATE.plusDays(1), "any nino", ERROR));

        assertThat(accumulator.result(tooLateResult, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_allOutsideRange_emptyStatistics() {
        List<AuditResult> allOutsideRange = asList(
            new AuditResult("any correlation id", FROM_DATE.minusDays(1), "nino 1", PASS),
            new AuditResult("any correlation id", FROM_DATE.minusDays(1), "nino 2", FAIL),
            new AuditResult("any correlation id", TO_DATE.plusDays(1), "nino 3", NOTFOUND),
            new AuditResult("any correlation id", TO_DATE.plusDays(1), "nino 4", ERROR)
                                                  );

        assertThat(accumulator.result(allOutsideRange, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_multipleResultsInAndOutOfRange_onlyInRangeIncluded() {
        AuditResult tooEarly = new AuditResult("any correlation id", FROM_DATE.minusDays(1), "nino 1", PASS);
        AuditResult passInRange = new AuditResult("any correlation id", FROM_DATE, "nino 2", PASS);

        AuditResult errorInRange = new AuditResult("any correlation id", FROM_DATE, "nino 3", ERROR);
        AuditResult error2InRange = new AuditResult("any correlation id", TO_DATE, "nino 4", ERROR);

        AuditResult notFoundInRange = new AuditResult("any correlation id", TO_DATE, "nino 5", NOTFOUND);
        AuditResult tooLate = new AuditResult("any correlation id", TO_DATE.plusDays(1), "nino 6", FAIL);

        List<AuditResult> results = asList(tooEarly, passInRange, errorInRange, error2InRange, notFoundInRange, tooLate);
        assertThat(accumulator.result(results, emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(1, 0, 1, 2));
    }

    @Test
    public void result_oneDayArchivedResults_addToTotal() {
        List<AuditResult> results = asList(
            new AuditResult("any correlation id", IN_RANGE, "nino 1", PASS),
            new AuditResult("any correlation id", IN_RANGE, "nino 2", FAIL),
            new AuditResult("any correlation id", IN_RANGE, "nino 3", NOTFOUND),
            new AuditResult("any correlation id", IN_RANGE, "nino 4", ERROR)
                                          );

        List<ArchivedResult> archivedResults = singletonList(new ArchivedResult(ImmutableMap.<String, Integer>builder()
            .put(String.valueOf(PASS), 5)
            .put(String.valueOf(FAIL), 6)
            .put(String.valueOf(NOTFOUND), 7)
            .put(String.valueOf(ERROR), 8)
            .build()));

        assertThat(accumulator.result(results, archivedResults, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(6, 7, 8, 9));
    }

    @Test
    public void result_multipleDaysOfArchivedResults_addToTotal() {
        List<AuditResult> results = asList(
            new AuditResult("any correlation id", IN_RANGE, "nino 1", PASS),
            new AuditResult("any correlation id", IN_RANGE, "nino 2", FAIL),
            new AuditResult("any correlation id", IN_RANGE, "nino 3", NOTFOUND),
            new AuditResult("any correlation id", IN_RANGE, "nino 4", ERROR)
                                          );

        List<ArchivedResult> archivedResults = asList(new ArchivedResult(ImmutableMap.<String, Integer>builder()
                .put(String.valueOf(PASS), 5)
                .put(String.valueOf(FAIL), 6)
                .put(String.valueOf(NOTFOUND), 7)
                .put(String.valueOf(ERROR), 8)
                .build()),
            new ArchivedResult(ImmutableMap.<String, Integer>builder()
                .put(String.valueOf(PASS), 1)
                .put(String.valueOf(NOTFOUND), 3)
                .put(String.valueOf(ERROR), 4)
                .build())
        );

        assertThat(accumulator.result(results, archivedResults, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(7, 7, 11, 13));
    }

    private PassRateStatistics statisticsForCounts(int passes, int failures, int notFound, int errors) {
        int totalRequests = passes + failures + notFound + errors;
        return new PassRateStatistics(FROM_DATE,
            TO_DATE,
            totalRequests,
            passes,
            failures,
            notFound,
            errors);
    }
}

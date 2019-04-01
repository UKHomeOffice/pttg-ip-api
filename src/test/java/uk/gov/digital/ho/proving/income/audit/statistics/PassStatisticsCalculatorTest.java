package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

public class PassStatisticsCalculatorTest {

    private static final LocalDate SOME_DATE = LocalDate.now();
    private static final LocalDate FROM_DATE = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TO_DATE = LocalDate.of(2019, Month.JANUARY, 31);
    private static final LocalDate IN_RANGE = FROM_DATE.plusDays(2);

    private PassStatisticsCalculator accumulator = new PassStatisticsCalculator();

    @Test
    public void result_givenFromDate_inResult() {
        List<AuditResultByNino> someList = emptyList();

        LocalDate fromDate = LocalDate.of(2019, 2, 3);
        assertThat(accumulator.result(someList, fromDate, SOME_DATE).fromDate()).isEqualTo(fromDate);
    }

    @Test
    public void result_givenToDate_inResult() {
        List<AuditResultByNino> someList = emptyList();

        LocalDate toDate = LocalDate.of(2019, 3, 4);
        assertThat(accumulator.result(someList, SOME_DATE, toDate).toDate()).isEqualTo(toDate);
    }

    @Test
    public void result_emptyList_emptyResult() {
        assertThat(accumulator.result(emptyList(), FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_onePassInRange_onePassInStatistics() {
        List<AuditResultByNino> singlePassInRange = singletonList(new AuditResultByNino("some nino", emptyList(), IN_RANGE, PASS));

        assertThat(accumulator.result(singlePassInRange, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(1, 0, 0, 0));
    }

    @Test
    public void result_oneFailInRange_oneFailInStatistics() {
        List<AuditResultByNino> singleFailInRange = singletonList(new AuditResultByNino("some nino", emptyList(), IN_RANGE, FAIL));

        assertThat(accumulator.result(singleFailInRange, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void result_oneNotFoundInRange_oneNotFoundInStatistics() {
        List<AuditResultByNino> singleNotFoundInRange = singletonList(new AuditResultByNino("some nino", emptyList(), IN_RANGE, NOTFOUND));

        assertThat(accumulator.result(singleNotFoundInRange, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void result_oneErrorInRange_oneErrorInStatistics() {
        List<AuditResultByNino> singleErrorInRange = singletonList(new AuditResultByNino("some nino", emptyList(), IN_RANGE, ERROR));

        assertThat(accumulator.result(singleErrorInRange, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 1));
    }

    @Test
    public void result_tooEarly_notCounted() {
        List<AuditResultByNino> tooEarlyResult = singletonList(new AuditResultByNino("some nino", emptyList(), FROM_DATE.minusDays(1), PASS));

        assertThat(accumulator.result(tooEarlyResult, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_firstDay_counted() {
        List<AuditResultByNino> resultOnFirstDay = singletonList(new AuditResultByNino("some nino", emptyList(), FROM_DATE, FAIL));

        assertThat(accumulator.result(resultOnFirstDay, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void result_lastDay_counted() {
        List<AuditResultByNino> resultByLastDay = singletonList(new AuditResultByNino("some nino", emptyList(), TO_DATE, NOTFOUND));

        assertThat(accumulator.result(resultByLastDay, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void result_tooLate_counted() {
        List<AuditResultByNino> tooLateResult = singletonList(new AuditResultByNino("some nino", emptyList(), TO_DATE.plusDays(1), ERROR));

        assertThat(accumulator.result(tooLateResult, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_allOutsideRange_emptyStatistics() {
        List<AuditResultByNino> allOutsideRange = asList(
            new AuditResultByNino("nino 1", emptyList(), FROM_DATE.minusDays(1), PASS),
            new AuditResultByNino("nino 2", emptyList(), FROM_DATE.minusDays(1), FAIL),
            new AuditResultByNino("nino 3", emptyList(), TO_DATE.plusDays(1), NOTFOUND),
            new AuditResultByNino("nino 4", emptyList(), TO_DATE.plusDays(1), ERROR)
        );

        assertThat(accumulator.result(allOutsideRange, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_multipleResultsInAndOutOfRange_onlyInRangeIncluded() {
        AuditResultByNino tooEarly = new AuditResultByNino("nino 1", emptyList(), FROM_DATE.minusDays(1), PASS);
        AuditResultByNino passInRange = new AuditResultByNino("nino 2", emptyList(), FROM_DATE, PASS);

        AuditResultByNino errorInRange = new AuditResultByNino("nino 3", emptyList(), FROM_DATE, ERROR);
        AuditResultByNino error2InRange = new AuditResultByNino("nino 4", emptyList(), TO_DATE, ERROR);

        AuditResultByNino notFoundInRange = new AuditResultByNino("nino 5", emptyList(), TO_DATE, NOTFOUND);
        AuditResultByNino tooLate = new AuditResultByNino("nino 6", emptyList(), TO_DATE.plusDays(1), FAIL);

        List<AuditResultByNino> results = asList(tooEarly, passInRange, errorInRange, error2InRange, notFoundInRange, tooLate);
        assertThat(accumulator.result(results, FROM_DATE, TO_DATE))
            .isEqualTo(statisticsForCounts(1, 0, 1, 2));
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

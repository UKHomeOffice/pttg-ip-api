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

    private PassStatisticsCalculator accumulator = new PassStatisticsCalculator(FROM_DATE, TO_DATE);

    @Test
    public void result_givenFromDate_inResult() {
        List<AuditResultByNino> someList = emptyList();

        LocalDate fromDate = LocalDate.of(2019, 2, 3);
        PassStatisticsCalculator accumulator = new PassStatisticsCalculator(fromDate, SOME_DATE);

        assertThat(accumulator.result(someList).fromDate()).isEqualTo(fromDate);
    }

    @Test
    public void result_givenToDate_inResult() {
        List<AuditResultByNino> someList = emptyList();

        LocalDate toDate = LocalDate.of(2019, 3, 4);
        PassStatisticsCalculator accumulator = new PassStatisticsCalculator(SOME_DATE, toDate);

        assertThat(accumulator.result(someList).toDate()).isEqualTo(toDate);
    }

    @Test
    public void result_emptyList_emptyResult() {
        assertThat(accumulator.result(emptyList()))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_onePassInRange_onePassInStatistics() {
        assertThat(accumulator.result(singletonList(new AuditResultByNino("some nino", emptyList(), IN_RANGE, PASS))))
            .isEqualTo(statisticsForCounts(1, 0, 0, 0));
    }

    @Test
    public void result_oneFailInRange_oneFailInStatistics() {
        assertThat(accumulator.result(singletonList(new AuditResultByNino("some nino", emptyList(), IN_RANGE, FAIL))))
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void result_oneNotFoundInRange_oneNotFoundInStatistics() {
        assertThat(accumulator.result(singletonList(new AuditResultByNino("some nino", emptyList(), IN_RANGE, NOTFOUND))))
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void result_oneErrorInRange_oneErrorInStatistics() {
        assertThat(accumulator.result(singletonList(new AuditResultByNino("some nino", emptyList(), IN_RANGE, ERROR))))
            .isEqualTo(statisticsForCounts(0, 0, 0, 1));
    }

    @Test
    public void result_tooEarly_notCounted() {
        assertThat(accumulator.result(singletonList(new AuditResultByNino("some nino", emptyList(), FROM_DATE.minusDays(1), PASS))))
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_firstDay_counted() {
        assertThat(accumulator.result(singletonList(new AuditResultByNino("some nino", emptyList(), FROM_DATE, FAIL))))
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void result_lastDay_counted() {
        assertThat(accumulator.result(singletonList(new AuditResultByNino("some nino", emptyList(), TO_DATE, NOTFOUND))))
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void result_tooLate_counted() {
        assertThat(accumulator.result(singletonList(new AuditResultByNino("some nino", emptyList(), TO_DATE.plusDays(1), ERROR))))
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

        assertThat(accumulator.result(allOutsideRange))
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

        assertThat(accumulator.result(asList(tooEarly, passInRange, errorInRange, error2InRange, notFoundInRange, tooLate)))
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

package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;


public class PassStatisticsAccumulatorTest {

    private static final LocalDate SOME_DATE = LocalDate.now();
    private static final LocalDate FROM_DATE = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TO_DATE = LocalDate.of(2019, Month.JANUARY, 31);

    private PassStatisticsAccumulator accumulator = new PassStatisticsAccumulator(FROM_DATE, TO_DATE);


    @Test
    public void result_givenFromDate_inResult() {
        LocalDate fromDate = LocalDate.of(2019, 2, 3);
        PassStatisticsAccumulator accumulator = new PassStatisticsAccumulator(fromDate, SOME_DATE);

        assertThat(accumulator.result().fromDate()).isEqualTo(fromDate);
    }

    @Test
    public void result_givenToDate_inResult() {
        LocalDate toDate = LocalDate.of(2019, 3, 4);
        PassStatisticsAccumulator accumulator = new PassStatisticsAccumulator(SOME_DATE, toDate);

        assertThat(accumulator.result().toDate()).isEqualTo(toDate);
    }

    @Test
    public void result_noResults_allZeros() {
        PassStatisticsAccumulator accumulator = new PassStatisticsAccumulator(SOME_DATE, SOME_DATE);
        PassRateStatistics result = accumulator.result();

        assertThat(result.totalRequests())
            .isEqualTo(result.passes())
            .isEqualTo(result.failures())
            .isEqualTo(result.notFound())
            .isEqualTo(result.errors())
            .isEqualTo(0);
    }

    @Test
    public void result_onePassInRange_expectedResult() {
        accumulator.accumulate(singleResultInRange(PASS));
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(1, 0, 0, 0));
    }

    @Test
    public void result_oneFailureInRange_expectedResult() {
        accumulator.accumulate(singleResultInRange(FAIL));
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void result_oneNotFoundInRange_expectedResult() {
        accumulator.accumulate(singleResultInRange(NOTFOUND));
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void result_oneErrorInRange_expectedResult() {
        accumulator.accumulate(singleResultInRange(ERROR));
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 1));
    }

    private List<AuditResultByNino> singleResultInRange(AuditResultType resultType) {
        return singletonList(new AuditResultByNino("some nino",
            emptyList(),
            LocalDate.of(2019, Month.JANUARY, 2),
            resultType));
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

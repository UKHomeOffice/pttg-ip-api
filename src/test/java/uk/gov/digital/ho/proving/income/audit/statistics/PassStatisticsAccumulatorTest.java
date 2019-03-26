package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static java.util.Arrays.asList;
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

    @Test
    public void result_twoOfEachStatusInRange_expectedResult() {
        List<AuditResultByNino> records = asList(
            resultInRange("AA111111A", PASS),
            resultInRange("BA111111A", FAIL),
            resultInRange("CA111111A", NOTFOUND),
            resultInRange("DA111111A", ERROR),
            resultInRange("EA111111A", PASS),
            resultInRange("FA111111A", FAIL),
            resultInRange("GA111111A", NOTFOUND),
            resultInRange("HA111111A", ERROR)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(2, 2, 2, 2));
    }

    @Test
    public void result_onePassInRangeOneBefore_onlyCountInRange() {
        List<AuditResultByNino> records = asList(
            new AuditResultByNino("some nino", emptyList(), FROM_DATE, PASS),
            new AuditResultByNino("some nino", emptyList(), FROM_DATE.minusDays(1), PASS)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(1, 0, 0, 0));
    }

    @Test
    public void result_onePassInRangeOneAfter_doNotCount() {
        List<AuditResultByNino> records = asList(
            new AuditResultByNino("some nino", emptyList(), TO_DATE, PASS),
            new AuditResultByNino("some nino", emptyList(), TO_DATE.plusDays(1), PASS)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_oneFailureInRangeOneBefore_onlyCountInRange() {
        List<AuditResultByNino> records = asList(
            new AuditResultByNino("some nino", emptyList(), FROM_DATE, FAIL),
            new AuditResultByNino("some nino", emptyList(), FROM_DATE.minusDays(1), FAIL)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void result_oneFailureInRangeOneAfter_doNotCount() {
        List<AuditResultByNino> records = asList(
            new AuditResultByNino("some nino", emptyList(), TO_DATE, FAIL),
            new AuditResultByNino("some nino", emptyList(), TO_DATE.plusDays(1), FAIL)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_oneNotFoundInRangeOneBefore_onlyCountInRange() {
        List<AuditResultByNino> records = asList(
            new AuditResultByNino("some nino", emptyList(), FROM_DATE, NOTFOUND),
            new AuditResultByNino("some nino", emptyList(), FROM_DATE.minusDays(1), NOTFOUND)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void result_oneNotFoundInRangeOneAfter_doNotCount() {
        List<AuditResultByNino> records = asList(
            new AuditResultByNino("some nino", emptyList(), TO_DATE, NOTFOUND),
            new AuditResultByNino("some nino", emptyList(), TO_DATE.plusDays(1), NOTFOUND)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void result_oneErrorInRangeOneBefore_onlyCountInRange() {
        List<AuditResultByNino> records = asList(
            new AuditResultByNino("some nino", emptyList(), FROM_DATE, ERROR),
            new AuditResultByNino("some nino", emptyList(), FROM_DATE.minusDays(1), ERROR)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 1));
    }

    @Test
    public void result_oneErrorInRangeOneAfter_doNotCount() {
        List<AuditResultByNino> records = asList(
            new AuditResultByNino("some nino", emptyList(), TO_DATE, ERROR),
            new AuditResultByNino("some nino", emptyList(), TO_DATE.plusDays(1), ERROR)
        );
        accumulator.accumulate(records);
        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void accumulate_firstPassTooEarlySecondInRange_onlyCountOne() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), PASS));
        accumulator.accumulate(singleResult(FROM_DATE, PASS));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(1, 0, 0, 0));
    }

    @Test
    public void accumulate_firstPassInRangeSecondTooLate_notCounted() {
        accumulator.accumulate(singleResult(TO_DATE, PASS));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), PASS));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void accumulate_firstFailureTooEarlySecondInRange_onlyCountOne() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), FAIL));
        accumulator.accumulate(singleResult(FROM_DATE, FAIL));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void accumulate_firstFailureInRangeSecondTooLate_notCounted() {
        accumulator.accumulate(singleResult(TO_DATE, FAIL));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), FAIL));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void accumulate_firstNotFoundTooEarlySecondInRange_onlyCountOne() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), NOTFOUND));
        accumulator.accumulate(singleResult(FROM_DATE, NOTFOUND));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void accumulate_firstNotFoundInRangeSecondTooLate_notCounted() {
        accumulator.accumulate(singleResult(TO_DATE, NOTFOUND));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), NOTFOUND));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void accumulate_firstErrorTooEarlySecondInRange_onlyCountOne() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), ERROR));
        accumulator.accumulate(singleResult(FROM_DATE, ERROR));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 1));
    }

    @Test
    public void accumulate_firstErrorInRangeSecondTooLate_notCounted() {
        accumulator.accumulate(singleResult(TO_DATE, ERROR));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), ERROR));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    // TODO OJR EE-16843 Test keeping best result logic
    // TODO OJR EE-16843 Test single call to accumulator with in and out of range data for same nino

    private List<AuditResultByNino> singleResult(LocalDate date, AuditResultType resultType) {
        return singletonList(result("some nino", date, resultType));
    }

    private List<AuditResultByNino> singleResultInRange(AuditResultType resultType) {
        return singletonList(resultInRange("some nino", resultType));
    }

    private AuditResultByNino resultInRange(String nino, AuditResultType resultType) {
        return result(nino, LocalDate.of(2019, Month.JANUARY, 2), resultType);
    }

    private AuditResultByNino result(String nino, LocalDate date, AuditResultType resultType) {
        return new AuditResultByNino(nino,
            emptyList(),
            date,
            resultType);
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

package uk.gov.digital.ho.proving.income.audit.statistics;

import org.assertj.core.util.Lists;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;


public class PassStatisticsAccumulatorMultipleCallsTest {

    private static final LocalDate FROM_DATE = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TO_DATE = LocalDate.of(2019, Month.JANUARY, 31);
    private static final LocalDate IN_RANGE = FROM_DATE.plusDays(1);

    private PassStatisticsAccumulator accumulator = new PassStatisticsAccumulator(FROM_DATE, TO_DATE);

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

    @Test
    public void accumulate_passOutOfRangeFailInRange_notCountedAsPassBestResult() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), PASS));
        accumulator.accumulate(singleResult(IN_RANGE, FAIL));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), PASS));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void accumulate_passInRangeFailOutOfRange_countedAsPassBestResult() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), FAIL));
        accumulator.accumulate(singleResult(IN_RANGE, PASS));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), FAIL));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(1, 0, 0, 0));
    }

    @Test
    public void accumulate_failOutOfRangeNotFoundInRange_notCountedAsFailBestResult() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), FAIL));
        accumulator.accumulate(singleResult(IN_RANGE, NOTFOUND));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), FAIL));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void accumulate_failInRangeNotFoundOutOfRange_countedAsFailBestResult() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), NOTFOUND));
        accumulator.accumulate(singleResult(IN_RANGE, FAIL));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), NOTFOUND));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 1, 0, 0));
    }

    @Test
    public void accumulate_notFoundOutOfRangeErrorInRange_notCountedAsNotFoundBestResult() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), NOTFOUND));
        accumulator.accumulate(singleResult(IN_RANGE, ERROR));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), NOTFOUND));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 0, 0));
    }

    @Test
    public void accumulate_notFoundInRangeErrorOutOfRange_countedAsNotFoundBestResult() {
        accumulator.accumulate(singleResult(FROM_DATE.minusDays(1), ERROR));
        accumulator.accumulate(singleResult(IN_RANGE, NOTFOUND));
        accumulator.accumulate(singleResult(TO_DATE.plusDays(1), ERROR));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(0, 0, 1, 0));
    }

    @Test
    public void acceptanceTest_multipleNinosAndStatuses_expectedStatistics() {
        String nino1 = "AA111111A";
        String nino2 = "BB222222B";
        String nino3 = "CC333333C";
        String nino4 = "DD444444D";

        // Will not count towards stats
        List<AuditResultByNino> allOutOfRange = asList(
            new AuditResultByNino(nino1, emptyList(), FROM_DATE.minusDays(2), PASS),
            new AuditResultByNino(nino1, emptyList(), FROM_DATE.minusDays(1), PASS),
            new AuditResultByNino(nino1, emptyList(), TO_DATE.plusDays(1), PASS),
            new AuditResultByNino(nino1, emptyList(), TO_DATE.plusDays(2), PASS)
        );

        // Will count as a PASS
        List<AuditResultByNino> allInRangePassBest = asList(
            new AuditResultByNino(nino2, emptyList(), FROM_DATE.plusDays(1), FAIL),
            new AuditResultByNino(nino2, emptyList(), FROM_DATE.plusDays(2), PASS),
            new AuditResultByNino(nino2, emptyList(), TO_DATE.minusDays(1), FAIL)
        );

        // Will not contribute towards stats as best result is too early
        List<AuditResultByNino> betterResultOutOfRange = asList(
            new AuditResultByNino(nino3, emptyList(), FROM_DATE.plusDays(1), ERROR),
            new AuditResultByNino(nino3, emptyList(), FROM_DATE.minusDays(1), PASS),
            new AuditResultByNino(nino3, emptyList(), FROM_DATE.plusDays(2), NOTFOUND),
            new AuditResultByNino(nino3, emptyList(), TO_DATE.plusDays(1), FAIL)
        );

        // Will count as an ERROR
        List<AuditResultByNino> errorOnlyInRange = asList(
            new AuditResultByNino(nino4, emptyList(), FROM_DATE.plusDays(1), ERROR),
            new AuditResultByNino(nino4, emptyList(), TO_DATE.minusDays(1), ERROR)
        );

        accumulator.accumulate(newArrayList(concat(allOutOfRange, allInRangePassBest)));
        accumulator.accumulate(newArrayList(concat(errorOnlyInRange, betterResultOutOfRange)));

        assertThat(accumulator.result())
            .isEqualTo(statisticsForCounts(1, 0, 0, 1));
    }

    private List<AuditResultByNino> singleResult(LocalDate date, AuditResultType resultType) {
        return singletonList(new AuditResultByNino("some nino",
            emptyList(),
            date,
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

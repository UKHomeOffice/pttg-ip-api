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


public class PassStatisticsAccumulatorMultipleCallsTest {

    private static final LocalDate FROM_DATE = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TO_DATE = LocalDate.of(2019, Month.JANUARY, 31);

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

    // TODO OJR EE-16843 Test keeping best result logic

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

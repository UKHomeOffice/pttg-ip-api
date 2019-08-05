package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultComparator;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PassStatisticsResultsConsolidatorTest {

    private static final int CUTOFF_DAYS = 10;

    private static final AuditResultType ANY_RESULT = AuditResultType.PASS;
    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final String SOME_NINO = "AA112233A";
    private static final String SOME_OTHER_NINO = "BB112233A";

    @Mock
    private AuditResultComparator mockResultsComparator;

    private PassStatisticsResultsConsolidator statisticsResultsConsolidator;
    private static final LocalDate SOME_DATE = LocalDate.now();

    @Before
    public void setUp() {
        statisticsResultsConsolidator = new PassStatisticsResultsConsolidator(mockResultsComparator, CUTOFF_DAYS);
    }

    @Test
    public void consolidateResults_emptyList_returnEmptyList() {
        assertThat(statisticsResultsConsolidator.consolidateResults(Collections.emptyList()))
            .isEmpty();
    }

    @Test
    public void consolidateResults_oneResult_returnResult() {
        AuditResult someAuditResult = new AuditResult("any correlation id", ANY_DATE, SOME_NINO, ANY_RESULT);
        AuditResultsGroupedByNino singleResult = new AuditResultsGroupedByNino(someAuditResult);

        List<AuditResult> consolidatedResult = statisticsResultsConsolidator.consolidateResults(singletonList(singleResult));

        assertThat(consolidatedResult).containsExactlyInAnyOrder(someAuditResult);
    }

    @Test
    public void consolidateResults_twoNinos_oneResultEach_returnResults() {
        AuditResult someAuditResult = new AuditResult("any correlation id", ANY_DATE, SOME_NINO, AuditResultType.PASS);
        AuditResult someOtherAuditResult = new AuditResult("any other correlation id", ANY_DATE, SOME_OTHER_NINO, AuditResultType.FAIL);

        List<AuditResultsGroupedByNino> someResultsGroupedByNino = Arrays.asList(new AuditResultsGroupedByNino(someAuditResult),
                                                                                 new AuditResultsGroupedByNino(someOtherAuditResult));

        List<AuditResult> consolidatedResult = statisticsResultsConsolidator.consolidateResults(someResultsGroupedByNino);

        assertThat(consolidatedResult).containsExactlyInAnyOrder(someAuditResult, someOtherAuditResult);
    }

    @Test
    public void separateResultsByCutoff_oneResult_returnResult() {
        AuditResultsGroupedByNino singleResult = new AuditResultsGroupedByNino(new AuditResult("any correlation id", ANY_DATE, SOME_NINO, ANY_RESULT));

        List<AuditResultsGroupedByNino> separatedResults = statisticsResultsConsolidator.separateResultsByCutoff(singleResult);
        assertThat(separatedResults).hasSize(1);

        assertThat(separatedResults.get(0)).isEqualTo(singleResult);
    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenSecondAndThird_groupFirstTwo() {
        LocalDate date2 = withinCutoff(SOME_DATE);
        LocalDate date3 = afterCutoff(date2);

        AuditResultsGroupedByNino results = new AuditResultsGroupedByNino(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = new AuditResultsGroupedByNino(results.results().get(0));
        expectedResult1.add(results.results().get(1));
        AuditResultsGroupedByNino expectedResult2 = new AuditResultsGroupedByNino(results.results().get(2));
        assertThat(statisticsResultsConsolidator.separateResultsByCutoff(results))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2);
    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenFirstAndSecond_groupLastTwo() {
        LocalDate date2 = afterCutoff(SOME_DATE);
        LocalDate date3 = withinCutoff(date2);

        AuditResultsGroupedByNino results = new AuditResultsGroupedByNino(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = new AuditResultsGroupedByNino(results.results().get(0));
        AuditResultsGroupedByNino expectedResult2 = new AuditResultsGroupedByNino(results.results().get(1));
        expectedResult2.add(results.results().get(2));

        assertThat(statisticsResultsConsolidator.separateResultsByCutoff(results))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2);

    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenEach_noGrouping() {
        LocalDate date2 = afterCutoff(SOME_DATE);
        LocalDate date3 = afterCutoff(date2);
        AuditResultsGroupedByNino results = new AuditResultsGroupedByNino(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = new AuditResultsGroupedByNino(results.results().get(0));
        AuditResultsGroupedByNino expectedResult2 = new AuditResultsGroupedByNino(results.results().get(1));
        AuditResultsGroupedByNino expectedResult3 = new AuditResultsGroupedByNino(results.results().get(2));
        assertThat(statisticsResultsConsolidator.separateResultsByCutoff(results))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2, expectedResult3);
    }

    @Test
    public void separateResultsByCutoff_threeResults_noGaps_groupAll() {
        LocalDate date2 = withinCutoff(SOME_DATE);
        LocalDate date3 = withinCutoff(date2);
        AuditResultsGroupedByNino results = new AuditResultsGroupedByNino(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        List<AuditResultsGroupedByNino> separatedResults = statisticsResultsConsolidator.separateResultsByCutoff(results);
        assertThat(separatedResults).containsExactly(results);
    }

    private LocalDate withinCutoff(LocalDate date) {
        return date.plusDays(CUTOFF_DAYS - 1);
    }

    private LocalDate afterCutoff(LocalDate date) {
        return date.plusDays(CUTOFF_DAYS);
    }
}

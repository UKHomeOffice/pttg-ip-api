package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditResult;
import uk.gov.digital.ho.proving.income.audit.AuditResultComparator;
import uk.gov.digital.ho.proving.income.audit.AuditResultType;
import uk.gov.digital.ho.proving.income.audit.ResultCutoffSeparator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toCollection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PassStatisticsResultsConsolidatorTest {

    private static final int CUTOFF_DAYS = 10;

    private static final AuditResultType ANY_RESULT = AuditResultType.PASS;
    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final String SOME_NINO = "AA112233A";
    private static final String SOME_OTHER_NINO = "BB112233A";
    private static final LocalDate SOME_DATE = LocalDate.now();

    @Mock
    private AuditResultComparator mockResultsComparator;
    @Mock
    private ResultCutoffSeparator mockCutoffSeparator;

    private PassStatisticsResultsConsolidator statisticsResultsConsolidator;

    @Before
    public void setUp() {
        statisticsResultsConsolidator = new PassStatisticsResultsConsolidator(mockResultsComparator, CUTOFF_DAYS, mockCutoffSeparator);
    }

    @Test
    public void consolidateResults_emptyList_returnEmptyList() {
        assertThat(statisticsResultsConsolidator.consolidateResults(Collections.emptyList()))
            .isEmpty();
    }

    @Test
    public void consolidateResults_oneResultFromSeparator_returnResult() {
        AuditResult someAuditResult = new AuditResult("any correlation id", ANY_DATE, SOME_NINO, ANY_RESULT);
        AuditResultsGroupedByNino singleResult = new AuditResultsGroupedByNino(someAuditResult);

        given(mockCutoffSeparator.separateResultsByCutoff(singleResult)).willReturn(singletonList(singleResult));

        List<AuditResult> consolidatedResult = statisticsResultsConsolidator.consolidateResults(singletonList(singleResult));
        assertThat(consolidatedResult).containsExactlyInAnyOrder(someAuditResult);
    }

    @Test
    public void consolidateResults_twoNinos_oneResultEach_fromSeparator_returnResults() {
        AuditResult someAuditResult = new AuditResult("any correlation id", ANY_DATE, SOME_NINO, AuditResultType.PASS);
        AuditResultsGroupedByNino someGroupedResult = new AuditResultsGroupedByNino(someAuditResult);

        AuditResult someOtherAuditResult = new AuditResult("any other correlation id", ANY_DATE, SOME_OTHER_NINO, AuditResultType.FAIL);
        AuditResultsGroupedByNino someOtherGroupedResult = new AuditResultsGroupedByNino(someOtherAuditResult);

        List<AuditResultsGroupedByNino> someResultsGroupedByNino = Arrays.asList(someGroupedResult, someOtherGroupedResult);

        given(mockCutoffSeparator.separateResultsByCutoff(someGroupedResult)).willReturn(singletonList(someGroupedResult));
        given(mockCutoffSeparator.separateResultsByCutoff(someOtherGroupedResult)).willReturn(singletonList(someOtherGroupedResult));

        List<AuditResult> consolidatedResult = statisticsResultsConsolidator.consolidateResults(someResultsGroupedByNino);
        assertThat(consolidatedResult).containsExactlyInAnyOrder(someAuditResult, someOtherAuditResult);
    }

    @Test
    public void separateResultsByCutoff_oneResult_returnResult() {
        // TODO OJR EE_21001 2019-08-07 Migrated to ResultCutoffSeparatorTest
        AuditResultsGroupedByNino singleResult = new AuditResultsGroupedByNino(new AuditResult("any correlation id", ANY_DATE, SOME_NINO, ANY_RESULT));

        List<AuditResultsGroupedByNino> separatedResults = statisticsResultsConsolidator.separateResultsByCutoff(singleResult);
        assertThat(separatedResults).containsExactly(singleResult);
    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenSecondAndThird_groupFirstTwo() {
        // TODO OJR EE_21001 2019-08-07 Migrated to ResultCutoffSeparatorTest
        LocalDate date2 = withinCutoff(SOME_DATE);
        LocalDate date3 = afterCutoff(date2);

        AuditResultsGroupedByNino results = new AuditResultsGroupedByNino(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = groupedResults(results.get(0), results.get(1));
        AuditResultsGroupedByNino expectedResult2 = new AuditResultsGroupedByNino(results.get(2));
        assertThat(statisticsResultsConsolidator.separateResultsByCutoff(results))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2);
    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenFirstAndSecond_groupLastTwo() {
        // TODO OJR EE_21001 2019-08-07 Migrated to ResultCutoffSeparatorTest
        LocalDate date2 = afterCutoff(SOME_DATE);
        LocalDate date3 = withinCutoff(date2);

        AuditResultsGroupedByNino results = new AuditResultsGroupedByNino(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = new AuditResultsGroupedByNino(results.get(0));
        AuditResultsGroupedByNino expectedResult2 = groupedResults(results.get(1), results.get(2));

        assertThat(statisticsResultsConsolidator.separateResultsByCutoff(results))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2);

    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenEach_noGrouping() {
        // TODO OJR EE_21001 2019-08-07 Migrated to ResultCutoffSeparatorTest
        LocalDate date2 = afterCutoff(SOME_DATE);
        LocalDate date3 = afterCutoff(date2);
        AuditResultsGroupedByNino expectedToBeSplitResult = groupedResults(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT),
                                                                           new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT),
                                                                           new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = new AuditResultsGroupedByNino(expectedToBeSplitResult.get(0));
        AuditResultsGroupedByNino expectedResult2 = new AuditResultsGroupedByNino(expectedToBeSplitResult.get(1));
        AuditResultsGroupedByNino expectedResult3 = new AuditResultsGroupedByNino(expectedToBeSplitResult.get(2));
        assertThat(statisticsResultsConsolidator.separateResultsByCutoff(expectedToBeSplitResult))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2, expectedResult3);
    }

    @Test
    public void separateResultsByCutoff_threeResults_noGaps_groupAll() {
        // TODO OJR EE_21001 2019-08-07 Migrated to ResultCutoffSeparatorTest
        LocalDate date2 = withinCutoff(SOME_DATE);
        LocalDate date3 = withinCutoff(date2);
        AuditResultsGroupedByNino expectedToBeGroupedResults = groupedResults(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT),
                                                                              new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT),
                                                                              new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        List<AuditResultsGroupedByNino> separatedResults = statisticsResultsConsolidator.separateResultsByCutoff(expectedToBeGroupedResults);
        assertThat(separatedResults).containsExactly(expectedToBeGroupedResults);
    }

    private AuditResultsGroupedByNino groupedResults(AuditResult... auditResults) {
        return Arrays.stream(auditResults)
                     .collect(toCollection(AuditResultsGroupedByNino::new));
    }

    private LocalDate withinCutoff(LocalDate date) {
        return date.plusDays(CUTOFF_DAYS);
    }

    private LocalDate afterCutoff(LocalDate date) {
        return date.plusDays(CUTOFF_DAYS + 1);
    }
}

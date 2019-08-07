package uk.gov.digital.ho.proving.income.audit;

import org.junit.Before;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.audit.statistics.AuditResultsGroupedByNino;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toCollection;
import static org.assertj.core.api.Assertions.assertThat;

public class ResultCutoffSeparatorTest {

    private static final int CUTOFF_DAYS = 10;

    private static final AuditResultType ANY_RESULT = AuditResultType.PASS;
    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final String SOME_NINO = "AA112233A";
    private static final LocalDate SOME_DATE = LocalDate.now();

    private ResultCutoffSeparator separator;

    @Before
    public void setUp() {
        separator = new ResultCutoffSeparator(CUTOFF_DAYS);
    }

    @Test
    public void separateResultsByCutoff_oneResult_returnResult() {
        AuditResultsGroupedByNino singleResult = new AuditResultsGroupedByNino(new AuditResult("any correlation id", ANY_DATE, SOME_NINO, ANY_RESULT));

        List<AuditResultsGroupedByNino> separatedResults = separator.separateResultsByCutoff(singleResult);
        assertThat(separatedResults).containsExactly(singleResult);
    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenSecondAndThird_groupFirstTwo() {
        LocalDate date2 = withinCutoff(SOME_DATE);
        LocalDate date3 = afterCutoff(date2);

        AuditResultsGroupedByNino results = new AuditResultsGroupedByNino(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = groupedResults(results.get(0), results.get(1));
        AuditResultsGroupedByNino expectedResult2 = new AuditResultsGroupedByNino(results.get(2));
        assertThat(separator.separateResultsByCutoff(results))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2);
    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenFirstAndSecond_groupLastTwo() {
        LocalDate date2 = afterCutoff(SOME_DATE);
        LocalDate date3 = withinCutoff(date2);

        AuditResultsGroupedByNino results = new AuditResultsGroupedByNino(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT));
        results.add(new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = new AuditResultsGroupedByNino(results.get(0));
        AuditResultsGroupedByNino expectedResult2 = groupedResults(results.get(1), results.get(2));

        assertThat(separator.separateResultsByCutoff(results))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2);

    }

    @Test
    public void separateResultsByCutoff_threeResults_gapBetweenEach_noGrouping() {
        LocalDate date2 = afterCutoff(SOME_DATE);
        LocalDate date3 = afterCutoff(date2);
        AuditResultsGroupedByNino expectedToBeSplitResult = groupedResults(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT),
                                                                           new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT),
                                                                           new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        AuditResultsGroupedByNino expectedResult1 = new AuditResultsGroupedByNino(expectedToBeSplitResult.get(0));
        AuditResultsGroupedByNino expectedResult2 = new AuditResultsGroupedByNino(expectedToBeSplitResult.get(1));
        AuditResultsGroupedByNino expectedResult3 = new AuditResultsGroupedByNino(expectedToBeSplitResult.get(2));
        assertThat(separator.separateResultsByCutoff(expectedToBeSplitResult))
            .containsExactlyInAnyOrder(expectedResult1, expectedResult2, expectedResult3);
    }

    @Test
    public void separateResultsByCutoff_threeResults_noGaps_groupAll() {
        LocalDate date2 = withinCutoff(SOME_DATE);
        LocalDate date3 = withinCutoff(date2);
        AuditResultsGroupedByNino expectedToBeGroupedResults = groupedResults(new AuditResult("any correlation id", SOME_DATE, SOME_NINO, ANY_RESULT),
                                                                              new AuditResult("any correlation id", date2, SOME_NINO, ANY_RESULT),
                                                                              new AuditResult("any correlation id", date3, SOME_NINO, ANY_RESULT));

        List<AuditResultsGroupedByNino> separatedResults = separator.separateResultsByCutoff(expectedToBeGroupedResults);
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

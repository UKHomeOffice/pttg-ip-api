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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PassStatisticsResultsConsolidatorTest {

    private static final AuditResultType ANY_RESULT = AuditResultType.PASS;
    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final String SOME_NINO = "AA112233A";
    private static final String SOME_OTHER_NINO = "BB112233A";

    @Mock
    private AuditResultComparator mockResultsComparator;
    @Mock
    private ResultCutoffSeparator mockCutoffSeparator;

    private PassStatisticsResultsConsolidator statisticsResultsConsolidator;

    @Before
    public void setUp() {
        statisticsResultsConsolidator = new PassStatisticsResultsConsolidator(mockResultsComparator, mockCutoffSeparator);
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
}

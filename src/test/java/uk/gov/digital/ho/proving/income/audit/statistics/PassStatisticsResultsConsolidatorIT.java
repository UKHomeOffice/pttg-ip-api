package uk.gov.digital.ho.proving.income.audit.statistics;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.proving.income.audit.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    PassStatisticsResultsConsolidator.class,
    AuditResultComparator.class,
    AuditResultTypeComparator.class
})
public class PassStatisticsResultsConsolidatorIT {

    private static final int CUTOFF_DAYS = 10;

    @Autowired
    public PassStatisticsResultsConsolidator consolidator;
    private static final LocalDate SOME_DATE = LocalDate.now();

    @Test
    public void consolidateResults_oneNinoWorseResultInRange_returnBetterResult() {
        LocalDate someDate = LocalDate.now();
        String someNino = "AA112233A";
        AuditResult betterResult = new AuditResult("any correlation id", someDate, someNino, AuditResultType.PASS);

        LocalDate withinCutoffDate = someDate.plusDays(CUTOFF_DAYS - 1);
        AuditResult worseResultWithinRange = new AuditResult("any other correlation id", withinCutoffDate, someNino, AuditResultType.FAIL);

        Map<String, List<AuditResult>> someResults = ImmutableMap.of(someNino, Arrays.asList(betterResult, worseResultWithinRange));

        List<AuditResult> consolidatedResult = consolidator.consolidateResults(someResults);

        assertThat(consolidatedResult).containsExactly(betterResult);
    }

    @Test
    public void consolidateResults_oneNinoBetterResultInRange_returnBetterResult() {
        LocalDate someDate = LocalDate.now();
        String someNino = "AA112233A";
        AuditResult worseResult = new AuditResult("any correlation id", someDate, someNino, AuditResultType.FAIL);

        LocalDate withinCutoffDate = someDate.plusDays(CUTOFF_DAYS - 1);
        AuditResult betterResultInRange = new AuditResult("any other correlation id", withinCutoffDate, someNino, AuditResultType.PASS);

        Map<String, List<AuditResult>> someResults = ImmutableMap.of(someNino, Arrays.asList(worseResult, betterResultInRange));

        List<AuditResult> consolidatedResult = consolidator.consolidateResults(someResults);

        assertThat(consolidatedResult).containsExactly(betterResultInRange);
    }

    @Test
    public void consolidateResults_twoNinosResultsAllInCutoff_returnTwoResults() {
        LocalDate someDate = LocalDate.now();
        LocalDate withinCutoffDate = someDate.plusDays(CUTOFF_DAYS - 1);

        String someNino = "AA112233A";
        AuditResult worseResult = new AuditResult("any correlation id", someDate, someNino, AuditResultType.NOTFOUND);
        AuditResult betterResultInRange = new AuditResult("any correlation id", withinCutoffDate, someNino, AuditResultType.PASS);

        String someOtherNino = "BB112233A";
        AuditResult betterResult = new AuditResult("any correlation id", someDate, someOtherNino, AuditResultType.NOTFOUND);
        AuditResult worseResultInRange = new AuditResult("any correlation id", withinCutoffDate, someOtherNino, AuditResultType.ERROR);

        Map<String, List<AuditResult>> someResults = ImmutableMap.of(someNino, Arrays.asList(worseResult, betterResultInRange),
                                                                     someOtherNino, Arrays.asList(betterResult, worseResultInRange));

        List<AuditResult> consolidatedResult = consolidator.consolidateResults(someResults);
        assertThat(consolidatedResult).containsExactlyInAnyOrder(betterResult, betterResultInRange);
    }

    @Test
    public void consolidateResults_oneNinoResultsAfterCutoff_returnTwoResults() {
        LocalDate afterCutoffDate = SOME_DATE.plusDays(CUTOFF_DAYS);

        String someNino = "AA112233A";
        AuditResult someResult = new AuditResult("any correlation id", SOME_DATE, someNino, AuditResultType.PASS);
        AuditResult resultAfterCutoffDate = new AuditResult("any correlation id", afterCutoffDate, someNino, AuditResultType.FAIL);

        Map<String, List<AuditResult>> someResults = ImmutableMap.of(someNino, Arrays.asList(someResult, resultAfterCutoffDate));

        List<AuditResult> consolidatedResult = consolidator.consolidateResults(someResults);
        assertThat(consolidatedResult).containsExactlyInAnyOrder(someResult, resultAfterCutoffDate);
    }

    @Test
    public void consolidateResults_multipleNinosAndResults_splitWhenAfterCutoff() {
        List<AuditResult> shouldBePassAndFail = passAndAFail();
        List<AuditResult> shouldBeNotFoundAndError = notFoundAndAnError();
        List<AuditResult> shouldBePass = Collections.singletonList(new AuditResult("any correlation id", SOME_DATE, "nino3", AuditResultType.PASS));

        Map<String, List<AuditResult>> someResults = ImmutableMap.<String, List<AuditResult>>builder()
            .put("nino1", shouldBePassAndFail)
            .put("nino2", shouldBeNotFoundAndError)
            .put("nino3", shouldBePass)
            .build();

        List<AuditResult> expectedResults = Arrays.asList(shouldBePassAndFail.get(1), shouldBePassAndFail.get(2),
                                                          shouldBeNotFoundAndError.get(0), shouldBeNotFoundAndError.get(1),
                                                          shouldBePass.get(0));


        List<AuditResult> actualResults = consolidator.consolidateResults(someResults);

        assertThat(actualResults).containsExactlyInAnyOrder(expectedResults.toArray(new AuditResult[]{}));
    }

    private List<AuditResult> passAndAFail() {
        LocalDate date2 = withinCutoff(SOME_DATE);
        LocalDate date3 = afterCutoff(date2);
        LocalDate date4 = withinCutoff(date3);
        return Arrays.asList(
            new AuditResult("any correlation id", SOME_DATE, "nino1", AuditResultType.ERROR),
            new AuditResult("any correlation id", date2, "nino1", AuditResultType.PASS),

            new AuditResult("any correlation id", date3, "nino1", AuditResultType.FAIL),
            new AuditResult("any correlation id", date4, "nino1", AuditResultType.NOTFOUND));
    }

    private List<AuditResult> notFoundAndAnError() {
        LocalDate date2 = afterCutoff(SOME_DATE);
        LocalDate date3 = withinCutoff(date2);

        return Arrays.asList(
            new AuditResult("any correlation id", SOME_DATE, "nino2", AuditResultType.ERROR),

            new AuditResult("any correlation id", date2, "nino2", AuditResultType.NOTFOUND),
            new AuditResult("any correlation id", date3, "nino2", AuditResultType.NOTFOUND));
    }

    private LocalDate withinCutoff(LocalDate date) {
        return date.plusDays(CUTOFF_DAYS - 1);
    }

    private LocalDate afterCutoff(LocalDate date) {
        return date.plusDays(CUTOFF_DAYS);
    }
}

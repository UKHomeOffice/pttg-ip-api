package uk.gov.digital.ho.proving.income.audit.statistics;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.*;

import javax.lang.model.util.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@RunWith(MockitoJUnitRunner.class)
public class AuditResultFetcherTest {

    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final LocalDateTime ANY_DATE_TIME = LocalDateTime.now();
    private static final AuditEventType ANY_AUDIT_EVENT_TYPE = INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
    private static final JsonNode ANY_JSON = null;
    private static final AuditResultType ANY_AUDIT_RESULT_TYPE = AuditResultType.PASS;
    private static final AuditResult ANY_AUDIT_RECORD = new AuditResult("any correlation id", ANY_DATE, "any nino", ANY_AUDIT_RESULT_TYPE);

    @Mock
    private AuditResultConsolidator mockConsolidator;
    @Mock
    private AuditClient mockAuditClient;
    @Mock
    private PassStatisticsResultsConsolidator mockStatisticsResultsConsolidator;

    private AuditResultFetcher auditResultFetcher;

    @Before
    public void setUp() {
        auditResultFetcher = new AuditResultFetcher(mockAuditClient, mockConsolidator, mockStatisticsResultsConsolidator);
    }

    @Test
    public void getAuditResults_someCorrelationIds_callGetByCorrelationIdWithEachInTurn() {

        given(mockConsolidator.getAuditResult(any())).willReturn(ANY_AUDIT_RECORD);

        List<AuditEventType> expectedEventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        ArgumentCaptor<String> correlationIdCaptor = ArgumentCaptor.forClass(String.class);

        List<String> someCorrelationIds = Arrays.asList("some correlationId", "some other correlation id");
        auditResultFetcher.getAuditResults(someCorrelationIds);

        then(mockAuditClient).should(atLeastOnce())
                             .getHistoryByCorrelationId(correlationIdCaptor.capture(), eq(expectedEventTypes));
        assertThat(correlationIdCaptor.getAllValues()).containsExactlyInAnyOrderElementsOf(someCorrelationIds);
    }

    @Test
    public void getAuditResults_givenResultsFromAuditService_passedToConsolidator() {
        stubConsolidator();

        List<AuditRecord> someAuditRecords = asList(
            new AuditRecord("some id", ANY_DATE_TIME, "some email", ANY_AUDIT_EVENT_TYPE, ANY_JSON, "some nino"),
            new AuditRecord("some other id", ANY_DATE_TIME, "some email", ANY_AUDIT_EVENT_TYPE, ANY_JSON, "some nino"));
        List<AuditRecord> someOtherAuditRecords = singletonList(
            new AuditRecord("yet some other id", ANY_DATE_TIME, "some other email", ANY_AUDIT_EVENT_TYPE, ANY_JSON, "some other nino"));

        given(mockAuditClient.getHistoryByCorrelationId(eq("some correlationId"), anyList()))
            .willReturn(someAuditRecords);
        given(mockAuditClient.getHistoryByCorrelationId(eq("some other correlation id"), anyList()))
            .willReturn(someOtherAuditRecords);

        List<String> someCorrelationIds = Arrays.asList("some correlationId", "some other correlation id");
        auditResultFetcher.getAuditResults(someCorrelationIds);

        then(mockConsolidator).should().getAuditResult(someAuditRecords);
        then(mockConsolidator).should().getAuditResult(someOtherAuditRecords);
    }

    @Test
    public void getAuditResults_oneResult_passedToStatisticsResultsConsolidator() {
        AuditResult someAuditResult = new AuditResult("some correlation id", ANY_DATE, "some nino", ANY_AUDIT_RESULT_TYPE);
        given(mockConsolidator.getAuditResult(any())).willReturn(someAuditResult);

        auditResultFetcher.getAuditResults(singletonList("some correlation id"));

        List<AuditResultsGroupedByNino> expectedGroupedResults = singletonList(new AuditResultsGroupedByNino(someAuditResult));
        then(mockStatisticsResultsConsolidator).should()
                                               .consolidateResults(expectedGroupedResults);
    }

    @Test
    public void getAuditResults_multipleResultsForMultipleNinos_passedToStatisticsResultsConsolidator() {
        AuditResult nino1Result = new AuditResult("some correlation id", ANY_DATE, "nino1", AuditResultType.PASS);
        AuditResult nino2Result = new AuditResult("some correlation other id", ANY_DATE, "nino2", AuditResultType.PASS);
        AuditResult nino2OtherResult = new AuditResult("yet some correlation other id", ANY_DATE, "nino2", AuditResultType.FAIL);

        given(mockConsolidator.getAuditResult(anyList())).willReturn(nino1Result, nino2Result, nino2OtherResult);

        auditResultFetcher.getAuditResults(Arrays.asList("some correlation id", "some correlation other id", "yet some correlation other id"));

        AuditResultsGroupedByNino nino2GroupedResults = new AuditResultsGroupedByNino(nino2Result);
        nino2GroupedResults.add(nino2OtherResult);
        List<AuditResultsGroupedByNino> expectedGroupedResults = Arrays.asList(new AuditResultsGroupedByNino(nino1Result),
                                                                               nino2GroupedResults);

        ArgumentCaptor<List> groupedResultsCaptor = ArgumentCaptor.forClass(List.class);
        then(mockStatisticsResultsConsolidator).should()
                                               .consolidateResults(groupedResultsCaptor.capture());
        List<AuditResultsGroupedByNino> actualGroupedResults = groupedResultsCaptor.getValue();

        assertThat(actualGroupedResults).containsExactlyInAnyOrderElementsOf(expectedGroupedResults);
    }

    @Test
    public void getAuditResults_resultFromStatisticsResultConsolidator_returned() {
        AuditResult someAuditResult = new AuditResult("some correlation id", ANY_DATE, "some nino", AuditResultType.PASS);
        AuditResult someOtherAuditResult = new AuditResult("some other correlation id", ANY_DATE, "some nino", AuditResultType.FAIL);
        given(mockConsolidator.getAuditResult(any())).willReturn(someAuditResult, someOtherAuditResult);

        List<AuditResult> expectedResults = singletonList(someAuditResult);
        given(mockStatisticsResultsConsolidator.consolidateResults(anyList())).willReturn(expectedResults);

        List<AuditResult> returnedResults = auditResultFetcher.getAuditResults(Arrays.asList("some correlation id", "some other correlation id"));

        assertThat(returnedResults).isEqualTo(expectedResults);
    }

    private void stubConsolidator() {
        when(mockConsolidator.getAuditResult(any()))
            .thenReturn(ANY_AUDIT_RECORD);
    }

    private void stubConsolidator(List<AuditResult> results) {
        stubConsolidator(results.toArray(new AuditResult[0]));
    }

    private void stubConsolidator(AuditResult... results) {
        AuditResult[] allResultsExceptFirst = ArrayUtils.subarray(results, 1, results.length);
        when(mockConsolidator.getAuditResult(any()))
            .thenReturn(results[0], allResultsExceptFirst);
    }
}

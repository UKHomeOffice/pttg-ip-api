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
    private AuditResultComparator mockComparator;

    private AuditResultFetcher auditResultFetcher;

    @Before
    public void setUp() {
        auditResultFetcher = new AuditResultFetcher(mockAuditClient, mockConsolidator, mockComparator);
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
    public void getAuditResults_multipleResultsPerNino_usesComparator() {
        List<String> someCorrelationIds = Arrays.asList("some correlation id", "some other correlation id");

        AuditResult passResult = new AuditResult("some correlation id", ANY_DATE, "some nino", AuditResultType.PASS);
        AuditResult failResult = new AuditResult("some other correlation id", ANY_DATE, "some nino", AuditResultType.FAIL);
        stubConsolidator(passResult, failResult);

        auditResultFetcher.getAuditResults(someCorrelationIds);

        then(mockComparator)
            .should()
            .compare(passResult, failResult);
    }

    @Test
    public void getAuditResults_givenResultsFromConsolidator_returned() {
        List<AuditResult> expectedResults = asList(
            new AuditResult("some correlation id", ANY_DATE, "some nino", ANY_AUDIT_RESULT_TYPE),
            new AuditResult("some other correlation id", ANY_DATE, "some other nino", ANY_AUDIT_RESULT_TYPE));

        stubConsolidator(expectedResults);

        List<AuditResult> returnedResults = auditResultFetcher.getAuditResults(asList("some correlation id", "some other correlation id"));

        assertThat(returnedResults).containsExactlyInAnyOrderElementsOf(expectedResults);
    }

    @Test
    public void getAuditResults_multipleResultsPerNino_returnOnlyBest() {
        AuditResult passResult = new AuditResult("some correlation id", ANY_DATE, "some nino", AuditResultType.PASS);
        AuditResult failResult = new AuditResult("some other correlation id", ANY_DATE, "some nino", AuditResultType.FAIL);

        given(mockConsolidator.getAuditResult(anyList())).willReturn(passResult, failResult);
        given(mockComparator.compare(passResult, failResult)).willReturn(1);

        List<AuditResult> auditResults = auditResultFetcher.getAuditResults(asList("some correlation id", "some other correlation id"));

        assertThat(auditResults).containsOnly(passResult);
    }

    @Test
    public void getAuditResults_multipleResultsPerNino_returnOldestBest() {
        List<String> correlationIds = Arrays.asList("some correlation id", "some other correlation id", "yet some other correlation id");

        AuditResult firstNotFound = new AuditResult("some correlation id", ANY_DATE, "some nino", AuditResultType.NOTFOUND);
        AuditResult firstFail = new AuditResult("some other correlation id", ANY_DATE.minusDays(2), "some nino", AuditResultType.FAIL);
        AuditResult secondFail = new AuditResult("yet some other correlation id", ANY_DATE.minusDays(1), "some nino", AuditResultType.FAIL);

        stubConsolidator(firstNotFound, firstFail, secondFail);
        when(mockComparator.compare(firstNotFound, firstFail)).thenReturn(-1);
        when(mockComparator.compare(firstFail, secondFail)).thenReturn(1);

        List<AuditResult> returnedResults = auditResultFetcher.getAuditResults(correlationIds);

        assertThat(returnedResults).containsOnly(firstFail);
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

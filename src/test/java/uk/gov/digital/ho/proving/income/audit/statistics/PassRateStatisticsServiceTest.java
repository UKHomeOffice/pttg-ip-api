package uk.gov.digital.ho.proving.income.audit.statistics;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@RunWith(MockitoJUnitRunner.class)
public class PassRateStatisticsServiceTest {

    @Mock
    private AuditClient mockAuditClient;
    @Mock
    private AuditResultConsolidator mockConsolidator;
    @Mock
    private PassStatisticsCalculator mockPassStatisticsCalculator;
    @Mock
    private AuditResultComparator mockResultComparator;

    private PassRateStatisticsService service;

    private static final long SOME_LONG = 3;
    private static final LocalDate SOME_DATE = LocalDate.now();
    private static final LocalDateTime SOME_DATE_TIME = LocalDateTime.now();
    private static final AuditEventType SOME_AUDIT_EVENT_TYPE = INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
    private static final JsonNode SOME_JSON = null;
    private static final AuditResultType SOME_AUDIT_RESULT_TYPE = AuditResultType.PASS;

    private static final AuditResult ANY_AUDIT_RECORD = new AuditResult("any correlation id", LocalDate.now(), "any nino", SOME_AUDIT_RESULT_TYPE);
    private static final int CUT_OFF_DAYS = 10;

    @Before
    public void setUp() {
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator, mockConsolidator, mockResultComparator, CUT_OFF_DAYS);
    }

    /**************************************************************
     * AuditClient collaborator
     * getAllCorrelationIdsForEventType, getHistoryByCorrelationId
     **************************************************************/
    @Test
    public void generatePassStatistics_anyParams_getAllCorrelationIdsForEventType() {

        ArgumentCaptor<List<AuditEventType>> eventTypesCaptor = ArgumentCaptor.forClass(List.class);
        when(mockAuditClient.getAllCorrelationIdsForEventType(eventTypesCaptor.capture(), any(LocalDate.class)))
            .thenReturn(asList("any correlationId", "any other correlation id"));
        when(mockConsolidator.getAuditResult(any())).thenReturn(ANY_AUDIT_RECORD);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);

        assertThat(eventTypesCaptor.getValue())
            .contains(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
    }

    @Test
    public void generatePassStatistics_givenEndDate_getAllCorrelationIdsWithCutOffDate() {
        LocalDate anyDate = LocalDate.parse("2019-07-01");
        LocalDate someEndDate = LocalDate.parse("2019-07-31");

        service.generatePassRateStatistics(anyDate, someEndDate);

        LocalDate expectedEndDate = someEndDate.plusDays(CUT_OFF_DAYS);
        then(mockAuditClient).should().getAllCorrelationIdsForEventType(anyList(), eq(expectedEndDate));
    }

    @Test
    public void generatePassStatistics_correlationIdsFromAuditService_callGetByCorrelationIdWithEachInTurn() {
        List<String> expectedCorrelationIds = stubGetAllCorrelationIds("some correlationId", "some other correlation id");

        List<AuditRecord> anyAuditRecords = singletonList(new AuditRecord("any id", SOME_DATE_TIME, "any email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "any nino"));
        when(mockConsolidator.getAuditResult(any())).thenReturn(ANY_AUDIT_RECORD);

        List<AuditEventType> expectedEventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        ArgumentCaptor<String> correlationIdCaptor = ArgumentCaptor.forClass(String.class);
        when(mockAuditClient.getHistoryByCorrelationId(correlationIdCaptor.capture(), eq(expectedEventTypes)))
            .thenReturn(anyAuditRecords);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        assertThat(correlationIdCaptor.getAllValues()).isEqualTo(expectedCorrelationIds);
    }

    /**************************
     * AuditClient collaborator
     * getArchivedResults
     **************************/

    @Test
    public void generatePassStatistics_givenDate_getArchivedResultsForDates() {
        LocalDate fromDate = LocalDate.of(2019, Month.JANUARY, 1);
        LocalDate toDate = LocalDate.of(2019, Month.JANUARY, 31);

        service.generatePassRateStatistics(fromDate, toDate);
        verify(mockAuditClient).getArchivedResults(eq(fromDate), eq(toDate));
    }

    @Test
    public void generatePassStatistics_returnedArchivedResults_passToCalculator() {
        List<ArchivedResult> someArchivedResults = singletonList(new ArchivedResult(singletonMap("PASSED", 20)));
        when(mockAuditClient.getArchivedResults(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(someArchivedResults);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockPassStatisticsCalculator).result(anyList(), eq(someArchivedResults), any(LocalDate.class), any(LocalDate.class));
    }

    /***************************************
     * AuditResultConsolidator collaborator
     ***************************************/

    @Test
    public void generatePassStatistics_givenResultsFromAuditService_passedToConsolidator() {

        stubGetAllCorrelationIds("some correlationId", "some other correlation id");

        List<AuditRecord> someAuditRecords = asList(
            new AuditRecord("some id", SOME_DATE_TIME, "some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "some nino"),
            new AuditRecord("some other id", SOME_DATE_TIME, "some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "some other nino"));
        List<AuditRecord> someOtherAuditRecords = singletonList(
            new AuditRecord("yet some other id", SOME_DATE_TIME, "yet some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "yet some other nino"));

        when(mockAuditClient.getHistoryByCorrelationId(eq("some correlationId"), anyList()))
            .thenReturn(someAuditRecords);
        when(mockAuditClient.getHistoryByCorrelationId(eq("some other correlation id"), anyList()))
            .thenReturn(someOtherAuditRecords);
        when(mockConsolidator.getAuditResult(any())).thenReturn(ANY_AUDIT_RECORD);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);

        verify(mockConsolidator).getAuditResult(someAuditRecords);
        verify(mockConsolidator).getAuditResult(someOtherAuditRecords);
    }

    /****************************************
     * PassStatisticsCalculator collaborator
     ****************************************/

    @Test
    public void generatePassStatistics_givenFromDate_passedToCalculator() {
        LocalDate fromDate = LocalDate.now();
        service.generatePassRateStatistics(fromDate, SOME_DATE);

        verify(mockPassStatisticsCalculator).result(anyList(), anyList(), eq(fromDate), any(LocalDate.class));
    }

    @Test
    public void generatePassStatistics_givenToDate_passedToCalculator() {
        LocalDate toDate = LocalDate.now();
        service.generatePassRateStatistics(SOME_DATE, toDate);

        verify(mockPassStatisticsCalculator).result(anyList(), anyList(), any(LocalDate.class), eq(toDate));
    }

    @Test
    public void generatePassStatistics_givenAuditResultsFromConsolidator_expectedListPassedToCalculator() {
        stubGetAllCorrelationIds("some correlation id", "some other correlation id");

        List<AuditResult> expectedResults = asList(
            new AuditResult("some correlation id", SOME_DATE, "some nino", SOME_AUDIT_RESULT_TYPE),
            new AuditResult("some other correlation id", SOME_DATE, "some other nino", SOME_AUDIT_RESULT_TYPE));

        when(mockConsolidator.getAuditResult(anyList()))
            .thenReturn(expectedResults.get(0), expectedResults.get(1));

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);

        ArgumentCaptor<List<AuditResult>> auditResultsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockPassStatisticsCalculator).result(auditResultsCaptor.capture(), anyList(), eq(SOME_DATE), eq(SOME_DATE));

        assertThat(auditResultsCaptor.getValue())
            .containsOnly(expectedResults.get(0), expectedResults.get(1));
    }

    @Test
    public void generatePassStatistics_multipleResultsPerNino_passOnlyBestToCalculator() {
        stubGetAllCorrelationIds("some correlation id", "some other correlation id");

        AuditResult passResult = new AuditResult("some correlation id", SOME_DATE, "some nino", AuditResultType.PASS);
        AuditResult failResult = new AuditResult("some other correlation id", SOME_DATE, "some nino", AuditResultType.FAIL);

        when(mockConsolidator.getAuditResult(anyList()))
            .thenReturn(passResult, failResult);

        when(mockResultComparator.compare(passResult, failResult)).thenReturn(1);
        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);

        ArgumentCaptor<List<AuditResult>> auditResultsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockPassStatisticsCalculator).result(auditResultsCaptor.capture(), anyList(), eq(SOME_DATE), eq(SOME_DATE));

        assertThat(auditResultsCaptor.getValue())
            .containsOnly(passResult);
    }

    @Test
    public void generatePassStatistics_multipleResultsPerNino_passOldestBestResultToCalculator() {
        stubGetAllCorrelationIds("some correlation id", "some other correlation id", "yet some other correlation id");

        AuditResult firstNotFound = new AuditResult("some correlation id", SOME_DATE, "some nino", AuditResultType.NOTFOUND);
        AuditResult firstFail = new AuditResult("some other correlation id", SOME_DATE.minusDays(2), "some nino", AuditResultType.FAIL);
        AuditResult secondFail = new AuditResult("yet some other correlation id", SOME_DATE.minusDays(1), "some nino", AuditResultType.FAIL);

        when(mockConsolidator.getAuditResult(anyList()))
            .thenReturn(firstNotFound, firstFail, secondFail);
        when(mockResultComparator.compare(firstNotFound, firstFail)).thenReturn(-1);
        when(mockResultComparator.compare(firstFail, secondFail)).thenReturn(1);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);

        ArgumentCaptor<List<AuditResult>> auditResultsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockPassStatisticsCalculator).result(auditResultsCaptor.capture(), anyList(), eq(SOME_DATE), eq(SOME_DATE));

        assertThat(auditResultsCaptor.getValue())
            .containsOnly(firstFail);
    }

    @Test
    public void generatePassStatistics_givenResultFromCalculator_returnedToCaller() {
        PassRateStatistics passRateStatistics = new PassRateStatistics(SOME_DATE, SOME_DATE, SOME_LONG, SOME_LONG, SOME_LONG, SOME_LONG, SOME_LONG);
        when(mockPassStatisticsCalculator.result(anyList(), anyList(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(passRateStatistics);

        PassRateStatistics actualStatistics = service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        assertThat(actualStatistics).isEqualTo(passRateStatistics);
    }

    @Test
    public void generatePassStatistics_taxYear_datesPassedToCalculator() {
        TaxYear taxYear = TaxYear.valueOf("2017/2018");
        service.generatePassRateStatistics(taxYear);

        verify(mockPassStatisticsCalculator)
            .result(anyList(), anyList(), eq(taxYear.startDate()), eq(taxYear.endDate()));
    }


    /*****************************************
     * AuditResultTypeComparator collaborator
     *****************************************/

    @Test
    public void generatePassStatistics_multipleResultsPerNino_usesComparator() {
        stubGetAllCorrelationIds("some correlation id", "some other correlation id");

        AuditResult passResult = new AuditResult("some correlation id", SOME_DATE, "some nino", AuditResultType.PASS);
        AuditResult failResult = new AuditResult("some other correlation id", SOME_DATE, "some nino", AuditResultType.FAIL);

        when(mockConsolidator.getAuditResult(anyList()))
            .thenReturn(passResult, failResult);
        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);

        verify(mockResultComparator).compare(passResult, failResult);
    }

    private List<String> stubGetAllCorrelationIds(String... correlationIds) {
        List<String> allCorrelationIds = asList(correlationIds);
        when(mockAuditClient.getAllCorrelationIdsForEventType(any(), any(LocalDate.class)))
            .thenReturn(allCorrelationIds);
        return allCorrelationIds;
    }
}

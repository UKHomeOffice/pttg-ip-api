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
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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

    private PassRateStatisticsService service;

    private static final int PAGE_SIZE = 2;


    // TODO OJR EE-19133 - ANY_X is more appropriate in most (maybe all) cases than SOME_X - try to clean up when done
    private static final long SOME_LONG = 3;
    private static final LocalDate SOME_DATE = LocalDate.MAX;
    private static final LocalDate ANY_DATE = LocalDate.MAX;
    private static final LocalDateTime SOME_DATE_TIME = LocalDateTime.MAX;
    private static final LocalDateTime ANY_DATE_TIME = LocalDateTime.MAX;
    private static final AuditEventType SOME_AUDIT_EVENT_TYPE = INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
    private static final AuditEventType ANY_AUDIT_EVENT_TYPE = INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
    private static final JsonNode SOME_JSON = null;
    private static final JsonNode ANY_JSON = null;
    private static final AuditResultType SOME_AUDIT_RESULT_TYPE = AuditResultType.PASS;

    @Before
    public void setUp() {
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator, mockConsolidator, PAGE_SIZE);
    }

    /**************************************************************
     * AuditClient collaborator
     * getAllCorrelationIdsForEventType, getHistoryByCorrelationId
     **************************************************************/
    @Test
    public void generatePassStatistics_anyParams_getAllCorrelationIdsForEventType() {

        ArgumentCaptor<List<AuditEventType>> eventTypesCaptor = ArgumentCaptor.forClass(List.class);
        when(mockAuditClient.getAllCorrelationIdsForEventType(eventTypesCaptor.capture()))
            .thenReturn(asList("any correlationId", "any other correlation id"));

        service.generatePassRateStatistics(ANY_DATE, ANY_DATE);

        assertThat(eventTypesCaptor.getValue())
            .contains(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
    }

    @Test
    public void generatePassStatistics_correlationIdsFromAuditService_callGetByCorrelationIdWithEachInTurn() {
        List<String> expectedCorrelationIds = stubGetAllCorrelationIds("some correlationId", "some other correlation id");

        List<AuditRecord> anyAuditRecords = singletonList(new AuditRecord("any id", ANY_DATE_TIME, "any email", ANY_AUDIT_EVENT_TYPE, ANY_JSON, "any nino"));

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

        List<String> correlationIds = asList("some correlationId", "some other correlation id");
        when(mockAuditClient.getAllCorrelationIdsForEventType(any()))
            .thenReturn(correlationIds);

        List<AuditRecord> someAuditRecords = asList(
            new AuditRecord("some id", SOME_DATE_TIME, "some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "some nino"),
            new AuditRecord("some other id", SOME_DATE_TIME, "some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "some other nino"));
        List<AuditRecord> someOtherAuditRecords = singletonList(
            new AuditRecord("yet some other id", SOME_DATE_TIME, "yet some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "yet some other nino"));

        when(mockAuditClient.getHistoryByCorrelationId(eq(correlationIds.get(0)), anyList()))
            .thenReturn(someAuditRecords);
        when(mockAuditClient.getHistoryByCorrelationId(eq(correlationIds.get(1)), anyList()))
            .thenReturn(someOtherAuditRecords);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);

        verify(mockConsolidator).auditResultsByCorrelationId(someAuditRecords);
        verify(mockConsolidator).auditResultsByCorrelationId(someOtherAuditRecords);
    }

    @Test
    public void generatePassStatistics_givenResultsByCorrelationIdFromConsolidator_consolidateByNino() {
        stubGetAllCorrelationIds();

        List<AuditResult> resultsByCorrelationId = singletonList(new AuditResult("some correlation id", SOME_DATE, "some nino", SOME_AUDIT_RESULT_TYPE));
        List<AuditResult> moreResultsByCorrelationId = singletonList(new AuditResult("some other correlation id", SOME_DATE, "some other nino", SOME_AUDIT_RESULT_TYPE));
        when(mockConsolidator.auditResultsByCorrelationId(anyList()))
            .thenReturn(resultsByCorrelationId, moreResultsByCorrelationId);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockConsolidator).consolidatedAuditResultsByNino(resultsByCorrelationId);
        verify(mockConsolidator).consolidatedAuditResultsByNino(moreResultsByCorrelationId);
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
    public void generatePassStatistics_givenResultsByNinoFromConsolidator_passedToCalculator() {
        stubGetAllCorrelationIds("any correlation id");

        List<AuditResultByNino> resultsByNino = singletonList(new AuditResultByNino("some nino", emptyList(), SOME_DATE, SOME_AUDIT_RESULT_TYPE));
        when(mockConsolidator.consolidatedAuditResultsByNino(anyList()))
            .thenReturn(resultsByNino);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockPassStatisticsCalculator).result(eq(resultsByNino), anyList(), eq(SOME_DATE), eq(SOME_DATE));
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

    private void stubGetAllCorrelationIds() {
        stubGetAllCorrelationIds("some correlationId", "some other correlation id");
    }

    private List<String> stubGetAllCorrelationIds(String... correlationIds) {
        List<String> allCorrelationIds = (List<String>) asList(correlationIds);
        when(mockAuditClient.getAllCorrelationIdsForEventType(any()))
            .thenReturn(allCorrelationIds);
        return allCorrelationIds;
    }
}


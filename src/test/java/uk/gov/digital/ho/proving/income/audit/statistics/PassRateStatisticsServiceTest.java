package uk.gov.digital.ho.proving.income.audit.statistics;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
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

    private static final long SOME_LONG = 3;
    private static final LocalDate SOME_DATE = LocalDate.MAX;
    private static final LocalDateTime SOME_DATE_TIME = LocalDateTime.MAX;
    private static final AuditEventType SOME_AUDIT_EVENT_TYPE = INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
    private static final JsonNode SOME_JSON = null;
    private static final AuditResultType SOME_AUDIT_RESULT_TYPE = AuditResultType.PASS;

    @Before
    public void setUp() {
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator, mockConsolidator, PAGE_SIZE);
    }

    /**************************
     * AuditClient collaborator
     **************************/

    @Test
    public void generatePassStatistics_givenPageSize_requestedPageSize() {
        int pageSize = 200;
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator, mockConsolidator, pageSize);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockAuditClient).getAuditHistoryPaginated(anyList(), anyInt(), eq(pageSize));
    }

    @Test
    public void generatePassStatistics_firstRequest_firstPageZeroIndexed() {
        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockAuditClient).getAuditHistoryPaginated(anyList(), eq(0), anyInt());
    }

    @Test
    public void generatePassStatisitics_anyParams_expectedEventTypes() {
        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        List<AuditEventType> eventTypes = asList(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
        verify(mockAuditClient).getAuditHistoryPaginated(eq(eventTypes), anyInt(), anyInt());
    }

    @Test
    public void generatePassStatistics_noResults_noMoreRequests() {
        when(mockAuditClient.getAuditHistoryPaginated(anyList(), anyInt(), anyInt()))
            .thenReturn(emptyList());

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockAuditClient, times(1))
            .getAuditHistoryPaginated(anyList(), anyInt(), anyInt());
    }

    @Test
    public void generatePassStatistics_fullPageOfResultsFromAuditService_requestAnotherPage() {
        AuditRecord someAuditRecord = new AuditRecord("some id", SOME_DATE_TIME, "some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "some nino");

        when(mockAuditClient.getAuditHistoryPaginated(anyList(), eq(0), eq(PAGE_SIZE)))
            .thenReturn(asList(someAuditRecord, someAuditRecord));

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockAuditClient)
            .getAuditHistoryPaginated(anyList(), eq(0), eq(PAGE_SIZE));
        verify(mockAuditClient)
            .getAuditHistoryPaginated(anyList(), eq(1), eq(PAGE_SIZE));
    }

    @Test
    public void generatePassStatistics_partialOfResultsFromAuditService_noMoreRequests() {
        AuditRecord someAuditRecord = new AuditRecord("some id", SOME_DATE_TIME, "some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "some nino");

        when(mockAuditClient.getAuditHistoryPaginated(anyList(), eq(0), eq(PAGE_SIZE)))
            .thenReturn(singletonList(someAuditRecord));

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockAuditClient)
            .getAuditHistoryPaginated(anyList(), eq(0), eq(PAGE_SIZE));
        verifyNoMoreInteractions(mockAuditClient);
    }

    /***************************************
     * AuditResultConsolidator collaborator
     ***************************************/

    @Test
    public void generatePassStatistics_givenResultsFromAuditService_passedToConsolidator() {
        AuditRecord someAuditRecord = new AuditRecord("some id", SOME_DATE_TIME, "some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "some nino");
        List<AuditRecord> returnedAuditRecords = singletonList(someAuditRecord);

        when(mockAuditClient.getAuditHistoryPaginated(anyList(), eq(0), eq(PAGE_SIZE)))
            .thenReturn(returnedAuditRecords);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockConsolidator).auditResultsByCorrelationId(returnedAuditRecords);
    }

    @Test
    public void generatePassStatistics_givenResultsByCorrelationIdFromConsolidator_consolidateByNino() {
        List<AuditResult> byCorrelationId = singletonList(new AuditResult("some correlation id", SOME_DATE, "some nino", SOME_AUDIT_RESULT_TYPE));
        when(mockConsolidator.auditResultsByCorrelationId(anyList()))
            .thenReturn(byCorrelationId);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockConsolidator).consolidatedAuditResultsByNino(byCorrelationId);
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
}

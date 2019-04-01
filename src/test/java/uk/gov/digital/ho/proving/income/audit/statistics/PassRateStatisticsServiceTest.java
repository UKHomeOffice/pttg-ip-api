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

    private static final LocalDate SOME_DATE = LocalDate.now();
    private static final int PAGE_SIZE = 1;
    private static final LocalDateTime SOME_DATE_TIME = LocalDateTime.now();
    private static final AuditEventType SOME_AUDIT_EVENT_TYPE = INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
    private static final JsonNode SOME_JSON = null;

    @Before
    public void setUp() {
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator, mockConsolidator, PAGE_SIZE);
    }

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
    public void generatePassStatistics_resultsFromAuditSerivce_requestAnotherPage() {
        AuditRecord someAuditRecord = new AuditRecord("some id", SOME_DATE_TIME, "some email", SOME_AUDIT_EVENT_TYPE, SOME_JSON, "some nino");

        when(mockAuditClient.getAuditHistoryPaginated(anyList(), eq(0), eq(PAGE_SIZE)))
            .thenReturn(singletonList(someAuditRecord));

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockAuditClient)
            .getAuditHistoryPaginated(anyList(), eq(0), eq(PAGE_SIZE));
        verify(mockAuditClient)
            .getAuditHistoryPaginated(anyList(), eq(1), eq(PAGE_SIZE));
    }

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
        AuditResultType someAuditResultType = AuditResultType.PASS;
        List<AuditResult> byCorrelationId = singletonList(new AuditResult("some correlation id", SOME_DATE, "some nino", someAuditResultType));
        when(mockConsolidator.auditResultsByCorrelationId(anyList()))
            .thenReturn(byCorrelationId);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);
        verify(mockConsolidator).consolidatedAuditResultsByNino(byCorrelationId);
    }
}

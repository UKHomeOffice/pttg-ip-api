package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.*;

import java.time.LocalDate;
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
    private PassStatisticsCalculator mockPassStatisticsCalculator;
    @Mock
    private AuditResultFetcher mockAuditResultFetcher;

    private PassRateStatisticsService service;

    private static final long SOME_LONG = 3;
    private static final LocalDate SOME_DATE = LocalDate.now();
    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final AuditResultType SOME_AUDIT_RESULT_TYPE = AuditResultType.PASS;

    private static final int CUT_OFF_DAYS = 10;

    @Before
    public void setUp() {
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator, mockAuditResultFetcher, CUT_OFF_DAYS);
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

        service.generatePassRateStatistics(ANY_DATE, ANY_DATE);

        assertThat(eventTypesCaptor.getValue())
            .contains(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);
    }

    @Test
    public void generatePassStatistics_givenEndDate_getAllCorrelationIdsWithCutOffDate() {
        LocalDate someEndDate = LocalDate.parse("2019-07-31");

        service.generatePassRateStatistics(ANY_DATE, someEndDate);

        LocalDate expectedEndDate = someEndDate.plusDays(CUT_OFF_DAYS);
        then(mockAuditClient).should().getAllCorrelationIdsForEventType(anyList(), eq(expectedEndDate));
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

        service.generatePassRateStatistics(ANY_DATE, ANY_DATE);
        verify(mockPassStatisticsCalculator).result(anyList(), eq(someArchivedResults), any(LocalDate.class), any(LocalDate.class));
    }

    /***********************************
     * AuditResultFetcher collaborator
     ***********************************/

    @Test
    public void generatePassStatistics_correlationIdsFromAuditService_passedToFetcher() {
        List<String> someCorrelationIDs = stubGetAllCorrelationIds("some correlationId", "some other correlation id");

        service.generatePassRateStatistics(ANY_DATE, ANY_DATE);

        then(mockAuditResultFetcher).should().getAuditResults(someCorrelationIDs);
    }

    /****************************************
     * PassStatisticsCalculator collaborator
     ****************************************/

    @Test
    public void generatePassStatistics_givenFromDate_passedToCalculator() {
        LocalDate fromDate = LocalDate.now();
        service.generatePassRateStatistics(fromDate, ANY_DATE);

        verify(mockPassStatisticsCalculator).result(anyList(), anyList(), eq(fromDate), any(LocalDate.class));
    }

    @Test
    public void generatePassStatistics_givenToDate_passedToCalculator() {
        LocalDate toDate = LocalDate.now();
        service.generatePassRateStatistics(ANY_DATE, toDate);

        verify(mockPassStatisticsCalculator).result(anyList(), anyList(), any(LocalDate.class), eq(toDate));
    }

    @Test
    public void generatePassStatistics_givenAuditResultsFromFetcher_expectedListPassedToCalculator() {
        List<String> someCorrelationIds = stubGetAllCorrelationIds("some correlation id", "some other correlation id");

        List<AuditResult> expectedResults = asList(
            new AuditResult("some correlation id", SOME_DATE, "some nino", SOME_AUDIT_RESULT_TYPE),
            new AuditResult("some other correlation id", SOME_DATE, "some other nino", SOME_AUDIT_RESULT_TYPE));

        when(mockAuditResultFetcher.getAuditResults(someCorrelationIds))
            .thenReturn(expectedResults);

        service.generatePassRateStatistics(SOME_DATE, SOME_DATE);

        ArgumentCaptor<List<AuditResult>> auditResultsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockPassStatisticsCalculator).result(auditResultsCaptor.capture(), anyList(), eq(SOME_DATE), eq(SOME_DATE));

        assertThat(auditResultsCaptor.getValue()).containsExactlyElementsOf(expectedResults);
    }

    @Test
    public void generatePassStatistics_givenResultFromCalculator_returnedToCaller() {
        PassRateStatistics passRateStatistics = new PassRateStatistics(SOME_DATE, SOME_DATE, SOME_LONG, SOME_LONG, SOME_LONG, SOME_LONG, SOME_LONG);
        when(mockPassStatisticsCalculator.result(anyList(), anyList(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(passRateStatistics);

        PassRateStatistics actualStatistics = service.generatePassRateStatistics(ANY_DATE, ANY_DATE);
        assertThat(actualStatistics).isEqualTo(passRateStatistics);
    }

    @Test
    public void generatePassStatistics_taxYear_datesPassedToCalculator() {
        TaxYear taxYear = TaxYear.valueOf("2017/2018");
        service.generatePassRateStatistics(taxYear);

        verify(mockPassStatisticsCalculator)
            .result(anyList(), anyList(), eq(taxYear.startDate()), eq(taxYear.endDate()));
    }

    private List<String> stubGetAllCorrelationIds(String... correlationIds) {
        List<String> allCorrelationIds = asList(correlationIds);
        when(mockAuditClient.getAllCorrelationIdsForEventType(any(), any(LocalDate.class)))
            .thenReturn(allCorrelationIds);
        return allCorrelationIds;
    }
}

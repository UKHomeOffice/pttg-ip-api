package uk.gov.digital.ho.proving.income.audit.statistics;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.audit.AuditEventType;
import uk.gov.digital.ho.proving.income.audit.AuditRecord;
import uk.gov.digital.ho.proving.income.audit.AuditResultConsolidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@RunWith(MockitoJUnitRunner.class)
public class PassRateStatisticsServiceCalendarMonthTest {

    @Mock
    private AuditClient mockAuditClient;
    @Mock
    private AuditResultConsolidator mockConsolidator;
    @Mock
    private PassStatisticsCalculator mockPassStatisticsCalculator;

    private PassRateStatisticsService service;

    private static final int PAGE_SIZE = 2;

    @Before
    public void setUp() {
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator, mockConsolidator, PAGE_SIZE);
    }

    @Test
    public void generatePassStatistics_thirtyOneDayMonth_fromFirstToThirtyFirst() {
        YearMonth yearMonth = YearMonth.of(2018, Month.AUGUST);
        service.generatePassRateStatistics(yearMonth);

        verify(mockPassStatisticsCalculator).result(anyList(), anyList(), eq(LocalDate.of(2018, Month.AUGUST, 1)), eq(LocalDate.of(2018, Month.AUGUST, 31)));
    }

    @Test
    public void generatePassStatistics_thirtyDayMonth_fromFirstToThirtieth() {
        YearMonth yearMonth = YearMonth.of(2018, Month.SEPTEMBER);
        service.generatePassRateStatistics(yearMonth);

        verify(mockPassStatisticsCalculator).result(anyList(), anyList(), eq(LocalDate.of(2018, Month.SEPTEMBER, 1)), eq(LocalDate.of(2018, Month.SEPTEMBER, 30)));
    }

    @Test
    public void generatePassStatistics_februaryNonLeapYear_fromFirstToTwentyEighth() {
        YearMonth yearMonth = YearMonth.of(2018, Month.FEBRUARY);
        service.generatePassRateStatistics(yearMonth);

        verify(mockPassStatisticsCalculator).result(anyList(), anyList(), eq(LocalDate.of(2018, Month.FEBRUARY, 1)), eq(LocalDate.of(2018, Month.FEBRUARY, 28)));
    }

    @Test
    public void generatePassStatistics_februaryLeapYear_fromFirstToTwentyEighth() {
        YearMonth yearMonth = YearMonth.of(2016, Month.FEBRUARY);
        service.generatePassRateStatistics(yearMonth);

        verify(mockPassStatisticsCalculator).result(anyList(), anyList(), eq(LocalDate.of(2016, Month.FEBRUARY, 1)), eq(LocalDate.of(2016, Month.FEBRUARY, 29)));
    }
}

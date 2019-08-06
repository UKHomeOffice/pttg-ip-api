package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.audit.AuditResultComparator;
import uk.gov.digital.ho.proving.income.audit.AuditResultConsolidator;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PassRateStatisticsServiceCalendarMonthTest {

    @Mock
    private AuditClient mockAuditClient;
    @Mock
    private PassStatisticsCalculator mockPassStatisticsCalculator;
    @Mock
    private AuditResultFetcher mockAuditResultFetcher;

    private PassRateStatisticsService service;

    @Before
    public void setUp() {
        int anyInt = 5;
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator, mockAuditResultFetcher, anyInt);
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

package uk.gov.digital.ho.proving.income.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatistics;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsService;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;

import static ch.qos.logback.classic.Level.INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PassRateStatisticsResourceTest {

    private static final TaxYear SOME_TAX_YEAR = TaxYear.valueOf("2018/2019");
    private static final LocalDate SOME_DATE = LocalDate.now();
    private static final YearMonth SOME_MONTH = YearMonth.now();
    private static final Model SOME_MODEL = mock(Model.class);
    private static final PassRateStatistics SOME_STATISTICS = PassRateStatistics.builder()
        .fromDate(LocalDate.now())
        .toDate(LocalDate.now())
        .passes(1)
        .totalRequests(1)
        .build();

    @Mock
    private Model mockModel;

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Mock
    private PassRateStatisticsService mockService;

    private PassRateStatisticsResource resource;

    @Before
    public void setUp() {
        resource = new PassRateStatisticsResource(mockService);
        Logger logger = (Logger) LoggerFactory.getLogger(PassRateStatisticsResource.class);
        logger.addAppender(mockAppender);
    }

    @Test
    public void getPassRateStatisticsCsv_anyValidParams_returnEmptyString() {
        mockStatisticsForTaxYear();

        assertThat(resource.getPassRateStatisticsCsv(SOME_TAX_YEAR, null, null, null, SOME_MODEL))
            .isEqualTo("");
    }

    @Test
    public void getPassRateStatisticsCsv_taxYear_serviceCalled() {
        mockStatisticsForTaxYear();

        resource.getPassRateStatisticsCsv(SOME_TAX_YEAR, null, null, null, SOME_MODEL);

        verify(mockService).generatePassRateStatistics(SOME_TAX_YEAR);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void getPassRateStatisticsCsv_month_serviceCalled() {
        mockStatisticsForMonth();

        resource.getPassRateStatisticsCsv(null, SOME_MONTH, null, null, SOME_MODEL);

        verify(mockService).generatePassRateStatistics(SOME_MONTH);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void getPassRateStatisticsCsv_dates_serviceCalled() {
        mockStatisticsForDates();

        LocalDate someFromDate = LocalDate.now();
        LocalDate someToDate = someFromDate.plusDays(1);
        resource.getPassRateStatisticsCsv(null, null, someFromDate, someToDate, SOME_MODEL);

        verify(mockService).generatePassRateStatistics(someFromDate, someToDate);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void getPassRateStatisticsCsv_resultsForTaxYear_setOnModel() {
        when(mockService.generatePassRateStatistics(SOME_TAX_YEAR))
            .thenReturn(SOME_STATISTICS);

        resource.getPassRateStatisticsCsv(SOME_TAX_YEAR, null, null, null, mockModel);

        verify(mockModel).addAttribute("statistics", SOME_STATISTICS);
    }

    @Test
    public void getPassRateStatisticsCsv_resultsForMonth_setOnModel() {
        when(mockService.generatePassRateStatistics(SOME_MONTH))
            .thenReturn(SOME_STATISTICS);

        resource.getPassRateStatisticsCsv(null, SOME_MONTH, null, null, mockModel);

        verify(mockModel).addAttribute("statistics", SOME_STATISTICS);
    }

    @Test
    public void getPassRateStatisticsCsv_resultsForDates_setOnModel() {
        when(mockService.generatePassRateStatistics(SOME_DATE, SOME_DATE))
            .thenReturn(SOME_STATISTICS);

        resource.getPassRateStatisticsCsv(null, null, SOME_DATE, SOME_DATE, mockModel);

        verify(mockModel).addAttribute("statistics", SOME_STATISTICS);
    }

    @Test
    public void getPassRateStatisticsCsv_taxYear_logEntryParameters() {
        mockStatisticsForTaxYear();

        TaxYear taxYear = TaxYear.valueOf("2010/2011");
        resource.getPassRateStatisticsCsv(taxYear, null, null, null, SOME_MODEL);

        verifyEntryLogContains("taxYear=2010/2011");
    }

    @Test
    public void getPassRateStatisticsCsv_month_logEntryParameters() {
        mockStatisticsForMonth();

        YearMonth month = YearMonth.parse("2019-01");
        resource.getPassRateStatisticsCsv(null, month, null, null, SOME_MODEL);

        verifyEntryLogContains("month=2019-01");
    }

    @Test
    public void getPassRateStatisticsCsv_dates_logEntryParameters() {
        mockStatisticsForDates();

        LocalDate fromDate = LocalDate.parse("2018-08-01");
        LocalDate toDate = LocalDate.parse("2018-08-31");
        resource.getPassRateStatisticsCsv(null, null, fromDate, toDate, SOME_MODEL);

        verifyEntryLogContains("fromDate=2018-08-01");
        verifyEntryLogContains("toDate=2018-08-31");
    }

    @Test
    public void getPassRateStatisticsCsv_someResults_logExitWithDates() {
        PassRateStatistics statistics = PassRateStatistics.builder()
            .fromDate(LocalDate.parse("2017-02-01"))
            .toDate(LocalDate.parse("2017-02-28"))
            .build();

        when(mockService.generatePassRateStatistics(any(YearMonth.class)))
            .thenReturn(statistics);

        YearMonth month = YearMonth.of(2017, Month.FEBRUARY);
        resource.getPassRateStatisticsCsv(null, month, null, null, SOME_MODEL);

        verifyExitLogContains("fromDate=2017-02-01", "toDate=2017-02-28");
    }

    @Test
    public void getPassRateStatisticsCsv_someResults_logExitWithRequestCount() {
        PassRateStatistics statistics = PassRateStatistics.builder()
            .totalRequests(29)
            .build();

        when(mockService.generatePassRateStatistics(any(YearMonth.class)))
            .thenReturn(statistics);

        YearMonth month = YearMonth.of(2017, Month.FEBRUARY);
        resource.getPassRateStatisticsCsv(null, month, null, null, SOME_MODEL);

        verifyExitLogContains("totalRequests=29");
    }

    private void mockStatisticsForTaxYear() {
        when(mockService.generatePassRateStatistics(any(TaxYear.class)))
            .thenReturn(SOME_STATISTICS);
    }

    private void mockStatisticsForMonth() {
        when(mockService.generatePassRateStatistics(any(YearMonth.class)))
            .thenReturn(SOME_STATISTICS);
    }

    private void mockStatisticsForDates() {
        when(mockService.generatePassRateStatistics(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(SOME_STATISTICS);
    }

    private void verifyEntryLogContains(String expectedMessage) {
        verify(mockAppender).doAppend(argThat(argument -> {
                LoggingEvent loggingEvent = (LoggingEvent) argument;
                return loggingEvent.getLevel() == INFO &&
                    loggingEvent.getFormattedMessage().contains("Request for pass rate statistics") &&
                    loggingEvent.getFormattedMessage().contains(expectedMessage);
            }
        ));
    }

    private void verifyExitLogContains(String... expectedContents) {
        verify(mockAppender).doAppend(argThat(argument -> {
            LoggingEvent loggingEvent = (LoggingEvent) argument;
            return loggingEvent.getLevel() == INFO &&
                loggingEvent.getFormattedMessage().contains("results") &&
                Arrays.stream(expectedContents).allMatch(expected -> loggingEvent.getFormattedMessage().contains(expected));
        }));
    }
}

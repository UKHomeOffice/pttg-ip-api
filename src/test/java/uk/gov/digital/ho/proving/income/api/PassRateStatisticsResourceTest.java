package uk.gov.digital.ho.proving.income.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatistics;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsService;

import java.time.LocalDate;
import java.time.YearMonth;

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
    private PassRateStatisticsService mockService;

    private PassRateStatisticsResource resource;

    @Before
    public void setUp() {
        resource = new PassRateStatisticsResource(mockService);
    }

    @Test
    public void getPassRateStatisticsCsv_anyValidParams_returnEmptyString() {
        assertThat(resource.getPassRateStatisticsCsv(SOME_TAX_YEAR, null, null, null, SOME_MODEL))
            .isEqualTo("");
    }

    @Test
    public void getPassRateStatisticsCsv_taxYear_serviceCalled() {
        resource.getPassRateStatisticsCsv(SOME_TAX_YEAR, null, null, null, SOME_MODEL);

        verify(mockService).generatePassRateStatistics(SOME_TAX_YEAR);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void getPassRateStatisticsCsv_month_serviceCalled() {
        resource.getPassRateStatisticsCsv(null, SOME_MONTH, null, null, SOME_MODEL);

        verify(mockService).generatePassRateStatistics(SOME_MONTH);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void getPassRateStatisticsCsv_dates_serviceCalled() {
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
    public void gePassRateStatisticsCsv_resultsForMonth_setOnModel() {
        when(mockService.generatePassRateStatistics(SOME_MONTH))
            .thenReturn(SOME_STATISTICS);

        resource.getPassRateStatisticsCsv(null, SOME_MONTH, null, null, mockModel);

        verify(mockModel).addAttribute("statistics", SOME_STATISTICS);
    }

    @Test
    public void gePassRateStatisticsCsv_resultsForDates_setOnModel() {
        when(mockService.generatePassRateStatistics(SOME_DATE, SOME_DATE))
            .thenReturn(SOME_STATISTICS);

        resource.getPassRateStatisticsCsv(null, null, SOME_DATE, SOME_DATE, mockModel);

        verify(mockModel).addAttribute("statistics", SOME_STATISTICS);
    }
}

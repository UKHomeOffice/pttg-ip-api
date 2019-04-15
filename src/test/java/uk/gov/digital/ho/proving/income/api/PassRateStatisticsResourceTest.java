package uk.gov.digital.ho.proving.income.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.supercsv.io.ICsvBeanWriter;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatistics;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsCsvBuilder;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Month;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PassRateStatisticsResourceTest {

    private static final LocalDate SOME_DATE = LocalDate.now();
    private static final long SOME_LONG = 1;

    @Mock
    private PassRateStatisticsService mockPassRateStatisticsService;
    @Mock
    private PassRateStatisticsCsvBuilder mockCsvBuilder;
    @Mock
    private HttpServletResponse mockResponse;

    private PassRateStatisticsResource resource;

    @Before
    public void setUp() throws IOException {
        when(mockResponse.getWriter()).thenReturn(mock(PrintWriter.class));
        resource = new PassRateStatisticsResource(mockPassRateStatisticsService, mockCsvBuilder);
    }

    @Test
    public void getPassRateStatistics_givenFromDate_passedToResource() throws IOException {
        LocalDate fromDate = LocalDate.of(2018, Month.AUGUST, 23);
        resource.getPassRateStatistics(mockResponse, fromDate, SOME_DATE);

        verify(mockPassRateStatisticsService).generatePassRateStatistics(eq(fromDate), any(LocalDate.class));
    }

    @Test
    public void getPassRateStatistics_givenToDate_passedToResource() throws IOException {
        LocalDate toDate = LocalDate.of(2018, Month.AUGUST, 24);
        resource.getPassRateStatistics(mockResponse, SOME_DATE, toDate);

        verify(mockPassRateStatisticsService).generatePassRateStatistics(any(LocalDate.class), eq(toDate));
    }

    @Test
    public void getPassRateStatistics_anyRequest_setCsvWriterOnCsvBuilder() throws IOException {
        resource.getPassRateStatistics(mockResponse, SOME_DATE, SOME_DATE);
        verify(mockCsvBuilder).csvWriter(any(ICsvBeanWriter.class));
        verify(mockResponse).getWriter();
    }

    @Test
    public void getPassRateStatistics_resultsFromService_addedToResponse() throws IOException {
        when(mockPassRateStatisticsService.generatePassRateStatistics(SOME_DATE, SOME_DATE))
            .thenReturn(someStatistics());

        resource.getPassRateStatistics(mockResponse, SOME_DATE, SOME_DATE);

        verify(mockCsvBuilder).buildCsv(someStatistics());
    }

    @Test
    public void getPassRateStatistics_always_setContentTypeCsv() throws IOException {
        resource.getPassRateStatistics(mockResponse, SOME_DATE, SOME_DATE);

        verify(mockResponse).setContentType("text/csv");
    }

    private PassRateStatistics someStatistics() {
        return PassRateStatistics.builder()
            .fromDate(SOME_DATE)
            .toDate(SOME_DATE)
            .passes(SOME_LONG)
            .failures(SOME_LONG)
            .notFound(SOME_LONG)
            .errors(SOME_LONG)
            .build();
    }
}

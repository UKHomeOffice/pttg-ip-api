package uk.gov.digital.ho.proving.income.api;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.supercsv.io.ICsvBeanWriter;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatistics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsCsvViewTest {

    @Mock
    private Map<String, Object> mockModel;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private ICsvBeanWriter mockCsvWriter;

    @Spy
    private StatisticsCsvView view;

    @Before
    public void setUp() throws IOException {
        doReturn(mockCsvWriter).when(view).getCsvWriter(any());
    }

    @Test
    public void renderMergedOutputModel_anyParams_setContentTypeCsv() throws Exception {
        view.renderMergedOutputModel(mockModel, mockRequest, mockResponse);

        verify(mockResponse).setContentType("text/csv");
    }

    @Test
    public void renderMergedOutputModel_anyParams_writeHeader() throws Exception {
        view.renderMergedOutputModel(mockModel, mockRequest, mockResponse);

        verify(mockCsvWriter).writeHeader("From Date", "To Date", "Total Requests", "Passed", "Not Passed", "Not Found", "Error");
    }

    @Test
    public void renderMergedOutputModel_givenStatisticsInModel_writeToCsv() throws Exception {
        PassRateStatistics somePassRateStatistics = PassRateStatistics.builder()
            .totalRequests(3)
            .notFound(3)
            .fromDate(LocalDate.now())
            .toDate(LocalDate.now()).build();
        Map<String, Object> model = ImmutableMap.of("statistics", somePassRateStatistics);

        view.renderMergedOutputModel(model, mockRequest, mockResponse);

        verify(mockCsvWriter).write(somePassRateStatistics, "From Date", "To Date", "Total Requests", "Passed", "Not Passed", "Not Found", "Error");
    }

    @Test
    public void renderMergedOutputModel_anyParams_closeCsvReader() throws Exception {
        view.renderMergedOutputModel(mockModel, mockRequest, mockResponse);

        verify(mockCsvWriter).close();
    }
}

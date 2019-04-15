package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.supercsv.io.ICsvBeanWriter;

import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PassRateStatisticsCsvBuilderTest {

    private static final PassRateStatistics SOME_STATISTICS = new PassRateStatistics(LocalDate.now(), LocalDate.now(), 1, 1, 1, 1, 1);

    @Mock
    private ICsvBeanWriter csvWriter;

    private PassRateStatisticsCsvBuilder csvBuilder;

    @Before
    public void setUp() {
        csvBuilder = new PassRateStatisticsCsvBuilder();
        csvBuilder.csvWriter(csvWriter);
    }

    @Test
    public void buildCsv_anyStatistics_addHeader() throws IOException {
        csvBuilder.buildCsv(SOME_STATISTICS);

        verify(csvWriter).writeHeader("From Date", "To Date", "Total Requests", "Pass", "Fail", "Not Found", "Error");
    }

    @Test
    public void buildCsv_anyStatistics_writerClosed() throws IOException {
        csvBuilder.buildCsv(SOME_STATISTICS);

        verify(csvWriter).close();
    }

    @Test
    public void buildCsv_givenStatistics_writtenToCsv() throws IOException {
        csvBuilder.buildCsv(SOME_STATISTICS);
        verify(csvWriter).write(SOME_STATISTICS, "From Date", "To Date", "Total Requests", "Pass", "Fail", "Not Found", "Error");
    }
}

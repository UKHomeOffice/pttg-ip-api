package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.audit.AuditResultByNino;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PassRateStatisticsServiceTest {

    @Mock
    private AuditClient mockAuditClient;
    @Mock
    private PassStatisticsCalculator mockPassStatisticsCalculator;

    private PassRateStatisticsService service;

    @Before
    public void setUp() {
        service = new PassRateStatisticsService(mockAuditClient, mockPassStatisticsCalculator);
    }

    @Test
    public void generatePassRateStatistics_givenDates_callCollaborators() {
        LocalDate fromDate = LocalDate.MIN;
        LocalDate toDate = LocalDate.MAX;
        List<AuditResultByNino> records;
//        given some records returned

        service.generatePassRateStatistics(fromDate, toDate);
//        verify(mockPassStatisticsCalculator).result(records);
    }
}

package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.audit.FileUtils;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(
    properties = {
        "audit.history.passratestats.pagesize=5"
    })
public class PassRateStatisticsServiceIT {

    private static final LocalDate FROM_DATE = LocalDate.of(2018, Month.AUGUST, 1);
    private static final LocalDate TO_DATE = LocalDate.of(2018, Month.AUGUST, 31);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PassRateStatisticsService passRateStatisticsService;
    @Autowired
    private FileUtils fileUtils;

    private MockRestServiceServer mockAuditService;

    @Before
    public void setUp() {
        mockAuditService = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    @Test
    public void passRateStatistics_noData_statisticsAllZero() {
        String auditHistory = "[ ]";
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(requestTo(containsString("page=0")))
            .andExpect(requestTo(containsString("size=5")))
            .andExpect(method(GET))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        PassRateStatistics actualStatistics = passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE);
        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 0, 0, 0, 0, 0);
        assertThat(actualStatistics).isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_singleResponseWithData_populateStatistics() {
        String nino1RequestPass = fileUtils.
    }
}

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
import static org.springframework.test.web.client.ExpectedCount.times;
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
    private static final String EMPTY_RESPONSE = "[ ]";

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
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(requestTo(containsString("page=0")))
            .andExpect(requestTo(containsString("size=5")))
            .andExpect(method(GET))
            .andRespond(withSuccess(EMPTY_RESPONSE, APPLICATION_JSON));

        PassRateStatistics actualStatistics = passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE);
        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 0, 0, 0, 0, 0);
        assertThat(actualStatistics).isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_singleResponseWithData_populateStatistics() {
        String nino1PassRequest = fileUtils.buildRequest("correlationID 1", "2018-08-01 09:00:00.000", "nino 1");
        String nino1PassResponse = fileUtils.buildResponse("correlationID 1", "2018-08-01 09:00:01.000", "nino 1", "true");

        String nino2FailRequest = fileUtils.buildRequest("correlationID 2", "2018-08-01 09:00:00.000", "nino 2");
        String nino2FailResponse = fileUtils.buildResponse("correlationID 2", "2018-08-01 09:00:01.000", "nino 2", "false");

        String auditHistory = String.format("[%s]", String.join(", ", nino1PassRequest, nino1PassResponse, nino2FailRequest, nino2FailResponse));

        mockAuditServiceResponses(auditHistory, EMPTY_RESPONSE);

        PassRateStatistics actualStatistics = passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE);
        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 2, 1, 1, 0, 0);
        assertThat(actualStatistics).isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_responsesSplitBetweenPages_populateStatistics() {
        String nino1PassRequest = fileUtils.buildRequest("correlationID 1", "2018-08-01 09:00:00.000", "nino 1");
        String nino1PassResponse = fileUtils.buildResponse("correlationID 1", "2018-08-01 09:00:01.000", "nino 1", "true");

        String nino2FailRequest = fileUtils.buildRequest("correlationID 2", "2018-08-01 09:00:00.000", "nino 2");
        String nino2FailResponse = fileUtils.buildResponse("correlationID 2", "2018-08-01 09:00:01.000", "nino 2", "false");

        String nino3NotFoundRequest = fileUtils.buildRequest("correlationID 3", "2018-08-02 10:00:00.000", "nino 3");
        String nino3NotFoundResponse = fileUtils.buildResponseNotFound("correlationID 3", "2018-08-02 10:00:00.500");

        String nino4PassRequest = fileUtils.buildRequest("correlationID 4", "2018-08-03 11:00:00.000", "nino 4");
        String nino4PassResponse = fileUtils.buildResponse("correlationID 4", "2018-08-03 11:00:00.000", "nino 4", "true");

        String nino5PassRequest = fileUtils.buildRequest("correlationID 5", "2018-08-03 11:30:00.000", "nino 5");
        String nino5PassResponse = fileUtils.buildResponse("correlationID 5", "2018-08-03 11:31:00.000", "nino 5", "true");

        String response1 = String.format("[%s]", String.join(", ", nino1PassRequest, nino2FailResponse, nino3NotFoundResponse, nino5PassRequest, nino5PassResponse));
        String response2 = String.format("[%s]", String.join(", ", nino4PassResponse, nino3NotFoundRequest, nino4PassRequest, nino2FailRequest, nino1PassResponse));
        mockAuditServiceResponses(response1, response2, EMPTY_RESPONSE);

        PassRateStatistics actualStatistics = passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE);
        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 5, 3, 1, 1, 0);
        assertThat(actualStatistics).isEqualTo(expectedStatistics);
    }

    // TODO OJR EE-16843: Test best result kept
    // TODO OJR EE-16843: Test no response counts error
    // TODO OJR EE-16843: Test out of date range ignored


    private void mockAuditServiceResponses(String... responses) {
        for (int i = 0; i < responses.length; i++) {
            String response = responses[i];
            mockAuditService
                .expect(requestTo(containsString("/history")))
                .andExpect(requestTo(containsString("page=" + i)))
                .andRespond(withSuccess(response, APPLICATION_JSON));

        }
    }
}

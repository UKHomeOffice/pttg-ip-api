package uk.gov.digital.ho.proving.income.audit.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private static final String EMPTY_RESPONSE = "[ ]";

    @Autowired
    @Qualifier("auditRestTemplate")
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

        String auditHistory = joinAuditRecordsAsJsonList(nino1PassRequest, nino1PassResponse, nino2FailRequest, nino2FailResponse);

        mockAuditServiceResponses(auditHistory);

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

        String response1 = joinAuditRecordsAsJsonList(nino1PassRequest, nino2FailResponse, nino3NotFoundResponse, nino5PassRequest, nino5PassResponse);
        String response2 = joinAuditRecordsAsJsonList(nino4PassResponse, nino3NotFoundRequest, nino4PassRequest, nino2FailRequest, nino1PassResponse);
        mockAuditServiceResponses(response1, response2, EMPTY_RESPONSE);

        PassRateStatistics actualStatistics = passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE);
        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 5, 3, 1, 1, 0);
        assertThat(actualStatistics).isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_multipleResults_bestResultsCountToStats() {
        // Nino1 has a best result of pass
        String nino1FailRequest = fileUtils.buildRequest("correlationID 1", "2018-08-01 09:00:00.000", "nino 1");
        String nino1FailResponse = fileUtils.buildResponse("correlationID 1", "2018-08-01 09:01:00.000", "nino 1", "false");
        String nino1PassRequest = fileUtils.buildRequest("correlationID 2", "2018-08-01 09:02:00.000", "nino 1");
        String nino1PassResponse = fileUtils.buildResponse("correlationID 2", "2018-08-01 09:03:00.000", "nino 1", "true");

        // Nino2 has a best result of failed
        String nino2FailRequest = fileUtils.buildRequest("correlationID 3", "2018-08-01 09:04:00.000", "nino2");
        String nino2FailResponse = fileUtils.buildResponse("correlationID 3", "2018-08-01 09:05:00.000", "nino2", "false");
        String nino2NotFoundRequest = fileUtils.buildRequest("correlationID 4", "2018-08-01 09:06:00.000", "nino2");
        String nino2NotFoundResponse = fileUtils.buildResponseNotFound("correlationID 4", "2018-08-01 09:07:00.000");

        // Nino3 has a best result of not found

        // no corresponding response so this is an error
        String nino3ErrorRequest = fileUtils.buildRequest("correlationID 5", "2018-08-01 09:08:00.000", "nino3");
        String nino3NotFoundRequest = fileUtils.buildRequest("correlationID 6", "2018-08-01 09:09:00.000", "nino3");
        String nino3NotFoundResponse = fileUtils.buildResponseNotFound("correlationID 6", "2018-08-01 09:10:00.000");

        // Nino4 has a best result of error

        // no corresponding response so this is an error
        String nino4ErrorRequest = fileUtils.buildRequest("correlationID 7", "2018-08-01 09:11:00.000", "nino 4");

        String auditHistoryResponse1 = joinAuditRecordsAsJsonList(nino1FailRequest, nino1FailResponse, nino2FailRequest, nino2NotFoundRequest, nino4ErrorRequest);
        String auditHistoryResponse2 = joinAuditRecordsAsJsonList(nino1PassResponse, nino1PassRequest, nino2FailResponse, nino3ErrorRequest, nino3NotFoundResponse);
        String auditHistoryResponse3 = joinAuditRecordsAsJsonList(nino2NotFoundResponse, nino3NotFoundRequest);

        mockAuditServiceResponses(auditHistoryResponse1, auditHistoryResponse2, auditHistoryResponse3);

        PassRateStatistics expectedPassRateStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 4, 1, 1, 1, 1);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedPassRateStatistics);

    }

    @Test
    public void passRateStatistics_requestsOutOfRange_notCounted() {
        // Both request and response too early - should NOT be counted
        String passRequestTooEarly = fileUtils.buildRequest("correlationID 1", FROM_DATE.minusDays(1).atTime(9, 0), "nino1");
        String passResponseTooEarly = fileUtils.buildResponse("correlationID 1", FROM_DATE.minusDays(1).atTime(9, 1), "nino1", "true");

        // Both request and response too late - should NOT be counted
        String failRequestTooLate = fileUtils.buildRequest("correlationID 2", TO_DATE.plusDays(1).atTime(9, 0), "nino2");
        String failResponseTooLate = fileUtils.buildResponse("correlationID 2", TO_DATE.plusDays(1).atTime(9, 0), "nino2", "false");

        // Request too early but response in range - should be counted
        String passRequest2TooEarly = fileUtils.buildRequest("correlationID 3", FROM_DATE.minusDays(1).atTime(9, 0), "nino3");
        String passResponse2InRange = fileUtils.buildResponse("correlationID 3", FROM_DATE.atTime(9, 0), "nino3", "true");

        // Request in range but response too late - should NOT be counted
        String failRequest2InRange = fileUtils.buildRequest("correlationID 4", TO_DATE.atTime(9, 0), "nino4");
        String failResponse2InRange = fileUtils.buildResponse("correlationID 4", TO_DATE.plusDays(1).atTime(9, 0), "nino4", "false");

        // Request and response last day - counted
        String notFoundRequestLastDay = fileUtils.buildRequest("correlationID 5", TO_DATE.atTime(23, 58), "nino5");
        String notFoundResponseLastDay = fileUtils.buildResponseNotFound("correlationID 5", TO_DATE.atTime(23, 59));

        String auditHistoryResponse1 = joinAuditRecordsAsJsonList(passRequestTooEarly, failRequestTooLate, failResponseTooLate, notFoundRequestLastDay, notFoundResponseLastDay);
        String auditHistoryResponse2 = joinAuditRecordsAsJsonList(passRequest2TooEarly, failResponse2InRange, failRequest2InRange, passResponse2InRange, passResponseTooEarly);
        mockAuditServiceResponses(auditHistoryResponse1, auditHistoryResponse2, EMPTY_RESPONSE);

        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 1, 0, 0, 1, 0);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedStatistics);
    }

    private void mockAuditServiceResponses(String... responses) {
        for (int i = 0; i < responses.length; i++) {
            String response = responses[i];
            mockAuditService
                .expect(requestTo(containsString("/history")))
                .andExpect(requestTo(containsString("page=" + i)))
                .andRespond(withSuccess(response, APPLICATION_JSON));

        }
    }

    private String joinAuditRecordsAsJsonList(String... auditRecords) {
        return String.format("[%s]", String.join(", ", auditRecords));
    }
}

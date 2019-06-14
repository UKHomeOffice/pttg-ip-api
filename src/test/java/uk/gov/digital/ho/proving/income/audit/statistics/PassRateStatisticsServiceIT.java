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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest
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
    public void passRateStatistics_noCorrelationIds_statisticsAllZero() {
        mockAuditService
            .expect(requestTo(containsString("/correlationIds")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(method(GET))
            .andRespond(withSuccess(EMPTY_RESPONSE, APPLICATION_JSON));

        mockArchivedResultsResponse(EMPTY_RESPONSE);

        PassRateStatistics actualStatistics = passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE);
        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 0, 0, 0, 0, 0);
        assertThat(actualStatistics).isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_twoResultsTwoNinos_populateStatistics() {
        String nino1PassRequest = fileUtils.buildRequest("correlation-id-1", "2018-08-01 09:00:00.000", "nino 1");
        String nino1PassResponse = fileUtils.buildResponse("correlation-id-1", "2018-08-01 09:00:01.000", "nino 1", "true");

        String nino2FailRequest = fileUtils.buildRequest("correlation-id-2", "2018-08-01 09:00:00.000", "nino 2");
        String nino2FailResponse = fileUtils.buildResponse("correlation-id-2", "2018-08-01 09:00:01.000", "nino 2", "false");

        mockGetCorrelationIds("correlation-id-1", "correlation-id-2");
        mockGetHistoryByCorrelationId("correlation-id-1", nino1PassRequest, nino1PassResponse);
        mockGetHistoryByCorrelationId("correlation-id-2", nino2FailRequest, nino2FailResponse);
        mockArchivedResultsResponse(EMPTY_RESPONSE);

        PassRateStatistics actualStatistics = passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE);
        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 2, 1, 1, 0, 0);
        assertThat(actualStatistics).isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_multipleResults_bestResultsCountToStats() {
        // Nino1 has a best result of pass
        String nino1FailRequest = fileUtils.buildRequest("correlation-id-1", "2018-08-01 09:00:00.000", "nino 1");
        String nino1FailResponse = fileUtils.buildResponse("correlation-id-1", "2018-08-01 09:01:00.000", "nino 1", "false");
        String nino1PassRequest = fileUtils.buildRequest("correlation-id-2", "2018-08-01 09:02:00.000", "nino 1");
        String nino1PassResponse = fileUtils.buildResponse("correlation-id-2", "2018-08-01 09:03:00.000", "nino 1", "true");

        // Nino2 has a best result of failed
        String nino2FailRequest = fileUtils.buildRequest("correlation-id-3", "2018-08-01 09:04:00.000", "nino2");
        String nino2FailResponse = fileUtils.buildResponse("correlation-id-3", "2018-08-01 09:05:00.000", "nino2", "false");
        String nino2NotFoundRequest = fileUtils.buildRequest("correlation-id-4", "2018-08-01 09:06:00.000", "nino2");
        String nino2NotFoundResponse = fileUtils.buildResponseNotFound("correlation-id-4", "2018-08-01 09:07:00.000");

        // Nino3 has a best result of not found

        // no corresponding response so this is an error
        String nino3ErrorRequest = fileUtils.buildRequest("correlation-id-5", "2018-08-01 09:08:00.000", "nino3");
        String nino3NotFoundRequest = fileUtils.buildRequest("correlation-id-6", "2018-08-01 09:09:00.000", "nino3");
        String nino3NotFoundResponse = fileUtils.buildResponseNotFound("correlation-id-6", "2018-08-01 09:10:00.000");

        // Nino4 has a best result of error

        // no corresponding response so this is an error
        String nino4ErrorRequest = fileUtils.buildRequest("correlation-id-7", "2018-08-01 09:11:00.000", "nino 4");

        mockGetCorrelationIds("correlation-id-1", "correlation-id-2", "correlation-id-3", "correlation-id-4", "correlation-id-5",
                              "correlation-id-6", "correlation-id-7");

        mockGetHistoryByCorrelationId("correlation-id-1", nino1FailRequest, nino1FailResponse);
        mockGetHistoryByCorrelationId("correlation-id-2", nino1PassRequest, nino1PassResponse);
        mockGetHistoryByCorrelationId("correlation-id-3", nino2FailRequest, nino2FailResponse);
        mockGetHistoryByCorrelationId("correlation-id-4", nino2NotFoundRequest, nino2NotFoundResponse);
        mockGetHistoryByCorrelationId("correlation-id-5", nino3ErrorRequest);
        mockGetHistoryByCorrelationId("correlation-id-6", nino3NotFoundRequest, nino3NotFoundResponse);
        mockGetHistoryByCorrelationId("correlation-id-7", nino4ErrorRequest);

        mockArchivedResultsResponse(EMPTY_RESPONSE);

        PassRateStatistics expectedPassRateStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 4, 1, 1, 1, 1);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedPassRateStatistics);

    }

    @Test
    public void passRateStatistics_requestsOutOfRange_notCounted() {
        // Pass too early - should NOT be counted
        String passRequestTooEarly = fileUtils.buildRequest("correlation-id-1", FROM_DATE.minusDays(1).atTime(9, 0), "nino1");
        String passResponseTooEarly = fileUtils.buildResponse("correlation-id-1", FROM_DATE.minusDays(1).atTime(9, 1), "nino1", "true");

        // Fail too late - should NOT be counted
        String failRequestTooLate = fileUtils.buildRequest("correlation-id-2", TO_DATE.plusDays(1).atTime(9, 0), "nino2");
        String failResponseTooLate = fileUtils.buildResponse("correlation-id-2", TO_DATE.plusDays(1).atTime(9, 1), "nino2", "false");

        // Pass in range - should be counted
        String passRequestInRange = fileUtils.buildRequest("correlation-id-3", FROM_DATE.atTime(9, 0), "nino3");
        String passResponseInRange = fileUtils.buildResponse("correlation-id-3", FROM_DATE.atTime(9, 1), "nino3", "true");

        // Fail in range - should be counted.
        String failRequestInRange = fileUtils.buildRequest("correlation-id-4", TO_DATE.atTime(9, 0), "nino4");
        String failResponseInRange = fileUtils.buildResponse("correlation-id-4", TO_DATE.atTime(9, 1), "nino4", "false");

        // Not found out range last day - should NOT becounted
        String notFoundRequestTooLate = fileUtils.buildRequest("correlation-id-5", TO_DATE.plusDays(1).atTime(23, 58), "nino5");
        String notFoundResponseTooLate = fileUtils.buildResponseNotFound("correlation-id-5", TO_DATE.plusDays(1).atTime(23, 59));

        // Error in range - should be counted.
        String errorRequestInRange = fileUtils.buildRequest("correlation-id-6", TO_DATE.atTime(9, 0), "nino6");
        // Not having a corresponding response for the errorRequestInRange is what makes it an ERROR.

        mockGetCorrelationIds("correlation-id-1", "correlation-id-2", "correlation-id-3", "correlation-id-4", "correlation-id-5", "correlation-id-6");
        mockGetHistoryByCorrelationId("correlation-id-1", passRequestTooEarly, passResponseTooEarly);
        mockGetHistoryByCorrelationId("correlation-id-2", failRequestTooLate, failResponseTooLate);
        mockGetHistoryByCorrelationId("correlation-id-3", passRequestInRange, passResponseInRange);
        mockGetHistoryByCorrelationId("correlation-id-4", failRequestInRange, failResponseInRange);
        mockGetHistoryByCorrelationId("correlation-id-5", notFoundRequestTooLate, notFoundResponseTooLate);
        mockGetHistoryByCorrelationId("correlation-id-6", errorRequestInRange);

        mockArchivedResultsResponse(EMPTY_RESPONSE);

        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 3, 1, 1, 0, 1);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_archivedResults_addedToCount() {
        String passRequest = fileUtils.buildRequest("correlation-id-1", "2018-08-01 09:02:00.000", "nino 1");
        String passResponse = fileUtils.buildResponse("correlation-id-1", "2018-08-01 09:03:00.000", "nino 1", "true");
        String failRequest = fileUtils.buildRequest("correlation-id-2", "2018-08-01 09:02:00.000", "nino 2");
        String failResponse = fileUtils.buildResponse("correlation-id-2", "2018-08-01 09:03:00.000", "nino 2", "false");

        mockGetCorrelationIds("correlation-id-1", "correlation-id-2");
        mockGetHistoryByCorrelationId("correlation-id-1", passRequest, passResponse);
        mockGetHistoryByCorrelationId("correlation-id-2", failRequest, failResponse);

        String archivedResults = fileUtils.buildArchivedResults(0, 0, 1, 2);
        mockArchivedResultsResponse(archivedResults);

        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 5, 1, 1, 1, 2);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedStatistics);
    }

    private String joinAuditRecordsAsJsonList(String... auditRecords) {
        return String.format("[%s]", String.join(", ", auditRecords));
    }

    private String joinCorrelationIdsAsJsonList(String... correlationIds) {
        List<String> quotedCorrelationIds = Arrays.stream(correlationIds)
                                                  .map(correlationId -> String.format("\"%s\"", correlationId))
                                                  .collect(Collectors.toList());

        return String.format("[ %s ]", String.join(", ", quotedCorrelationIds));
    }

    private void mockGetCorrelationIds(String... correlationIds) {
        mockAuditService
            .expect(requestTo(containsString("/correlationIds")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(method(GET))
            .andRespond(withSuccess(joinCorrelationIdsAsJsonList(correlationIds), APPLICATION_JSON));

    }

    private void mockGetHistoryByCorrelationId(String correlationId, String... auditRecords) {
        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=" + correlationId)))
            .andExpect(method(GET))
            .andRespond(withSuccess(joinAuditRecordsAsJsonList(auditRecords), APPLICATION_JSON));
    }

    private void mockArchivedResultsResponse(String response) {
        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(requestTo(containsString("fromDate=2018-08-01")))
            .andExpect(requestTo(containsString("toDate=2018-08-31")))
            .andExpect(method(GET))
            .andRespond(withSuccess(response, APPLICATION_JSON));
    }
}

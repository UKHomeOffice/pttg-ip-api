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
@SpringBootTest()
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
    public void passRateStatistics_noData_statisticsAllZero() {
        mockAuditService
            .expect(requestTo(containsString("/correlationIds")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(method(GET))
            .andRespond(withSuccess("[ \"correlation-id-1\" ]", APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-1")))
            .andExpect(method(GET))
            .andRespond(withSuccess(EMPTY_RESPONSE, APPLICATION_JSON));

        mockArchivedResultsResponse(EMPTY_RESPONSE);

        PassRateStatistics actualStatistics = passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE);
        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 0, 0, 0, 0, 0);
        assertThat(actualStatistics).isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_singleResponseWithData_populateStatistics() {
        String nino1PassRequest = fileUtils.buildRequest("correlation-id-1", "2018-08-01 09:00:00.000", "nino 1");
        String nino1PassResponse = fileUtils.buildResponse("correlation-id-1", "2018-08-01 09:00:01.000", "nino 1", "true");
        String correlationId1Events = joinAuditRecordsAsJsonList(nino1PassRequest, nino1PassResponse);

        String nino2FailRequest = fileUtils.buildRequest("correlation-id-2", "2018-08-01 09:00:00.000", "nino 2");
        String nino2FailResponse = fileUtils.buildResponse("correlation-id-2", "2018-08-01 09:00:01.000", "nino 2", "false");
        String correlationId2Events = joinAuditRecordsAsJsonList(nino2FailRequest, nino2FailResponse);

        mockAuditService
            .expect(requestTo(containsString("/correlationIds")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(method(GET))
            .andRespond(withSuccess("[ \"correlation-id-1\", \"correlation-id-2\" ]", APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-1")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId1Events, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-2")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId2Events, APPLICATION_JSON));

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

        String correlationId1Events = joinAuditRecordsAsJsonList(nino1FailRequest, nino1FailResponse);
        String correlationId2Events = joinAuditRecordsAsJsonList(nino1PassRequest, nino1PassResponse);

        // Nino2 has a best result of failed
        String nino2FailRequest = fileUtils.buildRequest("correlation-id-3", "2018-08-01 09:04:00.000", "nino2");
        String nino2FailResponse = fileUtils.buildResponse("correlation-id-3", "2018-08-01 09:05:00.000", "nino2", "false");
        String nino2NotFoundRequest = fileUtils.buildRequest("correlation-id-4", "2018-08-01 09:06:00.000", "nino2");
        String nino2NotFoundResponse = fileUtils.buildResponseNotFound("correlation-id-4", "2018-08-01 09:07:00.000");

        String correlationId3Events = joinAuditRecordsAsJsonList(nino2FailRequest, nino2FailResponse);
        String correlationId4Events = joinAuditRecordsAsJsonList(nino2NotFoundRequest, nino2NotFoundResponse);

        // Nino3 has a best result of not found

        // no corresponding response so this is an error
        String nino3ErrorRequest = fileUtils.buildRequest("correlation-id-5", "2018-08-01 09:08:00.000", "nino3");
        String nino3NotFoundRequest = fileUtils.buildRequest("correlation-id-6", "2018-08-01 09:09:00.000", "nino3");
        String nino3NotFoundResponse = fileUtils.buildResponseNotFound("correlation-id-6", "2018-08-01 09:10:00.000");
        String correlationId5Events = joinAuditRecordsAsJsonList(nino3ErrorRequest);
        String correlationId6Events = joinAuditRecordsAsJsonList(nino3NotFoundRequest, nino3NotFoundResponse);


        // Nino4 has a best result of error

        // no corresponding response so this is an error
        String nino4ErrorRequest = fileUtils.buildRequest("correlation-id-7", "2018-08-01 09:11:00.000", "nino 4");
        String correlationId7Events = joinAuditRecordsAsJsonList(nino4ErrorRequest);

        mockAuditService
            .expect(requestTo(containsString("/correlationIds")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(method(GET))
            .andRespond(withSuccess(
                "[ \"correlation-id-1\", \"correlation-id-2\", \"correlation-id-3\"," +
                    " \"correlation-id-4\", \"correlation-id-5\", \"correlation-id-6\", \"correlation-id-7\"]",
                APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-1")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId1Events, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-2")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId2Events, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-3")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId3Events, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-4")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId4Events, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-5")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId5Events, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-6")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId6Events, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-7")))
            .andExpect(method(GET))
            .andRespond(withSuccess(correlationId7Events, APPLICATION_JSON));

        mockArchivedResultsResponse(EMPTY_RESPONSE);

        PassRateStatistics expectedPassRateStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 4, 1, 1, 1, 1);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedPassRateStatistics);

    }

    @Test
    public void passRateStatistics_requestsOutOfRange_notCounted() {
        // Both request and response too early - should NOT be counted
        String passRequestTooEarly = fileUtils.buildRequest("correlation-id-1", FROM_DATE.minusDays(1).atTime(9, 0), "nino1");
        String passResponseTooEarly = fileUtils.buildResponse("correlation-id-1", FROM_DATE.minusDays(1).atTime(9, 1), "nino1", "true");

        // Both request and response too late - should NOT be counted
        String failRequestTooLate = fileUtils.buildRequest("correlation-id-2", TO_DATE.plusDays(1).atTime(9, 0), "nino2");
        String failResponseTooLate = fileUtils.buildResponse("correlation-id-2", TO_DATE.plusDays(1).atTime(9, 0), "nino2", "false");

        // Request too early but response in range - should be counted
        String passRequest2TooEarly = fileUtils.buildRequest("correlation-id-3", FROM_DATE.minusDays(1).atTime(9, 0), "nino3");
        String passResponse2InRange = fileUtils.buildResponse("correlation-id-3", FROM_DATE.atTime(9, 0), "nino3", "true");

        // Request in range but response too late - should NOT be counted
        String failRequest2InRange = fileUtils.buildRequest("correlation-id-4", TO_DATE.atTime(9, 0), "nino4");
        String failResponse2InRange = fileUtils.buildResponse("correlation-id-4", TO_DATE.plusDays(1).atTime(9, 0), "nino4", "false");

        // Request and response last day - counted
        String notFoundRequestLastDay = fileUtils.buildRequest("correlation-id-5", TO_DATE.atTime(23, 58), "nino5");
        String notFoundResponseLastDay = fileUtils.buildResponseNotFound("correlation-id-5", TO_DATE.atTime(23, 59));

        String auditHistoryResponse1 = joinAuditRecordsAsJsonList(passRequestTooEarly, failRequestTooLate, failResponseTooLate, notFoundRequestLastDay, notFoundResponseLastDay);
        String auditHistoryResponse2 = joinAuditRecordsAsJsonList(passRequest2TooEarly, failResponse2InRange, failRequest2InRange, passResponse2InRange, passResponseTooEarly);
        mockAuditServiceResponses(auditHistoryResponse1, auditHistoryResponse2, EMPTY_RESPONSE);

        mockArchivedResultsResponse(EMPTY_RESPONSE);

        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 1, 0, 0, 1, 0);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_archivedResults_addedToCount() {
        String passRequest = fileUtils.buildRequest("correlation-id-1", "2018-08-01 09:02:00.000", "nino 1");
        String passResponse = fileUtils.buildResponse("correlation-id-1", "2018-08-01 09:03:00.000", "nino 1", "true");
        String failRequest = fileUtils.buildRequest("correlation-id-2", "2018-08-01 09:02:00.000", "nino 2");
        String failResponse = fileUtils.buildResponse("correlation-id-2", "2018-08-01 09:03:00.000", "nino 2", "false");

        String auditHistoryResponse = joinAuditRecordsAsJsonList(passRequest, passResponse, failRequest, failResponse);
        mockAuditServiceResponses(auditHistoryResponse);

        String archivedResults = fileUtils.buildArchivedResults(0, 0, 1, 2);
        mockArchivedResultsResponse(archivedResults);

        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 5, 1, 1, 1, 2);
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

    private void mockArchivedResultsResponse(String response) {
        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(requestTo(containsString("fromDate=2018-08-01")))
            .andExpect(requestTo(containsString("toDate=2018-08-31")))
            .andExpect(method(GET))
            .andRespond(withSuccess(response, APPLICATION_JSON));
    }
}

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
        // Pass too early - should NOT be counted
        String passRequestTooEarly = fileUtils.buildRequest("correlation-id-1", FROM_DATE.minusDays(1).atTime(9, 0), "nino1");
        String passResponseTooEarly = fileUtils.buildResponse("correlation-id-1", FROM_DATE.minusDays(1).atTime(9, 1), "nino1", "true");
        String tooEarlyPassEvents = joinAuditRecordsAsJsonList(passRequestTooEarly, passResponseTooEarly);

        // Fail too late - should NOT be counted
        String failRequestTooLate = fileUtils.buildRequest("correlation-id-2", TO_DATE.plusDays(1).atTime(9, 0), "nino2");
        String failResponseTooLate = fileUtils.buildResponse("correlation-id-2", TO_DATE.plusDays(1).atTime(9, 1), "nino2", "false");
        String tooLateFailEvents = joinAuditRecordsAsJsonList(failRequestTooLate, failResponseTooLate);

        // Pass in range - should be counted
        String passRequestInRange = fileUtils.buildRequest("correlation-id-3", FROM_DATE.atTime(9, 0), "nino3");
        String passResponseInRange = fileUtils.buildResponse("correlation-id-3", FROM_DATE.atTime(9, 1), "nino3", "true");
        String passEventsInRange = joinAuditRecordsAsJsonList(passRequestInRange, passResponseInRange);

        // Fail in range - should be counted.
        String failRequestInRange = fileUtils.buildRequest("correlation-id-4", TO_DATE.atTime(9, 0), "nino4");
        String failResponseInRange = fileUtils.buildResponse("correlation-id-4", TO_DATE.atTime(9, 1), "nino4", "false");
        String failEventsInRange = joinAuditRecordsAsJsonList(failRequestInRange, failResponseInRange);

        // Not found out range last day - should NOT becounted
        String notFoundRequestTooLate = fileUtils.buildRequest("correlation-id-5", TO_DATE.plusDays(1).atTime(23, 58), "nino5");
        String notFoundResponseTooLate = fileUtils.buildResponseNotFound("correlation-id-5", TO_DATE.plusDays(1).atTime(23, 59));
        String tooLateNotFoundEvents = joinAuditRecordsAsJsonList(notFoundRequestTooLate, notFoundResponseTooLate);

        // Error in range - should be counted.
        String errorRequestInRange = fileUtils.buildRequest("correlation-id-6", TO_DATE.atTime(9, 0), "nino6");
        String errorEventInRange = joinAuditRecordsAsJsonList(errorRequestInRange);
        // Not having a corresponding response for the errorRequestInRange is what makes it an ERROR.

        mockAuditService
            .expect(requestTo(containsString("/correlationIds")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(method(GET))
            .andRespond(withSuccess(
                "[ \"correlation-id-1\", \"correlation-id-2\", \"correlation-id-3\"," +
                    " \"correlation-id-4\", \"correlation-id-5\", \"correlation-id-6\"]",
                APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-1")))
            .andExpect(method(GET))
            .andRespond(withSuccess(tooEarlyPassEvents, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-2")))
            .andExpect(method(GET))
            .andRespond(withSuccess(tooLateFailEvents, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-3")))
            .andExpect(method(GET))
            .andRespond(withSuccess(passEventsInRange, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-4")))
            .andExpect(method(GET))
            .andRespond(withSuccess(failEventsInRange, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-5")))
            .andExpect(method(GET))
            .andRespond(withSuccess(tooLateNotFoundEvents, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-6")))
            .andExpect(method(GET))
            .andRespond(withSuccess(errorEventInRange, APPLICATION_JSON));

        mockArchivedResultsResponse(EMPTY_RESPONSE);

        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 3, 1, 1, 0, 1);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedStatistics);
    }

    @Test
    public void passRateStatistics_archivedResults_addedToCount() {
        String passRequest = fileUtils.buildRequest("correlation-id-1", "2018-08-01 09:02:00.000", "nino 1");
        String passResponse = fileUtils.buildResponse("correlation-id-1", "2018-08-01 09:03:00.000", "nino 1", "true");
        String passEvents = joinAuditRecordsAsJsonList(passRequest, passResponse);
        String failRequest = fileUtils.buildRequest("correlation-id-2", "2018-08-01 09:02:00.000", "nino 2");
        String failResponse = fileUtils.buildResponse("correlation-id-2", "2018-08-01 09:03:00.000", "nino 2", "false");
        String failEvents = joinAuditRecordsAsJsonList(failRequest, failResponse);

        mockAuditService
            .expect(requestTo(containsString("/correlationIds")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(method(GET))
            .andRespond(withSuccess(
                "[ \"correlation-id-1\", \"correlation-id-2\" ]",
                APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-1")))
            .andExpect(method(GET))
            .andRespond(withSuccess(passEvents, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/historyByCorrelationId")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_REQUEST")))
            .andExpect(requestTo(containsString("eventTypes=INCOME_PROVING_FINANCIAL_STATUS_RESPONSE")))
            .andExpect(requestTo(containsString("correlationId=correlation-id-2")))
            .andExpect(method(GET))
            .andRespond(withSuccess(failEvents, APPLICATION_JSON));

        String archivedResults = fileUtils.buildArchivedResults(0, 0, 1, 2);
        mockArchivedResultsResponse(archivedResults);

        PassRateStatistics expectedStatistics = new PassRateStatistics(FROM_DATE, TO_DATE, 5, 1, 1, 1, 2);
        assertThat(passRateStatisticsService.generatePassRateStatistics(FROM_DATE, TO_DATE))
            .isEqualTo(expectedStatistics);
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

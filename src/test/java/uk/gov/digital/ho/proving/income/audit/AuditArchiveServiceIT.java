package uk.gov.digital.ho.proving.income.audit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(
properties = {
    "audit.history.months=6",
    "pttg.audit.endpoint=http://audit.local/audit",
    "audit.history.endpoint=http://audit.local/history",
    "audit.archive.endpoint=http://audit.local/archive"
})
public class AuditArchiveServiceIT {

    @Autowired
    @Qualifier("createAuditRestTemplate")
    private RestTemplate restTemplate;
    @Autowired
    private AuditArchiveService auditArchiveService;

    @Autowired
    private FileUtils fileUtils;

    private MockRestServiceServer mockAuditService;

    @Before
    public void setUp() {
        mockAuditService = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    @Test
    public void archiveAudit_noHistoryData_noArchiveRequested() {
        String auditHistory = "[]";
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(GET))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        auditArchiveService.archiveAudit();
    }

    @Test
    public void archiveAudit_multipleNinos_archiveAll() {
        String request1 = fileUtils.buildRequest("corr-id-1", "2019-01-01 11:00:00.000", "nino_1");
        String response1 = fileUtils.buildResponse("corr-id-1", "2019-01-01 12:00:00.000", "nino_1", "true");
        String request2 = fileUtils.buildRequest("corr-id-2", "2019-01-01 11:00:00.000", "nino_2");
        String response2 = fileUtils.buildResponse("corr-id-2", "2019-01-01 12:00:00.000", "nino_2", "true");
        String auditHistory = String.format("[%s, %s, %s, %s]", request1, request2, response1, response2);
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(GET))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        String endOfArchive = LocalDate.now().minusMonths(6).minusDays(1).format(DateTimeFormatter.ISO_DATE);
        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_1")))
            .andExpect(jsonPath("$.lastArchiveDate", is(endOfArchive)))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-1")))
            .andExpect(jsonPath("$.result", is("PASS")))
            .andExpect(jsonPath("$.resultDate", is("2019-01-01")))
            .andRespond(withSuccess());
        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_2")))
            .andExpect(jsonPath("$.lastArchiveDate", is(endOfArchive)))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-2")))
            .andExpect(jsonPath("$.result", is("PASS")))
            .andExpect(jsonPath("$.resultDate", is("2019-01-01")))
            .andRespond(withSuccess());

        auditArchiveService.archiveAudit();
    }

    @Test
    public void archiveAudit_noResponseForCorrelationId_requestStatusIsError() {
        String errorRequest = fileUtils.buildRequest("corr-id-1", "2019-01-02 11:00:00.000", "nino_1");
        String auditHistory = String.format("[%s]", errorRequest);
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(GET))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_1")))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-1")))
            .andExpect(jsonPath("$.result", is("ERROR")))
            .andExpect(jsonPath("$.resultDate", is("2019-01-02")))
            .andRespond(withSuccess());

        auditArchiveService.archiveAudit();
    }

    @Test
    public void archiveAudit_errorThenNotFound_bestResultReturned() {
        String errorRequest = fileUtils.buildRequest("corr-id-1", "2019-01-02 11:00:00.000", "nino_1");
        String notFoundRequest = fileUtils.buildRequest("corr-id-2", "2019-01-03 12:00:00.000", "nino_1");
        String notFoundResponse = fileUtils.buildResponseNotFound("corr-id-2", "2019-01-03 13:00:00.000");
        String auditHistory = String.format("[%s, %s, %s]", errorRequest, notFoundRequest, notFoundResponse);
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(GET))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_1")))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-1", "corr-id-2")))
            .andExpect(jsonPath("$.result", is("NOTFOUND")))
            .andRespond(withSuccess());

        auditArchiveService.archiveAudit();
    }

    @Test
    public void archiveAudit_multipleMatchingResults_latestResultReturned() {
        String earlyRequest = fileUtils.buildRequest("corr-id-1", "2019-01-01 12:00:00.000", "nino_1");
        String earlyResponse = fileUtils.buildResponse("corr-id-1", "2019-01-01 13:00:00.000", "nino_1", "true");
        String laterRequest = fileUtils.buildRequest("corr-id-2", "2019-01-02 14:00:00.000", "nino_1");
        String laterResponse = fileUtils.buildResponse("corr-id-2", "2019-01-02 15:00:00.000", "nino_1", "true");
        String auditHistory = String.format("[%s, %s, %s, %s]", earlyRequest, earlyResponse, laterRequest, laterResponse);
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(GET))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", equalTo("nino_1")))
            .andExpect(jsonPath("$.resultDate", equalTo("2019-01-02")))
            .andRespond(withSuccess());

        auditArchiveService.archiveAudit();
    }

    @Test
    public void archiveAudit_multipleMatchingAndMultipleResults_latestResultForAllRequestsReturned() {
        // nino_1 has best result of FAIL
        String nino1RequestFail = fileUtils.buildRequest("corr-id-1", "2019-01-01 12:00:00.000", "nino_1");
        String nino1ResponseFail = fileUtils.buildResponse("corr-id-1", "2019-01-01 13:00:00.000", "nino_1", "false");
        String nino1RequestNoResponse = fileUtils.buildRequest("corr-id-3", "2019-01-02 14:00:00.000", "nino_1");

        // nino_2 has best result of PASS, with the latest being on 2019-01-03
        String nino2FirstPassRequest = fileUtils.buildRequest("corr-id-2", "2019-01-02 14:00:00.000", "nino_2");
        String nino2FirstPassResponse = fileUtils.buildResponse("corr-id-2", "2019-01-02 15:00:00.000", "nino_2", "true");
        String nino2SecondPassRequest = fileUtils.buildRequest("corr-id-5", "2019-01-03 14:00:00.000", "nino_2");
        String nino2SecondPassResponse = fileUtils.buildResponse("corr-id-5", "2019-01-03 15:00:00.000", "nino_2", "true");
        String nino2NotFoundRequest = fileUtils.buildRequest("corr-id-6", "2019-01-04 14:00:00.000", "nino_2");
        String nino2NotFoundResponse = fileUtils.buildResponseNotFound("corr-id-6", "2019-01-04 15:00:00.000");

        // nino_3 has best result of NOTFOUND
        String nino3RequestNoResponse = fileUtils.buildRequest("corr-id-7", "2019-01-02 14:00:00.000", "nino_3");
        String nino3RequestNotFound = fileUtils.buildRequest("corr-id-8", "2019-01-03 14:00:00.000", "nino_3");
        String nino3ResponseNotFound = fileUtils.buildResponseNotFound("corr-id-8", "2019-01-03 15:00:00.000");

        String auditHistory = String.format("[%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s]", nino1RequestFail, nino1ResponseFail,
            nino1RequestNoResponse, nino2FirstPassRequest, nino2FirstPassResponse, nino2SecondPassRequest, nino2SecondPassResponse,
            nino2NotFoundRequest, nino3RequestNoResponse, nino3RequestNotFound, nino3ResponseNotFound, nino2NotFoundResponse);

        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(GET))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        // should have archived best result for nino_1 - FAIL
        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_1")))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-1", "corr-id-3")))
            .andExpect(jsonPath("$.result", is("FAIL")))
            .andExpect(jsonPath("$.resultDate", is("2019-01-01")))
            .andRespond(withSuccess());

        // should have archived latest PASS for nino_2
        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_2")))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-2", "corr-id-5", "corr-id-6")))
            .andExpect(jsonPath("$.result", is("PASS")))
            .andExpect(jsonPath("$.resultDate", is("2019-01-03")))
            .andRespond(withSuccess());

        // should have archived best result for nino_3 - NOTFOUND
        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_3")))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-7", "corr-id-8")))
            .andExpect(jsonPath("$.result", is("NOTFOUND")))
            .andExpect(jsonPath("$.resultDate", is("2019-01-03")))
            .andRespond(withSuccess());

        auditArchiveService.archiveAudit();
    }

}

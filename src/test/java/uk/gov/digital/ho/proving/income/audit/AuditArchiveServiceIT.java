package uk.gov.digital.ho.proving.income.audit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
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
    private RestTemplate restTemplate;
    @Autowired
    private AuditArchiveService auditArchiveService;

    @Value("classpath:json/AuditRecordRequestTemplate.json")
    private Resource auditRecordRequestTemplate;
    @Value("classpath:json/AuditRecordResponseTemplate.json")
    private Resource auditRecordResponseTemplate;
    @Value("classpath:json/AuditRecordResponseNotFoundTemplate.json")
    private Resource auditRecordResourceNotFoundTemplate;

    private MockRestServiceServer mockAuditService;

    @Before
    public void setUp() {
        mockAuditService = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    @Test
    public void archiveAudit_multipleNinos_archiveAll() {
        String request1 = buildRequest("corr-id-1", "2019-01-01 11:00:00.000", "nino_1");
        String response1 = buildResponse("corr-id-1", "2019-01-01 12:00:00.000", "nino_1", "true");
        String request2 = buildRequest("corr-id-2", "2019-01-01 11:00:00.000", "nino_2");
        String response2 = buildResponse("corr-id-2", "2019-01-01 12:00:00.000", "nino_2", "true");
        String auditHistory = String.format("[%s, %s, %s, %s]", request1, request2, response1, response2);
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(POST))
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
    public void archiveAudit_noResponse_errorReturned() {
        String errorRequest = buildRequest("corr-id-1", "2019-01-02 11:00:00.000", "nino_1");
        String auditHistory = String.format("[%s]", errorRequest);
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(POST))
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
        String errorRequest = buildRequest("corr-id-1", "2019-01-02 11:00:00.000", "nino_1");
        String notFoundRequest = buildRequest("corr-id-2", "2019-01-03 12:00:00.000", "nino_1");
        String notFoundResponse = buildResponseNotFound("corr-id-2", "2019-01-03 13:00:00.000");
        String auditHistory = String.format("[%s, %s, %s]", errorRequest, notFoundRequest, notFoundResponse);
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(POST))
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
        String request1 = buildRequest("corr-id-1", "2019-01-01 12:00:00.000", "nino_1");
        String response1 = buildResponse("corr-id-1", "2019-01-01 13:00:00.000", "nino_1", "true");
        String request2 = buildRequest("corr-id-2", "2019-01-02 14:00:00.000", "nino_1");
        String response2 = buildResponse("corr-id-2", "2019-01-02 15:00:00.000", "nino_1", "true");
        String auditHistory = String.format("[%s, %s, %s, %s]", request1, response1, request2, response2);
        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(POST))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        String endOfArchive = LocalDate.now().minusMonths(6).minusDays(1).format(DateTimeFormatter.ISO_DATE);
        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_1")))
            .andExpect(jsonPath("$.lastArchiveDate", is(endOfArchive)))
            .andRespond(withSuccess());

        auditArchiveService.archiveAudit();
    }

    @Test
    public void archiveAudit_multipleMatchingAndMultipleresults_latestResultForAllRequestsReturned() {
        String nino1request1 = buildRequest("corr-id-1", "2019-01-01 12:00:00.000", "nino_1");
        String nino1response1 = buildResponse("corr-id-1", "2019-01-01 13:00:00.000", "nino_1", "false");
        String nino1request2noResponse = buildRequest("corr-id-3", "2019-01-02 14:00:00.000", "nino_1");

        String nino2request1 = buildRequest("corr-id-2", "2019-01-02 14:00:00.000", "nino_2");
        String nino2response1 = buildResponse("corr-id-2", "2019-01-02 15:00:00.000", "nino_2", "true");
        String nino2request2 = buildRequest("corr-id-5", "2019-01-03 14:00:00.000", "nino_2");
        String nino2response2 = buildResponse("corr-id-5", "2019-01-03 15:00:00.000", "nino_2", "true");
        String nino2request3 = buildRequest("corr-id-6", "2019-01-04 14:00:00.000", "nino_2");
        String nino2response3 = buildResponseNotFound("corr-id-6", "2019-01-04 15:00:00.000");

        String nino3request1noResponse = buildRequest("corr-id-7", "2019-01-02 14:00:00.000", "nino_3");
        String nino3request2 = buildRequest("corr-id-8", "2019-01-03 14:00:00.000", "nino_3");
        String nino3response2 = buildResponseNotFound("corr-id-8", "2019-01-03 15:00:00.000");

        String auditHistory = String.format("[%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s]", nino1request1, nino1response1,
            nino2request1, nino2response1, nino1request2noResponse, nino2request2, nino2response2, nino2request3, nino2response3,
            nino3request1noResponse, nino3request2, nino3response2);

        mockAuditService
            .expect(requestTo(containsString("/history")))
            .andExpect(method(POST))
            .andRespond(withSuccess(auditHistory, APPLICATION_JSON));

        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_1")))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-1", "corr-id-3")))
            .andExpect(jsonPath("$.result", is("FAIL")))
            .andExpect(jsonPath("$.resultDate", is("2019-01-01")))
            .andRespond(withSuccess());

        mockAuditService
            .expect(requestTo(containsString("/archive")))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.nino", is("nino_2")))
            .andExpect(jsonPath("$.eventIds.*", containsInAnyOrder("corr-id-2", "corr-id-5", "corr-id-6")))
            .andExpect(jsonPath("$.result", is("PASS")))
            .andExpect(jsonPath("$.resultDate", is("2019-01-03")))
            .andRespond(withSuccess());

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

    private String buildRequest(String correlationId, String dateTime, String nino) {
        String requestTemplate = FileUtils.loadJsonResource(auditRecordRequestTemplate);
        requestTemplate = requestTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        requestTemplate = requestTemplate.replaceAll("\\$\\{date-time}", dateTime);
        requestTemplate = requestTemplate.replaceAll("\\$\\{nino}", nino);
        return requestTemplate;
    }

    private String buildResponse(String correlationId, String dateTime, String nino, String pass) {
        String responseTemplate = FileUtils.loadJsonResource(auditRecordResponseTemplate);
        responseTemplate = responseTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        responseTemplate = responseTemplate.replaceAll("\\$\\{date-time}", dateTime);
        responseTemplate = responseTemplate.replaceAll("\\$\\{nino}", nino);
        responseTemplate = responseTemplate.replaceAll("\\$\\{pass}", pass);
        return responseTemplate;
    }

    private String buildResponseNotFound(String correlationId, String dateTime) {
        String responseTemplate = FileUtils.loadJsonResource(auditRecordResourceNotFoundTemplate);
        responseTemplate = responseTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        responseTemplate = responseTemplate.replaceAll("\\$\\{date-time}", dateTime);
        return responseTemplate;
    }
}

package uk.gov.digital.ho.proving.income.api;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PassRateStatisticsResourceIT {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private MockRestServiceServer mockAuditService;

    @Value("classpath:csv/expectedResults.csv")
    private Resource expectedResultsCsv;
    @Value("classpath:json/AuditRecordRequest.json")
    private Resource auditRecordRequest;
    @Value("classpath:json/AuditRecordResponsePass.json")
    private Resource auditRecordResponsePass;


    @Before
    public void setUp() {
        mockAuditService = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void passRateStatistics_someArchivedData_returnExpectedContent() {
        String historyResponse = String.format("[%s,%s]", readResource(auditRecordRequest), readResource(auditRecordResponsePass));
        mockAuditService.expect(requestTo(containsString("/history")))
            .andExpect(method(GET))
            .andRespond(withSuccess(historyResponse, MediaType.APPLICATION_JSON));
        mockAuditService.expect(requestTo(containsString("/archive")))
            .andExpect(method(GET))
            .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = testRestTemplate.exchange("/statistics?month={month}", GET, null, String.class, ImmutableMap.of("month", "2019-02"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).isEqualTo("text/csv;charset=UTF-8");
        assertThat(response.getBody()).isEqualToNormalizingNewlines(readResource(expectedResultsCsv));
    }

    @Test
    public void passRateStatistics_requestByDate_returnSuccess() {
        mockAuditService.expect(requestTo(containsString("/history")))
            .andExpect(method(GET))
            .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        mockAuditService.expect(requestTo(containsString("/archive")))
            .andExpect(method(GET))
            .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = testRestTemplate.exchange("/statistics?fromDate={fromDate}&toDate={toDate}",
            GET, null, String.class, ImmutableMap.of("fromDate", "2019-02-01", "toDate", "2019-02-28"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String readResource(Resource resource) {
        try {
            return new String(Files.readAllBytes(Paths.get(resource.getURI())));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to read from resource " + resource);
        }
    }
}

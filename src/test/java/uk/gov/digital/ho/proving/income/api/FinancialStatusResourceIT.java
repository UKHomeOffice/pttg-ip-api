package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.ServiceRunner;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusRequest;
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class FinancialStatusResourceIT {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockUpstreamServices;

    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final String ANY_NINO = "AA123456A";
    private static final List<Applicant> ANY_APPLICANTS = Collections.singletonList(new Applicant("any forename", "any surname", ANY_DATE, ANY_NINO));
    private static final FinancialStatusRequest ANY_FINANCIAL_STATUS_REQUEST = new FinancialStatusRequest(ANY_APPLICANTS, ANY_DATE, 0);
    private static final HmrcIndividual ANY_HMRC_INDIVIDUAL = new HmrcIndividual("any firstName", "any lastName", ANY_NINO, ANY_DATE);
    private static final IncomeRecord ANY_INCOME_RECORD = new IncomeRecord(emptyList(), emptyList(), emptyList(), ANY_HMRC_INDIVIDUAL);

    @Before
    public void setUp() {
        mockUpstreamServices = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void getFinancialStatus_noComponentTrace_sendTraceUpstream() throws JsonProcessingException {
        HttpEntity<FinancialStatusRequest> request = new HttpEntity<>(ANY_FINANCIAL_STATUS_REQUEST);

        stubAuditService();
        stubHmrcServiceExpecting(header("x-component-trace", containsString("pttg-ip-api")));

        testRestTemplate.exchange("/incomeproving/v3/individual/financialstatus", POST, request, String.class);
        mockUpstreamServices.verify();
    }

    @Test
    public void getFinancialStatus_withComponentTrace_addToTrace() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-component-trace", "some-component");
        HttpEntity<FinancialStatusRequest> request = new HttpEntity<>(ANY_FINANCIAL_STATUS_REQUEST, headers);

        stubAuditService();
        stubHmrcServiceExpecting(header("x-component-trace", allOf(containsString("pttg-ip-api"), containsString("some-component"))));

        testRestTemplate.exchange("/incomeproving/v3/individual/financialstatus", POST, request, String.class);
        mockUpstreamServices.verify();
    }

    @Test
    public void getFinancialStatus_hmrcServiceReturnsComponentTrace_returnHeader() throws JsonProcessingException {
        String expectedComponentTrace = "hmrc-service";
        stubAuditService();
        stubHmrcService(expectedComponentTrace);


        HttpEntity<FinancialStatusRequest> request = new HttpEntity<>(ANY_FINANCIAL_STATUS_REQUEST);
        ResponseEntity<String> response = testRestTemplate.exchange("/incomeproving/v3/individual/financialstatus", POST, request, String.class);

        mockUpstreamServices.verify();
        assertThat(response.getHeaders().get("x-component-trace")).isEqualTo(expectedComponentTrace);
    }

    private void stubAuditService() {
        mockUpstreamServices.expect(requestTo(containsString("/audit"))).andRespond(withSuccess());
    }

    private void stubHmrcService(String componentTrace) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-component-trace", componentTrace);

        mockUpstreamServices.expect(requestTo(containsString("/income")))
                            .andExpect(method(POST))
                            .andRespond(withSuccess(objectMapper.writeValueAsString(ANY_INCOME_RECORD), APPLICATION_JSON)
                                            .headers(headers));
    }

    private void stubHmrcServiceExpecting(RequestMatcher requestMatcher) throws JsonProcessingException {
        mockUpstreamServices.expect(requestTo(containsString("/income")))
                            .andExpect(method(POST))
                            .andExpect(requestMatcher)
                            .andRespond(withSuccess(objectMapper.writeValueAsString(ANY_INCOME_RECORD), APPLICATION_JSON));
    }
}

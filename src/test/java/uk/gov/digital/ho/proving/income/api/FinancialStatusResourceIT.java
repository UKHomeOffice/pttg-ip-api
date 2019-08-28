package uk.gov.digital.ho.proving.income.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class FinancialStatusResourceIT {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TestRestTemplate testRestTemplate;

    private MockRestServiceServer mockHmrcService;

    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final String ANY_NINO = "QQ123456C";
    private static final List<Applicant> ANY_APPLICANTS = Collections.singletonList(new Applicant("any forename", "any surname", ANY_DATE, ANY_NINO));
    private static final FinancialStatusRequest ANY_FINANCIAL_STATUS_REQUEST = new FinancialStatusRequest(ANY_APPLICANTS, ANY_DATE, 0);

    @Before
    public void setUp() {
        mockHmrcService = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void getFinancialStatus_noComponentTrace_sendTraceUpstream() {
        HttpEntity<FinancialStatusRequest> request = new HttpEntity<>(ANY_FINANCIAL_STATUS_REQUEST);

        mockHmrcService.expect(requestTo(containsString("/income")))
                       .andExpect(method(POST))
                       .andExpect(header("x-component-trace", containsString("pttg-ip-api")));

        testRestTemplate.exchange("/incomeproving/v3/individual/financialstatus", POST, request, String.class);
        mockHmrcService.verify();
    }

    @Test
    public void getFinancialStatus_withComponentTrace_addToTrace() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-component-trace", "some-component");
        HttpEntity<FinancialStatusRequest> request = new HttpEntity<>(ANY_FINANCIAL_STATUS_REQUEST, headers);

        mockHmrcService.expect(requestTo(containsString("/income")))
                       .andExpect(method(POST))
                       .andExpect(header("x-component-trace", containsString("pttg-ip-api"), containsString("some-component")));

        testRestTemplate.exchange("/incomeproving/v3/individual/financialstatus", POST, request, String.class);
        mockHmrcService.verify();
    }
}

package uk.gov.digital.ho.proving.income.alert;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.proving.income.acl.EarningsServiceNoUniqueMatch;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.audit.AuditEntryJpaRepository;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecordService;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration
@RunWith(SpringRunner.class)
@TestPropertySource(
    locations = "classpath:application-alertintegrationtest.properties")
public class AlertingIntegrationTest {

    private static final String url = "/incomeproving/v2/individual/financialstatus";
    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(options().port(8084));

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private TestRestTemplate restTemplate;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private AuditEntryJpaRepository auditEntryJpaRepository;

    @Value("${alert.individual.usage.daily.threshold}")
    private int dailyUsageThreshold;
    @Value("${alert.match.failure.threshold}")
    private int matchFailureThreshold;

    @MockBean
    private IncomeRecordService mockIncomRecordService;


    @Before
    public void setup() {
        when(mockIncomRecordService.getIncomeRecord(any(), any(), any())).thenReturn(new IncomeRecord(Collections.emptyList(), Collections.emptyList()));
        stubFor(post(urlPathMatching("/api/events"))
            .willReturn(aResponse()
                .withStatus(200)));
        auditEntryJpaRepository.deleteAll();
    }

    @Test
    public void shouldNotAlertWhenManyRequestsBelowThreshold() throws Exception {
        makeRequests(dailyUsageThreshold);

        verify(
            0,
            postRequestedFor(
                urlEqualTo("/api/events")).
                withRequestBody(containing("Proving Things, Income Proving, Excessive Usage"))
        );
    }

    @Test
    public void shouldAlertWhenTooManyRequests() throws Exception {
        makeRequests(dailyUsageThreshold + 1);

        verify(
            postRequestedFor(
                urlEqualTo("/api/events")).
                withHeader("Content-Type", equalTo("application/json")).
                withHeader("Authorization", equalTo("Bearer test-sysdig-secret")).
                withRequestBody(containing("Proving Things, Income Proving, Excessive Usage"))
        );
    }

    @Test
    public void shouldNotAlertWhenManyMatchFailuresBelowThreshold() throws Exception {
        when(mockIncomRecordService.getIncomeRecord(any(), any(), any())).thenThrow(new EarningsServiceNoUniqueMatch());

        makeRequests(matchFailureThreshold);

        verify(
            0,
            postRequestedFor(
                urlEqualTo("/api/events")).
                withRequestBody(containing("Proving Things, Income Proving, Excessive match failures"))
        );
    }

    @Test
    public void shouldAlertWhenTooManyMatchFailures() throws Exception {
        when(mockIncomRecordService.getIncomeRecord(any(), any(), any())).thenThrow(new EarningsServiceNoUniqueMatch());

        makeRequests(matchFailureThreshold + 1);

        verify(
            postRequestedFor(
                urlEqualTo("/api/events")).
                withHeader("Content-Type", equalTo("application/json")).
                withHeader("Authorization", equalTo("Bearer test-sysdig-secret")).
                withRequestBody(containing("Proving Things, Income Proving, Excessive match failures"))
        );
    }

    private void makeRequests(int count) {
        for (int i = 0; i < count; i++) {
            makeRequest();
        }
    }
    private void makeRequest() {
        restTemplate.exchange(
            url,
            HttpMethod.POST,
            toEntity("{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2017-08-21\",\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":0}"),
            Void.class);
    }

    private HttpEntity<String> toEntity(String message) {
        return new HttpEntity<>(message, generateRestHeaders());
    }

    private HttpHeaders generateRestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(RequestData.USER_ID_HEADER, "bean.man");
        return headers;
    }


}

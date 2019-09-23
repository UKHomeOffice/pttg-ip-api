package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.ServiceRunner;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceRunner.class)
public class AuditClientRetryTest {

    private static final AuditEventType ANY_EVENT_TYPE = INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
    private static final UUID ANY_UUID = UUID.randomUUID();
    private static final Map<String, Object> ANY_MAP = emptyMap();

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public AuditClient auditClient;

    private MockRestServiceServer auditMockService;

    @Before
    public void setUp() {
        auditMockService = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void add_httpException_shouldRetry() {
        auditMockService.expect(times(4), requestTo(containsString("/audit")))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR));
        auditMockService.expect(requestTo(containsString("/audit")))
                        .andRespond(withSuccess());

        assertThatCode(() -> auditClient.add(ANY_EVENT_TYPE, ANY_UUID, ANY_MAP)).doesNotThrowAnyException();
        auditMockService.verify();
    }
}

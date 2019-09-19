package uk.gov.digital.ho.proving.income.hmrc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.ServiceRunner;
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.time.LocalDate;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceRunner.class)
public class HmrcClientRetryTest {

    private static final LocalDate ANY_DATE = LocalDate.now();
    private static final Identity ANY_IDENTITY = new Identity("any name", "any name", ANY_DATE, "any nino");
    private static final HmrcIndividual ANY_INDIVIDUAL = new HmrcIndividual("any name", "any name", "any nino", ANY_DATE);
    private static final IncomeRecord EXPECTED_INCOME_RECORD = new IncomeRecord(emptyList(), emptyList(), emptyList(), ANY_INDIVIDUAL);

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public HmrcClient hmrcClient;

    private MockRestServiceServer hmrcMockService;

    @Before
    public void setUp() {
        hmrcMockService = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void getIncomeRecord_serverError_shouldRetry() throws JsonProcessingException {
        hmrcMockService.expect(requestTo(containsString("/income")))
                       .andRespond(withStatus(INTERNAL_SERVER_ERROR));
        hmrcMockService.expect(requestTo(containsString("/income")))
                       .andRespond(withStatus(INTERNAL_SERVER_ERROR));
        hmrcMockService.expect(requestTo(containsString("/income")))
                       .andRespond(withSuccess(hmrcSuccessResponse(), MediaType.APPLICATION_JSON));

        IncomeRecord actualIncomeRecord = hmrcClient.getIncomeRecord(ANY_IDENTITY, ANY_DATE, ANY_DATE);
        assertThat(actualIncomeRecord).isEqualTo(EXPECTED_INCOME_RECORD);
    }

    private String hmrcSuccessResponse() throws JsonProcessingException {
        return objectMapper.writeValueAsString(EXPECTED_INCOME_RECORD);
    }
}

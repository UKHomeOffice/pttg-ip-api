package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusCheckResponse;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusRequest;
import uk.gov.digital.ho.proving.income.api.domain.ResponseStatus;
import uk.gov.digital.ho.proving.income.audit.AuditClient;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({FinancialStatusResource.class, NinoUtils.class})
public class FinancialStatusResourceWebTest {

    private static final String SMOKE_TEST_NINO = "QQ123456C";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean private FinancialStatusService mockFinancialStatusService;
    @MockBean private AuditClient mockAuditClient;
    @MockBean private RestTemplate mockRestTemplate;

    @Test
    public void getFinancialStatus_smokeTestNino_smokeTest_returnOk() throws Exception {
        when(mockFinancialStatusService.getIncomeRecords(anyList(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(emptyMap());
        when(mockFinancialStatusService.calculateResponse(any(LocalDate.class), anyInt(), eq(emptyMap())))
            .thenReturn(new FinancialStatusCheckResponse(new ResponseStatus("any", "any"), emptyList(), emptyList()));

        mockMvc.perform(financialStatusRequest(RequestData.SMOKE_TESTS_USER_ID))
               .andExpect(status().isOk());
    }

    @Test
    public void getFinancialStatus_smokeTestNino_notASmokeTest_returnBadRequest() throws Exception {
        when(mockFinancialStatusService.getIncomeRecords(anyList(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(emptyMap());
        when(mockFinancialStatusService.calculateResponse(any(LocalDate.class), anyInt(), eq(emptyMap())))
            .thenReturn(new FinancialStatusCheckResponse(new ResponseStatus("any", "any"), emptyList(), emptyList()));

        mockMvc.perform(financialStatusRequest("not a smoke test user"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status.code").value("0004"));
    }

    private MockHttpServletRequestBuilder financialStatusRequest(String userId) throws JsonProcessingException {
        return post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestWithTestNino())
            .header(RequestData.USER_ID_HEADER, userId);
    }

    private String requestWithTestNino() throws JsonProcessingException {
        LocalDate anyDateOfBirth = LocalDate.now();
        List<Applicant> applicant = singletonList(new Applicant("any forename", "any surname", anyDateOfBirth, SMOKE_TEST_NINO));
        return objectMapper.writeValueAsString(new FinancialStatusRequest(applicant, LocalDate.now(), 0));
    }
}

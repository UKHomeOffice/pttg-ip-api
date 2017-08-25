package uk.gov.digital.ho.proving.income.feedback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.audit.AuditService;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserFeedbackController.class, secure = false)
public class UserFeedbackControllerWebTest {

    private static final String feedbackUrl = "/incomeproving/v2/feedback";

    @MockBean FeedbackService mockFeedbackService;
    @MockBean RestTemplate mockRestTemplate;
    @MockBean AuditService mockAuditService;

    @Autowired private MockMvc mockMvc;

    @Test
    public void shouldReturnHttpOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(feedbackUrl)
                                                .content("{some feedback}")
                                                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(mockFeedbackService).add("{some feedback}");
    }
}

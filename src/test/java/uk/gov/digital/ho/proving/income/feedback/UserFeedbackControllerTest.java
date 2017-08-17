package uk.gov.digital.ho.proving.income.feedback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class UserFeedbackControllerTest {

    @Mock private FeedbackService mockFeedbackService;

    @InjectMocks
    private UserFeedbackController userFeedbackController;

    @Test
    public void shouldUseCollaborators() {

        userFeedbackController.recordFeedback("any feedback");

        verify(mockFeedbackService).add("any feedback");
    }

    @Test
    public void shouldReturnHttpOK() {

        ResponseEntity responseEntity = userFeedbackController.recordFeedback("any feedback");

        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        assertThat(responseEntity.getBody()).isNull();
    }
}

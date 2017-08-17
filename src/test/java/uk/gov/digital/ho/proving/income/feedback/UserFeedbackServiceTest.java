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
public class UserFeedbackServiceTest {

    @Mock private FeedbackRepository mockFeedbackRepository;

    @InjectMocks
    private UserFeedbackService userFeedbackService;

    @Test
    public void shouldUseCollaborators() {

        userFeedbackService.recordFeedback("any feedback");

        verify(mockFeedbackRepository).add("any feedback");
    }

    @Test
    public void shouldReturnHttpOK() {

        ResponseEntity responseEntity = userFeedbackService.recordFeedback("any feedback");

        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        assertThat(responseEntity.getBody()).isNull();
    }
}

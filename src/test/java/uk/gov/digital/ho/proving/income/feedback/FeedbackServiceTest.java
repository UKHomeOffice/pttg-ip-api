package uk.gov.digital.ho.proving.income.feedback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.RequestData;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceTest {

    @Mock private FeedbackEntryJpaRepository mockFeedbackEntryJpaRepository;
    @Mock private RequestData mockRequestData;

    @Captor private ArgumentCaptor<FeedbackEntry> captorFeedbackEntry;

    @InjectMocks
    private FeedbackService feedbackService;

    @Before
    public void setup() {
        when(mockRequestData.sessionId()).thenReturn("some session id");
        when(mockRequestData.deploymentName()).thenReturn("some deployment name");
        when(mockRequestData.deploymentNamespace()).thenReturn("some deployment namespace");
        when(mockRequestData.userId()).thenReturn("some user id");
    }

    @Test
    public void shouldUseCollaborators() {

        feedbackService.add("any feedback");

        verify(mockFeedbackEntryJpaRepository).save(any(FeedbackEntry.class));
    }

    @Test
    public void shouldCreateFeedbackEntry() {

        feedbackService.add("some feedback");

        verify(mockFeedbackEntryJpaRepository).save(captorFeedbackEntry.capture());

        FeedbackEntry arg = captorFeedbackEntry.getValue();

        try {
            UUID.fromString(arg.getUuid());
        } catch (IllegalArgumentException e) {
            fail("Invalid UUID");
        }

        assertThat(arg.getSessionId()).isEqualTo("some session id");
        assertThat(arg.getDeployment()).isEqualTo("some deployment name");
        assertThat(arg.getNamespace()).isEqualTo("some deployment namespace");
        assertThat(arg.getUserId()).isEqualTo("some user id");
        assertThat(arg.getDetail()).isEqualTo("some feedback");
    }
}

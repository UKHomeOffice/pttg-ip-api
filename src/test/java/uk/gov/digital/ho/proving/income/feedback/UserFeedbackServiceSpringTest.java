package uk.gov.digital.ho.proving.income.feedback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.proving.income.ServiceRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= RANDOM_PORT, classes = {ServiceRunner.class})
public class UserFeedbackServiceSpringTest {

    @Autowired
    private UserFeedbackService controller;

    @Test
    public void shouldInitialiseController() {
        assertThat(controller).isNotNull();
    }
}


package uk.gov.digital.ho.proving.income.feedback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
public class UserFeedbackController {

    private final FeedbackService feedbackService;

    public UserFeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping(path = "/incomeproving/v2/feedback", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity recordFeedback(
        @RequestBody String feedback) {

        log.info("Add feedback entry");

        feedbackService.add(feedback);

        log.info("Feedback entry added");

        return new ResponseEntity(HttpStatus.OK);
    }
}

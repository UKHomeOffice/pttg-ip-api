package uk.gov.digital.ho.proving.income.feedback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserFeedbackService {

    private final FeedbackRepository feedbackRepository;

    public UserFeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping(path = "/incomeproving/v2/feedback/{nino}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity recordFeedback(
        @PathVariable(value = "nino") String nino,
        @RequestBody String feedback) {

        feedbackRepository.add(nino, feedback);

        return new ResponseEntity(HttpStatus.OK);
    }
}

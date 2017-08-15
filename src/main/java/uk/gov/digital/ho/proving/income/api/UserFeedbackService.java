package uk.gov.digital.ho.proving.income.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserFeedbackService {

    @PostMapping(path = "/incomeproving/v2/feedback/{nino}")
    public ResponseEntity recordFeedback(
        @PathVariable(value = "nino") String nino

    ) {

        return new ResponseEntity(HttpStatus.OK);
    }
}

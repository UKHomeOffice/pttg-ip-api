package uk.gov.digital.ho.proving.income;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * I've decided to keep this test as it is useful for debugging Jackson marshaling / unmarshaling errors in other tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= RANDOM_PORT, classes = {ServiceRunner.class})
@Ignore
public class JacksonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void thatRequestsAreUnmartialed() throws IOException {
        String json = "{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2017-08-21\"}],\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":0}";
        FinancialStatusRequest request = objectMapper.readValue(json, FinancialStatusRequest.class);

        assertThat(request).isNotNull();
    }

    @Test
    public void thatRequestsMartialed() throws IOException {
        Applicant applicant = new Applicant("Mark", "Surname", LocalDate.of(2017, Month.AUGUST, 21), "AA123456A");
        List<Applicant> applicants = Arrays.asList(applicant);
        LocalDate raisedDate = LocalDate.of(2017, Month.AUGUST, 21);

        FinancialStatusRequest request = new FinancialStatusRequest(applicants, raisedDate, 0);
        String json = objectMapper.writeValueAsString(request);

        assertThat(json).isNotNull();
    }

}

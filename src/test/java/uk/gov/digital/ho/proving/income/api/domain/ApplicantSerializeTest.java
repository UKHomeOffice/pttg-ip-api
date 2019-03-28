package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;
import uk.gov.digital.ho.proving.income.application.TimeoutProperties;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicantSerializeTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", new TimeoutProperties()).createObjectMapper();

    @Test
    public void thatJsonIsDeserialized() throws IOException {
        String json = "{\"forename\": \"forename-value\", \"surname\": \"surname-value\", \"dateOfBirth\": \"1999-12-31\", \"nino\": \"nino-value\"}";
        Applicant applicant= objectMapper.readValue(json, Applicant.class);

        assertThat(applicant).isNotNull();
        assertThat(applicant.forename()).isEqualTo("forename-value");
        assertThat(applicant.surname()).isEqualTo("surname-value");
        assertThat(applicant.dateOfBirth()).isEqualTo(LocalDate.of(1999, Month.DECEMBER, 31));
        assertThat(applicant.nino()).isEqualTo("nino-value");
    }

    @Test
    public void thatObjectIsSerialized() throws IOException {
        Applicant applicant= new Applicant("forename-value", "surname-value", LocalDate.of(1999, Month.DECEMBER, 31), "nino-value");

        String json = objectMapper.writeValueAsString(applicant);

        assertThat(json).isNotNull();
        assertThat(objectMapper.readTree(json)).isEqualTo(objectMapper.readTree("{\"forename\": \"forename-value\", \"surname\": \"surname-value\", \"dateOfBirth\": \"1999-12-31\", \"nino\": \"nino-value\"}"));
    }
}

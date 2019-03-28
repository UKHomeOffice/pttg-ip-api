package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;
import uk.gov.digital.ho.proving.income.application.TimeoutProperties;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HmrcIndividualTest {
    private ObjectMapper objectMapper = new ServiceConfiguration("", new TimeoutProperties()).createObjectMapper();

    @Test
    public void thatJsonIsDeserialized() throws IOException {
        String json = "{\"firstName\": \"firstname\", \"lastName\": \"lastname\", \"dateOfBirth\": \"1970-01-01\", \"nino\": \"QQ123456C\"}";

        HmrcIndividual individual = objectMapper.readValue(json, HmrcIndividual.class);

        assertThat(individual).isNotNull();
        assertThat(individual.nino()).isEqualTo("QQ123456C");
        assertThat(individual.dateOfBirth()).isEqualTo(LocalDate.parse("1970-01-01"));
        assertThat(individual.lastName()).isEqualTo("lastname");
        assertThat(individual.firstName()).isEqualTo("firstname");
    }
}

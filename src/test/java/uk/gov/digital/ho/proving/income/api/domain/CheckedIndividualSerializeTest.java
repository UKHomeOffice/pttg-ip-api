package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckedIndividualSerializeTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0,0, 0, 0, 0).createObjectMapper();

    @Test
    public void thatJsonIsDeserialized() throws IOException {
        String json = "{\"nino\": \"nino-value\", \"employers\": [\"employer1\", \"employer2\"]}";

        CheckedIndividual checkedIndividual = objectMapper.readValue(json, CheckedIndividual.class);

        assertThat(checkedIndividual).isNotNull();
        assertThat(checkedIndividual.nino()).isEqualTo("nino-value");
        assertThat(checkedIndividual.employers()).isNotNull();
        assertThat(checkedIndividual.employers().get(0)).isEqualTo("employer1");
        assertThat(checkedIndividual.employers().get(1)).isEqualTo("employer2");
    }

    @Test
    public void thatObjectIsSerialized() throws IOException {
        CheckedIndividual checkedIndividual = new CheckedIndividual("nino-value", Arrays.asList("employer1", "employer2"));

        String json = objectMapper.writeValueAsString(checkedIndividual);

        assertThat(json).isNotNull();
        assertThat(objectMapper.readTree(json)).isEqualTo(objectMapper.readTree("{\"nino\": \"nino-value\", \"employers\": [\"employer1\", \"employer2\"]}"));
    }
}

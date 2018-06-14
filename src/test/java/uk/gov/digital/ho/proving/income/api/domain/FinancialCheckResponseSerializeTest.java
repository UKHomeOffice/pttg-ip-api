package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class FinancialCheckResponseSerializeTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0).createObjectMapper();

    @Test
    public void thatJsonIsDeserialized() throws IOException {
        String json = "{\"status\": {\"code\": \"code-value\", \"message\": \"message-value\"}, \"individuals\": [], \"categoryChecks\": []}";

        FinancialStatusCheckResponse response = objectMapper.readValue(json, FinancialStatusCheckResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.status()).isNotNull();
        assertThat(response.individuals()).isNotNull();
        assertThat(response.categoryChecks()).isNotNull();
    }

    @Test
    public void thatObjectIsSerialized() throws IOException {
        FinancialStatusCheckResponse response = new FinancialStatusCheckResponse(new ResponseStatus("code-value", "message-value"), new ArrayList<>(), new ArrayList<>());

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).isNotNull();
        assertThat(objectMapper.readTree(json)).isEqualTo(objectMapper.readTree("{\"status\": {\"code\": \"code-value\", \"message\": \"message-value\"}, \"individuals\": [], \"categoryChecks\": []}"));
    }
}

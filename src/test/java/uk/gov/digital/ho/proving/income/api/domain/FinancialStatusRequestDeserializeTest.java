package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class FinancialStatusRequestDeserializeTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0).createObjectMapper();

    @Test
    public void thatJsonIsDeserialized() throws IOException {
        String json = "{\"individuals\": [], \"applicationRaisedDate\": \"2018-05-31\", \"dependants\": 0}";

        FinancialStatusRequest request = objectMapper.readValue(json, FinancialStatusRequest.class);

        assertThat(request).isNotNull();
        assertThat(request.applicants()).isNotNull();
        assertThat(request.dependants()).isEqualTo(0);
    }

    @Test
    public void thatObjectIsSerialized() throws IOException {
        FinancialStatusRequest request = new FinancialStatusRequest(new ArrayList<>(), LocalDate.of(2018, Month.MAY, 31), 0);

        String json = objectMapper.writeValueAsString(request);

        assertThat(objectMapper.readTree(json)).isEqualTo(objectMapper.readTree("{\"individuals\": [], \"applicationRaisedDate\": \"2018-05-31\", \"dependants\": 0}"));

    }

}

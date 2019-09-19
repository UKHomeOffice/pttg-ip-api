package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseResponseSerializeTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0, 0, 0, 0, 0).createObjectMapper();

    @Test
    public void thatJsonIsDeserialized() throws IOException {
        String json = "{\"status\": {\"code\": \"code-value\", \"message\": \"message-value\"}}";
        BaseResponse response = objectMapper.readValue(json, BaseResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.status()).isNotNull();
        assertThat(response.status().code()).isEqualTo("code-value");
        assertThat(response.status().message()).isEqualTo("message-value");
    }

    @Test
    public void thatObjectIsSerialized() throws IOException {
        BaseResponse response = new BaseResponse(new ResponseStatus("code-value", "message-value"));

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).isNotNull();
        assertThat(objectMapper.readTree(json)).isEqualTo(objectMapper.readTree("{\"status\": {\"code\": \"code-value\", \"message\": \"message-value\"}}"));
    }

}

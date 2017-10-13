package uk.gov.digital.ho.proving.income.api;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestDataTest {

    private RequestData requestData;

    @Before
    public void setup() {
        requestData = new RequestData();
        ReflectionTestUtils.setField(requestData, "hmrcBasicAuth", "user:password");
    }

    @Test
    public void shouldProduceBasicAuthHeaderValue() {
        assertThat(requestData.hmrcBasicAuth()).isEqualTo("Basic dXNlcjpwYXNzd29yZA==");
    }

}

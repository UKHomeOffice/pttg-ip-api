package uk.gov.digital.ho.proving.income.application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TimeoutProperties.class, TimeoutPropertiesTest.TestConfig.class})
@TestPropertySource(properties = {
    "timeouts.hmrc-service.read-ms=120000",
    "timeouts.hmrc-service.connect-ms=1000"
})

public class TimeoutPropertiesTest {

    @TestConfiguration
    @EnableConfigurationProperties
    public static class TestConfig {}

    private TimeoutProperties timeoutProperties = new TimeoutProperties();

    @Before
    public void setup() {
        timeoutProperties.setHmrcService(new TimeoutProperties.HmrcService());
    }

    @Test
    public void shouldLoadRestTemplateTimeouts() {
        assertThat(timeoutProperties.getHmrcService().getReadMs()).isEqualTo(1000);
        assertThat(timeoutProperties.getHmrcService().getConnectMs()).isEqualTo(2000);
    }
}


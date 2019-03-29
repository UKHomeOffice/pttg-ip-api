package uk.gov.digital.ho.proving.income.application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    "timeouts.hmrc-service.connect-ms=1000",
    "timeouts.audit-service.read-ms=10000",
    "timeouts.audit-service.connect-ms=1000"
})

public class TimeoutPropertiesTest {

    @TestConfiguration
    @EnableConfigurationProperties
    static class TestConfig {}

    @Autowired
    private TimeoutProperties timeoutProperties;

    @Test
    public void shouldLoadRestTemplateTimeouts() {
        assertThat(timeoutProperties.getHmrcService().getReadMs()).isEqualTo(120000);
        assertThat(timeoutProperties.getHmrcService().getConnectMs()).isEqualTo(1000);
        assertThat(timeoutProperties.getAuditService().getReadMs()).isEqualTo(10000);
        assertThat(timeoutProperties.getAuditService().getConnectMs()).isEqualTo(1000);
    }
}


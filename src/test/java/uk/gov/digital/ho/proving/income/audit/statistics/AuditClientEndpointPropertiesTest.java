package uk.gov.digital.ho.proving.income.audit.statistics;

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
@ContextConfiguration(classes = {AuditClientEndpointProperties.class, AuditClientEndpointPropertiesTest.TestConfig.class})
@TestPropertySource(properties = {
    "pttg.audit.url=http://somehost:8000",
    "pttg.audit.audit-endpoint=${pttg.audit.url}/audit",
    "pttg.audit.history-endpoint=${pttg.audit.url}/history",
    "pttg.audit.archive-endpoint=${pttg.audit.url}/archive",
    "pttg.audit.archive-history-page-size=1000"
})
public class AuditClientEndpointPropertiesTest {

    @TestConfiguration
    @EnableConfigurationProperties
    public static class TestConfig {}

    @Autowired
    AuditClientEndpointProperties auditClientEndpointProperties;

    @Test
    public void shouldLoadEndpointProperties() {
        assertThat(auditClientEndpointProperties.getAuditEndpoint()).isEqualTo("http://somehost:8000/audit");
        assertThat(auditClientEndpointProperties.getHistoryEndpoint()).isEqualTo("http://somehost:8000/history");
        assertThat(auditClientEndpointProperties.getArchiveEndpoint()).isEqualTo("http://somehost:8000/archive");
        assertThat(auditClientEndpointProperties.getArchiveHistoryPageSize()).isEqualTo(1000);
    }
}

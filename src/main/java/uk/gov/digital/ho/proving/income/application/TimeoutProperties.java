package uk.gov.digital.ho.proving.income.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "timeouts")
@Getter
@Setter
public class TimeoutProperties {

    private HmrcService hmrcService;
    private AuditService auditService;

    public static class HmrcService extends TimeoutPropertiesTemplate {}
    public static class AuditService extends TimeoutPropertiesTemplate {}

    @NoArgsConstructor
    @Setter
    @Getter
    private static class TimeoutPropertiesTemplate {
        private int readMs;
        private int connectMs;
    }
}

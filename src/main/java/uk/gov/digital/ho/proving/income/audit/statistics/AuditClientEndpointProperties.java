package uk.gov.digital.ho.proving.income.audit.statistics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "pttg.audit")
@NoArgsConstructor
@Setter
@Getter
public class AuditClientEndpointProperties {

    private String auditEndpoint;
    private String historyEndpoint;
    private String archiveEndpoint;
    private String correlationIdsEndpoint;
    private String historyByCorrelationIdEndpoint;
    private int archiveHistoryPageSize;
}



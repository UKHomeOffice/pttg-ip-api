package uk.gov.digital.ho.proving.income.audit;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
class ArchiveAuditRequest {
    private List<AuditEventType> eventTypes;
    private String nino;
    private LocalDate lastArchiveDate;
    private List<String> eventIds;
    private String result;
}

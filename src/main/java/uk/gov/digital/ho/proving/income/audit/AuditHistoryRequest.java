package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
class AuditHistoryRequest {
    @JsonProperty
    private LocalDate toDate;
    @JsonProperty
    private List<AuditEventType> eventTypes;
}

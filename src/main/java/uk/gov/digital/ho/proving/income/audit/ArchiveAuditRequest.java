package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
class ArchiveAuditRequest {
    @JsonProperty
    private String result;
    @JsonProperty
    private LocalDate lastArchiveDate;
    @JsonProperty
    private List<String> correlationIds;
    @JsonProperty
    private String nino;
}

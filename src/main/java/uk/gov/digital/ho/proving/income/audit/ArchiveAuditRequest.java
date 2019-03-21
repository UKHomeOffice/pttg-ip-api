package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty
    private String nino;
    @JsonProperty
    private LocalDate lastArchiveDate;
    @JsonProperty
    private List<String> eventIds;
    @JsonProperty
    private String result;
    @JsonProperty
    private LocalDate resultDate;
}

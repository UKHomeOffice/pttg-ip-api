package uk.gov.digital.ho.proving.income.domain.hmrc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employer {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String payeReference;
}

package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employments {
    @JsonProperty
    private final Employer employer;
}

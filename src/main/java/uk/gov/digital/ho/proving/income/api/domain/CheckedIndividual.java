package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class CheckedIndividual {
    @JsonProperty(value = "nino")
    private String nino;
    @JsonProperty(value = "employers")
    private List<String> employers;
}

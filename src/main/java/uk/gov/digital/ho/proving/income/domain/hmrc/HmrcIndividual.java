package uk.gov.digital.ho.proving.income.domain.hmrc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HmrcIndividual {
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String nino;
    @JsonProperty
    private LocalDate dateOfBirth;
}

package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
public class Identity {
    @JsonProperty
    private String firstname;
    @JsonProperty
    private String lastname;
    @JsonProperty
    private LocalDate dateOfBirth;
    @JsonProperty
    private String nino;
}

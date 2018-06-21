package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class Individual {
    @JsonProperty
    private String title = "";
    @JsonProperty
    private String forename;
    @JsonProperty
    private String surname;
    @JsonProperty
    private String nino;

    public Individual(String forename, String surname, String nino) {
        this.forename = forename;
        this.surname = surname;
        this.nino = nino;
    }
}

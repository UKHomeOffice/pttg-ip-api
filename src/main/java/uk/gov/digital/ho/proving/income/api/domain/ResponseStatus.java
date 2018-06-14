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
public class ResponseStatus {
    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "message")
    private String message;
}

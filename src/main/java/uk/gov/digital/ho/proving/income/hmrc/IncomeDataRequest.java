package uk.gov.digital.ho.proving.income.hmrc;

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
class IncomeDataRequest {

    @JsonProperty(value = "firstName")
    private String firstName;

    @JsonProperty(value = "lastName")
    private String lastName;

    @JsonProperty(value = "nino")
    private String nino;

    @JsonProperty(value = "dateOfBirth")
    private LocalDate dateOfBirth;

    @JsonProperty(value = "fromDate")
    private LocalDate fromDate;

    @JsonProperty(value = "toDate")
    private LocalDate toDate;
}

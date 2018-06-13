package uk.gov.digital.ho.proving.income.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class Applicant {
    private String forename;
    private String surname;
    private LocalDate dateOfBirth;
    private String nino;
}

package uk.gov.digital.ho.proving.income.domain.hmrc;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class Identity {
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String nino;
}

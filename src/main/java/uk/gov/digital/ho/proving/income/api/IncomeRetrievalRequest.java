package uk.gov.digital.ho.proving.income.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@ToString
public class IncomeRetrievalRequest {
    private String nino;
    private String forename;
    private String surname;
    private LocalDate dateOfBirth;
    private LocalDate toDate;
    private LocalDate fromDate;
}

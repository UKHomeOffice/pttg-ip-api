package uk.gov.digital.ho.proving.income.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@ToString
public class IncomeRetrievalRequest {
    private final String nino;
    private final String forename;
    private final String surname;
    private final LocalDate dateOfBirth;
    private final LocalDate toDate;
    private final LocalDate fromDate;
}

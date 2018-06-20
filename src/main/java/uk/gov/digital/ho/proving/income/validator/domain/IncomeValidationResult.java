package uk.gov.digital.ho.proving.income.validator.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class IncomeValidationResult {
    private IncomeValidationStatus status;
    private BigDecimal threshold;
    private List<CheckedIndividual> individuals;
    private LocalDate assessmentStartDate;
}

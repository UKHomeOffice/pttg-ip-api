package uk.gov.digital.ho.proving.income.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.api.domain.FinancialCheckValues;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class FinancialCheckResult {
    private FinancialCheckValues financialCheckValue;
    private BigDecimal threshold;
    private List<CheckedIndividual> individuals;
}

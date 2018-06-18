package uk.gov.digital.ho.proving.income.calculation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.api.domain.FinancialCheckValues;
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments;
import uk.gov.digital.ho.proving.income.domain.hmrc.Income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class CalculationResult {
    private FinancialCheckValues financialCheckValue;
    private BigDecimal threshold;
    private List<CheckedIndividual> individuals;
    private Boolean result;
}

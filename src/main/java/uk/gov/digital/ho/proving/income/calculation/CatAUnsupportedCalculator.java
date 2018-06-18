package uk.gov.digital.ho.proving.income.calculation;

import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.api.domain.FinancialCheckValues;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CatAUnsupportedCalculator implements Calculator {

    private FinancialCheckValues reason;

    public CatAUnsupportedCalculator(FinancialCheckValues reason) {
        this.reason = reason;
    }

    @Override
    public CalculationResult calculate(CalculationRequest calculationRequest) {
        ApplicantIncome applicantIncome = calculationRequest.applicantIncomes().get(0);
        List<String> employments = applicantIncome.employments().stream().map(e -> e.employer().name()).collect(Collectors.toList());
        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);
        return new CalculationResult(reason, BigDecimal.ZERO, Arrays.asList(checkedIndividual), false);

    }
}

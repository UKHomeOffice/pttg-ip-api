package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CatAUnsupportedIncomeValidator implements IncomeValidator {

    private IncomeValidationStatus reason;

    public CatAUnsupportedIncomeValidator(IncomeValidationStatus reason) {
        this.reason = reason;
    }

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncomes().get(0);
        List<String> employments = applicantIncome.employments().stream().map(e -> e.employer().name()).collect(Collectors.toList());
        CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), employments);
        return new IncomeValidationResult(reason, BigDecimal.ZERO, Arrays.asList(checkedIndividual));

    }
}

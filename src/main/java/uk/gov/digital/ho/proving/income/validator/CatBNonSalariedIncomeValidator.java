package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatBNonSalariedIncomeValidator implements IncomeValidator {

    private static final String CALCULATION_TYPE = "Category B non salaried";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {

        List<CheckedIndividual> checkedIndividuals =
            incomeValidationRequest.applicantIncomes()
                .stream()
                .map(applicantIncome ->
                    new CheckedIndividual(
                        applicantIncome.applicant().nino(),
                        IncomeValidationHelper.toEmployerNames(applicantIncome.incomeRecord().employments())
                    ))
                .collect(Collectors.toList());

        BigDecimal yearlyThreshold = new SalariedThresholdCalculator(incomeValidationRequest.dependants()).yearlyThreshold();
        LocalDate assessmentStartDate = incomeValidationRequest.applicationRaisedDate().minusYears(1);

        return new IncomeValidationResult(
            IncomeValidationStatus.CATB_NON_SALARIED_PASSED,
            yearlyThreshold,
            checkedIndividuals,
            assessmentStartDate,
            CALCULATION_TYPE);
    }
}

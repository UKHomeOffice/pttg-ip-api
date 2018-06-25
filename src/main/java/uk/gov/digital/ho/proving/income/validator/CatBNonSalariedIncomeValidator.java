package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.SalariedThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatBNonSalariedIncomeValidator implements IncomeValidator {

    private static final String CALCULATION_TYPE = "Category B non salaried";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        List<CheckedIndividual> checkedIndividuals = new ArrayList<>();
        for(ApplicantIncome applicantIncome : incomeValidationRequest.applicantIncomes()) {
            CheckedIndividual checkedIndividual = new CheckedIndividual(applicantIncome.applicant().nino(), IncomeValidationHelper.toEmployerNames(applicantIncome.incomeRecord().employments()));
            checkedIndividuals.add(checkedIndividual);
        }
        return new IncomeValidationResult(IncomeValidationStatus.CATB_NON_SALARIED_PASSED, new SalariedThresholdCalculator(incomeValidationRequest.dependants()).yearlyThreshold(), checkedIndividuals, incomeValidationRequest.applicationRaisedDate().minusYears(1), CALCULATION_TYPE);
    }
}

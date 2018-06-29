package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.domain.CategoryCheck;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class IncomeValidationService {

    private IncomeValidator catASalariedIncomeValidator;
    private IncomeValidator catBNonSalariedIncomeValidator;
    private IncomeValidator employmentCheckIncomeValidator;

    public IncomeValidationService(
        IncomeValidator catASalariedIncomeValidator,
        IncomeValidator catBNonSalariedIncomeValidator,
        IncomeValidator employmentCheckIncomeValidator
    ) {
        this.catASalariedIncomeValidator = catASalariedIncomeValidator;
        this.catBNonSalariedIncomeValidator = catBNonSalariedIncomeValidator;
        this.employmentCheckIncomeValidator = employmentCheckIncomeValidator;
    }

    public List<CategoryCheck> validate(IncomeValidationRequest incomeValidationRequest) {

        List<CategoryCheck> categoryChecks = new ArrayList<>();

        categoryChecks.add(checkCategory(incomeValidationRequest, catASalariedIncomeValidator, "A"));

        CategoryCheck catBNonSalariedCategoryCheck = checkCategory(incomeValidationRequest, employmentCheckIncomeValidator, "B");
        if (catBNonSalariedCategoryCheck.passed()) {
            catBNonSalariedCategoryCheck = checkCategory(incomeValidationRequest, catBNonSalariedIncomeValidator, "B");
        }
        categoryChecks.add(catBNonSalariedCategoryCheck);

        return categoryChecks;
    }

    private CategoryCheck checkCategory(IncomeValidationRequest incomeValidationRequest, IncomeValidator incomeValidator, String category) {
        IncomeValidationResult result = incomeValidator.validate(incomeValidationRequest);
        return new CategoryCheck(
            category,
            result.calculationType(),
            result.status().isPassed(),
            incomeValidationRequest.applicationRaisedDate(),
            result.assessmentStartDate(),
            result.status(),
            result.threshold(),
            result.individuals());
    }
}

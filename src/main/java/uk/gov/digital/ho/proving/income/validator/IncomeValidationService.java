package uk.gov.digital.ho.proving.income.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.domain.CategoryCheck;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class IncomeValidationService {

    @Autowired
    private IncomeValidator catASalariedIncomeValidator;
    @Autowired
    private IncomeValidator catBNonSalariedIncomeValidator;

    public List<CategoryCheck> validate(IncomeValidationRequest incomeValidationRequest) {

        List<CategoryCheck> categoryChecks = new ArrayList<>();

        categoryChecks.add(checkCategory(incomeValidationRequest, catASalariedIncomeValidator, "A"));
        categoryChecks.add(checkCategory(incomeValidationRequest, catBNonSalariedIncomeValidator, "B"));

        return categoryChecks;

    }

    private CategoryCheck checkCategory(IncomeValidationRequest incomeValidationRequest, IncomeValidator incomeValidator, String category) {
        IncomeValidationResult catAResult = incomeValidator.validate(incomeValidationRequest);
        return new CategoryCheck(
            category,
            catAResult.status().isPassed(),
            incomeValidationRequest.applicationRaisedDate(),
            incomeValidationRequest.assessmentStartDate(),
            catAResult.status(),
            catAResult.threshold(),
            catAResult.individuals());
    }
}

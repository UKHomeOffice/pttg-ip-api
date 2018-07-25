package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.domain.CategoryCheck;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

@Service
public class IncomeValidationService {

    private final List<IncomeValidator> incomeValidators;

    public IncomeValidationService(List<ActiveIncomeValidator> incomeValidators) {
        this.incomeValidators = unmodifiableList(incomeValidators);
    }

    public List<CategoryCheck> validate(IncomeValidationRequest incomeValidationRequest) {
        return incomeValidators.stream()
            .map(incomeValidator -> checkCategory(incomeValidationRequest, incomeValidator))
            .collect(Collectors.toList());
    }

    private CategoryCheck checkCategory(IncomeValidationRequest incomeValidationRequest, IncomeValidator incomeValidator) {
        IncomeValidationResult result = incomeValidator.validate(incomeValidationRequest);
        return CategoryCheck.from(result, incomeValidationRequest.applicationRaisedDate());
    }
}

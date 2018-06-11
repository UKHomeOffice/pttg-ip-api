package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import uk.gov.digital.ho.proving.income.domain.Individual;

import java.util.List;

public class FinancialStatusCheckResponse extends BaseResponse {

    @JsonInclude(Include.NON_NULL)
    private final List<Individual> individuals;

    @JsonInclude(Include.NON_NULL)
    private final List<CategoryCheck> categoryChecks;

    public FinancialStatusCheckResponse(ResponseStatus status, List<Individual> individuals, List<CategoryCheck> categoryChecks) {
        super(status);
        this.individuals = individuals;
        this.categoryChecks = categoryChecks;
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public List<CategoryCheck> getCategoryChecks() {
        return categoryChecks;
    }

    @Override
    public String toString() {
        return "FinancialStatusCheckResponse{" +
            "individuals=" + individuals +
            ", categoryChecks=" + categoryChecks +
            '}';
    }
}

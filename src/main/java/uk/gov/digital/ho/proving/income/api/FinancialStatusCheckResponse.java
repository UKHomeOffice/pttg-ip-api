package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import uk.gov.digital.ho.proving.income.domain.Individual;

public class FinancialStatusCheckResponse extends BaseResponse {

    @JsonInclude(Include.NON_NULL)
    private final Individual individual;

    @JsonInclude(Include.NON_NULL)
    private final CategoryCheck categoryCheck;

    public FinancialStatusCheckResponse(ResponseStatus status, Individual individual, CategoryCheck categoryCheck) {
        super(status);
        this.individual = individual;
        this.categoryCheck = categoryCheck;
    }

    public Individual getIndividual() {
        return individual;
    }


    public CategoryCheck getCategoryCheck() {
        return categoryCheck;
    }


    @Override
    public String toString() {
        return "FinancialStatusCheckResponse{" +
            "individual=" + individual +
            ", categoryCheck=" + categoryCheck +
            '}';
    }
}

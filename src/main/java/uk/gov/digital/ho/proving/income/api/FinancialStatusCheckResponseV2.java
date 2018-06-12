package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import uk.gov.digital.ho.proving.income.domain.Individual;

@Deprecated
public class FinancialStatusCheckResponseV2 extends BaseResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Individual individual;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final CategoryCheckV2 categoryCheck;

    public FinancialStatusCheckResponseV2(ResponseStatus status, Individual individual, CategoryCheckV2 categoryCheck) {
        super(status);
        this.individual = individual;
        this.categoryCheck = categoryCheck;
    }

    public Individual getIndividual() {
        return individual;
    }


    public CategoryCheckV2 getCategoryCheck() {
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

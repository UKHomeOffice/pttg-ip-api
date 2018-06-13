package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.domain.Individual;

import java.util.List;

@Getter
@Accessors(fluent = true)
@ToString
public class FinancialStatusCheckResponse extends BaseResponse {
    @JsonProperty
    private final List<Individual> individuals;
    @JsonProperty
    private final List<CategoryCheck> categoryChecks;

    public FinancialStatusCheckResponse(ResponseStatus status, List<Individual> individuals, List<CategoryCheck> categoryChecks) {
        super(status);
        this.individuals = individuals;
        this.categoryChecks = categoryChecks;
    }
}

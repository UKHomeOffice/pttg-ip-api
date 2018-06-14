package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.domain.Individual;

import java.beans.ConstructorProperties;
import java.util.List;

@Getter
@Accessors(fluent = true)
@ToString
public class FinancialStatusCheckResponse extends BaseResponse {
    @JsonProperty(value = "individuals")
    private final List<Individual> individuals;
    @JsonProperty(value = "categoryChecks")
    private final List<CategoryCheck> categoryChecks;

    @ConstructorProperties(value={"status", "individuals", "categoryChecks"})
    public FinancialStatusCheckResponse(
        ResponseStatus status,
        List<Individual> individuals,
        List<CategoryCheck> categoryChecks
    ) {
        super(status);
        this.individuals = individuals;
        this.categoryChecks = categoryChecks;
    }
}

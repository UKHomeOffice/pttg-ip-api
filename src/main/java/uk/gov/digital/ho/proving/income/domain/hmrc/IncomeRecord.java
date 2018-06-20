package uk.gov.digital.ho.proving.income.domain.hmrc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeRecord {

    @JsonProperty
    private List<Income> paye;
    @JsonProperty
    private final List<AnnualSelfAssessmentTaxReturn> selfAssessment;
    @JsonProperty
    private List<Employments> employments;
    @JsonProperty
    private HmrcIndividual hmrcIndividual;

    public List<Income> deDuplicatedIncome() {

        return paye.stream()
                        .distinct()
                        .collect(Collectors.toList());
    }
}


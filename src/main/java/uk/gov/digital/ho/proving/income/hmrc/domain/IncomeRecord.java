package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class IncomeRecord {

    @JsonProperty(value = "paye", required = true)
    private List<Income> paye;

    @JsonProperty(value = "selfAssessment", required = true)
    private final List<AnnualSelfAssessmentTaxReturn> selfAssessment;

    @JsonProperty(value = "employments", required = true)
    private List<Employments> employments;

    @JsonProperty(value = "individual", required = true)
    private HmrcIndividual individual;

    public List<Income> deDuplicatedIncome() {

        return paye.stream()
                        .distinct()
                        .collect(toList());
    }

    public LocalDate dateOfBirth() {
        return individual != null? individual.dateOfBirth():null;
    }
}


package uk.gov.digital.ho.proving.income.domain.hmrc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeRecord {

    private List<Income> paye;
    private final List<AnnualSelfAssessmentTaxReturn> selfAssessment;
    private List<Employments> employments;
    private Individual individual;

    public List<Income> deDuplicatedIncome() {

        return paye.stream()
                        .distinct()
                        .collect(Collectors.toList());
    }
}


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

    private List<Income> income;
    private List<Employments> employments;

    public List<Income> deDuplicatedIncome() {

        return income.stream()
                        .distinct()
                        .collect(Collectors.toList());
    }
}


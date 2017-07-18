package uk.gov.digital.ho.proving.income.domain.hmrc;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class IncomeRecord {
    private List<Income> income;
    private List<Employments> employments;
}

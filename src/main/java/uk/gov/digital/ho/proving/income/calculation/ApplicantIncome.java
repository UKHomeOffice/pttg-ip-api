package uk.gov.digital.ho.proving.income.calculation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;

import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class ApplicantIncome {
    private Applicant applicant;
    private IncomeRecord incomeRecord;

    public List<Employments> employments() {
        return incomeRecord.employments();
    }
}

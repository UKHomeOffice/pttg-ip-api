package uk.gov.digital.ho.proving.income.validator.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.hmrc.domain.Employments;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

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

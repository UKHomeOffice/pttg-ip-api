package uk.gov.digital.ho.proving.income.validator.domain;

import jersey.repackaged.com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.Individual;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class IncomeValidationRequest {
    private List<ApplicantIncome> applicantIncomes;
    private LocalDate applicationRaisedDate;
    private Integer dependants;

    public static IncomeValidationRequest create(LocalDate applicationRaisedDate, Map<Individual, IncomeRecord> incomeRecords, Integer dependants) {
        List<ApplicantIncome> applicantIncomes = new ArrayList<>();
        for(Individual individual : incomeRecords.keySet()) {
            IncomeRecord incomeRecord = incomeRecords.get(individual);
            Applicant applicant = new Applicant(individual.forename(), individual.surname(), incomeRecord.dateOfBirth(), individual.nino());
            ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord);
            applicantIncomes.add(applicantIncome);
        }
        return new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, dependants);
    }

    public boolean isJointRequest() {
        return applicantIncomes.size() > 1;
    }

    public IncomeValidationRequest toApplicantOnly() {
        if(applicantIncomes.size() < 1) {
            throw new IllegalStateException("There are no applicants");
        }

        ApplicantIncome applicantIncome = applicantIncomes.get(0);
        return new IncomeValidationRequest(ImmutableList.of(applicantIncome), applicationRaisedDate, dependants);
    }

    public IncomeValidationRequest toPartnerOnly() {
        if(applicantIncomes.size() < 2) {
            throw new IllegalStateException("There is no partner");
        }

        ApplicantIncome partnerIncome = applicantIncomes.get(1);
        return new IncomeValidationRequest(ImmutableList.of(partnerIncome), applicationRaisedDate, dependants);
    }

}

package uk.gov.digital.ho.proving.income.validator.domain;

import jersey.repackaged.com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.api.domain.Individual;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class IncomeValidationRequest {
    private List<ApplicantIncome> applicantIncomes;
    private LocalDate applicationRaisedDate;
    private Integer dependants;

    public static IncomeValidationRequest create(LocalDate applicationRaisedDate, Map<Individual, IncomeRecord> incomeRecords, Integer dependants) {
        List<ApplicantIncome> applicantIncomes = new ArrayList<>();
        for (Individual individual : incomeRecords.keySet()) {
            IncomeRecord incomeRecord = incomeRecords.get(individual);
            Applicant applicant = new Applicant(individual.forename(), individual.surname(), incomeRecord.dateOfBirth(), individual.nino());
            ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord);
            applicantIncomes.add(applicantIncome);
        }
        return new IncomeValidationRequest(applicantIncomes, applicationRaisedDate, dependants);
    }

    public List<CheckedIndividual> getCheckedIndividuals() {
        return allIncome()
            .stream()
            .map(CheckedIndividual::from)
            .collect(Collectors.toList());
    }

    public LocalDate applicationRaisedDate() {
        return applicationRaisedDate;
    }

    public Integer dependants() {
        return dependants;
    }

    private boolean containsApplicant() {
        return applicantIncomes.size() > 0;
    }

    private boolean containsPartner() {
        return applicantIncomes.size() > 1;
    }

    public boolean isJointRequest() {
        return containsPartner();
    }

    public List<ApplicantIncome> allIncome() {
        return ImmutableList.copyOf(applicantIncomes);
    }

    public ApplicantIncome applicantIncome() {
        if (containsApplicant()) {
            return applicantIncomes.get(0);
        }
        throw new IllegalStateException("There are no applicants");
    }

    public ApplicantIncome partnerIncome() {
        if (containsPartner()) {
            return applicantIncomes.get(1);
        }
        throw new IllegalStateException("There is no partner");
    }

    public IncomeValidationRequest toApplicantOnly() {
        if (containsApplicant()) {
            return new IncomeValidationRequest(ImmutableList.of(applicantIncome()), applicationRaisedDate, dependants);
        }
        throw new IllegalStateException("There are no applicants");
    }

    public IncomeValidationRequest toPartnerOnly() {
        if (containsPartner()) {
            return new IncomeValidationRequest(ImmutableList.of(partnerIncome()), applicationRaisedDate, dependants);
        }
        throw new IllegalStateException("There is no partner");
    }
}

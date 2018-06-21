package uk.gov.digital.ho.proving.income.validator.domain;

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
}

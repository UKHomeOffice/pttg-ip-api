package uk.gov.digital.ho.proving.income.calculation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.domain.Individual;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class CalculationRequest {
    private List<ApplicantIncome> applicantIncomes;
    private LocalDate lower;
    private LocalDate upper;
    private Integer dependants;

    public  static CalculationRequest create(LocalDate applicationRaisedDate, LocalDate startSearchDate, Map<Individual, IncomeRecord> incomeRecords, Integer dependants) {
        List<ApplicantIncome> applicantIncomes = new ArrayList<>();
        for(Individual individual : incomeRecords.keySet()) {
            IncomeRecord incomeRecord = incomeRecords.get(individual);
            Applicant applicant = new Applicant(individual.forename(), individual.surname(), incomeRecord.dateOfBirth(), individual.nino());
            ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord.paye(), incomeRecord.employments());
            applicantIncomes.add(applicantIncome);
        }
        return new CalculationRequest(applicantIncomes, startSearchDate, applicationRaisedDate, dependants);
    }
}

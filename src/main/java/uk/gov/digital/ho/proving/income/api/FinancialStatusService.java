package uk.gov.digital.ho.proving.income.api;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusCheckResponse;
import uk.gov.digital.ho.proving.income.api.domain.Individual;
import uk.gov.digital.ho.proving.income.api.domain.ResponseStatus;
import uk.gov.digital.ho.proving.income.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.IncomeValidationService;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class FinancialStatusService {

    private final HmrcClient hmrcClient;
    private final IncomeValidationService incomeValidationService;

    public FinancialStatusService(HmrcClient hmrcClient, IncomeValidationService incomeValidationService) {
        this.hmrcClient = hmrcClient;
        this.incomeValidationService = incomeValidationService;
    }

    LinkedHashMap<Individual, IncomeRecord> getIncomeRecords(List<Applicant> applicants, LocalDate startSearchDate, LocalDate applicationRaisedDate) {
        LinkedHashMap<Individual, IncomeRecord> incomeRecords = new LinkedHashMap<>();

        for (Applicant applicant : applicants) {

            IncomeRecord incomeRecord = hmrcClient.getIncomeRecord(
                new Identity(applicant.forename(), applicant.surname(), applicant.dateOfBirth(), applicant.nino()),
                startSearchDate,
                applicationRaisedDate);

            incomeRecords.put(individualFromRequestAndRecord(applicant, incomeRecord.individual(), applicant.nino()), incomeRecord);
        }

        return incomeRecords;
    }

    FinancialStatusCheckResponse calculateResponse(LocalDate applicationRaisedDate, Integer dependants, LinkedHashMap<Individual, IncomeRecord> incomeRecords) {

        List<Individual> individuals = new LinkedList<>(incomeRecords.keySet());

        FinancialStatusCheckResponse response = new FinancialStatusCheckResponse(successResponse(), individuals, new ArrayList<>());

        IncomeValidationRequest incomeValidationRequest = IncomeValidationRequest.create(applicationRaisedDate, incomeRecords, dependants);

        response.categoryChecks().addAll(incomeValidationService.validate(incomeValidationRequest));

        return response;
    }

    private Individual individualFromRequestAndRecord(Applicant applicant, HmrcIndividual hmrcIndividual, String nino) {
        if (hmrcIndividual != null) {
            return new Individual(hmrcIndividual.firstName(), hmrcIndividual.lastName(), nino);
        }
        // for service backward compatibility echo back request if hmrc service returns no individual
        return new Individual(applicant.forename(), applicant.surname(), nino);
    }

    private ResponseStatus successResponse() {
        return new ResponseStatus("100", "OK");
    }

}

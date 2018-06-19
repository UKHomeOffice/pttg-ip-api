package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.proving.income.api.domain.*;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.api.domain.Individual;
import uk.gov.digital.ho.proving.income.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationType;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.LocalDate.now;
import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@RestController
@Slf4j
public class FinancialStatusService {

    private final HmrcClient hmrcClient;
    private final AuditClient auditClient;
    private final NinoUtils ninoUtils;

    private static final int MINIMUM_DEPENDANTS = 0;
    private static final int MAXIMUM_DEPENDANTS = 99;

    private static final int NUMBER_OF_DAYS = 182;

    public FinancialStatusService(HmrcClient hmrcClient, AuditClient auditClient, NinoUtils ninoUtils) {
        this.hmrcClient = hmrcClient;
        this.auditClient = auditClient;
        this.ninoUtils = ninoUtils;
    }

    @Deprecated
    @PostMapping(value = "/incomeproving/v2/individual/financialstatus", produces = APPLICATION_JSON_VALUE)
    public FinancialStatusCheckResponseV2 getFinancialStatusV2(@Valid @RequestBody FinancialStatusRequestV2 requestV2) {
        Applicant applicant = new Applicant(requestV2.getForename(), requestV2.getSurname(), requestV2.getDateOfBirth(), requestV2.getNino());
        FinancialStatusRequest request = new FinancialStatusRequest(Arrays.asList(applicant), requestV2.getDateOfBirth(), requestV2.getDependants());

        FinancialStatusCheckResponse response = getFinancialStatus(request);

        CategoryCheck categoryCheck = response.categoryChecks().get(0);
        CategoryCheckV2 categoryCheckV2 = new CategoryCheckV2(categoryCheck.category(), categoryCheck.passed(), categoryCheck.failureReason(), categoryCheck.applicationRaisedDate(), categoryCheck.assessmentStartDate(), categoryCheck.threshold(), categoryCheck.individuals().get(0).employers());
        FinancialStatusCheckResponseV2 responseV2 = new FinancialStatusCheckResponseV2(response.status(), response.individuals().get(0), categoryCheckV2);
        return responseV2;
    }

    @PostMapping(value = "/incomeproving/v3/individual/financialstatus", produces = APPLICATION_JSON_VALUE)
    public FinancialStatusCheckResponse getFinancialStatus(@Valid @RequestBody FinancialStatusRequest request) {

        Applicant mainApplicant = request.applicants().get(0);

        final String redactedNino = ninoUtils.redact(mainApplicant.nino());
        log.info("Financial status check request received for {} - applicationRaisedDate = {}, dependents = {}", redactedNino, request.applicationRaisedDate(), request.dependants());

        UUID eventId = UUID.randomUUID();

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST,
                        eventId,
                        auditData(mainApplicant.nino(), mainApplicant.forename(), mainApplicant.surname(), mainApplicant.dateOfBirth(), request.applicationRaisedDate(), request.dependants()));

        final String sanitisedNino = ninoUtils.sanitise(mainApplicant.nino());
        ninoUtils.validate(sanitisedNino);

        validateDependents(request.dependants());
        validateApplicationRaisedDate(request.applicationRaisedDate());

        LocalDate startSearchDate = request.applicationRaisedDate().minusDays(NUMBER_OF_DAYS);

        IncomeRecord incomeRecord = hmrcClient.getIncomeRecord(
            new Identity(mainApplicant.forename(), mainApplicant.surname(), mainApplicant.dateOfBirth(), sanitisedNino),
            startSearchDate,
            request.applicationRaisedDate());

        Individual individual = individualFromRequestAndRecord(request, incomeRecord.hmrcIndividual(), sanitisedNino);

        FinancialStatusCheckResponse response = calculateResponse(request.applicationRaisedDate(), request.dependants(), startSearchDate, incomeRecord, individual);

        log.info("Financial status check result for {}", value("nino", redactedNino));

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_RESPONSE, eventId, auditData(response));

        return response;
    }

    private Individual individualFromRequestAndRecord(FinancialStatusRequest request, HmrcIndividual hmrcIndividual, String nino) {
        if (hmrcIndividual != null) {
            return new Individual(hmrcIndividual.firstName(), hmrcIndividual.lastName(), nino);
        }
        // for service backward compatibility echo back request if hmrc service returns no individual
        Applicant mainApplicant = request.applicants().get(0);
        return new Individual(mainApplicant.forename(), mainApplicant.surname(), nino);
    }

    private FinancialStatusCheckResponse calculateResponse(LocalDate applicationRaisedDate, Integer dependants, LocalDate startSearchDate, IncomeRecord incomeRecord, Individual individual) {

        FinancialStatusCheckResponse response = new FinancialStatusCheckResponse(successResponse(), Arrays.asList(individual), new ArrayList<>());
        Map<Individual, IncomeRecord> incomeRecords = new HashMap<>();
        incomeRecords.put(individual, incomeRecord);
        IncomeValidationRequest incomeValidationRequest = IncomeValidationRequest.create(applicationRaisedDate, startSearchDate, incomeRecords, dependants);

        IncomeValidationResult catAResult = IncomeValidationType.CATEGORY_A_SALARIED.calculator().validate(incomeValidationRequest);
        CategoryCheck categoryACheck = new CategoryCheck("A", catAResult.status().isPassed(), applicationRaisedDate, startSearchDate, catAResult.status(), catAResult.threshold(), catAResult.individuals());

        IncomeValidationResult catBResult = IncomeValidationType.CATEGORY_B_NON_SALARIED.calculator().validate(incomeValidationRequest);
        CategoryCheck categoryBCheck = new CategoryCheck("B", catBResult.status().isPassed(), applicationRaisedDate, startSearchDate, catBResult.status(), catBResult.threshold(), catBResult.individuals());

        response.categoryChecks().add(categoryACheck);
        response.categoryChecks().add(categoryBCheck);
        return response;
    }

    private ResponseStatus successResponse() {
        return new ResponseStatus("100", "OK");
    }

    private void validateApplicationRaisedDate(LocalDate applicationRaisedDate) {
        if (applicationRaisedDate == null) {
            throw new IllegalArgumentException("Error: applicationRaisedDate");
        }
        if (applicationRaisedDate.isAfter(now())) {
            throw new IllegalArgumentException("Error: applicationRaisedDate");
        }
    }

    private void validateDependents(Integer dependants) {
        if (dependants < MINIMUM_DEPENDANTS) {
            throw new IllegalArgumentException("Error: Dependants cannot be less than " + MINIMUM_DEPENDANTS);
        } else if (dependants > MAXIMUM_DEPENDANTS) {
            throw new IllegalArgumentException("Error: Dependants cannot be more than " + MAXIMUM_DEPENDANTS);
        }
    }

    private Map<String, Object> auditData(String nino, String forename, String surname, LocalDate dateOfBirth, LocalDate applicationRaisedDate, Integer dependants) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-financial-status");
        auditData.put("nino", nino);
        auditData.put("forename", forename);
        auditData.put("surname", surname);
        auditData.put("dateOfBirth", dateOfBirth == null ? null : dateOfBirth.format(DateTimeFormatter.ISO_DATE));
        auditData.put("applicationRaisedDate", applicationRaisedDate == null ? null : applicationRaisedDate.format(DateTimeFormatter.ISO_DATE));
        auditData.put("dependants", dependants);

        return auditData;
    }

    private Map<String, Object> auditData(FinancialStatusCheckResponse response) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-financial-status");
        auditData.put("response", response);

        return auditData;
    }
}

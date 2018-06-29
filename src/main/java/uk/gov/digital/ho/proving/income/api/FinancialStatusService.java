package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.proving.income.api.domain.*;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.validator.IncomeValidationService;
import uk.gov.digital.ho.proving.income.api.domain.Individual;
import uk.gov.digital.ho.proving.income.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private final IncomeValidationService incomeValidationService;

    private static final int MINIMUM_DEPENDANTS = 0;
    private static final int MAXIMUM_DEPENDANTS = 99;

    private static final int NUMBER_OF_DAYS_INCOME = 365;

    public FinancialStatusService(HmrcClient hmrcClient, AuditClient auditClient, NinoUtils ninoUtils, IncomeValidationService incomeValidationService) {
        this.hmrcClient = hmrcClient;
        this.auditClient = auditClient;
        this.ninoUtils = ninoUtils;
        this.incomeValidationService = incomeValidationService;
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

        validateApplicants(request.applicants());
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

        LocalDate startSearchDate = request.applicationRaisedDate().minusDays(NUMBER_OF_DAYS_INCOME);
        Map<Individual, IncomeRecord> incomeRecords = getIncomeRecords(request, mainApplicant, sanitisedNino, startSearchDate);

        FinancialStatusCheckResponse response = calculateResponse(request.applicationRaisedDate(), request.dependants(), startSearchDate, incomeRecords);

        log.info("Financial status check result for {}", value("nino", redactedNino));

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_RESPONSE, eventId, auditData(response));

        return response;
    }

    private void validateApplicants(List<Applicant> applicants) {
        if (applicants == null) {
            throw new IllegalArgumentException("Error: applicant not passed");
        }
        if (applicants.size() == 0) {
            throw new IllegalArgumentException("Error: zero applicants");
        }
        if (applicants.size() > 2) {
            throw new IllegalArgumentException("Error: more than 2 applicants");
        }
    }

    private Map<Individual, IncomeRecord> getIncomeRecords(FinancialStatusRequest request, Applicant mainApplicant, String sanitisedNino, LocalDate startSearchDate) {
        Map<Individual, IncomeRecord> incomeRecords = new HashMap<>();

        IncomeRecord applicantIncomeRecord = hmrcClient.getIncomeRecord(
            new Identity(mainApplicant.forename(), mainApplicant.surname(), mainApplicant.dateOfBirth(), sanitisedNino),
            startSearchDate,
            request.applicationRaisedDate());

        incomeRecords.put(individualFromRequestAndRecord(request, applicantIncomeRecord.hmrcIndividual(), sanitisedNino), applicantIncomeRecord);

        if (request.applicants().size() > 1) {
            Applicant partner = request.applicants().get(1);
            final String partnerSanitisedNino = ninoUtils.sanitise(partner.nino());
            ninoUtils.validate(partnerSanitisedNino);

            IncomeRecord partnerIncomeRecord = hmrcClient.getIncomeRecord(
                new Identity(partner.forename(), partner.surname(), partner.dateOfBirth(), partnerSanitisedNino),
                startSearchDate,
                request.applicationRaisedDate());

            incomeRecords.put(individualFromRequestAndRecord(request, partnerIncomeRecord.hmrcIndividual(), partnerSanitisedNino), partnerIncomeRecord);
        }

        return incomeRecords;
    }

    private Individual individualFromRequestAndRecord(FinancialStatusRequest request, HmrcIndividual hmrcIndividual, String nino) {
        if (hmrcIndividual != null) {
            return new Individual(hmrcIndividual.firstName(), hmrcIndividual.lastName(), nino);
        }
        // for service backward compatibility echo back request if hmrc service returns no individual
        Applicant mainApplicant = request.applicants().get(0);
        return new Individual(mainApplicant.forename(), mainApplicant.surname(), nino);
    }

    private FinancialStatusCheckResponse calculateResponse(LocalDate applicationRaisedDate, Integer dependants, LocalDate startSearchDate, Map<Individual, IncomeRecord> incomeRecords) {

        List<Individual> individuals = incomeRecords.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
        FinancialStatusCheckResponse response = new FinancialStatusCheckResponse(successResponse(), individuals, new ArrayList<>());

        IncomeValidationRequest incomeValidationRequest = IncomeValidationRequest.create(applicationRaisedDate, incomeRecords, dependants);

        response.categoryChecks().addAll(incomeValidationService.validate(incomeValidationRequest));
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

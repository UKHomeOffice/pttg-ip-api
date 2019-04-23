package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.proving.income.api.domain.*;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

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
public class FinancialStatusResource {

    private FinancialStatusService financialStatusService;
    private final AuditClient auditClient;
    private final NinoUtils ninoUtils;

    private static final int MINIMUM_DEPENDANTS = 0;
    private static final int MAXIMUM_DEPENDANTS = 99;

    private static final int NUMBER_OF_DAYS_INCOME = 365;

    public FinancialStatusResource(FinancialStatusService financialStatusService, AuditClient auditClient, NinoUtils ninoUtils) {
        this.financialStatusService = financialStatusService;
        this.auditClient = auditClient;
        this.ninoUtils = ninoUtils;
    }

    @PostMapping(value = "/incomeproving/v3/individual/financialstatus", produces = APPLICATION_JSON_VALUE)
    public FinancialStatusCheckResponse getFinancialStatus(@Valid @RequestBody FinancialStatusRequest request) {

        List<Applicant> applicants = sanitiseApplicants(request.applicants());

        UUID eventId = UUID.randomUUID();
        String redactedNino = ninoUtils.redact(applicants.get(0).nino());
        log.info("Financial status check request received for {} - applicationRaisedDate = {}, dependents = {}",
            redactedNino, request.applicationRaisedDate(), request.dependants());
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, eventId, auditData(applicants.get(0), request.applicationRaisedDate(), request.dependants()));

        validateApplicants(applicants);
        validateDependents(request.dependants());
        validateApplicationRaisedDate(request.applicationRaisedDate());

        LocalDate startSearchDate = request.applicationRaisedDate().minusDays(NUMBER_OF_DAYS_INCOME);
        LinkedHashMap<Individual, IncomeRecord> incomeRecords = financialStatusService.getIncomeRecords(applicants, startSearchDate, request.applicationRaisedDate());


        FinancialStatusCheckResponse response = financialStatusService.calculateResponse(request.applicationRaisedDate(), request.dependants(), incomeRecords);

        log.info("Financial status check passed for {} is: {}",
            value("nino", redactedNino), response.categoryChecks().stream().anyMatch(CategoryCheck::passed));
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

        for(Applicant applicant : applicants) {
            ninoUtils.validate(applicant.nino());
        }
    }

    private List<Applicant> sanitiseApplicants(List<Applicant> applicants) {
        List<Applicant> sanitisedApplicants = new ArrayList<>();

        for(Applicant applicant : applicants) {
            String sanitisedNino = ninoUtils.sanitise(applicant.nino());
            Applicant validApplicant = new Applicant(applicant.forename(), applicant.surname(), applicant.dateOfBirth(), sanitisedNino);
            sanitisedApplicants.add(validApplicant);
        }

        return sanitisedApplicants;
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

    private Map<String, Object> auditData(Applicant applicant, LocalDate applicationRaisedDate, Integer dependants) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-financial-status");
        auditData.put("nino", applicant.nino());
        auditData.put("forename", applicant.forename());
        auditData.put("surname", applicant.surname());
        auditData.put("dateOfBirth", applicant.dateOfBirth() == null ? null : applicant.dateOfBirth().format(DateTimeFormatter.ISO_DATE));
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

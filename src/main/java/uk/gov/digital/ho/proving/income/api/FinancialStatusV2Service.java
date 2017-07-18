package uk.gov.digital.ho.proving.income.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.income.acl.UnknownPaymentFrequencyType;
import uk.gov.digital.ho.proving.income.audit.AuditActions;
import uk.gov.digital.ho.proving.income.domain.Individual;
import uk.gov.digital.ho.proving.income.domain.hmrc.Identity;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecordService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.time.LocalDate.now;
import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.audit.AuditActions.auditEvent;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH_RESULT;

@RestController
@ControllerAdvice
public class FinancialStatusV2Service {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final IncomeRecordService incomeRecordService;

    private final ApplicationEventPublisher auditor;

    private static final int MINIMUM_DEPENDANTS = 0;
    private static final int MAXIMUM_DEPENDANTS = 99;

    private static final int NUMBER_OF_DAYS = 182;

    public FinancialStatusV2Service(IncomeRecordService incomeRecordService, ApplicationEventPublisher auditor) {
        this.incomeRecordService = incomeRecordService;
        this.auditor = auditor;
    }

    @RequestMapping(value = "/incomeproving/v2/individual/{nino}/financialstatus", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public FinancialStatusCheckResponse getTemporaryMigrationFamilyApplication(
        @PathVariable(value = "nino") String nino,
        @RequestParam(value = "forename") String forename,
        @RequestParam(value = "surname") String surname,
        @RequestParam(value = "dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
        @RequestParam(value = "applicationRaisedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate applicationRaisedDate,
        @RequestParam(value = "dependants", required = false, defaultValue = "0") Integer dependants) {

        LOGGER.info("Get financial status invoked for {} application received on {}.", value("nino", nino), applicationRaisedDate);
        LOGGER.debug("Get financial status invoked for {}, {} {} {} application received on {}.", value("nino", nino), forename, surname, dateOfBirth, applicationRaisedDate);

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, auditData(nino, forename, surname, dateOfBirth, applicationRaisedDate, dependants)));

        validateNino(sanitiseNino(nino));
        validateDependents(dependants);
        validateApplicationRaisedDate(applicationRaisedDate);

        LocalDate startSearchDate = applicationRaisedDate.minusDays(NUMBER_OF_DAYS);

        IncomeRecord incomeRecord = incomeRecordService.getIncomeRecord(
            new Identity(forename, surname, dateOfBirth, sanitiseNino(nino)),
            startSearchDate,
            applicationRaisedDate);

        FinancialStatusCheckResponse response = calculateResponse(applicationRaisedDate, dependants, startSearchDate, incomeRecord, new Individual("", forename, surname, sanitiseNino(nino)));

        LOGGER.debug("Financial status check result: {}", value("financialStatusCheckResponse", response));
        auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, auditData(response)));

        return response;
    }

    private FinancialStatusCheckResponse calculateResponse(LocalDate applicationRaisedDate, Integer dependants, LocalDate startSearchDate, IncomeRecord incomeRecord, Individual individual) {
        FinancialStatusCheckResponse response = new FinancialStatusCheckResponse();
        response.setIndividual(individual);

        switch (calculateFrequency(incomeRecord)) {
            case "M1":
                FinancialCheckResult categoryAMonthlySalaried = IncomeValidator.validateCategoryAMonthlySalaried2(incomeRecord.getIncome(), startSearchDate, applicationRaisedDate, dependants, incomeRecord.getEmployments());
                if (categoryAMonthlySalaried.getFinancialCheckValue().equals(FinancialCheckValues.MONTHLY_SALARIED_PASSED)) {
                    response.setCategoryCheck(new CategoryCheck("A", true, null, applicationRaisedDate, startSearchDate,  categoryAMonthlySalaried.getThreshold(), categoryAMonthlySalaried.getEmployers()));
                } else {
                    response.setCategoryCheck(new CategoryCheck("A", false, categoryAMonthlySalaried.getFinancialCheckValue(), applicationRaisedDate, startSearchDate, categoryAMonthlySalaried.getThreshold(), categoryAMonthlySalaried.getEmployers()));
                }
                break;
            case "W1":
                FinancialCheckResult categoryAWeeklySalaried = IncomeValidator.validateCategoryAWeeklySalaried2(incomeRecord.getIncome(), startSearchDate, applicationRaisedDate, dependants, incomeRecord.getEmployments());
                if (categoryAWeeklySalaried.getFinancialCheckValue().equals(FinancialCheckValues.WEEKLY_SALARIED_PASSED)) {
                    response.setCategoryCheck(new CategoryCheck("A", true, null, applicationRaisedDate, startSearchDate, categoryAWeeklySalaried.getThreshold(), categoryAWeeklySalaried.getEmployers()));
                } else {
                    response.setCategoryCheck(new CategoryCheck("A", false, categoryAWeeklySalaried.getFinancialCheckValue(), applicationRaisedDate, startSearchDate, categoryAWeeklySalaried.getThreshold(), categoryAWeeklySalaried.getEmployers()));
                }
                break;
            default:
                throw new UnknownPaymentFrequencyType();
        }
        response.setStatus(new ResponseStatus("100", "OK"));
        return response;
    }

    private String calculateFrequency(IncomeRecord incomeRecord) {
        return "M1";
    }

    private void validateApplicationRaisedDate(LocalDate applicationRaisedDate) {
        if (applicationRaisedDate.isAfter(now())) {
            throw new IllegalArgumentException("applicationRaisedDate");
        }
    }

    private void validateDependents(Integer dependants) {
        if (dependants < MINIMUM_DEPENDANTS) {
            throw new IllegalArgumentException("Dependants cannot be less than " + MINIMUM_DEPENDANTS);
        } else if (dependants > MAXIMUM_DEPENDANTS) {
            throw new IllegalArgumentException("Dependants cannot be more than " + MAXIMUM_DEPENDANTS);
        }
    }

    private String sanitiseNino(String nino) {
        return nino.replaceAll("\\s", "").toUpperCase();
    }

    private void validateNino(String nino) {
        final Pattern pattern = Pattern.compile("^[a-zA-Z]{2}[0-9]{6}[a-dA-D]{1}$");
        if (!pattern.matcher(nino).matches()) {
            throw new IllegalArgumentException("Invalid NINO");
        }
    }

    private Map<String, Object> auditData(String nino, String forename, String surname, LocalDate dateOfBirth, LocalDate applicationRaisedDate, Integer dependants) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-financial-status");
        auditData.put("nino", nino);
        auditData.put("forename", forename);
        auditData.put("surname", surname);
        auditData.put("dateOfBirth", dateOfBirth.format(DateTimeFormatter.ISO_DATE));
        auditData.put("applicationRaisedDate", applicationRaisedDate.format(DateTimeFormatter.ISO_DATE));
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

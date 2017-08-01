package uk.gov.digital.ho.proving.income.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.income.audit.AuditActions;
import uk.gov.digital.ho.proving.income.domain.Individual;
import uk.gov.digital.ho.proving.income.domain.hmrc.Identity;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecordService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;
import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.api.FrequencyCalculator.Frequency;
import static uk.gov.digital.ho.proving.income.api.FrequencyCalculator.calculate;
import static uk.gov.digital.ho.proving.income.api.NinoUtils.sanitiseNino;
import static uk.gov.digital.ho.proving.income.api.NinoUtils.validateNino;
import static uk.gov.digital.ho.proving.income.audit.AuditActions.auditEvent;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH_RESULT;

@RestController
@ControllerAdvice
public class FinancialStatusService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final IncomeRecordService incomeRecordService;

    private final ApplicationEventPublisher auditor;

    private static final int MINIMUM_DEPENDANTS = 0;
    private static final int MAXIMUM_DEPENDANTS = 99;

    private static final int NUMBER_OF_DAYS = 182;

    public FinancialStatusService(IncomeRecordService incomeRecordService, ApplicationEventPublisher auditor) {
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
        switch (calculateFrequency(incomeRecord)) {
            case CALENDAR_MONTHLY:
                return monthlyCheck(applicationRaisedDate, dependants, startSearchDate, incomeRecord, individual);
            case WEEKLY:
                return weeklyCheck(applicationRaisedDate, dependants, startSearchDate, incomeRecord, individual);
            case CHANGED:
                return unableToCalculate(applicationRaisedDate, startSearchDate, incomeRecord, individual, FinancialCheckValues.PAY_FREQUENCY_CHANGE);
            default:
                return unableToCalculate(applicationRaisedDate, startSearchDate, incomeRecord, individual, FinancialCheckValues.UNKNOWN_PAY_FREQUENCY);
        }
    }

    private FinancialStatusCheckResponse unableToCalculate(LocalDate applicationRaisedDate, LocalDate startSearchDate, IncomeRecord incomeRecord, Individual individual, FinancialCheckValues reason) {
        return new FinancialStatusCheckResponse(
            successResponse(),
            individual,
            new CategoryCheck("A", false, reason, applicationRaisedDate, startSearchDate, BigDecimal.ZERO, incomeRecord.getEmployments().stream().map(employments -> employments.getEmployer().getName()).collect(Collectors.toList())));
    }

    private FinancialStatusCheckResponse weeklyCheck(LocalDate applicationRaisedDate, Integer dependants, LocalDate startSearchDate, IncomeRecord incomeRecord, Individual individual) {
        FinancialCheckResult categoryAWeeklySalaried = IncomeValidator.validateCategoryAWeeklySalaried(incomeRecord.getIncome(), startSearchDate, applicationRaisedDate, dependants, incomeRecord.getEmployments());
        if (categoryAWeeklySalaried.getFinancialCheckValue().equals(FinancialCheckValues.WEEKLY_SALARIED_PASSED)) {
            return new FinancialStatusCheckResponse(
                successResponse(),
                individual,
                new CategoryCheck("A", true, null, applicationRaisedDate, startSearchDate, categoryAWeeklySalaried.getThreshold(), categoryAWeeklySalaried.getEmployers()));
        } else {
            return new FinancialStatusCheckResponse(
                successResponse(),
                individual,
                new CategoryCheck("A", false, categoryAWeeklySalaried.getFinancialCheckValue(), applicationRaisedDate, startSearchDate, categoryAWeeklySalaried.getThreshold(), categoryAWeeklySalaried.getEmployers()));
        }
    }

    private FinancialStatusCheckResponse monthlyCheck(LocalDate applicationRaisedDate, Integer dependants, LocalDate startSearchDate, IncomeRecord incomeRecord, Individual individual) {
        FinancialCheckResult categoryAMonthlySalaried = IncomeValidator.validateCategoryAMonthlySalaried(incomeRecord.getIncome(), startSearchDate, applicationRaisedDate, dependants, incomeRecord.getEmployments());
        if (categoryAMonthlySalaried.getFinancialCheckValue().equals(FinancialCheckValues.MONTHLY_SALARIED_PASSED)) {
            return new FinancialStatusCheckResponse(
                successResponse(),
                individual,
                new CategoryCheck("A", true, null, applicationRaisedDate, startSearchDate,  categoryAMonthlySalaried.getThreshold(), categoryAMonthlySalaried.getEmployers()));
        } else {
            return new FinancialStatusCheckResponse(
                successResponse(),
                individual,
                new CategoryCheck("A", false, categoryAMonthlySalaried.getFinancialCheckValue(), applicationRaisedDate, startSearchDate, categoryAMonthlySalaried.getThreshold(), categoryAMonthlySalaried.getEmployers()));
        }
    }

    private ResponseStatus successResponse() {
        return new ResponseStatus("100", "OK");
    }

    private Frequency calculateFrequency(IncomeRecord incomeRecord) {
        return calculate(incomeRecord);
    }

    private void validateApplicationRaisedDate(LocalDate applicationRaisedDate) {
        if (applicationRaisedDate == null) {
            throw new IllegalArgumentException("Parameter error: applicationRaisedDate");
        }
        if (applicationRaisedDate.isAfter(now())) {
            throw new IllegalArgumentException("Parameter error: applicationRaisedDate");
        }
    }

    private void validateDependents(Integer dependants) {
        if (dependants < MINIMUM_DEPENDANTS) {
            throw new IllegalArgumentException("Parameter error: Dependants cannot be less than " + MINIMUM_DEPENDANTS);
        } else if (dependants > MAXIMUM_DEPENDANTS) {
            throw new IllegalArgumentException("Parameter error: Dependants cannot be more than " + MAXIMUM_DEPENDANTS);
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

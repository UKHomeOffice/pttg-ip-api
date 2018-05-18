package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.domain.Individual;
import uk.gov.digital.ho.proving.income.domain.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.domain.hmrc.Identity;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;

import javax.validation.Valid;
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

    public FinancialStatusService(HmrcClient hmrcClient, AuditClient auditClient, final NinoUtils ninoUtils) {
        this.hmrcClient = hmrcClient;
        this.auditClient = auditClient;
        this.ninoUtils = ninoUtils;
    }

    @PostMapping(value = "/incomeproving/v2/individual/financialstatus", produces = APPLICATION_JSON_VALUE)
    public FinancialStatusCheckResponse getFinancialStatus(@Valid @RequestBody FinancialStatusRequest request) {

        log.info("Financial status check request received for {} - applicationRaisedDate = {}, dependents = {}", request.getNino(), request.getApplicationRaisedDate(), request.getDependants());

        UUID eventId = UUID.randomUUID();

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST,
                        eventId,
                        auditData(request.getNino(), request.getForename(), request.getSurname(), request.getDateOfBirth(), request.getApplicationRaisedDate(), request.getDependants()));

        final String sanitisedNino = ninoUtils.sanitise(request.getNino());
        ninoUtils.validate(sanitisedNino);
        validateDependents(request.getDependants());
        validateApplicationRaisedDate(request.getApplicationRaisedDate());

        LocalDate startSearchDate = request.getApplicationRaisedDate().minusDays(NUMBER_OF_DAYS);

        IncomeRecord incomeRecord = hmrcClient.getIncomeRecord(
            new Identity(request.getForename(), request.getSurname(), request.getDateOfBirth(), sanitisedNino),
            startSearchDate,
            request.getApplicationRaisedDate());

        Individual individual = individualFromRequestAndRecord(request, incomeRecord.getIndividual(), sanitisedNino);

        FinancialStatusCheckResponse response = calculateResponse(request.getApplicationRaisedDate(), request.getDependants(), startSearchDate, incomeRecord, individual);

        log.info("Financial status check result for {}", value("nino", request.getNino()));

        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_RESPONSE, eventId, auditData(response));

        return response;
    }

    private Individual individualFromRequestAndRecord(FinancialStatusRequest request, uk.gov.digital.ho.proving.income.domain.hmrc.Individual individual, String nino) {
        if (individual != null) {
            return new Individual(individual.getFirstName(), individual.getLastName(), nino);
        }
        // for service backward compatibility echo back request if hmrc service returns no individual
        return new Individual(request.getForename(), request.getSurname(), nino);
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
        FinancialCheckResult categoryAWeeklySalaried = IncomeValidator.validateCategoryAWeeklySalaried(incomeRecord.getPaye(), startSearchDate, applicationRaisedDate, dependants, incomeRecord.getEmployments());
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

    FinancialStatusCheckResponse monthlyCheck(LocalDate applicationRaisedDate, Integer dependants, LocalDate startSearchDate, IncomeRecord incomeRecord, Individual individual) {

        FinancialCheckResult categoryAMonthlySalaried = IncomeValidator.validateCategoryAMonthlySalaried(incomeRecord.deDuplicatedIncome(),
            startSearchDate,
            applicationRaisedDate,
            dependants,
            incomeRecord.getEmployments());

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

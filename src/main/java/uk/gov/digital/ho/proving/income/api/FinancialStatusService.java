package uk.gov.digital.ho.proving.income.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.income.acl.*;
import uk.gov.digital.ho.proving.income.audit.AuditActions;
import uk.gov.digital.ho.proving.income.domain.Application;
import uk.gov.digital.ho.proving.income.domain.IncomeProvingResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.time.LocalDate.now;
import static org.springframework.util.StringUtils.isEmpty;
import static uk.gov.digital.ho.proving.income.audit.AuditActions.auditEvent;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH_RESULT;
import static uk.gov.digital.ho.proving.income.util.DateUtils.parseIsoDate;

@RestController
@ControllerAdvice
public class FinancialStatusService extends AbstractIncomeProvingController {

    @Autowired
    private EarningsService earningsService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ApplicationEventPublisher auditor;

    private static final int MINIMUM_DEPENDANTS = 0;
    private static final int MAXIMUM_DEPENDANTS = 99;

    private static final int NUMBER_OF_DAYS = 182;

    @RequestMapping(value = "/incomeproving/v1/individual/{nino}/financialstatus", method = RequestMethod.GET)
    public ResponseEntity<FinancialStatusCheckResponse> getTemporaryMigrationFamilyApplication(
        @PathVariable(value = "nino") String nino,
        @RequestParam(value = "applicationRaisedDate") String applicationDateAsString,
        @RequestParam(value = "dependants", required = false) Integer dependants) {

        LOGGER.debug("Get financial status invoked for {} application received on {}.", nino, applicationDateAsString);

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, auditData(nino, applicationDateAsString, dependants)));

        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON);

        try {
            nino = sanitiseNino(nino);
            validateNino(nino);

            if (dependants == null) {
                dependants = 0;
            } else if (dependants < MINIMUM_DEPENDANTS) {
                throw new IllegalArgumentException("Dependants cannot be less than " + MINIMUM_DEPENDANTS);
            } else if (dependants > MAXIMUM_DEPENDANTS) {
                throw new IllegalArgumentException("Dependants cannot be more than " + MAXIMUM_DEPENDANTS);
            }

            if (isEmpty(applicationDateAsString)) {
                throw new IllegalArgumentException("applicationRaisedDate");
            }

            // We really shouldn't be mixing Optional with exceptions .. it's just wrong
            LocalDate inputApplicationRaisedDate = parseIsoDate(applicationDateAsString).orElseThrow( () -> new IllegalArgumentException("Application raised date is invalid"));

            if (inputApplicationRaisedDate.isAfter(now())) {
                throw new IllegalArgumentException("applicationRaisedDate");
            }

            LocalDate startSearchDate = inputApplicationRaisedDate.minusDays(NUMBER_OF_DAYS);
            LocalDate applicationRaisedDate = inputApplicationRaisedDate;

            IncomeProvingResponse incomeProvingResponse = individualService.lookup(nino, startSearchDate, applicationRaisedDate);

            Application application = earningsService.lookup(nino, applicationRaisedDate);

            FinancialStatusCheckResponse response = new FinancialStatusCheckResponse();
            response.setIndividual(application.getIndividual());

            switch (incomeProvingResponse.getPayFreq().toUpperCase()) {
                case "M1":
                    FinancialCheckValues categoryAMonthlySalaried = IncomeValidator.validateCategoryAMonthlySalaried(incomeProvingResponse.getIncomes(), startSearchDate, applicationRaisedDate, dependants);
                    if (categoryAMonthlySalaried.equals(FinancialCheckValues.MONTHLY_SALARIED_PASSED)) {
                        response.setCategoryCheck(new CategoryCheck("A", true, null, applicationRaisedDate, startSearchDate));
                    } else {
                        response.setCategoryCheck(new CategoryCheck("A", false, categoryAMonthlySalaried, applicationRaisedDate, startSearchDate));
                    }
                    break;
                case "W1":
                    FinancialCheckValues categoryAWeeklySalaried = IncomeValidator.validateCategoryAWeeklySalaried(incomeProvingResponse.getIncomes(), startSearchDate, applicationRaisedDate, dependants);
                    if (categoryAWeeklySalaried.equals(FinancialCheckValues.WEEKLY_SALARIED_PASSED)) {
                        response.setCategoryCheck(new CategoryCheck("A", true, null, applicationRaisedDate, startSearchDate));
                    } else {
                        response.setCategoryCheck(new CategoryCheck("A", false, categoryAWeeklySalaried, applicationRaisedDate, startSearchDate));
                    }
                    break;
                default:
                    throw new UnknownPaymentFrequencyType();
            }
            response.setStatus(new ResponseStatus("100", "OK"));

            auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, auditData(response)));

            return new ResponseEntity<>(response, headers, HttpStatus.OK);

        } // TODO All this below is a mess of exceptions and needs to be refactored
        catch (EarningsServiceFailedToMapDataToDomainClass | EarningsServiceNoUniqueMatch e) {
            LOGGER.error("Could not retrieve earning details.", e);
            return buildErrorResponse(headers, "0009", "Resource not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException iae) {
            LOGGER.error(iae.getMessage(), iae);
            return buildErrorResponse(headers, "0004", "Parameter error: " + iae.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnknownPaymentFrequencyType upte) {
            LOGGER.error("Unknown payment frequency type " + upte);
            return buildErrorResponse(headers, "0005", "Unknown payment frequency type", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            LOGGER.error("NINO is not valid", e);
            return buildErrorResponse(headers, "0004", "Parameter error: NINO is invalid", HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    protected ResponseEntity<FinancialStatusCheckResponse> buildErrorResponse(HttpHeaders headers, String statusCode, String statusMessage, HttpStatus status) {
        ResponseStatus error = new ResponseStatus(statusCode, statusMessage);
        FinancialStatusCheckResponse response = new FinancialStatusCheckResponse();
        response.setStatus(error);
        return new ResponseEntity<>(response, headers, status);
    }

    public void setEarningsService(EarningsService earningsService) {
        this.earningsService = earningsService;
    }

    public void setIndividualService(IndividualService individualService) {
        this.individualService = individualService;
    }

    private Map<String, Object> auditData(String nino, String applicationRaisedDate, Integer dependants) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-financial-status");
        auditData.put("nino", nino);
        auditData.put("applicationRaisedDate", applicationRaisedDate);
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

package uk.gov.digital.ho.proving.income.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.income.acl.*;
import uk.gov.digital.ho.proving.income.audit.AuditActions;
import uk.gov.digital.ho.proving.income.domain.IncomeProvingResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDate.now;
import static uk.gov.digital.ho.proving.income.audit.AuditActions.auditEvent;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH_RESULT;
import static uk.gov.digital.ho.proving.income.util.DateUtils.parseIsoDate;

@RestController
@ControllerAdvice
public class IncomeRetrievalService extends AbstractIncomeProvingController {

    @Autowired
    private EarningsService earningsService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ApplicationEventPublisher auditor;

    @RequestMapping(value = "/incomeproving/v1/individual/{nino}/income", method = RequestMethod.GET)
    public ResponseEntity<IncomeRetrievalResponse> getIncome(
        @PathVariable(value = "nino") String nino,
        @RequestParam(value = "fromDate") String fromDateAsString,
        @RequestParam(value = "toDate") String toDateAsString) {

        LOGGER.debug("Get income details invoked for {} nino between {} and {}", nino, fromDateAsString, toDateAsString);

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, auditData(nino, fromDateAsString, toDateAsString)));

        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON);

        try {
            String cleanNino = sanitiseNino(nino);
            validateNino(cleanNino);

            Optional<LocalDate> fromDate = parseIsoDate(fromDateAsString);
            if (!fromDate.isPresent()) {
                return buildErrorResponse(headers, "0001", "Parameter error: From date is invalid", HttpStatus.BAD_REQUEST);
            } else if(fromDate.get().isAfter(now())){
                return buildErrorResponse(headers, "0004", "Parameter error: fromDate", HttpStatus.BAD_REQUEST);
            }

            Optional<LocalDate> toDate = parseIsoDate(toDateAsString);
            if (!toDate.isPresent()) {
                return buildErrorResponse(headers, "0001", "Parameter error: To date is invalid", HttpStatus.BAD_REQUEST);
            } else if(toDate.get().isAfter(now())){
                return buildErrorResponse(headers, "0004", "Parameter error: toDate", HttpStatus.BAD_REQUEST);
            }

            Optional<IncomeProvingResponse> incomeProvingResponse = fromDate.flatMap(from ->
                toDate.map(to ->
                    individualService.lookup(cleanNino, from, to)
                )
            );

            return incomeProvingResponse.map(ips -> {
                    IncomeRetrievalResponse incomeRetrievalResponse = new IncomeRetrievalResponse();
                    incomeRetrievalResponse.setIndividual(ips.getindividual());
                    incomeRetrievalResponse.setIncomes(ips.getIncomes());

                    auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, auditData(incomeRetrievalResponse)));

                    return new ResponseEntity<>(incomeRetrievalResponse, headers, HttpStatus.OK);
                }
            ).orElse(buildErrorResponse(headers, "0004", "Invalid NINO", HttpStatus.NOT_FOUND));

        } catch (EarningsServiceFailedToMapDataToDomainClass | EarningsServiceNoUniqueMatch e) {
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
    protected ResponseEntity<IncomeRetrievalResponse> buildErrorResponse(HttpHeaders headers, String statusCode, String statusMessage, HttpStatus status) {
        ResponseStatus error = new ResponseStatus(statusCode, statusMessage);
        IncomeRetrievalResponse response = new IncomeRetrievalResponse();
        response.setStatus(error);
        return new ResponseEntity<>(response, headers, status);
    }

    public void setEarningsService(EarningsService earningsService) {
        this.earningsService = earningsService;
    }

    public void setIndividualService(IndividualService individualService) {
        this.individualService = individualService;
    }

    private Map<String, Object> auditData(String nino, String fromDate, String toDate) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-income");
        auditData.put("nino", nino);
        auditData.put("fromDate", fromDate);
        auditData.put("toDate", toDate);

        return auditData;
    }

    private Map<String, Object> auditData(IncomeRetrievalResponse response) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-income");
        auditData.put("response", response);

        return auditData;
    }
}

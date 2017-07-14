package uk.gov.digital.ho.proving.income.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.digital.ho.proving.income.acl.EarningsServiceFailedToMapDataToDomainClass;
import uk.gov.digital.ho.proving.income.acl.EarningsServiceNoUniqueMatch;
import uk.gov.digital.ho.proving.income.acl.UnknownPaymentFrequencyType;

import static net.logstash.logback.marker.Markers.append;

@ControllerAdvice
public class ResourceExceptionHandler extends ResponseEntityExceptionHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = EarningsServiceFailedToMapDataToDomainClass.class)
    public ResponseEntity<Object> handleException(EarningsServiceFailedToMapDataToDomainClass e, WebRequest request) {
        log.error(append("errorCode", "0009"), "Could not retrieve earning details.", e);
        return handleExceptionInternal(e, buildErrorResponse("0009", "Resource not found"), headers(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = EarningsServiceNoUniqueMatch.class)
    public ResponseEntity<Object> handleException(EarningsServiceNoUniqueMatch e, WebRequest request) {
        log.error(append("errorCode", "0009"), "Could not retrieve earning details.", e);
        return handleExceptionInternal(e, buildErrorResponse("0009", "Resource not found"), headers(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(IllegalArgumentException e, WebRequest request) {
        log.error(append("errorCode", "0004"), e.getMessage(), e);
        return handleExceptionInternal(e, buildErrorResponse("0004", e.getMessage()), headers(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = UnknownPaymentFrequencyType.class)
    public ResponseEntity<Object> handleException(UnknownPaymentFrequencyType e, WebRequest request) {
        log.error(append("errorCode", "0005"), "Unknown payment frequency type " + e);
        return handleExceptionInternal(e, buildErrorResponse("0005", "Unknown payment frequency type"), headers(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private FinancialStatusCheckResponse buildErrorResponse(String statusCode, String statusMessage) {
        uk.gov.digital.ho.proving.income.api.ResponseStatus error = new uk.gov.digital.ho.proving.income.api.ResponseStatus(statusCode, statusMessage);
        FinancialStatusCheckResponse response = new FinancialStatusCheckResponse();
        response.setStatus(error);
        return response;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}



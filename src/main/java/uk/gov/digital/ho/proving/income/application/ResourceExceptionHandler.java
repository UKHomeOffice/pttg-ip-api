package uk.gov.digital.ho.proving.income.application;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import uk.gov.digital.ho.proving.income.api.NinoUtils;
import uk.gov.digital.ho.proving.income.api.domain.BaseResponse;
import uk.gov.digital.ho.proving.income.api.domain.ResponseStatus;
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions.AuditDataException;
import uk.gov.digital.ho.proving.income.audit.AuditClient;

import java.util.Map;
import java.util.UUID;

import static net.logstash.logback.marker.Markers.append;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.application.LogEvent.*;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE;

@ControllerAdvice
@Slf4j
public class ResourceExceptionHandler {

    private final AuditClient auditClient;
    private final NinoUtils ninoUtils;

    public ResourceExceptionHandler(AuditClient auditClient, NinoUtils ninoUtils) {
        this.auditClient = auditClient;
        this.ninoUtils = ninoUtils;
    }

    @ExceptionHandler(AuditDataException.class)
    ResponseEntity<BaseResponse> handle(AuditDataException exception) {
        log.error(append("errorCode", "0001"), exception.getMessage(), append(EVENT, INCOME_PROVING_AUDIT_FAILURE));
        return buildErrorResponse(httpHeaders(), "0001", "Json marshalling error: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse> handle(MissingServletRequestParameterException exception) {
        log.error(append("errorCode", "0001"), exception.getMessage(), append(EVENT, INCOME_PROVING_SERVICE_RESPONSE_ERROR));
        return buildErrorResponse(httpHeaders(), "0001", "Missing parameter: " + exception.getParameterName(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse> handle(NoHandlerFoundException exception) {
        log.error(append("errorCode", "0009"), exception.getMessage(), append(EVENT, INCOME_PROVING_SERVICE_RESPONSE_NOT_FOUND));
        return buildErrorResponse(httpHeaders(), "0009", "Resource not found: " + exception.getRequestURL(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse> handle(MethodArgumentTypeMismatchException exception) {
        log.error(append("errorCode", "0004"), exception.getMessage(), append(EVENT, INCOME_PROVING_SERVICE_RESPONSE_ERROR));
        return buildErrorResponse(httpHeaders(), "0004", "Error: Invalid value for " + exception.getName(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handle(IllegalArgumentException exception, WebRequest request) {
        log.error(append("errorCode", "0004"), exception.getMessage(), append(EVENT, INCOME_PROVING_SERVICE_RESPONSE_ERROR));
        return buildErrorResponse(httpHeaders(), "0004", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse> handle(HttpMessageNotReadableException exception) {
        log.error(append("errorCode", "0004"), exception.getMessage(), append(EVENT, INCOME_PROVING_SERVICE_RESPONSE_ERROR));
        return buildErrorResponse(httpHeaders(), "0004", "Error: Invalid request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ApplicationExceptions.EarningsServiceNoUniqueMatchException.class)
    ResponseEntity<BaseResponse> handle(ApplicationExceptions.EarningsServiceNoUniqueMatchException e) {
        log.error(append("errorCode", "0009"), "Could not retrieve earning details.", e, append(EVENT, INCOME_PROVING_SERVICE_RESPONSE_NOT_FOUND));
        String errorMessage = String.format("Resource not found: %s", ninoUtils.redact(e.nino()));
        auditClient.add(INCOME_PROVING_FINANCIAL_STATUS_RESPONSE, UUID.randomUUID(), auditData(new BaseResponse(new ResponseStatus("0009", errorMessage))));
        return buildErrorResponse(httpHeaders(), "0009", errorMessage, HttpStatus.NOT_FOUND);
    }

    private Map<String, Object> auditData(BaseResponse response) {
        return ImmutableMap.of("method", "get-financial-status", "response", response);
    }

    private ResponseEntity<BaseResponse> buildErrorResponse(HttpHeaders headers, String statusCode, String statusMessage, HttpStatus status) {
        BaseResponse response = new BaseResponse(new ResponseStatus(statusCode, statusMessage));
        return new ResponseEntity<>(response, headers, status);
    }

    private HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        return headers;
    }
}

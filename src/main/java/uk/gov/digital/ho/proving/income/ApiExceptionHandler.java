package uk.gov.digital.ho.proving.income;

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
import uk.gov.digital.ho.proving.income.acl.EarningsServiceFailedToMapDataToDomainClass;
import uk.gov.digital.ho.proving.income.acl.EarningsServiceNoUniqueMatch;
import uk.gov.digital.ho.proving.income.acl.UnknownPaymentFrequencyType;
import uk.gov.digital.ho.proving.income.api.BaseResponse;
import uk.gov.digital.ho.proving.income.api.ResponseStatus;
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions.AuditDataException;

import static net.logstash.logback.marker.Markers.append;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    protected final String CONTENT_TYPE = "Content-type";
    protected final String APPLICATION_JSON = "application/json";

    @ExceptionHandler(AuditDataException.class)
    public Object auditDataMarshalFailureHandler(AuditDataException exception) {
        log.error(append("errorCode", "0001"), exception.getMessage());
        return buildErrorResponse(httpHeaders(), "0001", "Json marshalling error: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object missingParamterHandler(MissingServletRequestParameterException exception) {
        log.error(append("errorCode", "0001"), exception.getMessage());
        return buildErrorResponse(httpHeaders(), "0001", "Missing parameter: " + exception.getParameterName(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object requestHandlingNoHandlerFound(NoHandlerFoundException exception) {
        log.error(append("errorCode", "0009"), exception.getMessage());
        return buildErrorResponse(httpHeaders(), "0009", "Resource not found: " + exception.getRequestURL(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error(append("errorCode", "0004"), exception.getMessage());
        return buildErrorResponse(httpHeaders(), "0004", "Error: Invalid value for " + exception.getName(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(IllegalArgumentException exception, WebRequest request) {
        log.error(append("errorCode", "0004"), exception.getMessage());
        return buildErrorResponse(httpHeaders(), "0004", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<Object> invalidRequestBody(HttpMessageNotReadableException exception) {
        log.error(append("errorCode", "0004"), exception.getMessage());
        return buildErrorResponse(httpHeaders(), "0004", "Error: Invalid request", HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = UnknownPaymentFrequencyType.class)
    public ResponseEntity<Object> handleException(UnknownPaymentFrequencyType e, WebRequest request) {
        log.error(append("errorCode", "0005"), "Unknown payment frequency type " + e);
        return buildErrorResponse(httpHeaders(), "0005", "Unknown payment frequency type", HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(value = EarningsServiceFailedToMapDataToDomainClass.class)
    public ResponseEntity<Object> handleException(EarningsServiceFailedToMapDataToDomainClass e, WebRequest request) {
        log.error(append("errorCode", "0009"), "Could not retrieve earning details.", e);
        return buildErrorResponse(httpHeaders(), "0009", "Resource not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EarningsServiceNoUniqueMatch.class)
    public ResponseEntity<Object> handleException(EarningsServiceNoUniqueMatch e, WebRequest request) {
        log.error(append("errorCode", "0009"), "Could not retrieve earning details.", e);
        return buildErrorResponse(httpHeaders(), "0009", "Resource not found", HttpStatus.NOT_FOUND);
    }


    private ResponseEntity<Object> buildErrorResponse(HttpHeaders headers, String statusCode, String statusMessage, HttpStatus status) {
        BaseResponse response = new BaseResponse(new ResponseStatus(statusCode, statusMessage));
        return new ResponseEntity<>(response, headers, status);
    }

    private HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON);
        return headers;
    }
}

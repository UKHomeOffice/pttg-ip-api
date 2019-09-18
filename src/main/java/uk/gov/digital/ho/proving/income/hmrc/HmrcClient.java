package uk.gov.digital.ho.proving.income.hmrc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions.EarningsServiceNoUniqueMatchException;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.time.LocalDate;

import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.api.RequestData.*;
import static uk.gov.digital.ho.proving.income.application.LogEvent.*;

@Service
@Slf4j
public class HmrcClient {

    private final RestTemplate restTemplate;
    private final String hmrcServiceEndpoint;
    private final RequestData requestData;
    private final ServiceResponseLogger serviceResponseLogger;

    HmrcClient(RestTemplate restTemplate,
               @Value("${hmrc.service.endpoint}") String hmrcServiceEndpoint,
               RequestData requestData,
               ServiceResponseLogger serviceResponseLogger) {
        this.restTemplate = restTemplate;
        this.hmrcServiceEndpoint = hmrcServiceEndpoint;
        this.requestData = requestData;
        this.serviceResponseLogger = serviceResponseLogger;
    }

    @Retryable(
        include = { HttpServerErrorException.class },
        exclude = {EarningsServiceNoUniqueMatchException.class},
        maxAttemptsExpression = "#{${hmrc.service.retry.attempts}}",
        backoff = @Backoff(delayExpression = "#{${hmrc.service.retry.delay}}"))
    public IncomeRecord getIncomeRecord(Identity identity, LocalDate fromDate, LocalDate toDate) {

        try {

            log.info("About to call HMRC Service at {}", hmrcServiceEndpoint,
                value(EVENT, HMRC_REQUEST_SENT));

            ResponseEntity<IncomeRecord> responseEntity = restTemplate.exchange(
                hmrcServiceEndpoint,
                POST,
                createEntity(identity, fromDate, toDate),
                IncomeRecord.class);

            requestData.updateComponentTrace(responseEntity);
            serviceResponseLogger.record(identity, responseEntity.getBody());

            log.info("Received {} incomes and {} employments", responseEntity.getBody().paye().size(),
                responseEntity.getBody().employments().size(), value(EVENT, HMRC_RESPONSE_SUCCESS));

            return responseEntity.getBody();

        } catch (HttpStatusCodeException e) {
            requestData.updateComponentTrace(e);
            if (isNotFound(e)) {
                log.error("HMRC Service found no match", value(EVENT, HMRC_NOT_FOUND_RESPONSE));
                throw new EarningsServiceNoUniqueMatchException(identity.nino());
            }
            log.error("HMRC Service failed", e, value(EVENT, HMRC_ERROR_REPSONSE));
            throw e;
        }
    }

    @Recover
    IncomeRecord getIncomeRecordFailureRecovery(HttpServerErrorException e) {
        log.error("Failed to retrieve HMRC data after retries - {}", e.getMessage(), value(EVENT, HMRC_ERROR_REPSONSE));
        throw(e);
    }

    @Recover
    IncomeRecord getIncomeRecordFailureRecovery(EarningsServiceNoUniqueMatchException e) {
        throw(e);
    }

    private boolean isNotFound(HttpStatusCodeException e) {
        return e.getStatusCode() == HttpStatus.NOT_FOUND;
    }

    private HttpHeaders generateRestHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(SESSION_ID_HEADER, requestData.sessionId());
        headers.add(CORRELATION_ID_HEADER, requestData.correlationId());
        headers.add(USER_ID_HEADER, requestData.userId());
        headers.add(AUTHORIZATION, requestData.hmrcBasicAuth());
        headers.add(COMPONENT_TRACE_HEADER, requestData.componentTrace());

        return headers;
    }

    private HttpEntity createEntity(Identity identity, LocalDate fromDate, LocalDate toDate) {
        return new HttpEntity<>(
            new IncomeDataRequest(
                identity.firstname(),
                identity.lastname(),
                identity.nino(),
                identity.dateOfBirth(),
                fromDate,
                toDate),
            generateRestHeaders());
    }
}

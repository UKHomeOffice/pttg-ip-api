package uk.gov.digital.ho.proving.income.domain.hmrc;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.api.RequestData.*;

@Service
@Slf4j
public class HmrcClient {

    private final RestTemplate restTemplate;
    private final String hmrcServiceEndpoint;
    private final RequestData requestData;
    private final ServiceResponseLogger serviceResponseLogger;

    public HmrcClient(RestTemplate restTemplate,
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
        exclude = { HttpClientErrorException.class, ApplicationExceptions.EarningsServiceNoUniqueMatchException.class },
        maxAttemptsExpression = "#{${hmrc.service.retry.attempts}}",
        backoff = @Backoff(delayExpression = "#{${hmrc.service.retry.delay}}"))
    public IncomeRecord getIncomeRecord(Identity identity, LocalDate fromDate, LocalDate toDate) {

        try {

            log.info(String.format("About to call Income Service at %s", hmrcServiceEndpoint));

            ResponseEntity<IncomeRecord> responseEntity = restTemplate.exchange(
                String.format("%s?firstName={firstName}&lastName={lastName}&nino={nino}&dateOfBirth={dateOfBirth}&fromDate={fromDate}&toDate={toDate}", hmrcServiceEndpoint),
                HttpMethod.GET,
                createEntity(),
                IncomeRecord.class,
                ImmutableMap.
                    <String, String>builder().
                    put("firstName", identity.getFirstname()).
                    put("lastName", identity.getLastname()).
                    put("nino", identity.getNino()).
                    put("dateOfBirth", identity.getDateOfBirth().format(DateTimeFormatter.ISO_DATE)).
                    put("fromDate", fromDate.format(DateTimeFormatter.ISO_DATE)).
                    put("toDate", toDate.format(DateTimeFormatter.ISO_DATE)).
                    build());

            serviceResponseLogger.record(identity, responseEntity.getBody());

            log.info(String.format("Received %d incomes and %d employments ", responseEntity.getBody().getIncome().size(), responseEntity.getBody().getEmployments().size()));

            return responseEntity.getBody();

        } catch (HttpStatusCodeException e) {
            if (isNotFound(e)) {
                log.error("Income Service found no match");
                throw new ApplicationExceptions.EarningsServiceNoUniqueMatchException();
            }
            log.error("Income Service failed", e);
            throw e;
        }
    }

    @Recover
    IncomeRecord getIncomeRecordFailureRecovery(HttpServerErrorException e) {
        log.error("Failed to retrieve HMRC data after retries - {}", e.getMessage());
        throw(e);
    }

    @Recover
    IncomeRecord getIncomeRecordFailureRecovery(HttpClientErrorException e) {
        log.error("Failed to retrieve HMRC data (no retries attempted) - {}", e.getMessage());
        throw(e);
    }

    @Recover
    IncomeRecord getIncomeRecordFailureRecovery(ApplicationExceptions.EarningsServiceNoUniqueMatchException e) {
        log.error("Failed to retrieve HMRC data (no retries attempted) - {}", e.getMessage());
        throw(e);
    }

    private boolean isNotFound(HttpStatusCodeException e) {
        return e.getStatusCode() != null && e.getStatusCode() == HttpStatus.FORBIDDEN;
    }

    private HttpHeaders generateRestHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(SESSION_ID_HEADER, requestData.sessionId());
        headers.add(CORRELATION_ID_HEADER, requestData.correlationId());
        headers.add(USER_ID_HEADER, requestData.userId());
        headers.add(AUTHORIZATION, requestData.hmrcBasicAuth());

        return headers;
    }

    private HttpEntity createEntity() {
        return new HttpEntity<>(Void.class, generateRestHeaders());
    }

}

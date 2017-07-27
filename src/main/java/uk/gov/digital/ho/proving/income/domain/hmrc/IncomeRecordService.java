package uk.gov.digital.ho.proving.income.domain.hmrc;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.acl.EarningsServiceNoUniqueMatch;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.logging.LoggingInterceptor.CORRELATION_ID_HEADER;
import static uk.gov.digital.ho.proving.income.logging.LoggingInterceptor.USER_ID_HEADER;

@Service
@Slf4j
public class IncomeRecordService {
    private final RestTemplate restTemplate;
    private final String hmrcServiceEndpoint;

    public IncomeRecordService(RestTemplate restTemplate, @Value("${hmrc.service.endpoint}") String hmrcServiceEndpoint) {
        this.restTemplate = restTemplate;
        this.hmrcServiceEndpoint = hmrcServiceEndpoint;
    }

    public IncomeRecord getIncomeRecord(Identity identity, LocalDate fromDate, LocalDate toDate) {
        try {
            log.info(String.format("About to call income service %s for %s ", hmrcServiceEndpoint, identity.getNino()));
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
            log.info(String.format("Received %d incomes and %d employments ", responseEntity.getBody().getIncome().size(), responseEntity.getBody().getEmployments().size()));
            return responseEntity.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Income service failed", e);
            if (isNotFound(e)) {
                throw new EarningsServiceNoUniqueMatch();
            }
            throw e;
        }
    }

    private static boolean isNotFound(HttpStatusCodeException e) {
        return e.getStatusCode() != null && e.getStatusCode() == HttpStatus.FORBIDDEN;
    }


    private static HttpHeaders generateRestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_JSON_VALUE);
        headers.add(USER_ID_HEADER, MDC.get(USER_ID_HEADER));
        headers.add(CORRELATION_ID_HEADER, MDC.get(CORRELATION_ID_HEADER));
        return headers;
    }

    private static HttpEntity createEntity() {
        return new HttpEntity<>(Void.class, generateRestHeaders());
    }


}

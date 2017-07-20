package uk.gov.digital.ho.proving.income.domain.hmrc;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.acl.EarningsServiceNoUniqueMatch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class IncomeRecordService {
    private final RestTemplate restTemplate;
    private final String hmrcServiceEndpoint;

    public IncomeRecordService(RestTemplate restTemplate, @Value("${hmrc.service.endpoint}") String hmrcServiceEndpoint) {
        this.restTemplate = restTemplate;
        this.hmrcServiceEndpoint = hmrcServiceEndpoint;
    }

    public IncomeRecord getIncomeRecord(Identity identity, LocalDate fromDate, LocalDate toDate) {
        try {
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
        return responseEntity.getBody();
        } catch (HttpStatusCodeException e) {
            if (isNotFound(e)) {
                throw new EarningsServiceNoUniqueMatch();
            }
            throw e;
        }
    }

    private static boolean isNotFound(HttpStatusCodeException e) {
        return e.getStatusCode() != null && e.getStatusCode() == HttpStatus.NOT_FOUND;
    }


    private static HttpHeaders generateRestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_JSON_VALUE);
        return headers;
    }

    private static HttpEntity createEntity() {
        return new HttpEntity<>(Void.class, generateRestHeaders());
    }


}
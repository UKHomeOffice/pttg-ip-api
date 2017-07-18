package uk.gov.digital.ho.proving.income.domain.hmrc;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

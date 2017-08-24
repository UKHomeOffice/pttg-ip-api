package uk.gov.digital.ho.proving.income.alert.sysdig;

import com.fasterxml.jackson.databind.ObjectMapper;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.alert.IndividualVolumeUsage;
import uk.gov.digital.ho.proving.income.alert.MatchingFailureUsage;
import uk.gov.digital.ho.proving.income.alert.TimeOfRequestUsage;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class SysdigEventService {
    private final RestTemplate restTemplate;
    private final String sysdigEndpoint;
    private final String sysdigAccessToken;

    public SysdigEventService(RestTemplate restTemplate, @Value("${sysdig.service.endpoint}") String sysdigEndpoint, @Value("${sysdig.access.token}") String sysdigAccessToken) {
        this.restTemplate = restTemplate;
        this.sysdigEndpoint = sysdigEndpoint;
        this.sysdigAccessToken = sysdigAccessToken;
    }


    public void sendUsersExceedUsageThresholdEvent(IndividualVolumeUsage individualVolumeUsage) {
        try {
            log.warn("Excessive usage detected");
            log.info(sysdigEndpoint);
            Message message = new Message(new Event("Proving Things, Income Proving, Excessive Usage", String.format("Excessive usage detected; %s", individualVolumeUsage.getCountsByUser()), "6", ImmutableMap.of()));
            log.info(new ObjectMapper().writeValueAsString(message));
            restTemplate.exchange(sysdigEndpoint, HttpMethod.POST, toEntity(message), Void.class);
        } catch (Exception e) {
            log.error("Unable to alert on suspect usage", e);
        }
    }

    public  void sendRequestsOutsideHoursEvent(TimeOfRequestUsage timeOfRequestUsage) {
        try {
            log.warn("Request made outside usual hours");
            Message message = new Message(new Event("Proving Things, Income Proving, Out of hours activity", String.format("Activity detected outside usual hours; %d requests made", timeOfRequestUsage.getRequestCount()), "6", ImmutableMap.of()));
            restTemplate.exchange(sysdigEndpoint, HttpMethod.POST, toEntity(message), Void.class);
        } catch (Exception e) {
            log.error("Unable to alert on suspect usage", e);
        }
    }

    public void sendMatchingFailuresExceedThresholdEvent(MatchingFailureUsage matchingFailureUsage) {
        try {
            log.warn("Excessive number of match failures");
            Message message = new Message(new Event("Proving Things, Income Proving, Excessive match failures", String.format("Excessive match failures detected; %d match failures", matchingFailureUsage.getCountOfFailures()), "6", ImmutableMap.of()));
            restTemplate.exchange(sysdigEndpoint, HttpMethod.POST, toEntity(message), Void.class);
        } catch (Exception e) {
            log.error("Unable to alert on suspect usage", e);
        }
    }

    private HttpEntity<Message> toEntity(Message message) {
        return new HttpEntity<>(message, generateRestHeaders());
    }

    private HttpHeaders generateRestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, String.format("Bearer %s", sysdigAccessToken));
        log.info(headers.toString());
        return headers;
    }
}

package uk.gov.digital.ho.proving.income.domain.hmrc;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.api.RequestData.*;

@RunWith(MockitoJUnitRunner.class)
public class HmrcClientTest {

    @Mock private RestTemplate mockRestTemplate;
    @Mock private RequestData mockRequestData;
    @Mock private ServiceResponseLogger mockServiceResponseLogger;

    @Captor private ArgumentCaptor<Map<String, String>> captorVariables;
    @Captor private ArgumentCaptor<String> captorUrlTemplate;
    @Captor private ArgumentCaptor<IncomeRecord> captorResponseBody;
    @Captor private ArgumentCaptor<HttpEntity> captorEntity;

    private HmrcClient service;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() throws Exception {

        when(mockRequestData.sessionId()).thenReturn("some session id");
        when(mockRequestData.correlationId()).thenReturn("some correlation id");
        when(mockRequestData.userId()).thenReturn("some user id");
        when(mockRequestData.hmrcBasicAuth()).thenReturn("some basic auth");

        service = new HmrcClient(mockRestTemplate, "http://income-service/income", mockRequestData, mockServiceResponseLogger);

        when(mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), Matchers.<Map<String, String>>any()))
            .thenReturn(new ResponseEntity<>(new IncomeRecord(
                emptyList(),
                emptyList(),
                emptyList(),
                aIndividual()
            ), HttpStatus.OK));

        service.getIncomeRecord(
            new Identity(
                "John",
                "Smith",
                LocalDate.of(1965, Month.JULY, 19), "NE121212A"),
            LocalDate.of(2017, Month.JANUARY, 1),
            LocalDate.of(2017, Month.JULY, 1)
        );
    }

    @Test
    public void shouldSendServiceResponseToLogger() {
        verify(mockServiceResponseLogger).record(eq(new Identity("John",
                                                                "Smith",
                                                                LocalDate.of(1965, Month.JULY, 19), "NE121212A")),
                                                    captorResponseBody.capture());

        assertThat(captorResponseBody.getValue().getPaye()).isEmpty();
        assertThat(captorResponseBody.getValue().getEmployments()).isEmpty();
    }

    @Test
    public void shouldMakeGetRequest() throws Exception {
        verify(mockRestTemplate).exchange(anyString(),
                                            eq(HttpMethod.GET),
                                            any(HttpEntity.class),
                                            Matchers.<Class<IncomeRecord>>any(),
                                            Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldPassFirstnameAsRestParameter() throws Exception {
        verify(mockRestTemplate).exchange(captorUrlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), captorVariables.capture());

        assertThat(captorVariables.getValue()).containsEntry("firstName", "John");
        assertThat(captorUrlTemplate.getValue()).contains("firstName={firstName}");
    }

    @Test
    public void shouldPassLastnameAsRestParameter() throws Exception {
        verify(mockRestTemplate).exchange(captorUrlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), captorVariables.capture());

        assertThat(captorVariables.getValue()).containsEntry("lastName", "Smith");
        assertThat(captorUrlTemplate.getValue()).contains("lastName={lastName}");
    }

    @Test
    public void shouldPassNinoAsRestParameter() throws Exception {
        verify(mockRestTemplate).exchange(captorUrlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), captorVariables.capture());

        assertThat(captorVariables.getValue()).containsEntry("nino", "NE121212A");
        assertThat(captorUrlTemplate.getValue()).contains("nino={nino}");
    }

    @Test
    public void shouldPassDateOfBirthAsRestParameterInISOFormat() throws Exception {
        verify(mockRestTemplate).exchange(captorUrlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), captorVariables.capture());

        assertThat(captorVariables.getValue()).containsEntry("dateOfBirth", "1965-07-19");
        assertThat(captorUrlTemplate.getValue()).contains("dateOfBirth={dateOfBirth}");
    }

    @Test
    public void shouldPassFromDateAsRestParameterInISOFormat() throws Exception {
        verify(mockRestTemplate).exchange(captorUrlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), captorVariables.capture());

        assertThat(captorVariables.getValue()).containsEntry("fromDate", "2017-01-01");
        assertThat(captorUrlTemplate.getValue()).contains("fromDate={fromDate}");
    }

    @Test
    public void shouldPassToDateAsRestParameterInISOFormat() throws Exception {
        verify(mockRestTemplate).exchange(captorUrlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), captorVariables.capture());

        assertThat(captorVariables.getValue()).containsEntry("toDate", "2017-07-01");
        assertThat(captorUrlTemplate.getValue()).contains("toDate={toDate}");
    }

    @Test(expected = ApplicationExceptions.EarningsServiceNoUniqueMatchException.class)
    public void forbiddenShouldBeMappedToNoMatch() {
        when(mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), Matchers.<Map<String, String>>any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        service.getIncomeRecord(
            new Identity(
                "John",
                "Smith",
                LocalDate.of(1965, Month.JULY, 19), "NE121212A"),
            LocalDate.of(2017, Month.JANUARY, 1),
            LocalDate.of(2017, Month.JULY, 1)
        );
    }

    @Test
    public void shouldSendHttpHeaders() {
        verify(mockRestTemplate).exchange(anyString(),
                                            any(HttpMethod.class),
                                            captorEntity.capture(),
                                            Matchers.<Class<IncomeRecord>>any(),
                                            Matchers.<Map<String, String>>any());

        assertThat(captorEntity.getValue().getHeaders()).containsEntry(CONTENT_TYPE, Arrays.asList(APPLICATION_JSON_VALUE));
        assertThat(captorEntity.getValue().getHeaders()).containsEntry(SESSION_ID_HEADER, Arrays.asList("some session id"));
        assertThat(captorEntity.getValue().getHeaders()).containsEntry(CORRELATION_ID_HEADER, Arrays.asList("some correlation id"));
        assertThat(captorEntity.getValue().getHeaders()).containsEntry(USER_ID_HEADER, Arrays.asList("some user id"));
        assertThat(captorEntity.getValue().getHeaders()).containsEntry(AUTHORIZATION, Arrays.asList("some basic auth"));
    }

    private Individual aIndividual() {
        return new Individual("Joe", "Bloggs", "NE121212C", LocalDate.now());
    }


    @Test
    public void shouldRethrowHttpServerErrorException() {

        thrown.expect(HttpServerErrorException.class);

        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.BAD_GATEWAY);

        service.getIncomeRecordFailureRecovery(exception);
    }

    @Test
    public void shouldHttpClientErrorException() {

        thrown.expect(HttpClientErrorException.class);

        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);

        service.getIncomeRecordFailureRecovery(exception);
    }

    @Test
    public void shouldEarningsServiceNoUniqueMatch() {

        thrown.expect(ApplicationExceptions.EarningsServiceNoUniqueMatchException.class);

        ApplicationExceptions.EarningsServiceNoUniqueMatchException exception = new ApplicationExceptions.EarningsServiceNoUniqueMatchException();

        service.getIncomeRecordFailureRecovery(exception);
    }
}

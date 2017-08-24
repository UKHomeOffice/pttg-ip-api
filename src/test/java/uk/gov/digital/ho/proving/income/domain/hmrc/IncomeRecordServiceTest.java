package uk.gov.digital.ho.proving.income.domain.hmrc;

import org.junit.Before;
import org.junit.Test;
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
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.income.acl.EarningsServiceNoUniqueMatch;
import uk.gov.digital.ho.proving.income.api.RequestData;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncomeRecordServiceTest {

    @Mock private RestTemplate mockRestTemplate;
    @Mock private RequestData mockRequestData;
    @Mock private ServiceResponseLogger mockServiceResponseLogger;

    @Captor private ArgumentCaptor<Map<String, String>> captorVariables;
    @Captor private ArgumentCaptor<String> captorUrlTemplate;
    @Captor private ArgumentCaptor<IncomeRecord> captorResponseBody;

    private IncomeRecordService service;

    @Before
    public void before() throws Exception {

        service = new IncomeRecordService(mockRestTemplate, "http://income-service/income", mockRequestData, mockServiceResponseLogger);

        when(mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), Matchers.<Map<String, String>>any()))
            .thenReturn(new ResponseEntity<>(new IncomeRecord(
                emptyList(),
                emptyList()
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
        verify(mockServiceResponseLogger).record(captorResponseBody.capture());

        assertThat(captorResponseBody.getValue().getIncome()).isEmpty();
        assertThat(captorResponseBody.getValue().getEmployments()).isEmpty();
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

    @Test(expected = EarningsServiceNoUniqueMatch.class)
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

}

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
    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<Map<String, String>> variablesCaptor;
    @Captor
    private ArgumentCaptor<String> urlTemplate;
    private IncomeRecordService service;

    @Before
    public void before() throws Exception {
        service = new IncomeRecordService(restTemplate, "http://income-service/income");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), Matchers.<Map<String, String>>any()))
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
    public void shouldPassFirstnameAsRestParameter() throws Exception {
        verify(restTemplate).exchange(urlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), variablesCaptor.capture());

        assertThat(variablesCaptor.getValue()).containsEntry("firstName", "John");
        assertThat(urlTemplate.getValue()).contains("firstName={firstName}");
    }

    @Test
    public void shouldPassLastnameAsRestParameter() throws Exception {
        verify(restTemplate).exchange(urlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), variablesCaptor.capture());

        assertThat(variablesCaptor.getValue()).containsEntry("lastName", "Smith");
        assertThat(urlTemplate.getValue()).contains("lastName={lastName}");
    }

    @Test
    public void shouldPassNinoAsRestParameter() throws Exception {
        verify(restTemplate).exchange(urlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), variablesCaptor.capture());

        assertThat(variablesCaptor.getValue()).containsEntry("nino", "NE121212A");
        assertThat(urlTemplate.getValue()).contains("nino={nino}");
    }

    @Test
    public void shouldPassDateOfBirthAsRestParameterInISOFormat() throws Exception {
        verify(restTemplate).exchange(urlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), variablesCaptor.capture());

        assertThat(variablesCaptor.getValue()).containsEntry("dateOfBirth", "1965-07-19");
        assertThat(urlTemplate.getValue()).contains("dateOfBirth={dateOfBirth}");
    }

    @Test
    public void shouldPassFromDateAsRestParameterInISOFormat() throws Exception {
        verify(restTemplate).exchange(urlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), variablesCaptor.capture());

        assertThat(variablesCaptor.getValue()).containsEntry("fromDate", "2017-01-01");
        assertThat(urlTemplate.getValue()).contains("fromDate={fromDate}");
    }

    @Test
    public void shouldPassToDateAsRestParameterInISOFormat() throws Exception {
        verify(restTemplate).exchange(urlTemplate.capture(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), variablesCaptor.capture());

        assertThat(variablesCaptor.getValue()).containsEntry("toDate", "2017-07-01");
        assertThat(urlTemplate.getValue()).contains("toDate={toDate}");
    }

    @Test(expected = EarningsServiceNoUniqueMatch.class)
    public void forbiddenShouldBeMappedToNoMatch() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), Matchers.<Class<IncomeRecord>>any(), Matchers.<Map<String, String>>any()))
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

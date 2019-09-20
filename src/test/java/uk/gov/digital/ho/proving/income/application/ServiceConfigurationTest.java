package uk.gov.digital.ho.proving.income.application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceConfigurationTest {

    private static final String ANY_STRING = "";
    private static final int ANY_INT = 0;
    private static final ServiceConfiguration SPRING_CONFIG_WITH_RETRIES = new ServiceConfiguration(ANY_STRING, ANY_INT, ANY_INT, 5, ANY_INT, 5, ANY_INT);

    @Mock
    private RestTemplateBuilder mockRestTemplateBuilder;

    @Mock
    private RestTemplate mockRestTemplate;

    @Before
    public void setUp() {
        when(mockRestTemplateBuilder.setReadTimeout(anyInt())).thenReturn(mockRestTemplateBuilder);
        when(mockRestTemplateBuilder.setConnectTimeout(anyInt())).thenReturn(mockRestTemplateBuilder);

        when(mockRestTemplateBuilder.build()).thenReturn(mockRestTemplate);
    }

    @Test
    public void shouldSetTimeoutsOnRestTemplate() {
        // given
        int readTimeout = 1234;
        int connectTimeout = 4321;
        ServiceConfiguration springConfig = new ServiceConfiguration(null, readTimeout, connectTimeout, ANY_INT, ANY_INT, ANY_INT, ANY_INT);

        // when
        RestTemplate restTemplate = springConfig.createRestTemplate(mockRestTemplateBuilder);

        // then
        verify(mockRestTemplateBuilder).setReadTimeout(readTimeout);
        verify(mockRestTemplateBuilder).setConnectTimeout(connectTimeout);

        assertThat(restTemplate).isEqualTo(mockRestTemplate);
    }

    @Test
    public void hmrcRetryTemplate_givenBackOffDelay_setOnTemplate() {
        int expectedBackOffDelay = 9;
        ServiceConfiguration springConfig = new ServiceConfiguration(ANY_STRING, ANY_INT, ANY_INT, ANY_INT, expectedBackOffDelay, ANY_INT, ANY_INT);

        FixedBackOffPolicy hmrcBackOffPolicy = (FixedBackOffPolicy) ReflectionTestUtils.getField(springConfig.hmrcRetryTemplate(), "backOffPolicy");

        assertThat(hmrcBackOffPolicy.getBackOffPeriod()).isEqualTo(expectedBackOffDelay);
    }

    @Test
    public void hmrcRetryTemplate_givenMaxAttempts_setOnTemplate() {
        int expectedRetryAttempts = 23;
        ServiceConfiguration springConfig = new ServiceConfiguration(ANY_STRING, ANY_INT, ANY_INT, expectedRetryAttempts, ANY_INT, ANY_INT, ANY_INT);

        SimpleRetryPolicy hmrcRetryPolicy = (SimpleRetryPolicy) ReflectionTestUtils.getField(springConfig.hmrcRetryTemplate(), "retryPolicy");

        assertThat(hmrcRetryPolicy.getMaxAttempts()).isEqualTo(expectedRetryAttempts);
    }

    @Test
    public void hmrcRetryTemplate_httpServerException_shouldRetry() {
        SimpleRetryPolicy hmrcRetryPolicy = (SimpleRetryPolicy) ReflectionTestUtils.getField(SPRING_CONFIG_WITH_RETRIES.hmrcRetryTemplate(), "retryPolicy");
        assertThat(shouldRetryException(hmrcRetryPolicy, new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))).isTrue();
    }

    @Test
    public void hmrcRetryTemplate_httpClientException_shouldNotRetry() {
        SimpleRetryPolicy hmrcRetryPolicy = (SimpleRetryPolicy) ReflectionTestUtils.getField(SPRING_CONFIG_WITH_RETRIES.hmrcRetryTemplate(), "retryPolicy");
        assertThat(shouldRetryException(hmrcRetryPolicy, new HttpClientErrorException(HttpStatus.NOT_FOUND))).isFalse();
    }

    @Test
    public void hmrcRetryTemplate_earningsServiceNoUniqueMatchException_shouldNotRetry() {
        SimpleRetryPolicy hmrcRetryPolicy = (SimpleRetryPolicy) ReflectionTestUtils.getField(SPRING_CONFIG_WITH_RETRIES.hmrcRetryTemplate(), "retryPolicy");
        assertThat(shouldRetryException(hmrcRetryPolicy, new ApplicationExceptions.EarningsServiceNoUniqueMatchException("any nino"))).isFalse();
    }

    @Test
    public void auditRetryTemplate_givenRetryAttempts_setOnTemplate() {
        int expectedRetryAttempts = 20;
        ServiceConfiguration springConfig = new ServiceConfiguration(ANY_STRING, ANY_INT, ANY_INT, ANY_INT, ANY_INT, expectedRetryAttempts, ANY_INT);

        SimpleRetryPolicy auditRetryPolicy = (SimpleRetryPolicy) ReflectionTestUtils.getField(springConfig.auditRetryTemplate(), "retryPolicy");

        assertThat(auditRetryPolicy.getMaxAttempts()).isEqualTo(expectedRetryAttempts);
    }

    @Test
    public void auditRetryTemplate_givenBackoffDelay_setOnTemplate() {
        int expectedBackOffDelay = 2;
        ServiceConfiguration springConfig = new ServiceConfiguration(ANY_STRING, ANY_INT, ANY_INT, ANY_INT, ANY_INT, ANY_INT, expectedBackOffDelay);

        FixedBackOffPolicy auditBackOffPolicy = (FixedBackOffPolicy) ReflectionTestUtils.getField(springConfig.auditRetryTemplate(), "backOffPolicy");
        assertThat(auditBackOffPolicy.getBackOffPeriod()).isEqualTo(expectedBackOffDelay);
    }

    @Test
    public void auditRetryTemplate_restClientException_shouldRetry() {
        SimpleRetryPolicy auditRetryPolicy = (SimpleRetryPolicy) ReflectionTestUtils.getField(SPRING_CONFIG_WITH_RETRIES.auditRetryTemplate(), "retryPolicy");
        assertThat(shouldRetryException(auditRetryPolicy, new RestClientException("any message"))).isTrue();
    }

    @Test
    public void auditRetryTemplate_notRestClientException_shouldNotRetry() {
        SimpleRetryPolicy auditRetryPolicy = (SimpleRetryPolicy) ReflectionTestUtils.getField(SPRING_CONFIG_WITH_RETRIES.auditRetryTemplate(), "retryPolicy");

        Exception notARestClientException = new NullPointerException();
        assertThat(shouldRetryException(auditRetryPolicy, notARestClientException)).isFalse();

    }
    private Boolean shouldRetryException(SimpleRetryPolicy retryPolicy, Exception exception) {
        return ReflectionTestUtils.invokeMethod(retryPolicy, "retryForException", exception);
    }
}

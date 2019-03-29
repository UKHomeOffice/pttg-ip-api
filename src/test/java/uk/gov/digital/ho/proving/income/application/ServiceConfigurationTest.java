package uk.gov.digital.ho.proving.income.application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceConfigurationTest {

    @Mock
    private RestTemplateBuilder mockRestTemplateBuilder;

    @Mock
    private RestTemplate mockRestTemplate;

    private TimeoutProperties timeoutProperties = new TimeoutProperties();

    @Before
    public void setUp() {
        when(mockRestTemplateBuilder.setReadTimeout(anyInt())).thenReturn(mockRestTemplateBuilder);
        when(mockRestTemplateBuilder.setConnectTimeout(anyInt())).thenReturn(mockRestTemplateBuilder);
        when(mockRestTemplateBuilder.build()).thenReturn(mockRestTemplate);

        timeoutProperties.setHmrcService(new TimeoutProperties.HmrcService());
        timeoutProperties.setAuditService(new TimeoutProperties.AuditService());
    }

    @Test
    public void shouldSetTimeoutsOnHmrcRestTemplate() {
        // given
        int readTimeout = 1234;
        int connectTimeout = 4321;
        timeoutProperties.getHmrcService().setReadMs(readTimeout);
        timeoutProperties.getHmrcService().setConnectMs(connectTimeout);
        ServiceConfiguration springConfig = new ServiceConfiguration(null, timeoutProperties);
        // when
        RestTemplate restTemplate = springConfig.createHmrcRestTemplate(mockRestTemplateBuilder);
        // then
        verify(mockRestTemplateBuilder).setReadTimeout(readTimeout);
        verify(mockRestTemplateBuilder).setConnectTimeout(connectTimeout);

        assertThat(restTemplate).isEqualTo(mockRestTemplate);
    }

    @Test
    public void shouldSetTimeoutsOnAuditRestTemplate() {
        // given
        int readTimeout = 2468;
        int connectTimeout = 1357;
        timeoutProperties.getAuditService().setReadMs(readTimeout);
        timeoutProperties.getAuditService().setConnectMs(connectTimeout);
        ServiceConfiguration springConfig = new ServiceConfiguration(null, timeoutProperties);
        // when
        RestTemplate restTemplate = springConfig.createAuditRestTemplate(mockRestTemplateBuilder);
        // then
        verify(mockRestTemplateBuilder).setReadTimeout(readTimeout);
        verify(mockRestTemplateBuilder).setConnectTimeout(connectTimeout);

        assertThat(restTemplate).isEqualTo(mockRestTemplate);
    }
}

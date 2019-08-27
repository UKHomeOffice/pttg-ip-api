package uk.gov.digital.ho.proving.income.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.digital.ho.proving.income.api.RequestData.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RequestData.class})
@TestPropertySource(properties = {"auditing.deployment.name=some-name",
                                    "auditing.deployment.namespace=some-namespace",
                                    "audit.service.auth=some-auth",
                                    "hmrc.service.auth=some-auth"})
public class RequestDataTest {

    @Autowired
    private RequestData requestData;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldConfigureMDC_preHandle() {
        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(MDC.get(SESSION_ID_HEADER)).isEqualTo("unknown");
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isEqualTo("unknown");
    }

    @Test
    public void shouldResetMDC_postHandle() {
        requestData.preHandle(mockRequest, mockResponse, null);
        requestData.postHandle(null, null, null, null);

        assertThat(MDC.get(SESSION_ID_HEADER)).isNull();
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
    }

    @Test
    public void shouldExposeDeploymentName() {
        assertThat(requestData.deploymentName()).isEqualTo("some-name");
    }

    @Test
    public void shouldExposeDeploymentNameSpace() {
        assertThat(requestData.deploymentNamespace()).isEqualTo("some-namespace");
    }

    @Test
    public void shouldExposeAuditBasicAuthHeaderValue() {
        assertThat(requestData.auditBasicAuth()).isEqualTo("Basic c29tZS1hdXRo");
    }

    @Test
    public void shouldExposeHmrcBasicAuthHeaderValue() {
        assertThat(requestData.hmrcBasicAuth()).isEqualTo("Basic c29tZS1hdXRo");
    }

    @Test
    public void shouldExposeSessionId() {
        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(requestData.sessionId()).isEqualTo("unknown");
    }

    @Test
    public void shouldExposeCorrelationId() {
        when(mockRequest.getHeader(CORRELATION_ID_HEADER)).thenReturn("some correlation id");

        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(requestData.correlationId()).isEqualTo("some correlation id");
    }

    @Test
    public void shouldExposeUserId() {
        when(mockRequest.getHeader(USER_ID_HEADER)).thenReturn("some user id");

        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(requestData.userId()).isEqualTo("some user id");
    }

    @Test
    public void isASmokeTest_smokeTestUser_returnTrue() {
        when(mockRequest.getHeader(USER_ID_HEADER)).thenReturn("smoke-tests");

        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(requestData.isASmokeTest()).isTrue();
    }

    @Test
    public void isASmokeTest_notSmokeTestUser_returnFalse() {
        when(mockRequest.getHeader(USER_ID_HEADER)).thenReturn("some not a smoke test user");

        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(requestData.isASmokeTest()).isFalse();

    }
}

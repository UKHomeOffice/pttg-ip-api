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

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
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

    @Test
    public void preHandle_noComponentTraceHeader_create() {
        when(mockRequest.getHeader("x-component-trace")).thenReturn(null);

        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(requestData.componentTrace()).isEqualTo("pttg-ip-api");
    }

    @Test
    public void preHandle_componentTraceHeader_append() {
        when(mockRequest.getHeader("x-component-trace")).thenReturn("some-component");

        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(requestData.componentTrace()).isEqualTo("some-component,pttg-ip-api");
    }

    @Test
    public void preHandle_componentTraceHeaderMultipleComponents_append() {
        when(mockRequest.getHeader("x-component-trace")).thenReturn("some-component,some-other-component");

        requestData.preHandle(mockRequest, mockResponse, null);

        assertThat(requestData.componentTrace()).isEqualTo("some-component,some-other-component,pttg-ip-api");
    }

    @Test
    public void componentTrace_someComponentTrace_update() {
        String expectedComponentTrace = "some-component,some-other-component";
        requestData.componentTrace(asList("some-component", "some-other-component"));

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void componentTrace_multipleCalls_lastWins() {
        List<String> otherComponentTrace = singletonList("some-unexpected-component");
        List<String> winningComponentTrace = asList("some-component", "some-other-component");
        String expectedComponentTrace = "some-component,some-other-component";

        requestData.componentTrace(otherComponentTrace);
        requestData.componentTrace(winningComponentTrace);

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void componentTrace_null_doNotUpdate() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        requestData.componentTrace(null);
        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }
    @Test
    public void componentTrace_emptyList_doNotUpdate() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        requestData.componentTrace(emptyList());
        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }
}

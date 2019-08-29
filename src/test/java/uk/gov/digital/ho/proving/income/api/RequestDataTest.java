package uk.gov.digital.ho.proving.income.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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

    @After
    public void tearDown() {
        MDC.clear();
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
    public void updateComponentTrace_responseEntityWithHeaders_setComponentTrace() {
        String expectedComponentTrace = "some-component,some-other-component";
        HttpHeaders headers = new HttpHeaders();
        headers.add(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        ResponseEntity responseWithTraceHeader = new ResponseEntity(headers, HttpStatus.OK);
        requestData.updateComponentTrace(responseWithTraceHeader);

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void updateComponentTrace_responseEntityNoHeaders_doNotSetComponentTrace() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        ResponseEntity responseWithoutTraceHeader = new ResponseEntity(HttpStatus.OK);
        requestData.updateComponentTrace(responseWithoutTraceHeader);

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void updateComponentTrace_multipleResponseEntityHeaders_lastWins() {
        String otherComponentTrace = "some-unexpected-component";
        String winningComponentTrace = "some-component,some-other-component";

        HttpHeaders firstHeaders = new HttpHeaders();
        firstHeaders.add(COMPONENT_TRACE_HEADER, otherComponentTrace);
        HttpHeaders secondHeaders = new HttpHeaders();
        secondHeaders.add(COMPONENT_TRACE_HEADER, winningComponentTrace);

        ResponseEntity firstResponse = new ResponseEntity(firstHeaders, HttpStatus.OK);
        ResponseEntity secondResponse = new ResponseEntity(secondHeaders, HttpStatus.OK);

        requestData.updateComponentTrace(firstResponse);
        requestData.updateComponentTrace(secondResponse);

        assertThat(requestData.componentTrace()).isEqualTo(winningComponentTrace);
    }

    @Test
    public void updateComponentTrace_nullComponentEntityHeader_doNotUpdate() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        HttpHeaders headers = new HttpHeaders();
        headers.add(COMPONENT_TRACE_HEADER, null);
        requestData.updateComponentTrace(new ResponseEntity(headers, HttpStatus.OK));

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void updateComponentTrace_emptyComponentEntityHeader_doNotUpdate() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        HttpHeaders headers = new HttpHeaders();
        headers.add(COMPONENT_TRACE_HEADER, "");
        requestData.updateComponentTrace(new ResponseEntity(headers, HttpStatus.OK));

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void updateComponentTrace_httpExceptionWithTraceHeader_setComponentTrace() {
        String expectedComponentTrace = "some-component,some-other-component";
        HttpHeaders headers = new HttpHeaders();
        headers.add(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        HttpStatusCodeException exceptionWithTraceHeader = new HttpClientErrorException(HttpStatus.NOT_FOUND, "any status text", headers, null, null);
        requestData.updateComponentTrace(exceptionWithTraceHeader);

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void updateComponentTrace_httpExceptionWithOtherHeaders_doNotSetComponentTrace() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        HttpHeaders headers = new HttpHeaders();
        headers.add("any other header key", "any other header value");
        HttpStatusCodeException exceptionWithTraceHeader = new HttpClientErrorException(HttpStatus.NOT_FOUND, "any status text", headers, null, null);
        requestData.updateComponentTrace(exceptionWithTraceHeader);

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void updateComponentTrace_httpExceptionNoHeaders_doNotSetComponentTrace() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        HttpStatusCodeException exceptionWithoutTraceHeader = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        requestData.updateComponentTrace(exceptionWithoutTraceHeader);

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void updateComponentTrace_multipleExceptionHeaders_lastWins() {
        String otherComponentTrace = "some-unexpected-component";
        String winningComponentTrace = "some-component,some-other-component";

        HttpHeaders firstHeaders = new HttpHeaders();
        firstHeaders.add(COMPONENT_TRACE_HEADER, otherComponentTrace);
        HttpHeaders secondHeaders = new HttpHeaders();
        secondHeaders.add(COMPONENT_TRACE_HEADER, winningComponentTrace);

        HttpStatusCodeException firstException = new HttpClientErrorException(HttpStatus.NOT_FOUND, "any status text", firstHeaders, null, null);
        HttpStatusCodeException secondException = new HttpClientErrorException(HttpStatus.NOT_FOUND, "any status text", secondHeaders, null, null);

        requestData.updateComponentTrace(firstException);
        requestData.updateComponentTrace(secondException);

        assertThat(requestData.componentTrace()).isEqualTo(winningComponentTrace);
    }

    @Test
    public void updateComponentTrace_nullExceptionHeaders_doNotUpdate() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);


        HttpHeaders headers = new HttpHeaders();
        headers.add(COMPONENT_TRACE_HEADER, null);
        requestData.updateComponentTrace(new HttpClientErrorException(HttpStatus.NOT_FOUND, "any status text", headers, null, null));

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }

    @Test
    public void updateComponentTrace_emptyComponentExceptionHeader_doNotUpdate() {
        String expectedComponentTrace = "some-component";
        MDC.put(COMPONENT_TRACE_HEADER, expectedComponentTrace);

        HttpHeaders headers = new HttpHeaders();
        headers.add(COMPONENT_TRACE_HEADER, "");
        requestData.updateComponentTrace(new HttpClientErrorException(HttpStatus.NOT_FOUND, "any status text", headers, null, null));

        assertThat(requestData.componentTrace()).isEqualTo(expectedComponentTrace);
    }
}

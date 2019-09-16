package uk.gov.digital.ho.proving.income.api;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestData implements HandlerInterceptor {

    public static final String SESSION_ID_HEADER = "x-session-id";
    public static final String CORRELATION_ID_HEADER = "x-correlation-id";
    public static final String USER_ID_HEADER = "x-auth-userid";
    private static final String REQUEST_START_TIMESTAMP = "request-timestamp";
    public static final String REQUEST_DURATION_MS = "request_duration_ms";
    public static final String SMOKE_TESTS_USER_ID = "smoke-tests";

    public static final String COMPONENT_TRACE_HEADER = "x-component-trace";
    private static final String COMPONENT_NAME = "pttg-ip-api";

    @Value("${auditing.deployment.name}") private String deploymentName;
    @Value("${auditing.deployment.namespace}") private String deploymentNamespace;
    @Value("${hmrc.service.auth}") private String hmrcBasicAuth;
    @Value("${audit.service.auth}") private String auditBasicAuth;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        MDC.clear();
        MDC.put(SESSION_ID_HEADER, initialiseSessionId(request));
        MDC.put(CORRELATION_ID_HEADER, initialiseCorrelationId(request));
        MDC.put(USER_ID_HEADER, initialiseUserName(request));
        MDC.put("userHost", request.getRemoteHost());
        MDC.put(REQUEST_START_TIMESTAMP, initialiseRequestStart());
        MDC.put(COMPONENT_TRACE_HEADER, initialiseComponentTrace(request));

        return true;
    }

    private String initialiseSessionId(HttpServletRequest request) {
        String sessionId = WebUtils.getSessionId(request);
        return StringUtils.isNotBlank(sessionId) ? sessionId : "unknown";
    }

    private String initialiseCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        return StringUtils.isNotBlank(correlationId) ? correlationId : "unknown";
    }

    private String initialiseUserName(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID_HEADER);
        return StringUtils.isNotBlank(userId) ? userId : "anonymous";
    }

    private String initialiseRequestStart() {
        long requestStart = Instant.now().toEpochMilli();
        return Long.toString(requestStart);
    }

    private String initialiseComponentTrace(HttpServletRequest request) {
        String componentTrace = request.getHeader(COMPONENT_TRACE_HEADER);
        if (componentTrace == null) {
            return COMPONENT_NAME;
        }
        return componentTrace + "," + COMPONENT_NAME;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        MDC.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,   Exception ex) {

    }

    public long calculateRequestDuration() {
        long timeStamp = Instant.now().toEpochMilli();
        return timeStamp - Long.parseLong(MDC.get(REQUEST_START_TIMESTAMP));
    }

    public String deploymentName() {
        return deploymentName;
    }

    public String deploymentNamespace() {
        return deploymentNamespace;
    }

    public String hmrcBasicAuth() { return String.format("Basic %s", Base64.getEncoder().encodeToString(hmrcBasicAuth.getBytes())); }

    public String auditBasicAuth() { return String.format("Basic %s", Base64.getEncoder().encodeToString(auditBasicAuth.getBytes(Charset.forName("UTF-8")))); }

    public String sessionId() {
        return MDC.get(SESSION_ID_HEADER);
    }

    public String correlationId() {
        return MDC.get(CORRELATION_ID_HEADER);
    }

    public String userId() {
        return MDC.get(USER_ID_HEADER);
    }

    public boolean isASmokeTest() {
        return userId().equals(SMOKE_TESTS_USER_ID);
    }

    public String componentTrace() {
        return MDC.get(COMPONENT_TRACE_HEADER);
    }

    public void updateComponentTrace(ResponseEntity responseEntity) {
        List<String> components = responseEntity.getHeaders().get(COMPONENT_TRACE_HEADER);
        setComponentTrace(components);
    }

    public void updateComponentTrace(HttpStatusCodeException e) {
        if (e.getResponseHeaders() == null) {
            return;
        }

        List<String> components = e.getResponseHeaders().get(COMPONENT_TRACE_HEADER);
        setComponentTrace(components);
    }

    private List<String> removeEmptyEntries(List<String> components) {
        return components.stream()
                         .filter(StringUtils::isNotEmpty)
                         .collect(Collectors.toList());
    }

    private void setComponentTrace(List<String> components) {
        if (components == null) {
            return;
        }

        components = removeEmptyEntries(components);
        if (!components.isEmpty()) {
            MDC.put(COMPONENT_TRACE_HEADER, String.join(",", components));
        }
    }

    public void addComponentTraceHeader(HttpHeaders headers) {
        headers.add(COMPONENT_TRACE_HEADER, componentTrace());
    }
}

package uk.gov.digital.ho.proving.income.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Author Home Office Digital
 */
public class LoggingInterceptor implements HandlerInterceptor {
    private static final String USER_ID_HEADER = "x-auth-userid";
    private static final String CORRELATION_ID_HEADER = "x-correlation-id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        MDC.clear();
        MDC.put("userId", getUserName(request)); // or e.g. request.getRemoteUser()
        MDC.put("correlationId", getCorrelationId(request)); // or e.g. request.getRemoteUser()
        MDC.put("userHost", request.getRemoteHost());

        return true;
    }

    private String getCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        return StringUtils.isNotBlank(correlationId) ? correlationId : UUID.randomUUID().toString();
    }

    private String getUserName(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID_HEADER);
        return StringUtils.isNotBlank(userId) ? userId : "anonymous";
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        MDC.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}

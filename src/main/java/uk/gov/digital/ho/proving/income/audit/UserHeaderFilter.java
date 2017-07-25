package uk.gov.digital.ho.proving.income.audit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Slf4j
public class UserHeaderFilter implements Filter {
    public static final String USER_ID_HEADER = "x-auth-userid";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no init work required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader(USER_ID_HEADER);
        log.info("Found userId in header =  " + userId);
        try {
            // Setup MDC data:
            MDC.put(USER_ID_HEADER, StringUtils.isNotBlank(userId) ? userId : "anonymous");
            chain.doFilter(request, response);
        } finally {
            MDC.remove(USER_ID_HEADER);
        }
    }

    @Override
    public void destroy() {
        // nothing to do on destroy
    }
}

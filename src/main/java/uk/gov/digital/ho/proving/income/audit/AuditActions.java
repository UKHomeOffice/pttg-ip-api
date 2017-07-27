package uk.gov.digital.ho.proving.income.audit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static uk.gov.digital.ho.proving.income.logging.LoggingInterceptor.USER_ID_HEADER;

/**
 * @Author Home Office Digital
 */
public class AuditActions {

    public static UUID nextId() {
        return UUID.randomUUID();
    }

    public static AuditApplicationEvent auditEvent(AuditEventType type, UUID id, Map<String, Object> data) {

        if (data == null) data = new HashMap<String, Object>();

        data.put("eventId", id);

        return new AuditApplicationEvent(getPrincipal(), type.name(), data);
    }

    private static String getPrincipal() {
        if (StringUtils.isBlank(MDC.get(USER_ID_HEADER))) {
            return "anonymous";
        }
        return MDC.get(USER_ID_HEADER);
    }
}

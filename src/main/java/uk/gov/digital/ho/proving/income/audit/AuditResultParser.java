package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

@Component
public class AuditResultParser {

    private ObjectMapper objectMapper;

    public AuditResultParser(@Autowired ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AuditResult from(AuditRecord auditRecord) {
        String id = auditRecord.getId();
        AuditResultType resultType = getResultType(auditRecord.getDetail());
        LocalDate date = auditRecord.getDate().toLocalDate();
        String nino = auditRecord.getNino();
        return new AuditResult(id, date, nino, resultType);
    }

    private AuditResultType getResultType(JsonNode auditDetail) {
        try {
            if (objectMapper.treeToValue(auditDetail.at("/response/status/code"), String.class).equals("0009")) {
                return NOTFOUND;
            }
        } catch (JsonProcessingException e) {
            // swallow the exception, it doesn't automatically follow we have an error
        }

        try {
            JsonNode checks  = auditDetail.at("/response/categoryChecks");
            if (checks.size() > 0) {
                AuditResultType auditResultType = FAIL;
                for (JsonNode check : checks) {
                    if (objectMapper.treeToValue(check.get("passed"), Boolean.class)) {
                        auditResultType = PASS;
                    }
                }
                return auditResultType;
            }
        } catch (Exception e) {
            // swallow the exception, it doesn't automatically follow we have an error
        }

        return ERROR;
    }

}

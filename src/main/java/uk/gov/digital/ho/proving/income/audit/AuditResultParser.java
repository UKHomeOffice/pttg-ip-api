package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

@Component
class AuditResultParser {

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
        AuditResultType result = checkIfNotFound(auditDetail);
        if (result != ERROR) {
            return result;
        }

        return checkIfResult(auditDetail);
    }

    private AuditResultType checkIfNotFound(JsonNode auditDetail) {
        try {
            if (objectMapper.treeToValue(auditDetail.at("/response/status/code"), String.class).equals("0009")) {
                return NOTFOUND;
            }
        } catch (JsonProcessingException e) {}
        return ERROR;
    }

    private AuditResultType checkIfResult(JsonNode auditDetail) {
        try {
            JsonNode checks  = auditDetail.at("/response/categoryChecks");
            if (checks.size() == 0) {
                return ERROR;
            }
            return getCategoryCheckResult(checks);
        } catch (Exception e) {}
        return ERROR;

    }

    private AuditResultType getCategoryCheckResult(JsonNode checks) throws JsonProcessingException {
        AuditResultType auditResultType = FAIL;
        for (JsonNode check : checks) {
            if (objectMapper.treeToValue(check.get("passed"), Boolean.class)) {
                auditResultType = PASS;
                break;
            }
        }
        return auditResultType;
    }

    String getResultNino(JsonNode auditDetail) {
        try {
            JsonNode checks = auditDetail.at("/response/categoryChecks");
            if (checks.size() == 0) {
                return "";
            }
            return getNinoFromCategoryChecks(checks);
        } catch (Exception e) {}
        return "";
    }

    private String getNinoFromCategoryChecks(JsonNode checks) throws JsonProcessingException {
        for (JsonNode check : checks) {
            JsonNode individuals = check.at("/individuals");
            for (JsonNode individual : individuals) {
                String nino = objectMapper.treeToValue(individual.get("nino"), String.class);
                if (nino != null && !nino.isEmpty()) {
                    return nino;
                }

            }
        }
        return "";
    }

}

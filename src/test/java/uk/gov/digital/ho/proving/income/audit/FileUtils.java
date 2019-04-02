package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Configuration
public
class FileUtils {

    private ObjectMapper objectMapper;

    public FileUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Value("classpath:json/AuditRecordRequestTemplate.json")
    private Resource auditRecordRequestTemplate;
    @Value("classpath:json/AuditRecordResponseTemplate.json")
    private Resource auditRecordResponseTemplate;
    @Value("classpath:json/AuditRecordResponseNotFoundTemplate.json")
    private Resource auditRecordResourceNotFoundTemplate;

    String loadJsonResource(Resource resource) {

        try {
            return new String(Files.readAllBytes(Paths.get(resource.getURI())));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to read from resource " + resource);
        }
    }

    public String buildRequest(String correlationId, LocalDateTime dateTime, String nino) {
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        return buildRequest(correlationId, formattedDateTime, nino);
    }

    public String buildRequest(String correlationId, String dateTime, String nino) {
        String requestTemplate = loadJsonResource(auditRecordRequestTemplate);
        requestTemplate = requestTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        requestTemplate = requestTemplate.replaceAll("\\$\\{date-time}", dateTime);
        requestTemplate = requestTemplate.replaceAll("\\$\\{nino}", nino);
        return requestTemplate;
    }

    public String buildResponse(String correlationId, LocalDateTime dateTime, String nino, String pass) {
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        return buildResponse(correlationId, formattedDateTime, nino, pass);
    }

    public String buildResponse(String correlationId, String dateTime, String nino, String pass) {
        String responseTemplate = loadJsonResource(auditRecordResponseTemplate);
        responseTemplate = responseTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        responseTemplate = responseTemplate.replaceAll("\\$\\{date-time}", dateTime);
        responseTemplate = responseTemplate.replaceAll("\\$\\{nino}", nino);
        responseTemplate = responseTemplate.replaceAll("\\$\\{pass}", pass);
        return responseTemplate;
    }

    public String buildResponseNotFound(String correlationId, LocalDateTime dateTime) {
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        return buildResponseNotFound(correlationId, formattedDateTime);
    }

    public String buildResponseNotFound(String correlationId, String dateTime) {
        String responseTemplate = loadJsonResource(auditRecordResourceNotFoundTemplate);
        responseTemplate = responseTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        responseTemplate = responseTemplate.replaceAll("\\$\\{date-time}", dateTime);
        return responseTemplate;
    }

    AuditRecord buildRequestRecord(String correlationId, String dateTime, String nino) {
        String requestTemplate = loadJsonResource(auditRecordRequestTemplate);
        requestTemplate = requestTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        requestTemplate = requestTemplate.replaceAll("\\$\\{date-time}", dateTime);
        requestTemplate = requestTemplate.replaceAll("\\$\\{nino}", nino);
        return readAuditRecord(requestTemplate);
    }

    AuditRecord buildResponseRecord(String correlationId, String dateTime, String nino, String pass) {
        String responseTemplate = loadJsonResource(auditRecordResponseTemplate);
        responseTemplate = responseTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        responseTemplate = responseTemplate.replaceAll("\\$\\{date-time}", dateTime);
        responseTemplate = responseTemplate.replaceAll("\\$\\{nino}", nino);
        responseTemplate = responseTemplate.replaceAll("\\$\\{pass}", pass);
        return readAuditRecord(responseTemplate);
    }

    AuditRecord buildResponseNotFoundRecord(String correlationId, String dateTime) {
        String responseTemplate = loadJsonResource(auditRecordResourceNotFoundTemplate);
        responseTemplate = responseTemplate.replaceAll("\\$\\{correlation-id}", correlationId);
        responseTemplate = responseTemplate.replaceAll("\\$\\{date-time}", dateTime);
        return readAuditRecord(responseTemplate);
    }

    private AuditRecord readAuditRecord(String content) {
        try {
            return objectMapper.readValue(content, AuditRecord.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to parse json into AuditRecord: " + content);
        }
    }

}

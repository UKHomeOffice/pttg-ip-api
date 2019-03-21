package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = {
    ObjectMapper.class,
    FileUtils.class,
    AuditResultParser.class
})
public class AuditResultParserTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private AuditResultParser auditResultParser;

    @Value("classpath:json/AuditDetailWithNino.json")
    private Resource auditDetailWithNino;
    @Value("classpath:json/AuditDetailWithoutNino.json")
    private Resource auditDetailWithoutNino;

    private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Test
    public void from_anyAuditRecord_fillsStandardFields() throws IOException {
        String auditRecordRequestString = fileUtils.buildRequest("any_corr_id", LocalDateTime.now().format(DATE_FORMATTER), "any_nino");
        AuditRecord record = objectMapper.readValue(auditRecordRequestString, AuditRecord.class);

        AuditResult auditResult = auditResultParser.from(record);
        assertThat(auditResult.correlationId()).isEqualTo("any_corr_id");
        assertThat(auditResult.date()).isEqualTo(LocalDate.now());
        assertThat(auditResult.nino()).isEqualTo("any_nino");
    }

    @Test
    public void from_missingNino_ninoIsNull() throws IOException {
        String auditRecordFailResponseString = fileUtils.buildResponse("any_corr_id", LocalDateTime.now().format(DATE_FORMATTER), "any_nino", "false");
        AuditRecord record = objectMapper.readValue(auditRecordFailResponseString, AuditRecord.class);

        AuditResult auditResult = auditResultParser.from(record);
        assertThat(auditResult.nino()).isEqualTo(null);
    }

    @Test
    public void from_requestNotResponse_errorResultType() throws IOException {
        String auditRecordRequestString = fileUtils.buildRequest("any_corr_id", LocalDateTime.now().format(DATE_FORMATTER), "any_nino");
        AuditRecord record = objectMapper.readValue(auditRecordRequestString, AuditRecord.class);

        assertThat(auditResultParser.from(record).resultType()).isEqualTo(AuditResultType.ERROR);
    }

    @Test
    public void from_pass_correctResultType() throws IOException {
        String auditRecordResponseString = fileUtils.buildResponse("any_corr_id", LocalDateTime.now().format(DATE_FORMATTER), "any_nino", "true");
        AuditRecord record = objectMapper.readValue(auditRecordResponseString, AuditRecord.class);

        assertThat(auditResultParser.from(record).resultType()).isEqualTo(AuditResultType.PASS);
    }

    @Test
    public void from_fail_correctResultType() throws IOException {
        String auditRecordResponseString = fileUtils.buildResponse("any_corr_id", LocalDateTime.now().format(DATE_FORMATTER), "any_nino", "false");
        AuditRecord record = objectMapper.readValue(auditRecordResponseString, AuditRecord.class);

        assertThat(auditResultParser.from(record).resultType()).isEqualTo(AuditResultType.FAIL);
    }

    @Test
    public void from_notFound_correctResultType() throws IOException {
        String auditRecordRequestNotFoundString = fileUtils.buildResponseNotFound("any_corr_id", LocalDateTime.now().format(DATE_FORMATTER));
        AuditRecord record = objectMapper.readValue(auditRecordRequestNotFoundString, AuditRecord.class);

        assertThat(auditResultParser.from(record).resultType()).isEqualTo(AuditResultType.NOTFOUND);
    }

    @Test
    public void getResultNino_ninoExists_ninoIsReturned() throws IOException {
        String auditDetail = fileUtils.loadJsonResource(auditDetailWithNino);
        JsonNode auditDetailNode = objectMapper.readValue(auditDetail, JsonNode.class);

        assertThat(auditResultParser.getResultNino(auditDetailNode)).isEqualTo("ANY_NINO");
    }

    @Test
    public void getResultNino_ninoDoesntExist_blankIsReturned() throws IOException {
        String auditDetail = fileUtils.loadJsonResource(auditDetailWithoutNino);
        JsonNode auditDetailNode = objectMapper.readValue(auditDetail, JsonNode.class);

        assertThat(auditResultParser.getResultNino(auditDetailNode)).isEqualTo("");
    }

}


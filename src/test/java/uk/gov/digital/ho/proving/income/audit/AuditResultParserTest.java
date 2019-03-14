package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
public class AuditResultParserTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0).createObjectMapper();
    private AuditResultParser auditResultParser = new AuditResultParser(objectMapper);

    @Value("classpath:json/AuditRecordRequest.json")
    private Resource auditRecordRequest;
    @Value("classpath:json/AuditRecordResponsePass.json")
    private Resource auditRecordPassResponse;
    @Value("classpath:json/AuditRecordResponseFail.json")
    private Resource auditRecordFailResponse;
    @Value("classpath:json/AuditRecordResponseNotFound.json")
    private Resource auditRecordNotFoundResponse;
    @Value("classpath:json/AuditDetailWithNino.json")
    private Resource auditDetailWithNino;
    @Value("classpath:json/AuditDetailWithoutNino.json")
    private Resource auditDetailWithoutNino;

    @Test
    public void from_anyAuditRecord_fillsStandardFields() throws IOException {
        String auditRecordRequestString = FileUtils.loadJsonResource(auditRecordRequest);
        AuditRecord record = objectMapper.readValue(auditRecordRequestString, AuditRecord.class);

        AuditResult auditResult = auditResultParser.from(record);
        assertThat(auditResult.correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(auditResult.date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(auditResult.nino()).isEqualTo("PJ151008C");
    }

    @Test
    public void from_missingNino_ninoIsNull() throws IOException {
        String auditRecordFailResponseString = FileUtils.loadJsonResource(auditRecordFailResponse);
        AuditRecord record = objectMapper.readValue(auditRecordFailResponseString, AuditRecord.class);

        AuditResult auditResult = auditResultParser.from(record);
        assertThat(auditResult.nino()).isEqualTo(null);
    }

    @Test
    public void from_requestNotResponse_errorResultType() throws IOException {
        String auditRecordRequestString = FileUtils.loadJsonResource(auditRecordRequest);
        AuditRecord record = objectMapper.readValue(auditRecordRequestString, AuditRecord.class);

        assertThat(auditResultParser.from(record).resultType()).isEqualTo(AuditResultType.ERROR);
    }

    @Test
    public void from_pass_correctResultType() throws IOException {
        String auditRecordResponseString = FileUtils.loadJsonResource(auditRecordPassResponse);
        AuditRecord record = objectMapper.readValue(auditRecordResponseString, AuditRecord.class);

        assertThat(auditResultParser.from(record).resultType()).isEqualTo(AuditResultType.PASS);
    }

    @Test
    public void from_fail_correctResultType() throws IOException {
        String auditRecordResponseString = FileUtils.loadJsonResource(auditRecordFailResponse);
        AuditRecord record = objectMapper.readValue(auditRecordResponseString, AuditRecord.class);

        assertThat(auditResultParser.from(record).resultType()).isEqualTo(AuditResultType.FAIL);
    }

    @Test
    public void from_notFound_correctResultType() throws IOException {
        String auditRecordRequestNotFoundString = FileUtils.loadJsonResource(auditRecordNotFoundResponse);
        AuditRecord record = objectMapper.readValue(auditRecordRequestNotFoundString, AuditRecord.class);

        assertThat(auditResultParser.from(record).resultType()).isEqualTo(AuditResultType.NOTFOUND);
    }

    @Test
    public void getResultNino_ninoExists_ninoIsReturned() throws IOException {
        String auditDetail = FileUtils.loadJsonResource(auditDetailWithNino);
        JsonNode auditDetailNode = objectMapper.readValue(auditDetail, JsonNode.class);

        assertThat(auditResultParser.getResultNino(auditDetailNode)).isEqualTo("ANY_NINO");
    }

    @Test
    public void getResultNino_ninoDoesntExist_blankIsReturned() throws IOException {
        String auditDetail = FileUtils.loadJsonResource(auditDetailWithoutNino);
        JsonNode auditDetailNode = objectMapper.readValue(auditDetail, JsonNode.class);

        assertThat(auditResultParser.getResultNino(auditDetailNode)).isEqualTo("");
    }

}

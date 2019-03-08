package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private Resource getAuditRecordFailResponse;
    @Value("classpath:json/AuditRecordResponseNotFound.json")
    private Resource auditRecordNotFoundResponse;

    @Test
    public void from_requestNotResponse_errorResultType() throws IOException {
        String auditRecordRequestString = loadJsonResource(auditRecordRequest);
        AuditRecord record = objectMapper.readValue(auditRecordRequestString, AuditRecord.class);

        assertThat(auditResultParser.from(record).getResultType()).isEqualTo(AuditResultType.ERROR);
    }

    @Test
    public void from_pass_correctResultType() throws IOException {
        String auditRecordResponseString = loadJsonResource(auditRecordPassResponse);
        AuditRecord record = objectMapper.readValue(auditRecordResponseString, AuditRecord.class);

        assertThat(auditResultParser.from(record).getResultType()).isEqualTo(AuditResultType.PASS);
    }

    @Test
    public void from_fail_correctResultType() throws IOException {
        String auditRecordResponseString = loadJsonResource(getAuditRecordFailResponse);
        AuditRecord record = objectMapper.readValue(auditRecordResponseString, AuditRecord.class);

        assertThat(auditResultParser.from(record).getResultType()).isEqualTo(AuditResultType.FAIL);
    }

    @Test
    public void from_notFound_correctResultType() throws IOException {
        String auditRecordRequestNotFoundString = loadJsonResource(auditRecordNotFoundResponse);
        AuditRecord record = objectMapper.readValue(auditRecordRequestNotFoundString, AuditRecord.class);

        assertThat(auditResultParser.from(record).getResultType()).isEqualTo(AuditResultType.NOTFOUND);
    }

    private String loadJsonResource(Resource resource) throws IOException {
        return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    }

}

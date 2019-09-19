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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class AuditRecordTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0, 0, 0, 0, 0).createObjectMapper();

    @Value("classpath:json/AuditRecordRequest.json")
    private Resource auditRecordRequest;
    @Value("classpath:json/AuditRecordResponsePass.json")
    private Resource auditRecordResponse;
    @Value("classpath:json/AuditRecordResponseNotFound.json")
    private Resource auditRecordNotFoundResponse;

    @Test
    public void deserialize_request() throws IOException {
        String auditRecordRequestString = loadJsonResource(auditRecordRequest);
        AuditRecord record = objectMapper.readValue(auditRecordRequestString, AuditRecord.class);

        assertThat(record).isNotNull();
        assertThat(record.getId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(record.getDate()).isEqualTo(LocalDateTime.of(2019, 2, 25, 11, 13, 25, 897000000));
        assertThat(record.getDetail()).isInstanceOf(JsonNode.class);
    }

    @Test
    public void deserialize_response() throws IOException {
        String auditRecordResponseString = loadJsonResource(auditRecordResponse);
        AuditRecord record = objectMapper.readValue(auditRecordResponseString, AuditRecord.class);

        assertThat(record).isNotNull();
        assertThat(record.getId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(record.getDate()).isEqualTo(LocalDateTime.of(2019, 2, 25, 11, 13, 28, 805000000));
        assertThat(record.getDetail()).isInstanceOf(JsonNode.class);
    }

    @Test
    public void deserialize_notFoundResponse() throws IOException {
        String auditRecordNotFoundResponseString = loadJsonResource(auditRecordNotFoundResponse);
        AuditRecord record = objectMapper.readValue(auditRecordNotFoundResponseString, AuditRecord.class);

        assertThat(record).isNotNull();
        assertThat(record.getId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(record.getDate()).isEqualTo(LocalDateTime.of(2019, 2, 25, 15, 10, 12, 489000000));
        assertThat(record.getDetail()).isInstanceOf(JsonNode.class);
    }

    private String loadJsonResource(Resource resource) throws IOException {
        return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    }

}

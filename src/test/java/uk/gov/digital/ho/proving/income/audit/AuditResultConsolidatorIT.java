package uk.gov.digital.ho.proving.income.audit;

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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    ObjectMapper.class,
    AuditResultParser.class,
    AuditResultTypeComparator.class,
    AuditResultConsolidator.class
})
public class AuditResultConsolidatorIT {

    @Autowired
    AuditResultConsolidator auditResultConsolidator;
    @Autowired
    ObjectMapper objectMapper;

    @Value("classpath:json/AuditRecordRequest.json")
    private Resource auditRecordRequest;
    @Value("classpath:json/AuditRecordResponsePass.json")
    private Resource auditRecordResponsePass;
    @Value("classpath:json/AuditRecordResponseFail.json")
    private Resource auditRecordResponseFail;
    @Value("classpath:json/AuditRecordResponseNotFound.json")
    private Resource auditRecordResponseNotFound;

    @Test
    public void byCorrelationId_requestOnly_allDetailsFilled() {
        List<AuditRecord> records = loadJson(auditRecordRequest);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(ERROR);
    }

    @Test
    public void byCorrelationId_requestAndPassResponse_allDetailsFilled() {
        List<AuditRecord> records = loadJson(auditRecordRequest, auditRecordResponsePass);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(PASS);
    }

    @Test
    public void byCorrelationId_requestAndFailResponse_allDetailsFilled() {
        List<AuditRecord> records = loadJson(auditRecordRequest, auditRecordResponseFail);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(FAIL);
    }

    @Test
    public void byCorrelationId_requestAndNotFoundResponse_allDetailsFilled() {
        List<AuditRecord> records = loadJson(auditRecordRequest, auditRecordResponseNotFound);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(NOTFOUND);
    }

    @Test
    public void byCorrelationId_passResponseOnly_allDetailsFilled() {
        List<AuditRecord> records = loadJson(auditRecordResponsePass);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(PASS);
    }

    @Test
    public void byCorrelationId_failResponseOnly_allDetailsFilled() {
        List<AuditRecord> records = loadJson(auditRecordResponseFail);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(FAIL);
    }

    @Test
    public void byCorrelationId_notFoundResponseOnly_allDetailsFilled() {
        List<AuditRecord> records = loadJson(auditRecordResponseNotFound);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo(""); // No nino is provided in the response
        assertThat(results.get(0).resultType()).isEqualTo(NOTFOUND);
    }

    private List<AuditRecord> loadJson(Resource... resourceFiles) {
        return Arrays.stream(resourceFiles)
            .map(resource -> FileUtils.loadJsonResource(resource))
            .map(this::readAuditRecord)
            .collect(Collectors.toList());
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

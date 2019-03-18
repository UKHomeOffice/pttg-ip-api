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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    ObjectMapper.class,
    AuditResultParser.class,
    AuditResultComparator.class,
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
    @Value("classpath:json/AuditRecordRequest2.json")
    private Resource auditRecordRequest2;
    @Value("classpath:json/AuditRecordRequest3.json")
    private Resource auditRecordRequest3;
    @Value("classpath:json/AuditRecordResponsePass.json")
    private Resource auditRecordResponsePass;
    @Value("classpath:json/AuditRecordResponsePass2.json")
    private Resource auditRecordResponsePass2;
    @Value("classpath:json/AuditRecordResponsePass3.json")
    private Resource auditRecordResponsePass3;
    @Value("classpath:json/AuditRecordResponseFail.json")
    private Resource auditRecordResponseFail;
    @Value("classpath:json/AuditRecordResponseNotFound.json")
    private Resource auditRecordResponseNotFound;

    /*
     * auditResultsByCorrelationId
     */
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

    @Test
    public void byCorrelationId_multipleRequestResponses_allDetailsFilled() {
        List<AuditRecord> records = loadJson(auditRecordRequest, auditRecordResponseNotFound);
        records.addAll(loadJson(auditRecordRequest2, auditRecordResponsePass2));

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        AuditResult expected1 = getExpectedAuditResult1();
        AuditResult expected2 = getExpectedAuditResult2();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results).containsExactlyInAnyOrder(expected1, expected2);
    }

    private AuditResult getExpectedAuditResult1() {
        return new AuditResult(
            "3743b803-bd87-4518-8cae-d5b3e0566396",
            LocalDate.of(2019, 2, 25),
            "PJ151008C",
            NOTFOUND
        );
    }

    private AuditResult getExpectedAuditResult2() {
        return new AuditResult(
            "5e6d002f-fd09-4347-a7da-2cd23346da49",
            LocalDate.of(2019, 2, 26),
            "PP151005D",
            PASS
        );
    }

    /*
     * auditResultsByNino
     */
    @Test
    public void byNino_noResults_empty() {
        List<AuditResult> results = new ArrayList<>();

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.auditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(0);
    }

    @Test
    public void byNino_singleResult_resultUsed() {
        List<AuditResult> results = Arrays.asList(new AuditResult("any_correlation_id", LocalDate.now(), "any_nino", PASS));
        AuditResultByNino expected = new AuditResultByNino("any_nino", Arrays.asList("any_correlation_id"), LocalDate.now(), PASS);

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.auditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(1);
        assertThat(resultsByNino.get(0)).isEqualTo(expected);
    }

    @Test
    public void byNino_multipleResults_bestResultUsed() {
        List<AuditResult> results =
            Arrays.asList(
                new AuditResult("any_correlation_id", LocalDate.now(), "any_nino", PASS),
                new AuditResult("any_correlation_id_2", LocalDate.now(), "any_nino", FAIL),
                new AuditResult("any_correlation_id_3", LocalDate.now(), "any_nino", NOTFOUND),
                new AuditResult("any_correlation_id_4", LocalDate.now(), "any_nino", ERROR)
            );
        List<String> expectedCorrelationIds = Arrays.asList(
            "any_correlation_id",
            "any_correlation_id_2",
            "any_correlation_id_3",
            "any_correlation_id_4"
        );
        AuditResultByNino expected = new AuditResultByNino("any_nino", expectedCorrelationIds, LocalDate.now(), PASS);

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.auditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(1);
        assertThat(resultsByNino.get(0)).isEqualTo(expected);
    }

    @Test
    public void byNino_multipleSameResults_mostRecentUsed() {
        List<AuditResult> results =
            Arrays.asList(
                new AuditResult("any_correlation_id", LocalDate.now(), "any_nino", PASS),
                new AuditResult("any_correlation_id_2", LocalDate.now().plusDays(1), "any_nino", PASS),
                new AuditResult("any_correlation_id_3", LocalDate.now().plusDays(2), "any_nino", PASS),
                new AuditResult("any_correlation_id_4", LocalDate.now().plusDays(1), "any_nino", PASS)
            );
        List<String> expectedCorrelationIds = Arrays.asList(
            "any_correlation_id",
            "any_correlation_id_2",
            "any_correlation_id_3",
            "any_correlation_id_4"
        );
        AuditResultByNino expected = new AuditResultByNino("any_nino", expectedCorrelationIds, LocalDate.now().plusDays(2), PASS);

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.auditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(1);
        assertThat(resultsByNino.get(0)).isEqualTo(expected);
    }

    @Test
    public void byNino_multipleNinos_allIncluded() {
        List<AuditResult> results =
            Arrays.asList(
                new AuditResult("any_correlation_id", LocalDate.now(), "any_nino", PASS),
                new AuditResult("any_correlation_id_2", LocalDate.now().plusDays(1), "any_nino_2", PASS)
            );
        List<AuditResultByNino> expected = Arrays.asList(
                new AuditResultByNino("any_nino", Arrays.asList("any_correlation_id"), LocalDate.now(), PASS),
                new AuditResultByNino("any_nino_2", Arrays.asList("any_correlation_id_2"), LocalDate.now().plusDays(1), PASS)
            );

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.auditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(2);
        assertThat(resultsByNino).contains(expected.get(0), expected.get(1));
    }

    @Test
    public void byNino_multipleNinosAndResults_correctResultsIncluded() {
        List<AuditResult> results =
            Arrays.asList(
                new AuditResult("any_correlation_id_2", LocalDate.now(), "any_nino", FAIL),
                new AuditResult("any_correlation_id", LocalDate.now(), "any_nino", PASS),
                new AuditResult("any_correlation_id_3", LocalDate.now(), "any_nino_2", PASS),
                new AuditResult("any_correlation_id_4", LocalDate.now().plusDays(1), "any_nino_2", PASS)
            );
        List<AuditResultByNino> expected = Arrays.asList(
                new AuditResultByNino("any_nino", Arrays.asList("any_correlation_id_2", "any_correlation_id"), LocalDate.now(), PASS),
                new AuditResultByNino("any_nino_2", Arrays.asList("any_correlation_id_3", "any_correlation_id_4"), LocalDate.now().plusDays(1), PASS)
            );

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.auditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(2);
        assertThat(resultsByNino).contains(expected.get(0), expected.get(1));
    }

    private List<AuditRecord> loadJson(Resource... resourceFiles) {
        return Arrays.stream(resourceFiles)
            .map(FileUtils::loadJsonResource)
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

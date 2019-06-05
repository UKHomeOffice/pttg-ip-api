package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
@ContextConfiguration(classes = FileUtils.class)
public class AuditResultConsolidatorIT {

    @Autowired
    AuditResultConsolidator auditResultConsolidator;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    FileUtils fileUtils;

    /*
     * auditResultsByCorrelationId
     */
    @Test
    public void byCorrelationId_requestOnly_allDetailsFilled() {
        AuditRecord auditRecordRequest = fileUtils.buildRequestRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C");
        List<AuditRecord> records = Arrays.asList(auditRecordRequest);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(ERROR);
    }

    @Test
    public void byCorrelationId_requestAndPassResponse_allDetailsFilled() {
        AuditRecord auditRecordRequest = fileUtils.buildRequestRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C");
        AuditRecord auditRecordResponse = fileUtils.buildResponseRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C", "true");
        List<AuditRecord> records = Arrays.asList(auditRecordRequest, auditRecordResponse);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(PASS);
    }

    @Test
    public void byCorrelationId_requestAndFailResponse_allDetailsFilled() {
        AuditRecord auditRecordRequest = fileUtils.buildRequestRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C");
        AuditRecord auditRecordResponse = fileUtils.buildResponseRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C", "false");
        List<AuditRecord> records = Arrays.asList(auditRecordRequest, auditRecordResponse);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(FAIL);
    }

    @Test
    public void byCorrelationId_requestAndNotFoundResponse_allDetailsFilled() {
        AuditRecord auditRecordRequest = fileUtils.buildRequestRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C");
        AuditRecord auditRecordResponse = fileUtils.buildResponseNotFoundRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003");
        List<AuditRecord> records = Arrays.asList(auditRecordRequest, auditRecordResponse);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(NOTFOUND);
    }

    @Test
    public void byCorrelationId_passResponseOnly_allDetailsFilled() {
        AuditRecord auditRecordResponse = fileUtils.buildResponseRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C", "true");
        List<AuditRecord> records = Arrays.asList(auditRecordResponse);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(PASS);
    }

    @Test
    public void byCorrelationId_failResponseOnly_allDetailsFilled() {
        AuditRecord auditRecordResponse = fileUtils.buildResponseRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C", "false");
        List<AuditRecord> records = Arrays.asList(auditRecordResponse);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo("PJ151008C");
        assertThat(results.get(0).resultType()).isEqualTo(FAIL);
    }

    @Test
    public void byCorrelationId_notFoundResponseOnly_allDetailsFilled() {
        AuditRecord auditRecordResponse = fileUtils.buildResponseNotFoundRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003");
        List<AuditRecord> records = Arrays.asList(auditRecordResponse);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).correlationId()).isEqualTo("3743b803-bd87-4518-8cae-d5b3e0566396");
        assertThat(results.get(0).date()).isEqualTo(LocalDate.of(2019, 2, 25));
        assertThat(results.get(0).nino()).isEqualTo(""); // No nino is provided in the response
        assertThat(results.get(0).resultType()).isEqualTo(NOTFOUND);
    }

    @Test
    public void byCorrelationId_multipleRequestResponses_allDetailsFilled() {
        AuditRecord auditRecord1Request = fileUtils.buildRequestRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003", "PJ151008C");
        AuditRecord auditRecord1Response = fileUtils.buildResponseNotFoundRecord("3743b803-bd87-4518-8cae-d5b3e0566396", "2019-02-25 12:01:02.003");
        AuditRecord auditRecord2Request = fileUtils.buildRequestRecord("5e6d002f-fd09-4347-a7da-2cd23346da49", "2019-02-26 12:01:02.003", "PP151005D");
        AuditRecord auditRecord2Response = fileUtils.buildResponseRecord("5e6d002f-fd09-4347-a7da-2cd23346da49", "2019-02-26 12:01:02.003", "PP151005D", "true");
        List<AuditRecord> records = Arrays.asList(auditRecord1Request, auditRecord1Response, auditRecord2Request, auditRecord2Response);

        List<AuditResult> results = auditResultConsolidator.auditResultsByCorrelationId(records);

        AuditResult expected1 = getExpectedAuditResult1();
        AuditResult expected2 = getExpectedAuditResult2();

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

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.consolidatedAuditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(0);
    }

    @Test
    public void byNino_singleResult_resultUsed() {
        List<AuditResult> results = Arrays.asList(new AuditResult("any_correlation_id", LocalDate.now(), "any_nino", PASS));
        AuditResultByNino expected = new AuditResultByNino("any_nino", Arrays.asList("any_correlation_id"), LocalDate.now(), PASS);

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.consolidatedAuditResultsByNino(results);

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

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.consolidatedAuditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(1);
        assertThat(resultsByNino.get(0)).isEqualTo(expected);
    }

    @Test
    public void byNino_multipleSameResults_oldestUsed() {
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
        AuditResultByNino expected = new AuditResultByNino("any_nino", expectedCorrelationIds, LocalDate.now(), PASS);

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.consolidatedAuditResultsByNino(results);

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

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.consolidatedAuditResultsByNino(results);

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
                new AuditResultByNino("any_nino_2", Arrays.asList("any_correlation_id_3", "any_correlation_id_4"), LocalDate.now(), PASS)
            );

        List<AuditResultByNino> resultsByNino = auditResultConsolidator.consolidatedAuditResultsByNino(results);

        assertThat(resultsByNino.size()).isEqualTo(2);
        assertThat(resultsByNino).contains(expected.get(0), expected.get(1));
    }

    /*
     * getAuditResult
     */
    @Test
    public void getAuditResult_singleRequest_expectedAuditResultERROR() {
        AuditRecord request = fileUtils.buildRequestRecord("some correlation id", "2019-02-25 12:01:02.003", "some nino");

        AuditResult auditResult = auditResultConsolidator.getAuditResult(Collections.singletonList(request));
        assertThat(auditResult).isEqualTo(new AuditResult("some correlation id", LocalDate.parse("2019-02-25"), "some nino", ERROR));
    }

    @Test
    public void getAuditResult_singlePassResponse_expectedAuditResultPASS() {
        AuditRecord passResponse = fileUtils.buildResponseRecord("some correlation id", "2019-02-25 12:01:02.003", "some nino", "true");

        AuditResult auditResult = auditResultConsolidator.getAuditResult(Collections.singletonList(passResponse));
        assertThat(auditResult).isEqualTo(new AuditResult("some correlation id", LocalDate.parse("2019-02-25"), "some nino", PASS));
    }

    @Test
    public void getAuditResult_singleFailResponse_expectedAuditResultFAIL() {
        AuditRecord failResponse = fileUtils.buildResponseRecord("some correlation id", "2019-02-25 12:01:02.003", "some nino", "false");

        AuditResult auditResult = auditResultConsolidator.getAuditResult(Collections.singletonList(failResponse));
        assertThat(auditResult).isEqualTo(new AuditResult("some correlation id", LocalDate.parse("2019-02-25"), "some nino", FAIL));
    }

    @Test
    public void getAuditResult_singleNotFoundResponse_expectedAuditResultNOTFOUND() {
        AuditRecord notFoundResponse = fileUtils.buildResponseNotFoundRecord("some correlation id", "2019-02-25 12:01:02.003");

        AuditResult auditResult = auditResultConsolidator.getAuditResult(Collections.singletonList(notFoundResponse));
        assertThat(auditResult).isEqualTo(new AuditResult("some correlation id", LocalDate.parse("2019-02-25"), "", NOTFOUND));
    }

    @Test
    public void getAuditResult_requestAndPassResponse_expectedAuditResultPASS() {
        AuditRecord request = fileUtils.buildRequestRecord("some correlation id", "2019-02-25 12:01:02.003", "some nino");
        AuditRecord passResponse = fileUtils.buildResponseRecord("some correlation id", "2019-02-25 12:01:03.000", "some nino", "true");

        AuditResult auditResult = auditResultConsolidator.getAuditResult(Arrays.asList(request, passResponse));

        assertThat(auditResult).isEqualTo(new AuditResult("some correlation id", LocalDate.parse("2019-02-25"), "some nino", PASS));
    }

    @Test
    public void getAuditResult_requestAndFailResponse_expectedAuditResultFAIL() {
        AuditRecord request = fileUtils.buildRequestRecord("some correlation id", "2019-02-25 12:01:02.003", "some nino");
        AuditRecord failResponse = fileUtils.buildResponseRecord("some correlation id", "2019-02-25 12:01:03.000", "some nino", "false");

        AuditResult auditResult = auditResultConsolidator.getAuditResult(Arrays.asList(request, failResponse));

        assertThat(auditResult).isEqualTo(new AuditResult("some correlation id", LocalDate.parse("2019-02-25"), "some nino", FAIL));
    }

    @Test
    public void getAuditResult_requestAndNotFoundResponse_expectedAuditResultNOTFOUND() {
        AuditRecord request = fileUtils.buildRequestRecord("some correlation id", "2019-02-25 12:01:02.003", "some nino");
        AuditRecord notFoundResponse = fileUtils.buildResponseNotFoundRecord("some correlation id", "2019-02-25 12:01:03.000");

        AuditResult auditResult = auditResultConsolidator.getAuditResult(Arrays.asList(request, notFoundResponse));

        assertThat(auditResult).isEqualTo(new AuditResult("some correlation id", LocalDate.parse("2019-02-25"), "some nino", NOTFOUND));
    }

    @Test
    public void getAuditResult_passAndFail_auditResultPASS() {
        AuditRecord request1 = fileUtils.buildRequestRecord("some correlation id", "2019-02-25 12:01:02.003", "some nino");
        AuditRecord passResponse = fileUtils.buildResponseRecord("some correlation id", "2019-02-25 12:01:03.000", "some nino", "true");
        AuditRecord request2 = fileUtils.buildRequestRecord("some correlation id", "2019-02-25 12:01:02.003", "some nino");
        AuditRecord failResponse = fileUtils.buildResponseRecord("some correlation id", "2019-02-25 12:01:03.000", "some nino", "false");

        AuditResult auditResult = auditResultConsolidator.getAuditResult(Arrays.asList(request1, passResponse, request2, failResponse));

        assertThat(auditResult).isEqualTo(new AuditResult("some correlation id", LocalDate.parse("2019-02-25"), "some nino", PASS));
    }
}

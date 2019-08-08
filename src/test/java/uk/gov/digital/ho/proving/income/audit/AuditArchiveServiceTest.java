package uk.gov.digital.ho.proving.income.audit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static uk.gov.digital.ho.proving.income.audit.AuditArchiveService.AUDIT_EVENTS_TO_ARCHIVE;
import static uk.gov.digital.ho.proving.income.audit.AuditResultType.PASS;

@RunWith(MockitoJUnitRunner.class)
public class AuditArchiveServiceTest {

    private AuditArchiveService auditArchiveService;

    @Mock
    private AuditClient mockAuditClient;
    @Mock
    private AuditResultConsolidator mockAuditResultConsolidator;
    private int retainAuditHistoryMonths = 6;

    @Before
    public void setUp() {
        auditArchiveService = new AuditArchiveService(mockAuditClient, mockAuditResultConsolidator, retainAuditHistoryMonths);
    }

    @Test
    public void archiveAudit_historyFound_collaboratorsAreCalled() {
        LocalDate auditEndDate = ReflectionTestUtils.invokeMethod(auditArchiveService, "getLastDayToBeArchived");
        List<AuditRecord> history = new ArrayList<>();
        when(mockAuditClient.getAuditHistory(auditEndDate, AUDIT_EVENTS_TO_ARCHIVE)).thenReturn(history);
        List<AuditResult> resultsByCorrelationId = getAuditResultsByCorrelationId();
        when(mockAuditResultConsolidator.auditResultsByCorrelationId(anyList())).thenReturn(resultsByCorrelationId);
        List<ConsolidatedAuditResult> resultsByNino = getAuditResultsByNino();
        when(mockAuditResultConsolidator.consolidatedAuditResultsByNino(anyList())).thenReturn(resultsByNino);

        auditArchiveService.archiveAudit();

        verify(mockAuditClient).getAuditHistory(auditEndDate, AUDIT_EVENTS_TO_ARCHIVE);
        verify(mockAuditResultConsolidator).auditResultsByCorrelationId(history);
        verify(mockAuditResultConsolidator).consolidatedAuditResultsByNino(resultsByCorrelationId);
        verify(mockAuditClient).archiveAudit(any(ArchiveAuditRequest.class), any(LocalDate.class));
    }

    private List<AuditResult> getAuditResultsByCorrelationId() {
        AuditResult auditResult = new AuditResult("any_corr_id", LocalDate.now().minusMonths(7), "any_nino", PASS);
        return Arrays.asList(auditResult);
    }

    private List<ConsolidatedAuditResult> getAuditResultsByNino() {
        ConsolidatedAuditResult auditResult = new ConsolidatedAuditResult("any_nino", Arrays.asList("any_corr_id"), LocalDate.now().minusMonths(7), PASS);
        return Arrays.asList(auditResult);
    }

}

package uk.gov.digital.ho.proving.income.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.alert.*;
import uk.gov.digital.ho.proving.income.alert.sysdig.SuspectUsage;
import uk.gov.digital.ho.proving.income.api.RequestData;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuditRepositoryTest {
    @Mock
    private AuditRepository auditRepository;
    @Mock
    private RequestData requestData;
    @Mock
    private AuditEntryRepository auditEntryRepository;
    @Mock
    private AppropriateUsageChecker appropriateUsageChecker;
    @Captor
    private ArgumentCaptor<AuditEntry> auditEntryCaptor;


    @Before
    public void before() throws Exception {
        auditRepository = new AuditRepository(new ObjectMapper(), requestData, auditEntryRepository, appropriateUsageChecker);
    }

    @Test
    public void shouldSaveRecordUsingRequestData() throws Exception {
        when(requestData.correlationId()).thenReturn("7272-282802828-32838");
        when(requestData.sessionId()).thenReturn("47749-73474-4484-73673");
        when(requestData.userId()).thenReturn("charlie.boots");
        when(requestData.deploymentName()).thenReturn("MYAPP");
        when(requestData.deploymentNamespace()).thenReturn("PRODUCTION");

        auditRepository.add(AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.fromString("a1eaf791-08d5-4dad-8d59-dd0df9dabd3b"), ImmutableMap.of("name", "value"));

        verify(auditEntryRepository).save(auditEntryCaptor.capture());

        assertThat(auditEntryCaptor.getValue().getCorrelationId()).isEqualTo("7272-282802828-32838");
        assertThat(auditEntryCaptor.getValue().getSessionId()).isEqualTo("47749-73474-4484-73673");
        assertThat(auditEntryCaptor.getValue().getUserId()).isEqualTo("charlie.boots");
        assertThat(auditEntryCaptor.getValue().getDeployment()).isEqualTo("MYAPP");
        assertThat(auditEntryCaptor.getValue().getNamespace()).isEqualTo("PRODUCTION");
        assertThat(auditEntryCaptor.getValue().getUuid()).isEqualTo("a1eaf791-08d5-4dad-8d59-dd0df9dabd3b");
        assertThat(auditEntryCaptor.getValue().getType()).isEqualTo(AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST);
        assertThat(auditEntryCaptor.getValue().getDetail()).isEqualTo("{\"name\":\"value\"}");
    }

    @Test
    public void shouldCheckForAppropriateUsage() {
        SuspectUsage suspectUsage = new SuspectUsage(new IndividualVolumeUsage(ImmutableMap.of()), new TimeOfRequestUsage(0), new MatchingFailureUsage(0));
        when(appropriateUsageChecker.precheck()).thenReturn(suspectUsage);
        auditRepository.add(AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.fromString("a1eaf791-08d5-4dad-8d59-dd0df9dabd3b"), ImmutableMap.of("name", "value"));

        verify(appropriateUsageChecker).precheck();
        verify(appropriateUsageChecker).postcheck(suspectUsage);
    }
}

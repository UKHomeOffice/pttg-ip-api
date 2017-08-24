package uk.gov.digital.ho.proving.income.audit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.alert.AppropriateUsageChecker;
import uk.gov.digital.ho.proving.income.alert.IndividualVolumeUsage;
import uk.gov.digital.ho.proving.income.alert.MatchingFailureUsage;
import uk.gov.digital.ho.proving.income.alert.TimeOfRequestUsage;
import uk.gov.digital.ho.proving.income.alert.sysdig.SuspectUsage;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuditServiceTest {

    @Mock private ObjectMapper mockMapper;
    @Mock private RequestData mockRequestData;
    @Mock private AuditEntryJpaRepository mockRepository;
    @Mock private AppropriateUsageChecker mockAppropriateUsageChecker;


    @Captor private ArgumentCaptor<AuditEntry> captorAuditEntry;

    @InjectMocks private AuditService auditService;


    private AuditEventType someAuditEventType;
    private UUID someUUID;
    private Map<String, Object> someAuditData;


    @Before
    public void setup() {
        someAuditEventType = AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST;
        someUUID = UUID.randomUUID();
        someAuditData = new HashMap<>();
        when(mockRequestData.sessionId()).thenReturn("some session id");
        when(mockRequestData.correlationId()).thenReturn("some correlation id");
        when(mockRequestData.deploymentName()).thenReturn("some deployment name");
        when(mockRequestData.deploymentNamespace()).thenReturn("some deployment namespace");
        when(mockRequestData.userId()).thenReturn("some user id");
    }

    @Test
    public void shouldUseCollaborators() {

        auditService.add(someAuditEventType, someUUID, someAuditData);

        verify(mockRepository).save(any(AuditEntry.class));
    }

    @Test
    public void shouldThrowExceptionWhenJsonMappingError() throws JsonProcessingException {

        when(mockMapper.writeValueAsString(someAuditData)).thenThrow(JsonProcessingException.class);

        assertThatThrownBy(() -> auditService.add(someAuditEventType, someUUID, someAuditData))
                            .isInstanceOf(ApplicationExceptions.AuditDataException.class)
                            .hasCauseInstanceOf(JsonProcessingException.class);
    }

    @Test
    public void shouldCreateAuditEntry() throws JsonProcessingException {

        when(mockMapper.writeValueAsString(someAuditData)).thenReturn("some json");

        auditService.add(someAuditEventType, someUUID, someAuditData);

        verify(mockRepository).save(captorAuditEntry.capture());

        AuditEntry arg = captorAuditEntry.getValue();

        assertThat(arg.getUuid()).isEqualTo(someUUID.toString());
        assertThat(arg.getSessionId()).isEqualTo("some session id");
        assertThat(arg.getCorrelationId()).isEqualTo("some correlation id");
        assertThat(arg.getUserId()).isEqualTo("some user id");
        assertThat(arg.getDeployment()).isEqualTo("some deployment name");
        assertThat(arg.getNamespace()).isEqualTo("some deployment namespace");
        assertThat(arg.getType()).isEqualTo(someAuditEventType);
        assertThat(arg.getDetail()).isEqualTo("some json");
    }

    @Test
    public void shouldCheckForAppropriateUsage() {
        SuspectUsage suspectUsage = new SuspectUsage(new IndividualVolumeUsage(ImmutableMap.of()), new TimeOfRequestUsage(0), new MatchingFailureUsage(0));
        when(mockAppropriateUsageChecker.precheck()).thenReturn(suspectUsage);
        auditService.add(AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST, UUID.fromString("a1eaf791-08d5-4dad-8d59-dd0df9dabd3b"), ImmutableMap.of("name", "value"));

        verify(mockAppropriateUsageChecker).precheck();
        verify(mockAppropriateUsageChecker).postcheck(suspectUsage);
    }

}

package uk.gov.digital.ho.proving.income.alert;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditEntry;
import uk.gov.digital.ho.proving.income.audit.AuditEntryJpaRepository;
import uk.gov.digital.ho.proving.income.audit.AuditEventType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MatchingFailureCheckTest {
    private MatchingFailureCheck matchingFailureCheck;
    @Mock
    private AuditEntryJpaRepository repository;
    @Captor
    private ArgumentCaptor<LocalDateTime> startTimeCaptor;
    @Captor
    private ArgumentCaptor<LocalDateTime> endTimeCaptor;

    @Before
    public void before() throws Exception {
        matchingFailureCheck = new MatchingFailureCheck(3);
    }

    @Test
    public void shouldBeSuspectIfMoreThanThresholdFailures() throws Exception {
        List<AuditEntry> fourFailures = ImmutableList.of(
            auditEntryWithDetail("Resource not found"),
            auditEntryWithDetail("Resource not found"),
            auditEntryWithDetail("Resource not found"),
            auditEntryWithDetail("Resource not found")

        );
        when(repository.getEntriesBetweenDates(any(), any(), any())).thenReturn(fourFailures);
        assertThat(matchingFailureCheck.check(repository).isSuspect()).isTrue();
        assertThat(matchingFailureCheck.check(repository).getCountOfFailures()).isEqualTo(4);
    }

    @Test
    public void shouldNotBeSuspectIfSameAsThresholdFailures() throws Exception {
        List<AuditEntry> fourFailures = ImmutableList.of(
            auditEntryWithDetail("Resource not found"),
            auditEntryWithDetail("Resource not found"),
            auditEntryWithDetail("Resource not found")

        );
        when(repository.getEntriesBetweenDates(any(), any(), any())).thenReturn(fourFailures);
        assertThat(matchingFailureCheck.check(repository).isSuspect()).isFalse();
        assertThat(matchingFailureCheck.check(repository).getCountOfFailures()).isEqualTo(0);
    }

    @Test
    public void shouldNotBeSuspectIfManyEntriesButNotFailures() throws Exception {
        List<AuditEntry> fourFailures = ImmutableList.of(
            auditEntryWithDetail("Resource not found"),
            auditEntryWithDetail("Resource not found"),
            auditEntryWithDetail("{}"),
            auditEntryWithDetail("{}"),
            auditEntryWithDetail("{}"),
            auditEntryWithDetail("{}"),
            auditEntryWithDetail("{}"),
            auditEntryWithDetail("{}"),
            auditEntryWithDetail("Resource not found")

        );
        when(repository.getEntriesBetweenDates(any(), any(), any())).thenReturn(fourFailures);
        assertThat(matchingFailureCheck.check(repository).isSuspect()).isFalse();
        assertThat(matchingFailureCheck.check(repository).getCountOfFailures()).isEqualTo(0);
    }

    @Test
    public void shouldRetrieveEntriesInTheLastHour() throws Exception {
        matchingFailureCheck.check(repository);

        verify(repository).getEntriesBetweenDates(startTimeCaptor.capture(), endTimeCaptor.capture(), Mockito.eq(AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE));

        LocalDateTime startTime = startTimeCaptor.getValue();
        LocalDateTime endTime = endTimeCaptor.getValue();

        assertThat(startTime.until(endTime, ChronoUnit.MINUTES)).isEqualTo(60);
        assertThat(endTime.until(LocalDateTime.now(), ChronoUnit.MINUTES)).isEqualTo(0);

    }


    private AuditEntry auditEntryWithDetail(String detail) {
        AuditEntry auditEntry = new AuditEntry(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "dave",
            "dev",
            "dev",
            AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE,
            detail
        );
        return auditEntry;
    }

}
package uk.gov.digital.ho.proving.income.alert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditEntryJpaRepository;
import uk.gov.digital.ho.proving.income.audit.AuditEventType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimeOfRequestCheckTest {
    private TimeOfRequestCheck timeOfRequestCheck;
    @Mock
    private AuditEntryJpaRepository repository;
    @Captor
    private ArgumentCaptor<LocalDateTime> startTimeCaptor;
    @Captor
    private ArgumentCaptor<LocalDateTime> endTimeCaptor;


    @Before
    public void before() throws Exception {
        timeOfRequestCheck = new TimeOfRequestCheck("07:23", "19:13");
    }

    @Test
    public void shouldCountRequestsBetweenStartOfDayAndStartOfWorkingDay() throws Exception {
        timeOfRequestCheck.check(repository);

        verify(repository, atLeast(2)).countEntriesBetweenDates(startTimeCaptor.capture(), endTimeCaptor.capture(), Mockito.eq(AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE));

        LocalDateTime startOfDay = startTimeCaptor.getAllValues().get(0);
        LocalDateTime startOfWorkingDay = endTimeCaptor.getAllValues().get(0);

        assertThat(startOfDay.toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(startOfDay.getHour()).isEqualTo(0);
        assertThat(startOfDay.getMinute()).isEqualTo(0);
        assertThat(startOfDay.getSecond()).isEqualTo(0);

        assertThat(startOfWorkingDay.toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(startOfWorkingDay.getHour()).isEqualTo(7);
        assertThat(startOfWorkingDay.getMinute()).isEqualTo(23);
        assertThat(startOfWorkingDay.getSecond()).isEqualTo(0);

    }

    @Test
    public void shouldCountRequestsBetweenEndOfWorkingDayAndStartOfTomorrow() throws Exception {
        timeOfRequestCheck.check(repository);

        verify(repository, atLeast(2)).countEntriesBetweenDates(startTimeCaptor.capture(), endTimeCaptor.capture(), Mockito.eq(AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE));

        LocalDateTime endOfWorkingDay = startTimeCaptor.getAllValues().get(1);
        LocalDateTime endOfDay = endTimeCaptor.getAllValues().get(1);

        assertThat(endOfWorkingDay.toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(endOfWorkingDay.getHour()).isEqualTo(19);
        assertThat(endOfWorkingDay.getMinute()).isEqualTo(13);
        assertThat(endOfWorkingDay.getSecond()).isEqualTo(0);

        assertThat(endOfDay.toLocalDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(endOfDay.getHour()).isEqualTo(0);
        assertThat(endOfDay.getMinute()).isEqualTo(0);
        assertThat(endOfDay.getSecond()).isEqualTo(0);
    }

    @Test
    public void shouldSumBothCounts() {
        when(repository.countEntriesBetweenDates(any(), any(), any())).thenReturn(Long.valueOf(3));

        TimeOfRequestUsage timeOfRequestUsage = timeOfRequestCheck.check(repository);

        assertThat(timeOfRequestUsage.getRequestCount()).isEqualTo(6);
    }
}

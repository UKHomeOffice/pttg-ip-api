package uk.gov.digital.ho.proving.income.api;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusRequest;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.IncomeValidationService;
import utils.LogCapturer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FinancialStatusServiceTest {

    @InjectMocks
    private FinancialStatusService service;

    @Mock
    private HmrcClient mockHmrcClient;

    @Mock
    private AuditClient mockAuditClient;

    @Mock
    private NinoUtils mockNinoUtils;

    @Mock
    private IncomeValidationService incomeValidationService;

    @Test
    public void shouldNeverLogSuppliedNino() {
        // given
        String realNino = "RealNino";
        FinancialStatusRequest mockFinancialStatusRequest = mock(FinancialStatusRequest.class);
        Applicant applicant = new Applicant("forename", "surname", LocalDate.now(), realNino);
        List<Applicant> applicants = Arrays.asList(applicant);
        when(mockFinancialStatusRequest.applicants()).thenReturn(applicants);

        when(mockNinoUtils.redact(realNino)).thenReturn("RedactedNino");
        when(mockNinoUtils.sanitise(realNino)).thenReturn("SanitisedNino");

        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
        when(mockFinancialStatusRequest.applicationRaisedDate()).thenReturn(fiveDaysAgo);
        when(mockHmrcClient.getIncomeRecord(any(), any(), any())).thenReturn(mock(IncomeRecord.class));

        LogCapturer<FinancialStatusService> logCapturer = LogCapturer.forClass(FinancialStatusService.class);
        logCapturer.start();

        // when
        service.getFinancialStatus(mockFinancialStatusRequest);

        // then
        verify(mockNinoUtils, atLeastOnce()).redact(realNino);

        // verify log outputs never contain the `real` nino
        List<ILoggingEvent> allLogEvents = logCapturer.getAllEvents();
        for (final ILoggingEvent logEvent : allLogEvents) {
            final String logMessage = logEvent.getFormattedMessage();
            assertThat(logMessage).doesNotContain(realNino);
        }
    }
}

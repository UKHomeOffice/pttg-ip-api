package uk.gov.digital.ho.proving.income.api;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.domain.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;
import utils.LogCapturer;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncomeRetrievalServiceTest {
    private static final String REAL_NINO = "RealNino";
    private static final String REDACTED_NINO = "RedactedNino";
    private static final String FORE_NAME = "ForeName";
    private static final String SURNAME = "LastName";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, Month.DECEMBER, 25);
    private static final LocalDate TO_DATE = LocalDate.of(2014, Month.JANUARY, 1);
    private static final LocalDate FROM_DATE = LocalDate.of(2015, Month.DECEMBER, 29);

    @Mock
    private HmrcClient hmrcClient;

    @Mock
    private AuditClient auditClient;

    @Mock
    private NinoUtils ninoUtils;

    @InjectMocks
    private IncomeRetrievalService incomeRetrievalService;

    @Test
    public void shouldNeverLogSuppliedNino() {
        // given
        final IncomeRetrievalRequest incomeRetrievalRequest = new IncomeRetrievalRequest(REAL_NINO, FORE_NAME, SURNAME, DATE_OF_BIRTH, TO_DATE, FROM_DATE);

        when(ninoUtils.redact(REAL_NINO)).thenReturn(REDACTED_NINO);
        when(hmrcClient.getIncomeRecord(any(), any(), any())).thenReturn(mock(IncomeRecord.class));

        final LogCapturer<IncomeRetrievalService> logCapturer = LogCapturer.forClass(IncomeRetrievalService.class);
        logCapturer.start();

        // when
        incomeRetrievalService.getIncome(incomeRetrievalRequest);

        // then
        // verify supplied nino is never logged
        final List<ILoggingEvent> allLogEvents = logCapturer.getAllEvents();
        for (ILoggingEvent logEvent : allLogEvents) {
            final String logMessage = logEvent.getFormattedMessage();
            assertThat(logMessage).doesNotContain(REAL_NINO);
        }
    }
}

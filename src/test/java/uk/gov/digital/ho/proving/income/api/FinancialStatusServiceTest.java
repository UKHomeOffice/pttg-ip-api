package uk.gov.digital.ho.proving.income.api;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusCheckResponse;
import uk.gov.digital.ho.proving.income.api.domain.FinancialStatusRequest;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.IncomeValidationService;
import utils.LogCapturer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private IncomeValidationService mockIncomeValidationService;

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

    @Test
    public void shouldReturnCorrectIndividualNamesIfIndividualNotReturnedFromHmrc() {
        when(mockNinoUtils.sanitise("A")).thenReturn("A");
        when(mockNinoUtils.sanitise("B")).thenReturn("B");
        when(mockHmrcClient.getIncomeRecord(eq(getApplicantIdentity()), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(getApplicantIncomeRecord());
        when(mockHmrcClient.getIncomeRecord(eq(getPartnerIdentity()), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(getPartnerIncomeRecord());


        Applicant applicant = new Applicant("applicant", "surname", LocalDate.now(), "A");
        Applicant partner = new Applicant("partner", "surname", LocalDate.now(), "B");
        List<Applicant> applicants = ImmutableList.of(applicant, partner);
        FinancialStatusRequest request = new FinancialStatusRequest(applicants, LocalDate.now(), 0);

        FinancialStatusCheckResponse response = service.getFinancialStatus(request);

        assertThat(response.individuals().size()).isEqualTo(2).withFailMessage("The correct number of individuals should be returned");
        assertThat(response.individuals().get(0).forename()).isEqualTo("applicant").withFailMessage("The applicant's name should be returned");
        assertThat(response.individuals().get(0).nino()).isEqualTo("A").withFailMessage("The applicant's nino should be returned");
        assertThat(response.individuals().get(1).forename()).isEqualTo("partner").withFailMessage("The partner's name should be returned");
        assertThat(response.individuals().get(1).nino()).isEqualTo("B").withFailMessage("The partner's nino should be returned");
    }

    private Identity getApplicantIdentity() {
        return new Identity("applicant", "surname", LocalDate.now(), "A");
    }

    private IncomeRecord getApplicantIncomeRecord() {
        Income income = new Income(BigDecimal.ONE, LocalDate.now(), 1, null, "E1");
        return new IncomeRecord(ImmutableList.of(income), new ArrayList<>(), new ArrayList(), null);
    }

    private Identity getPartnerIdentity() {
        return new Identity("partner", "surname", LocalDate.now(), "B");
    }

    private IncomeRecord getPartnerIncomeRecord() {
        Income income = new Income(BigDecimal.ONE, LocalDate.now(), 1, null, "E2");
        return new IncomeRecord(ImmutableList.of(income), new ArrayList<>(), new ArrayList(), null);
    }

}

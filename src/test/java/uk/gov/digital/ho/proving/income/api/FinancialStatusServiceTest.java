package uk.gov.digital.ho.proving.income.api;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.*;
import uk.gov.digital.ho.proving.income.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.IncomeValidationService;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinancialStatusServiceTest {

    @InjectMocks
    private FinancialStatusService financialStatusService;

    @Mock
    private HmrcClient mockHmrcClient;

    @Mock
    private IncomeValidationService mockIncomeValidationService;


    @Test
    public void shouldReturnCorrectIndividualNamesIfIndividualNotReturnedFromHmrc() {
        when(mockHmrcClient.getIncomeRecord(eq(getApplicantIdentity()), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(getApplicantIncomeRecord());
        when(mockHmrcClient.getIncomeRecord(eq(getPartnerIdentity()), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(getPartnerIncomeRecord());
        when(mockIncomeValidationService.validate(any())).thenReturn(getCategoryChecks());


        FinancialStatusCheckResponse response = financialStatusService.calculateResponse(LocalDate.now(), 0, getIncomeRecords());


        assertThat(response.individuals().size()).isEqualTo(2).withFailMessage("The correct number of individuals should be returned");

        Optional<Individual> applicantIndividual = response.individuals().stream().filter(individual -> individual.nino().equals("A")).findFirst();
        Optional<Individual> partnerIndividual = response.individuals().stream().filter(individual -> individual.nino().equals("B")).findFirst();

        assertThat(applicantIndividual.isPresent()).withFailMessage("The applicant's nino should exist in the list of individuals");
        assertThat(applicantIndividual.get().forename()).isEqualTo("applicant").withFailMessage("The applicant's name should be returned");

        assertThat(partnerIndividual.isPresent()).withFailMessage("The partner's nino should exist in the list of individuals");
        assertThat(partnerIndividual.get().forename()).isEqualTo("partner").withFailMessage("The partner's name should be returned");
    }

    @Test
    public void shouldReturnIndividualsInCorrectOrder() {
        when(mockHmrcClient.getIncomeRecord(eq(getApplicantIdentity()), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(getApplicantIncomeRecord());
        when(mockHmrcClient.getIncomeRecord(eq(getPartnerIdentity()), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(getPartnerIncomeRecord());
        when(mockIncomeValidationService.validate(any())).thenReturn(getCategoryChecks());


        FinancialStatusCheckResponse response = financialStatusService.calculateResponse(LocalDate.now(), 0, getIncomeRecordsOutOfOrder());


        assertThat(response.individuals().size()).isEqualTo(2).withFailMessage("The correct number of individuals should be returned");
        assertThat(response.individuals().get(0).nino()).isEqualTo("A").withFailMessage("The applicant should be returned first");
        assertThat(response.individuals().get(1).nino()).isEqualTo("B").withFailMessage("The partner should be returned second");

        assertThat(response.categoryChecks().get(0).individuals().get(0).nino()).isEqualTo("A").withFailMessage("The applicant should be first in the category check");
        assertThat(response.categoryChecks().get(0).individuals().get(1).nino()).isEqualTo("B").withFailMessage("The partner should be second in the category check");

    }

    private Individual getApplicantIndividual() {
        return new Individual("applicant", "surname", "A");
    }

    private Identity getApplicantIdentity() {
        return new Identity("applicant", "surname", LocalDate.now(), "A");
    }

    private IncomeRecord getApplicantIncomeRecord() {
        Income income = new Income(BigDecimal.ONE, LocalDate.now(), 1, null, "E1");
        return new IncomeRecord(ImmutableList.of(income), new ArrayList<>(), new ArrayList(), null);
    }

    private Individual getPartnerIndividual() {
        return new Individual("partner", "surname", "B");
    }

    private Identity getPartnerIdentity() {
        return new Identity("apartner", "surname", LocalDate.now(), "B");
    }

    private IncomeRecord getPartnerIncomeRecord() {
        Income income = new Income(BigDecimal.ONE, LocalDate.now(), 1, null, "E2");
        return new IncomeRecord(ImmutableList.of(income), new ArrayList<>(), new ArrayList(), null);
    }

    private LinkedHashMap<Individual, IncomeRecord> getIncomeRecords() {
        LinkedHashMap<Individual, IncomeRecord> incomeRecords = new LinkedHashMap<>();
        incomeRecords.put(getApplicantIndividual(), getApplicantIncomeRecord());
        incomeRecords.put(getPartnerIndividual(), getPartnerIncomeRecord());
        return incomeRecords;
    }

    private Map<Individual, IncomeRecord> getIncomeRecordsOutOfOrder() {
        Map<Individual, IncomeRecord> incomeRecords = new HashMap<>();
        incomeRecords.put(getPartnerIndividual(), getPartnerIncomeRecord());
        incomeRecords.put(getApplicantIndividual(), getApplicantIncomeRecord());
        return incomeRecords;
    }

    private List<CategoryCheck> getCategoryChecks() {
        List<CheckedIndividual> checkedIndividuals = new ArrayList<>();
        CheckedIndividual applicant = new CheckedIndividual("A", Collections.unmodifiableList(Arrays.asList("E1")));
        checkedIndividuals.add(applicant);
        CheckedIndividual partner = new CheckedIndividual("B", Collections.unmodifiableList(Arrays.asList("E2")));
        checkedIndividuals.add(partner);
        CategoryCheck categoryCheck = new CategoryCheck("B", "Test", false, LocalDate.now(), LocalDate.now(), IncomeValidationStatus.CATB_NON_SALARIED_BELOW_THRESHOLD, BigDecimal.TEN, checkedIndividuals);
        List<CategoryCheck> categoryChecks = Collections.unmodifiableList(Arrays.asList(categoryCheck));
        return categoryChecks;
    }

}

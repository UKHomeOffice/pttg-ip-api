package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.Lists;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.api.domain.Individual;
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.getAllPayeInDateRange;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.getAllPayeIncomes;


public class IncomeValidationHelperTest {

    private final LocalDate someDate = LocalDate.of(2018, 9, 5);
    private final int someInt = 0;
    private final BigDecimal someAmount = new BigDecimal("12.30");
    private final Individual applicant = new Individual("some forename", "some surname", "some nino");
    private final HmrcIndividual applicantHmrcIndividual = new HmrcIndividual("some forename", "some surname", "some nino", someDate);
    private final Individual partner = new Individual("some other forename", "some other surname", "some other nino");
    private final HmrcIndividual partnerHmrcIndividual = new HmrcIndividual("some other forename", "some other surname", "some other nino", someDate);

    @Test
    public void testGetAllPayeIncomes() {

        List<Income> applicantPaye = Lists.newArrayList(
            new Income(someAmount, someDate, someInt, null, "some paye ref"),
            new Income(someAmount, someDate, someInt, null, "some paye ref")
        );
        List<Income> partnerPaye = Lists.newArrayList(
            new Income(someAmount, someDate, null, someInt, "some paye ref")
        );

        IncomeRecord applicantIncome = new IncomeRecord(applicantPaye, new ArrayList<>(), new ArrayList<>(), applicantHmrcIndividual);
        IncomeRecord partnerIncome = new IncomeRecord(partnerPaye, new ArrayList<>(), new ArrayList<>(), partnerHmrcIndividual);

        Map<Individual, IncomeRecord> incomeRecords = new HashMap<>();
        incomeRecords.put(applicant, applicantIncome);
        incomeRecords.put(partner, partnerIncome);

        IncomeValidationRequest request = IncomeValidationRequest.create(someDate, incomeRecords, someInt);

        List<Income> payeIncomes = getAllPayeIncomes(request);

        assertThat(payeIncomes).containsAll(applicantPaye);
        assertThat(payeIncomes).containsAll(partnerPaye);
        assertThat(payeIncomes).hasSize(applicantPaye.size() + partnerPaye.size());
    }

    @Test
    public void testGetAllPayeInDateRange() {
        LocalDate applicationStartDate = LocalDate.of(2018, 7, 27);
        LocalDate applicationRaisedDate = LocalDate.of(2018, 9, 5);

        Income incomeBeforeStartDate = new Income(someAmount, applicationStartDate.minusDays(1), someInt, null, "some paye ref");
        Income incomeOnStartDate = new Income(someAmount, applicationStartDate, someInt, null, "some paye ref");
        Income incomeAfterStartDate = new Income(someAmount, applicationStartDate.plusDays(1), someInt, null, "some paye ref");

        Income incomeBeforeEndDate = new Income(someAmount, applicationRaisedDate.minusDays(1), someInt, null, "some paye ref");
        Income incomeOnEndDate = new Income(someAmount, applicationRaisedDate, someInt, null, "some paye ref");
        Income incomeAfterEndDate = new Income(someAmount, applicationRaisedDate.plusDays(1), someInt, null, "some paye ref");

        List<Income> applicantPaye = Lists.newArrayList(incomeBeforeStartDate, incomeAfterStartDate, incomeOnEndDate);
        List<Income> partnerPaye = Lists.newArrayList(incomeOnStartDate, incomeBeforeEndDate, incomeAfterEndDate);

        IncomeRecord applicantIncome = new IncomeRecord(applicantPaye, new ArrayList<>(), new ArrayList<>(), applicantHmrcIndividual);
        IncomeRecord partnerIncome = new IncomeRecord(partnerPaye, new ArrayList<>(), new ArrayList<>(), partnerHmrcIndividual);

        Map<Individual, IncomeRecord> incomeRecords = new HashMap<>();
        incomeRecords.put(applicant, applicantIncome);
        incomeRecords.put(partner, partnerIncome);

        IncomeValidationRequest request = IncomeValidationRequest.create(applicationRaisedDate, incomeRecords, someInt);

        List<Income> payeInDateRange = getAllPayeInDateRange(request, applicationStartDate);

        assertThat(payeInDateRange).containsExactlyInAnyOrder(incomeOnStartDate, incomeAfterStartDate, incomeBeforeEndDate, incomeOnEndDate);
    }
}

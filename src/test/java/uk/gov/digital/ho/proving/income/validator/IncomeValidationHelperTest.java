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
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.getAllPayeIncomes;


public class IncomeValidationHelperTest {

    @Test
    public void testGetAllPayeIncomes() {
        LocalDate someDate = LocalDate.of(2018, 9, 5);
        int someInt = 0;
        BigDecimal someAmount =  new BigDecimal("12.30");

        Map<Individual, IncomeRecord> incomeRecords = new HashMap<>();
        Individual applicant = new Individual("some forename", "some surname", "some nino");
        HmrcIndividual applicantHmrcIndividual = new HmrcIndividual("some forename", "some surname", "some nino", someDate);
        Individual partner = new Individual("some other forename", "some other surname", "some other nino");
        HmrcIndividual partnerHmrcIndividual = new HmrcIndividual("some other forename", "some other surname", "some other nino", someDate);


        List<Income> paye1 = Lists.newArrayList(
            new Income(someAmount, someDate, someInt, null, "some paye ref"),
            new Income(someAmount, someDate, someInt, null, "some paye ref")
        );
        List<Income> paye2 = Lists.newArrayList(
            new Income(someAmount, someDate, null, someInt, "some paye ref")
        );

        IncomeRecord applicantIncome = new IncomeRecord(paye1, new ArrayList<>(), new ArrayList<>(), applicantHmrcIndividual);
        IncomeRecord partnerIncome = new IncomeRecord(paye2, new ArrayList<>(), new ArrayList<>(), partnerHmrcIndividual);
        incomeRecords.put(applicant, applicantIncome);
        incomeRecords.put(partner, partnerIncome);

        IncomeValidationRequest request = IncomeValidationRequest.create(someDate, incomeRecords, someInt);

        List<Income> payeIncomes = getAllPayeIncomes(request);

        assertThat(payeIncomes).containsAll(paye1);
        assertThat(payeIncomes).containsAll(paye2);
        assertThat(payeIncomes).hasSize(paye1.size() + paye2.size());
    }
}

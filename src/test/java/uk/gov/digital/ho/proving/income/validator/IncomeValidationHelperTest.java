package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.HmrcIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomeValidationHelperTest {

    @Test
    public void thatCheckedIndividualsRetainOrder() {
        List<ApplicantIncome> applicantIncomes = new ArrayList<>();
        IntStream.range(0, 10000).forEach(i -> {
            String is = Integer.toString(i);
            Applicant applicant = new Applicant(is, is, LocalDate.now(), is);
            IncomeRecord incomeRecord = new IncomeRecord(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HmrcIndividual(is, is, is, LocalDate.now()));
            applicantIncomes.add(new ApplicantIncome(applicant, incomeRecord));
        });

        List<CheckedIndividual> checkedIndividuals = IncomeValidationHelper.getCheckedIndividuals(applicantIncomes);


        Integer lastNino = -1;
        for (CheckedIndividual checkedIndividual : checkedIndividuals) {
                assertThat(lastNino.compareTo(Integer.parseInt(checkedIndividual.nino()))).isLessThan(0).withFailMessage(String.format("The checkedIndividual nino %s appeared before %s", lastNino, checkedIndividual.nino()));
                lastNino = Integer.parseInt(checkedIndividual.nino());
        }

    }
}

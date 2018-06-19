package uk.gov.digital.ho.proving.income.hmrc.domain;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomeRecordTest {

    private Income a = new Income(new BigDecimal("1"),
                                    LocalDate.MAX,
                                    1,
                                    1,
                                    "a");

    private Income b = new Income(new BigDecimal("2"),
                                    LocalDate.now(),
                                    1,
                                    1,
                                    "b");

    @Test
    public void shouldNotRemoveItems() {

        List<Income> incomeWithoutDuplicates = new ArrayList<>();

        incomeWithoutDuplicates.add(a);
        incomeWithoutDuplicates.add(b);

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, null, null, null);

        List<Income> deDuplicated = incomeRecord.deDuplicatedIncome();

        assertThat(incomeRecord.paye().equals(deDuplicated)).isTrue();
    }

    @Test
    public void shouldRemoveItems() {

        List<Income> incomeWithoutDuplicates = new ArrayList<>();

        incomeWithoutDuplicates.add(a);
        incomeWithoutDuplicates.add(a);
        incomeWithoutDuplicates.add(b);
        incomeWithoutDuplicates.add(b);

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, null, null, null);

        List<Income> deDuplicated = incomeRecord.deDuplicatedIncome();

        assertThat(incomeRecord.paye().equals(deDuplicated)).isFalse();
        assertThat(incomeRecord.paye().size()).isEqualTo(4);
        assertThat(deDuplicated.size()).isEqualTo(2);
        assertThat(incomeRecord.paye().containsAll(deDuplicated));
    }
}

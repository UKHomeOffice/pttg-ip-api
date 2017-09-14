package uk.gov.digital.ho.proving.income.domain.hmrc;

import org.junit.Test;

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

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, null);

        List<Income> deDuplicated = incomeRecord.deDuplicatedIncome();

        assertThat(incomeRecord.getIncome().equals(deDuplicated)).isTrue();
    }

    @Test
    public void shouldRemoveItems() {

        List<Income> incomeWithoutDuplicates = new ArrayList<>();

        incomeWithoutDuplicates.add(a);
        incomeWithoutDuplicates.add(a);
        incomeWithoutDuplicates.add(b);
        incomeWithoutDuplicates.add(b);

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, null);

        List<Income> deDuplicated = incomeRecord.deDuplicatedIncome();

        assertThat(incomeRecord.getIncome().equals(deDuplicated)).isFalse();
        assertThat(incomeRecord.getIncome().size()).isEqualTo(4);
        assertThat(deDuplicated.size()).isEqualTo(2);
        assertThat(incomeRecord.getIncome().containsAll(deDuplicated));
    }
}

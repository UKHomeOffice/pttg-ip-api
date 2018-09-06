package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.FrequencyCalculator.Frequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.FrequencyCalculator.Frequency.CALENDAR_MONTHLY;
import static uk.gov.digital.ho.proving.income.validator.FrequencyCalculator.Frequency.WEEKLY;
import static uk.gov.digital.ho.proving.income.validator.FrequencyCalculator.calculate;

public class FrequencyCalculatorTest {

    private final LocalDate someDate = LocalDate.of(2018, 1, 24);

    @Test
    public void shouldReturnMonthlyWhenMultiplePaymentsForOneMonth() {
        List<Income> paye = incomesForMonthNumber(1, 1, 2, 3, 4, 5, 6);
        IncomeRecord incomeRecord = new IncomeRecord(paye, null, null, null);
        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnWeeklyWhenMultiplePaymentsForOneWeek() {
        List<Income> paye = incomesForWeekNumber(50, 51, 51, 52, 53);
        IncomeRecord incomeRecord = new IncomeRecord(paye, null, null, null);
        Frequency frequency = calculate(incomeRecord);

        assertThat(frequency).isEqualTo(WEEKLY);
    }

    private List<Income> incomesForMonthNumber(int... monthNumbers) {
        final List<Income> incomes = new ArrayList<>();
        for (int monthNumber : monthNumbers) {
            incomes.add(new Income(BigDecimal.TEN, someDate.plusMonths(monthNumber), monthNumber, null, "any employer ref"));
        }
        return incomes;
    }

    private List<Income> incomesForWeekNumber(int... weekNumbers) {
        final List<Income> incomes = new ArrayList<>();
        for (int weekNumber : weekNumbers) {
            incomes.add(new Income(BigDecimal.TEN, someDate.plusWeeks(weekNumber), null, weekNumber, "any employer ref"));
        }
        return incomes;
    }
}

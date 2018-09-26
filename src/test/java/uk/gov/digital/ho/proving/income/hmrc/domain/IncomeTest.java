package uk.gov.digital.ho.proving.income.hmrc.domain;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IncomeTest {

    public static final LocalDate MONTH_4_PAYMENT_DATE = LocalDate.of(2018, 10, 24);

    private final Income incomeMonth3 = new Income(BigDecimal.ONE, LocalDate.of(2018, 9, 24), 3, null, "anyRef");
    private final Income incomeMonth4 = new Income(BigDecimal.ONE, MONTH_4_PAYMENT_DATE, 4, null, "anyRef");
    private final Income anotherIncomeMonth4 = new Income(BigDecimal.TEN, MONTH_4_PAYMENT_DATE, 4, null, "anyRef");
    private final Income negativeIncomeMonth4 = new Income(BigDecimal.valueOf(-5), MONTH_4_PAYMENT_DATE, 4, null, "anyRef");
    private final Income incomeMonth4NextYear =  new Income(BigDecimal.ONE, LocalDate.of(2019, 10, 24), 4, null, "anyRef");;
    private final Income incomeWeek26 = new Income(BigDecimal.ONE, LocalDate.of(2018, 9, 13), null, 26, "anyRef");;
    private final Income incomeWeek26OtherEmployer = new Income(BigDecimal.ONE, LocalDate.of(2018, 9, 13), null, 26, "any other ref");;
    private final Income incomeWeek27 = new Income(BigDecimal.ONE, LocalDate.of(2018, 9, 20), null, 27, "anyRef");;
    private final Income incomeWeek27NextYear = new Income(BigDecimal.ONE, LocalDate.of(2019, 9, 20), null, 27, "anyRef");;

    @Test
    public void shouldFindEqual() {

        Income a = new Income(new BigDecimal("1"),
            LocalDate.MIN,
            0,
            0,
            "a");

        Income b = new Income(new BigDecimal("1"),
            LocalDate.MIN,
            0,
            0,
            "a");

        assertThat(a).isEqualTo(b);
    }

    @Test
    public void shouldFindEquivalentHashcode() {

        Income a = new Income(new BigDecimal("1"),
            LocalDate.MIN,
            0,
            0,
            "a");

        Income b = new Income(new BigDecimal("1"),
            LocalDate.MIN,
            0,
            0,
            "a");

        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void addNullShouldThrowNullPointerException() {
        assertThatThrownBy(() -> incomeMonth3.add(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void addDifferentMonthNumberShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> incomeMonth3.add(incomeMonth4)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Can't add incomes for different month pay numbers.");
    }

    @Test
    public void addDifferentWeekNumberShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> incomeWeek26.add(incomeWeek27)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can't add incomes for different week pay numbers.");
    }

    @Test
    public void addWeeklyToMonthlyShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> incomeMonth4.add(incomeWeek27)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can't add a weekly payment to a monthly one.");
    }

    @Test
    public void addMonthlyToWeeklyShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> incomeWeek26.add(incomeMonth4)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can't add a weekly payment to a monthly one.");
    }

    @Test
    public void addSameMonthDifferentYearShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> incomeMonth4.add(incomeMonth4NextYear)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can't add payments for different years.");
    }

    @Test
    public void addSameWeekDifferentYearShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> incomeWeek27.add(incomeWeek27NextYear)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can't add payments for different years.");
    }

    @Test
    public void addDifferentEmploymentRefsShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> incomeWeek26.add(incomeWeek26OtherEmployer)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can't add payments for different employers.");

    }

    @Test
    public void addTwoMonthlyPaymentsShouldReturnMonthlyPaymentWithSummedPayments() {
        Income expected = new Income(BigDecimal.valueOf(11), MONTH_4_PAYMENT_DATE, 4, null, "anyRef");
        assertThat(incomeMonth4.add(anotherIncomeMonth4)).isEqualTo(expected);
    }

    @Test
    public void addNegativeIncomeShouldSubtractPayment() {
        Income expected = new Income(BigDecimal.valueOf(-4), MONTH_4_PAYMENT_DATE, 4, null, "anyRef");
        assertThat(incomeMonth4.add(negativeIncomeMonth4)).isEqualTo(expected);
    }
}

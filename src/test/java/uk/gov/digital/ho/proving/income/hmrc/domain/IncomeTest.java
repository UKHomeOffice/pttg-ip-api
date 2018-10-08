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
    private final Income incomeWeek26 = new Income(BigDecimal.ONE, LocalDate.of(2018, 9, 13), null, 26, "anyRef");;
    private final Income incomeWeek26OtherEmployer = new Income(BigDecimal.ONE, LocalDate.of(2018, 9, 13), null, 26, "any other ref");;
    private final Income incomeWeek27 = new Income(BigDecimal.ONE, LocalDate.of(2018, 9, 20), null, 27, "anyRef");;

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
    public void addDifferentMonthPayNumberShouldKeepInitialMonthPayNumber() {
        assertThat(incomeMonth3.add(incomeMonth4).monthPayNumber()).isEqualTo(incomeMonth3.monthPayNumber());
        assertThat(incomeMonth4.add(incomeMonth3).monthPayNumber()).isEqualTo(incomeMonth4.monthPayNumber());
    }

    @Test
    public void addDifferentWeekPayNumberShouldKeepInitialWeekPayNumber() {
        assertThat(incomeWeek26.add(incomeWeek27).weekPayNumber()).isEqualTo(incomeWeek26.weekPayNumber());
        assertThat(incomeWeek27.add(incomeWeek26).weekPayNumber()).isEqualTo(incomeWeek27.weekPayNumber());
    }

    @Test
    public void addWeeklyToMonthlyShouldKeepMonthPayNumber() {
        assertThat(incomeMonth4.add(incomeWeek27).monthPayNumber()).isEqualTo(incomeMonth4.monthPayNumber());
    }

    @Test
    public void addWeeklyToMonthlyShouldKeepNullWeekPayNumber() {
        assertThat(incomeMonth4.add(incomeWeek27).weekPayNumber()).isEqualTo(incomeMonth4.weekPayNumber())
            .isNull();
    }

    @Test
    public void addMonthlyToWeeklyShouldKeepWeekPayNumber() {
        assertThat(incomeWeek27.add(incomeMonth4).weekPayNumber()).isEqualTo(incomeWeek27.weekPayNumber());
    }

    @Test
    public void addMonthlyToWeeklyShouldKeepNullMonthPayNumber() {
        assertThat(incomeWeek27.add(incomeMonth4).monthPayNumber()).isEqualTo(incomeWeek27.monthPayNumber())
            .isNull();
    }

    @Test
    public void addShouldKeepInitialPaymentDate() {
        assertThat(incomeWeek26.add(incomeWeek27).paymentDate()).isEqualTo(incomeWeek26.paymentDate())
            .isNotEqualTo(incomeWeek27.paymentDate());
        assertThat(incomeWeek27.add(incomeWeek26).paymentDate()).isEqualTo(incomeWeek27.paymentDate())
            .isNotEqualTo(incomeWeek26.paymentDate());

        assertThat(incomeMonth3.add(incomeMonth4).paymentDate()).isEqualTo(incomeMonth3.paymentDate())
            .isNotEqualTo(incomeMonth4.paymentDate());
        assertThat(incomeMonth4.add(incomeMonth3).paymentDate()).isEqualTo(incomeMonth4.paymentDate())
            .isNotEqualTo(incomeMonth3.paymentDate());
    }

    @Test
    public void addShouldKeepInitialEmploymentReference() {
        assertThat(incomeWeek26.add(incomeWeek26OtherEmployer).employerPayeReference()).isEqualTo(incomeWeek26.employerPayeReference())
            .isNotEqualTo(incomeWeek26OtherEmployer.employerPayeReference());
        assertThat(incomeWeek26OtherEmployer.add(incomeWeek26).employerPayeReference()).isEqualTo(incomeWeek26OtherEmployer.employerPayeReference())
            .isNotEqualTo(incomeWeek26.employerPayeReference());
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

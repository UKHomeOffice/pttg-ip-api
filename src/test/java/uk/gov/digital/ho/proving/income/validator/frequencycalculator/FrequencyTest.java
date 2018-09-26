package uk.gov.digital.ho.proving.income.validator.frequencycalculator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.frequencycalculator.Frequency.*;

public class FrequencyTest {

    @Test
    public void shouldReturnUnknownForLessThan6DaysBetweenPayments() {
        assertThat(Frequency.of(5)).isEqualTo(UNKNOWN);
    }

    @Test
    public void shouldReturnWeeklyFor6DaysBetweenPayments() {
        assertThat(Frequency.of(6)).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnWeeklyFor7DaysBetweenPayments() {
        assertThat(Frequency.of(7)).isEqualTo(WEEKLY);
    }

    @Test
    public void shouldReturnFortnightlyFor8DaysBetweenPayments() {
        assertThat(Frequency.of(8)).isEqualTo(FORTNIGHTLY);
    }

    @Test
    public void shouldReturnFortnightlyFor14DaysBetweenPayments() {
        assertThat(Frequency.of(14)).isEqualTo(FORTNIGHTLY);
    }

    @Test
    public void shouldReturnFourWeeklyFor15DaysBetweenPayments() {
        assertThat(Frequency.of(15)).isEqualTo(FOUR_WEEKLY);
    }

    @Test
    public void shouldReturnFourWeeklyFor28DaysBetweenPayments() {
        assertThat(Frequency.of(28)).isEqualTo(FOUR_WEEKLY);
    }

    @Test
    public void shouldReturnMonthlyFor29DaysBetweenPayments() {
        assertThat(Frequency.of(29)).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnMonthlyFor31DaysBetweenPayments() {
        assertThat(Frequency.of(31)).isEqualTo(CALENDAR_MONTHLY);
    }

    @Test
    public void shouldReturnUnknownFor32DaysBetweenPayments() {
        assertThat(Frequency.of(32)).isEqualTo(UNKNOWN);
    }
}

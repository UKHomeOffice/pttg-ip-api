package uk.gov.digital.ho.proving.income.domain.hmrc;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomeTest {

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
}

package uk.gov.digital.ho.proving.income.api.domain;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TaxYearTest {

    @Test
    public void valueOf_givenStartYear_startDateIsSixthOfApril() {
        TaxYear taxYear = TaxYear.valueOf("2018/2019");
        assertThat(taxYear.startDate()).isEqualTo(LocalDate.of(2018, Month.APRIL, 6));
    }

    @Test
    public void valueOf_givenEndYear_startDateIsFifthOfApril() {
        TaxYear taxYear = TaxYear.valueOf("2018/2019");
        assertThat(taxYear.endDate()).isEqualTo(LocalDate.of(2019, Month.APRIL, 5));
    }

    @Test
    public void valueOf_threeCharStartYear_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("998/0999"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("format");
    }

    @Test
    public void valueOf_threeCharEndYear_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("0998/999"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("format");
    }

    @Test
    public void valueOf_fiveCharStartYear_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("01998/1999"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void valueOf_fiveCharEndYear_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("1998/01999"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void valueOf_noSlash_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("19981999"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("format");
    }

    @Test
    public void valueOf_twoSlashes_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("1998//1999"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("format");
    }

    @Test
    public void valueOf_threeYears_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("1998/1999/2000"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("format");
    }

    @Test
    public void valueOf_emptyString_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("format");
    }

    @Test
    public void valueOf_null_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("from null");
    }

    @Test
    public void valueOf_nonConsecutiveYears_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("2014/2016"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("consecutive");
    }

    @Test
    public void valueOf_consecutiveYearsLatestFirst_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("2014/2013"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("consecutive");
    }

    @Test
    public void valueOf_nonNumericalEndYear_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("2014/201A"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("format");
    }

    @Test
    public void valueOf_nonNumericalStartYear_illegalArgumentException() {
        assertThatThrownBy(() -> TaxYear.valueOf("2A14/2015"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("format");
    }
}

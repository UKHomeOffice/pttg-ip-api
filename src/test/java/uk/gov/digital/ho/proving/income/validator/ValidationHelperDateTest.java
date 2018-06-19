package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationHelperDateTest {

    @Test
    public void sameDayOneMonthDifference() {
        LocalDate from = LocalDate.of(2015, Month.JANUARY, 23);
        LocalDate to = LocalDate.of(2015, Month.FEBRUARY, 23);

        long months = IncomeValidationHelper.getDifferenceInMonthsBetweenDates(to, from);

        assertThat(months).isEqualTo(1L).withFailMessage("There should be 1 month between the dates");
    }

    @Test
    public void earlierDayOneMonthDifference() {
        LocalDate from = LocalDate.of(2015,Month.JUNE,17);
        LocalDate to = LocalDate.of(2015,Month.JULY,14);

        long months = IncomeValidationHelper.getDifferenceInMonthsBetweenDates(to, from);

        assertThat(months).isEqualTo(1L).withFailMessage("There should be 1 month between the dates");
    }

    @Test
    public void laterDayOneMonthDifference() {
        LocalDate from = LocalDate.of(2015,Month.JANUARY,1);
        LocalDate to = LocalDate.of(2015,Month.FEBRUARY,28);

        long months = IncomeValidationHelper.getDifferenceInMonthsBetweenDates(to, from);

        assertThat(months).isEqualTo(1L).withFailMessage("There should be 1 month between the dates");
    }

    @Test
    public void laterDayTwoMonthsDifference() {
        LocalDate from = LocalDate.of(2014,Month.DECEMBER,1);
        LocalDate to = LocalDate.of(2015,Month.FEBRUARY,28);

        long months = IncomeValidationHelper.getDifferenceInMonthsBetweenDates(to, from);

        assertThat(months).isEqualTo(2L).withFailMessage("There should be 2 months between the dates");
    }

    @Test
    public void moreThanOneYearMonthDifference() {
        LocalDate from = LocalDate.of(2014,Month.DECEMBER,1);
        LocalDate to = LocalDate.of(2016,Month.FEBRUARY,28);

        long months = IncomeValidationHelper.getDifferenceInMonthsBetweenDates(to, from);

        assertThat(months).isEqualTo(14L).withFailMessage("There should be 14 months between the dates");
    }




}

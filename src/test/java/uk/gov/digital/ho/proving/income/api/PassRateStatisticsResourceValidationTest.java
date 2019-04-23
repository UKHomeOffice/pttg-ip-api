package uk.gov.digital.ho.proving.income.api;

import org.junit.Test;
import org.springframework.ui.Model;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsService;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class PassRateStatisticsResourceValidationTest {

    private static final Model SOME_MODEL = mock(Model.class);
    private static final LocalDate SOME_DATE = LocalDate.now();
    private static final TaxYear SOME_TAX_YEAR = TaxYear.valueOf("2018/2019");
    private static final YearMonth SOME_YEAR_MONTH = YearMonth.now();

    private final PassRateStatisticsResource resource = new PassRateStatisticsResource(mock(PassRateStatisticsService.class));

    @Test
    public void getPassRateStatisticsCsv_noParams_illegalArgumentException() {
        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(null, null, null, null, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No arguments");
    }

    @Test
    public void getPassRateStatisticsCsv_fromDateOnly_illegalArgumentException() {
        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(null, null, SOME_DATE, null, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("fromDate");
    }

    @Test
    public void getPassRateStatisticsCsv_toDateOnly_illegalArgumentException() {
        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(null, null, null, SOME_DATE, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("toDate");
    }

    @Test
    public void getPassRateStatisticsCsv_taxYearAndMonth_illegalArgumentException() {
        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(SOME_TAX_YEAR, SOME_YEAR_MONTH, null, null, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("taxYear")
            .hasMessageContaining("month");
    }

    @Test
    public void getPassRateStatisticsCsv_taxYearAndFromDate_illegalArgumentException() {
        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(SOME_TAX_YEAR, null, SOME_DATE, null, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("taxYear")
            .hasMessageContaining("fromDate");
    }

    @Test
    public void getPassRateStatisticsCsv_taxYearAndToDate_illegalArgumentException() {
        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(SOME_TAX_YEAR, null, null, SOME_DATE, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("taxYear")
            .hasMessageContaining("toDate");
    }

    @Test
    public void getPassRateStatisticsCsv_monthAndFromDate_illegalArgumentException() {
        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(null, SOME_YEAR_MONTH, SOME_DATE, null, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("month")
            .hasMessageContaining("fromDate");
    }

    @Test
    public void getPassRateStatisticsCsv_monthAndToDate_illegalArgumentException() {
        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(null, SOME_YEAR_MONTH, null, SOME_DATE, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("month")
            .hasMessageContaining("toDate");
    }

    @Test
    public void getPassRateStatisticsCsv_toDateBeforeFromDate_illegalArgumentException() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = fromDate.minusDays(1);

        assertThatThrownBy(() -> resource.getPassRateStatisticsCsv(null, null, fromDate, toDate, SOME_MODEL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("toDate before the fromDate");
    }

    @Test
    public void getPassRateStatisticsCsv_toDateSameAsFromDate_noException() {
        assertThatCode(() -> resource.getPassRateStatisticsCsv(null, null, SOME_DATE, SOME_DATE, SOME_MODEL))
            .doesNotThrowAnyException();
    }
}

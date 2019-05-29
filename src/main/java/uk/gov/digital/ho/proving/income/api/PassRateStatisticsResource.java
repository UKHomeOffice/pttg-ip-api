package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.digital.ho.proving.income.api.domain.TaxYear;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatistics;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsService;

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@Slf4j
public class PassRateStatisticsResource {

    private final PassRateStatisticsService service;

    public PassRateStatisticsResource(PassRateStatisticsService service) {
        this.service = service;
    }

    @GetMapping(value = "/statistics")
    public void getPassRateStatisticsCsv(@RequestParam(value = "taxYear", required = false) TaxYear taxYear,
                                         @RequestParam(value = "month", required = false) YearMonth month,
                                         @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                         @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                         Model model) {
        log.info("Request for pass rate statistics. taxYear={} month={} fromDate={} toDate={}", taxYear, month, fromDate, toDate);
        validateParameters(taxYear, month, fromDate, toDate);

        PassRateStatistics statistics = getPassRateStatistics(taxYear, month, fromDate, toDate);
        model.addAttribute("statistics", statistics);

        log.info("Returning results for pass rate statistics - fromDate={} toDate={} totalRequests={}",
            statistics.getFromDate(), statistics.getToDate(), statistics.getTotalRequests());
    }

    private PassRateStatistics getPassRateStatistics(TaxYear taxYear, YearMonth month, LocalDate fromDate, LocalDate toDate) {
        if (taxYear != null) {
            return service.generatePassRateStatistics(taxYear);
        }
        if (month != null) {
            return service.generatePassRateStatistics(month);
        }
        return service.generatePassRateStatistics(fromDate, toDate);
    }

    private void validateParameters(TaxYear taxYear, YearMonth month, LocalDate fromDate, LocalDate toDate) {
        if (taxYear == null && month == null && fromDate == null && toDate == null) {
            throw new IllegalArgumentException("No valid arguments provided");
        }

        checkNotBothProvided(taxYear, month, "Cannot have taxYear and month in same request");
        checkNotBothProvided(taxYear, fromDate, "Cannot have taxYear and fromDate in same request");
        checkNotBothProvided(taxYear, toDate, "Cannot have taxYear and toDate in same request");
        checkNotBothProvided(month, fromDate, "Cannot have month and fromDate in same request");
        checkNotBothProvided(month, toDate, "Cannot have month and toDate in same request");

        checkDates(fromDate, toDate);
    }

    private void checkDates(LocalDate fromDate, LocalDate toDate) {
        if (onlyOneProvided(fromDate, toDate)) {
            throw new IllegalArgumentException("Neither or both of fromDate and toDate must be provided");
        }

        if (toDate != null && toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("Can't have a toDate before the fromDate");
        }
    }

    private void checkNotBothProvided(Object object1, Object object2, String message) {
        if (object1 != null && object2 != null) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean onlyOneProvided(LocalDate fromDate, LocalDate toDate) {
        return fromDate == null ^ toDate == null;
    }
}

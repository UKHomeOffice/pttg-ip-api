package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatistics;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsCsvBuilder;
import uk.gov.digital.ho.proving.income.audit.statistics.PassRateStatisticsService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@Slf4j
public class PassRateStatisticsResource {

    private final PassRateStatisticsService passRateStatisticsService;
    private final PassRateStatisticsCsvBuilder csvBuilder;

    public PassRateStatisticsResource(PassRateStatisticsService passRateStatisticsService, PassRateStatisticsCsvBuilder csvBuilder) {
        this.passRateStatisticsService = passRateStatisticsService;
        this.csvBuilder = csvBuilder;
    }

    @RequestMapping(value = "/statistics", produces = "text/csv")
    public void getPassRateStatistics(HttpServletResponse response,
                                      @RequestParam LocalDate fromDate,
                                      @RequestParam LocalDate toDate) throws IOException {
        csvBuilder.csvWriter(new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE));
        response.setContentType("text/csv");

        PassRateStatistics statistics = passRateStatisticsService.generatePassRateStatistics(fromDate, toDate);

        csvBuilder.buildCsv(statistics);
    }
}

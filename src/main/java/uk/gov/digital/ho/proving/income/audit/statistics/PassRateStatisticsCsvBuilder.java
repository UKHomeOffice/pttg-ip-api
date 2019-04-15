package uk.gov.digital.ho.proving.income.audit.statistics;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;
import org.supercsv.io.ICsvBeanWriter;

import java.io.IOException;

@Component
@Accessors(fluent = true)
public class PassRateStatisticsCsvBuilder {

    private static final String[] HEADERS = {
        "From Date",
        "To Date",
        "Total Requests",
        "Pass",
        "Fail",
        "Not Found",
        "Error"
    };

    @Setter
    private ICsvBeanWriter csvWriter;

    public void buildCsv(PassRateStatistics statistics) throws IOException {
        try(ICsvBeanWriter csvBeanWriter = csvWriter) {
            csvBeanWriter.writeHeader(HEADERS);
            csvBeanWriter.write(statistics, HEADERS);
        }
    }
}



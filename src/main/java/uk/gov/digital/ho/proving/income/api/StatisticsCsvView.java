package uk.gov.digital.ho.proving.income.api;

import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class StatisticsCsvView extends AbstractView {

    private static final String[] HEADERS = {
        "From Date",
        "To Date",
        "Total Requests",
        "Passed",
        "Not Passed",
        "Not Found",
        "Error"
    };
    private static final String[] FIELD_MAPPINGS = {
        "fromDate",
        "toDate",
        "totalRequests",
        "passes",
        "failures",
        "notFound",
        "errors"
    };

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("content-type", "text/csv;charset=UTF-8");

        try (ICsvBeanWriter csvWriter = getCsvWriter(response)) {
            csvWriter.writeHeader(HEADERS);
            csvWriter.write(model.get("statistics"), FIELD_MAPPINGS);
        }
    }

    ICsvBeanWriter getCsvWriter(HttpServletResponse response) throws IOException {
        return new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
    }
}

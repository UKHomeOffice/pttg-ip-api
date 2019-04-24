package uk.gov.digital.ho.proving.income.application;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import uk.gov.digital.ho.proving.income.api.StatisticsCsvView;

import java.util.Locale;

public class CsvViewResolver implements ViewResolver {

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        return new StatisticsCsvView();
    }
}

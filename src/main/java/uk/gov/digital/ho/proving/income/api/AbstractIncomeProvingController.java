package uk.gov.digital.ho.proving.income.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class AbstractIncomeProvingController {

    protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected final String CONTENT_TYPE = "Content-type";
    protected final String APPLICATION_JSON = "application/json";

    protected final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    protected abstract <T> T buildErrorResponse(HttpHeaders headers, String statusCode, String statusMessage, HttpStatus status);

    protected Date subtractXDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    protected String sanitiseNino(String nino) {
        return nino.replaceAll("\\s", "").toUpperCase();
    }

    protected void validateNino(String nino) {
        final Pattern pattern = Pattern.compile("^[a-zA-Z]{2}[0-9]{6}[a-dA-D]{1}$");
        if (!pattern.matcher(nino).matches()) {
            throw new IllegalArgumentException("Invalid NINO");
        }
    }

    protected Optional<Date> parseDate(String date) {
        sdf.setLenient(false);
        try {
            return Optional.of(sdf.parse(date));
        } catch (ParseException pe) {
            return Optional.empty();
        }
    }

}

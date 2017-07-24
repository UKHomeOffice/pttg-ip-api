package uk.gov.digital.ho.proving.income.api;

import java.util.regex.Pattern;

public class NinoUtils {
    private NinoUtils() {
        // utility
    }
    public static String sanitiseNino(String nino) {
        return nino.replaceAll("\\s", "").toUpperCase();
    }

    public static void validateNino(String nino) {
        final Pattern pattern = Pattern.compile("^[a-zA-Z]{2}[0-9]{6}[a-dA-D]{1}$");
        if (!pattern.matcher(nino).matches()) {
            throw new IllegalArgumentException("Parameter error: Invalid NINO");
        }
    }

}

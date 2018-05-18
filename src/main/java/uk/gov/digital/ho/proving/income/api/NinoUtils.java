package uk.gov.digital.ho.proving.income.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class NinoUtils {
    private static final Pattern NINO_PATTERN = Pattern.compile("(^((?!(BG|GB|KN|NK|NT|TN|ZZ)|([DFIQUV])[A-Z]|[A-Z]([DFIOQUV]))[A-Z]{2})[0-9]{6}[A-D]?$)");
    public static final int NUMBER_OF_VISIBLE_CHARS = 5;

    public String sanitise(final String nino) {
        if (isNull(nino)) {
            return null;
        }
        return StringUtils.deleteWhitespace(nino).toUpperCase();
    }

    public void validate(final String nino) {
        if (isInvalid(nino)) {
            String redactedNino = isNull(nino) ? "(null)" : redact(nino);
            throw new IllegalArgumentException(String.format("Invalid NINO: %s", redactedNino));
        }
    }

    public String redact(final String nino) {
        final String sanitisedNino = sanitise(nino);

        if (isInvalid(sanitisedNino)) {
            return nino;
        }

        final String firstFourCharacters = StringUtils.left(sanitisedNino, 4);
        final String lastTwoCharacters = StringUtils.right(sanitisedNino, 2);

        return firstFourCharacters + "***" + lastTwoCharacters;
    }

    private boolean isInvalid(final String nino) {
        return !isValid(nino);
    }

    private boolean isValid(final String nino) {
        return nonNull(nino) && NINO_PATTERN.matcher(nino).matches();
    }
}

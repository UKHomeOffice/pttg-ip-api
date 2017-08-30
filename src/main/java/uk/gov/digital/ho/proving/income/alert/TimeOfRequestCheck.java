package uk.gov.digital.ho.proving.income.alert;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditEntryJpaRepository;
import uk.gov.digital.ho.proving.income.audit.AuditEventType;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class TimeOfRequestCheck {
    private final String startTime;
    private final String endTime;
    private final String namespace;
    private final Clock clock;


    public TimeOfRequestCheck(
        @Value("${alert.acceptable.hours.start}") String startTime,
        @Value("${alert.acceptable.hours.end}") String endTime,
        @Value("${auditing.deployment.namespace}") String namespace,
        Clock clock) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.namespace = namespace;
        this.clock = clock;
    }


    public TimeOfRequestUsage check(AuditEntryJpaRepository repository) {
        Long beforeWorkingHours = repository.countEntriesBetweenDates(
            LocalDate.now(clock).atStartOfDay(),
            LocalDate.now(clock).atTime(getStartHour(), getStartMinute()),
            AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE,
            namespace);

        Long afterWorkingHours = repository.countEntriesBetweenDates(
            LocalDate.now(clock).atTime(getEndHour(), getEndMinute()),
            LocalDate.now(clock).atStartOfDay().plusDays(1),
            AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE,
            namespace);

        return new TimeOfRequestUsage(beforeWorkingHours + afterWorkingHours);
    }

    private int getEndHour() {
        return parseLondonTime(endTime).getHour();
    }

    private int getEndMinute() {
        return parseLondonTime(endTime).getMinute();
    }

    private int getStartMinute() {
        return parseLondonTime(startTime).getMinute();
    }

    private int getStartHour() {
        return parseLondonTime(startTime).getHour();
    }

    private LocalTime parseLondonTime(String time) {
        // config is specified as a UK time e.g 07:00 is 07:00 UTC in winter and 06:00 UTC in summer
        return LocalDate.
            now(clock).
            atTime(LocalTime.parse(time)).
            atZone(ZoneId.of("Europe/London")).
            withZoneSameInstant(ZoneId.systemDefault()).
            toLocalTime();
    }
}

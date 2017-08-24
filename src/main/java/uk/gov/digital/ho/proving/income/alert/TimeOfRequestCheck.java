package uk.gov.digital.ho.proving.income.alert;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.audit.AuditEntryJpaRepository;
import uk.gov.digital.ho.proving.income.audit.AuditEventType;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class TimeOfRequestCheck {
    private final String startTime;
    private final String endTime;

    public TimeOfRequestCheck(
        @Value("${alert.acceptable.hours.start}") String startTime,
        @Value("${alert.acceptable.hours.end}") String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimeOfRequestUsage check(AuditEntryJpaRepository repository) {
        Long beforeWorkingHours = repository.countEntriesBetweenDates(
            LocalDate.now().atStartOfDay(),
            LocalDate.now().atTime(getStartHour(), getStartMinute()),
            AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);

        Long afterWorkingHours = repository.countEntriesBetweenDates(
            LocalDate.now().atTime(getEndHour(), getEndMinute()),
            LocalDate.now().atStartOfDay().plusDays(1),
            AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE);

        return new TimeOfRequestUsage(beforeWorkingHours + afterWorkingHours);
    }

    private int getEndHour() {
        return LocalTime.parse(endTime).getHour();
    }

    private int getEndMinute() {
        return LocalTime.parse(endTime).getMinute();
    }

    private int getStartMinute() {
        return LocalTime.parse(startTime).getMinute();
    }

    private int getStartHour() {
        return LocalTime.parse(startTime).getHour();
    }
}

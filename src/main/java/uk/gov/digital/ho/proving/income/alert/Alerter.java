package uk.gov.digital.ho.proving.income.alert;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.alert.sysdig.SuspectUsage;
import uk.gov.digital.ho.proving.income.alert.sysdig.SysdigEventService;

@Component
public class Alerter {
    private final SysdigEventService sysdigEventService;

    public Alerter(SysdigEventService sysdigEventService) {
        this.sysdigEventService = sysdigEventService;
    }

    public void inappropriateUsage(SuspectUsage suspectUsage) {
        if (suspectUsage.getIndividualVolumeUsage().isSuspect()) {
            sysdigEventService.sendUsersExceedUsageThresholdEvent(suspectUsage.getIndividualVolumeUsage());
        }

        if (suspectUsage.getMatchingFailureUsage().isSuspect()) {
            sysdigEventService.sendMatchingFailuresExceedThresholdEvent(suspectUsage.getMatchingFailureUsage());
        }

        if (suspectUsage.getTimeOfRequestUsage().isSuspect()) {
            sysdigEventService.sendRequestsOutsideHoursEvent(suspectUsage.getTimeOfRequestUsage());
        }
    }
}

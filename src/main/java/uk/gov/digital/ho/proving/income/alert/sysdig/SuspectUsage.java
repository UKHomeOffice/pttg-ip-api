package uk.gov.digital.ho.proving.income.alert.sysdig;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.digital.ho.proving.income.alert.IndividualVolumeUsage;
import uk.gov.digital.ho.proving.income.alert.MatchingFailureUsage;
import uk.gov.digital.ho.proving.income.alert.TimeOfRequestUsage;

@EqualsAndHashCode
@Getter
public class SuspectUsage {
    private final IndividualVolumeUsage individualVolumeUsage;
    private final TimeOfRequestUsage timeOfRequestUsage;
    private final MatchingFailureUsage matchingFailureUsage;

    public SuspectUsage(IndividualVolumeUsage individualVolumeUsage, TimeOfRequestUsage timeOfRequestUsage, MatchingFailureUsage matchingFailureUsage) {
        this.individualVolumeUsage = individualVolumeUsage;
        this.timeOfRequestUsage = timeOfRequestUsage;
        this.matchingFailureUsage = matchingFailureUsage;
    }

    public boolean isSuspect() {
        return individualVolumeUsage.isSuspect() || timeOfRequestUsage.isSuspect() || matchingFailureUsage.isSuspect();
    }
}

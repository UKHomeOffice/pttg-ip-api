package uk.gov.digital.ho.proving.income.alert;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.income.alert.sysdig.SuspectUsage;
import uk.gov.digital.ho.proving.income.audit.AuditEntryJpaRepository;

@Component
public class AppropriateUsageChecker {
    private final AuditEntryJpaRepository repository;
    private final Alerter alerter;
    private final IndividualVolumeCheck individualVolumeCheck;
    private final TimeOfRequestCheck timeOfRequestCheck;
    private final MatchingFailureCheck matchingFailureCheck;

    public AppropriateUsageChecker(AuditEntryJpaRepository repository, Alerter alerter, IndividualVolumeCheck individualVolumeCheck, TimeOfRequestCheck timeOfRequestCheck, MatchingFailureCheck matchingFailureCheck) {
        this.repository = repository;
        this.alerter = alerter;
        this.individualVolumeCheck = individualVolumeCheck;
        this.timeOfRequestCheck = timeOfRequestCheck;
        this.matchingFailureCheck = matchingFailureCheck;
    }

    public SuspectUsage precheck() {
        return check();
    }

    public void postcheck(SuspectUsage suspectUsage) {
        SuspectUsage newSuspectUsage = check();
        if (newSuspectUsage.isSuspect() && !newSuspectUsage.equals(suspectUsage)) {
            alerter.inappropriateUsage(suspectUsage, newSuspectUsage);
        }
    }

    private SuspectUsage check() {
        return new SuspectUsage(
            individualVolumeCheck.check(repository),
            timeOfRequestCheck.check(repository),
            matchingFailureCheck.check(repository)
        );
    }
}

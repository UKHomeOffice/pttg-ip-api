package uk.gov.digital.ho.proving.income.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Builder
@Getter
@Accessors(fluent = true)
public class AuditSummary {

    private LocalDate fromDate;
    private LocalDate toDate;
    private int passed;
    private int notPassed;
    private int notFound;
    private int failed;

}

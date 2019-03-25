package uk.gov.digital.ho.proving.income.audit.statistics;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(fluent = true)
class PassRateStatistics {
    private final LocalDate fromDate;
    private final LocalDate toDate;

    private final int totalRequests;
    private final int passes;
    private final int failures;
    private final int notFound;
    private final int errors;
}

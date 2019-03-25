package uk.gov.digital.ho.proving.income.audit;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
class AuditResultByNino {
    private String nino;
    private List<String> correlationIds;
    private LocalDate date;
    private AuditResultType resultType;
}

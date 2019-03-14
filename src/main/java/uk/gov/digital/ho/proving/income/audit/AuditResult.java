package uk.gov.digital.ho.proving.income.audit;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
class AuditResult {

    private String correlationId;
    private LocalDate date;
    private String nino;
    private AuditResultType resultType;

}

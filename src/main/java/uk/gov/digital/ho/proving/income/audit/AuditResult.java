package uk.gov.digital.ho.proving.income.audit;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class AuditResult {

    private LocalDate date;
    private String nino;
    private AuditResultType resultType;

}

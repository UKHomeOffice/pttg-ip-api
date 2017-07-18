package uk.gov.digital.ho.proving.income.domain.hmrc;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Income {
    private BigDecimal payment;
    private LocalDate paymentDate;
    private Integer monthPayNumber;
    private Integer weekPayNumber;
    private String employerPayeReference;
}

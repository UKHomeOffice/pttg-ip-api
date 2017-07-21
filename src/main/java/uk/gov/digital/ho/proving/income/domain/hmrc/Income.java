package uk.gov.digital.ho.proving.income.domain.hmrc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Income {
    @JsonProperty("taxablePayment")
    private BigDecimal payment;
    private LocalDate paymentDate;
    private Integer monthPayNumber;
    private Integer weekPayNumber;
    private String employerPayeReference;
}

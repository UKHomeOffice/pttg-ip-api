package uk.gov.digital.ho.proving.income.domain.hmrc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Income {

    @JsonProperty("taxablePayment")
    private BigDecimal payment;
    @JsonProperty
    private LocalDate paymentDate;
    @JsonProperty
    private Integer monthPayNumber;
    @JsonProperty
    private Integer weekPayNumber;
    @JsonProperty
    private String employerPayeReference;
}

package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
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

    public int yearAndMonth() {
        return paymentDate.getYear() * 100 + paymentDate.getMonthValue();
    }

    public int yearMonthAndEmployer() {
        return yearAndMonth() + 10_000 * employerPayeReference.hashCode();
    }

    public int weekNumberAndEmployer() {
        return weekPayNumber + 10_000 * employerPayeReference.hashCode();
    }

    public Income add(Income otherIncome) {
        return new Income(payment.add(otherIncome.payment), paymentDate, monthPayNumber, weekPayNumber, employerPayeReference);
    }
}

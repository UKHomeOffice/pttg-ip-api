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
import java.util.Objects;

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
        return weekPayNumber + 100 * employerPayeReference.hashCode();
    }


    public Income add(Income otherIncome) {
        final String canNotAddOtherIncomeReason = checkOtherIncomeCanBeAdded(otherIncome);
        if (canNotAddOtherIncomeReason != null) {
            throw new IllegalArgumentException(canNotAddOtherIncomeReason);
        }

        return new Income(payment.add(otherIncome.payment), paymentDate, monthPayNumber, weekPayNumber, employerPayeReference);
    }

    private String checkOtherIncomeCanBeAdded(Income otherIncome) {
        if (!sameFrequency(otherIncome)) {
            return "Can't add a weekly payment to a monthly one.";
        }
        if (!sameMonthPayNumber(otherIncome)) {
            return "Can't add incomes for different month pay numbers.";
        }
        if (!sameWeekPayNumber(otherIncome)) {
            return "Can't add incomes for different week pay numbers.";
        }
        if (yearAndMonth() != otherIncome.yearAndMonth()) {
            return "Can't add payments for different years.";
        }
        if (!sameEmployer(otherIncome)) {
            return "Can't add payments for different employers.";
        }
        return null;
    }

    private boolean sameFrequency(Income otherIncome) {
        return (monthPayNumber != null && otherIncome.monthPayNumber != null) ||
            (weekPayNumber != null && otherIncome.weekPayNumber != null);
    }

    private boolean sameMonthPayNumber(Income otherIncome) {
        return Objects.equals(otherIncome.monthPayNumber, monthPayNumber);
    }

    private boolean sameWeekPayNumber(Income otherIncome) {
        return Objects.equals(otherIncome.weekPayNumber, weekPayNumber);
    }

    private boolean sameEmployer(Income otherIncome) {
        return Objects.equals(employerPayeReference, otherIncome.employerPayeReference);
    }
}

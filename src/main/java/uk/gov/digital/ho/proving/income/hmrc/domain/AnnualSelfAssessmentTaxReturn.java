package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.validator.domain.TaxYear;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class AnnualSelfAssessmentTaxReturn {
    @JsonProperty
    private final String taxYear;
    @JsonProperty
    private final BigDecimal selfEmploymentProfit;

    public boolean isFromTaxYear(TaxYear taxYear) {
        return TaxYear.of(this.taxYear).equals(taxYear);
    }
}

package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnualSelfAssessmentTaxReturn {

    @JsonProperty(value = "taxYear", required = true)
    private final String taxYear;

    @JsonProperty(value = "selfEmploymentProfit", required = true)
    private final BigDecimal selfEmploymentProfit;
}

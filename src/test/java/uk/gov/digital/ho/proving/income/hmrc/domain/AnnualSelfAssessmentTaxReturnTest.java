package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnualSelfAssessmentTaxReturnTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0).createObjectMapper();

    @Test
    public void shouldDeserialize() throws IOException {
        String json = "{ \"taxYear\": \"2018\", \"summaryIncome\": 50 }";

        AnnualSelfAssessmentTaxReturn taxReturn = objectMapper.readValue(json, AnnualSelfAssessmentTaxReturn.class);

        assertThat(taxReturn.taxYear()).isEqualTo("2018");
        assertThat(taxReturn.summaryIncome()).isEqualTo(new BigDecimal("50"));
    }

    @Test
    public void shouldDeserializeWithSelfEmployment() throws IOException {
        String json = "{ \"taxYear\": \"2018\", \"summaryIncome\": 50, \"selfEmploymentProfit\": 0 }";

        AnnualSelfAssessmentTaxReturn taxReturn = objectMapper.readValue(json, AnnualSelfAssessmentTaxReturn.class);

        assertThat(taxReturn.taxYear()).isEqualTo("2018");
        assertThat(taxReturn.summaryIncome()).isEqualTo(new BigDecimal("50"));
    }

    @Test
    public void shouldDeserializeWithZeroSummaryIncome() throws IOException {
        String json = "{ \"taxYear\": \"2018\", \"summaryIncome\": 0, \"selfEmploymentProfit\": 0 }";

        AnnualSelfAssessmentTaxReturn taxReturn = objectMapper.readValue(json, AnnualSelfAssessmentTaxReturn.class);

        assertThat(taxReturn.taxYear()).isEqualTo("2018");
        assertThat(taxReturn.summaryIncome()).isEqualTo(new BigDecimal("0"));
    }

}

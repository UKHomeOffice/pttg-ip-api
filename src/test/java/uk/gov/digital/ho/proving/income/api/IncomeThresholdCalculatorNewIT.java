package uk.gov.digital.ho.proving.income.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = IncomeThresholdCalculatorNew.class,
    properties = {"threshold.yearly.base=1", "threshold.yearly.oneDependant=2", "threshold.yearly.remainingDependantsIncrement=3"})
public class IncomeThresholdCalculatorNewIT {

    @Autowired
    private IncomeThresholdCalculatorNew incomeThresholdCalculatorNew;

    private static final BigDecimal EXPECTED_YEARLY_ZERO_DEPENDANTS = BigDecimal.valueOf(1);
    private static final BigDecimal EXPECTED_YEARLY_ONE_DEPENDANTS = BigDecimal.valueOf(2);
    private static final BigDecimal EXPECTED_YEARLY_TWO_DEPENDANTS = BigDecimal.valueOf(5);

    @Test
    public void autowiring_baseThreshold_wiredFromProperties() {
        assertThat(incomeThresholdCalculatorNew.yearlyThreshold(0)).isEqualTo(EXPECTED_YEARLY_ZERO_DEPENDANTS);
    }

    @Test
    public void autowiring_oneDependantThreshold_wiredFromProperties() {
        assertThat(incomeThresholdCalculatorNew.yearlyThreshold(1)).isEqualTo(EXPECTED_YEARLY_ONE_DEPENDANTS);
    }

    @Test
    public void autowiring_remainingDependantIncrememnt_wiredFromProperties() {
        assertThat(incomeThresholdCalculatorNew.yearlyThreshold(2)).isEqualTo(EXPECTED_YEARLY_TWO_DEPENDANTS);
    }
}

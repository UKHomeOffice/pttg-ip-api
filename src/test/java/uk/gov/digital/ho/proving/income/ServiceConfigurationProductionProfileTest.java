package uk.gov.digital.ho.proving.income;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.proving.income.hmrc.IncomeRecordServiceProductionResponseLogger;
import uk.gov.digital.ho.proving.income.hmrc.ServiceResponseLogger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= RANDOM_PORT, classes = {ServiceRunner.class})
@ActiveProfiles("")
public class ServiceConfigurationProductionProfileTest {

    @Autowired ServiceResponseLogger serviceResponseLogger;

    @Test
    public void shouldUseProductionBeanByDefault() {
        assertThat(serviceResponseLogger).isNotNull();
        assertThat(serviceResponseLogger).isInstanceOf(IncomeRecordServiceProductionResponseLogger.class);
    }

}

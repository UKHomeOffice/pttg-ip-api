package uk.gov.digital.ho.proving.income;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecordServiceNotProductionResponseLogger;
import uk.gov.digital.ho.proving.income.domain.hmrc.ServiceResponseLogger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= RANDOM_PORT, classes = {ServiceRunner.class})
@ActiveProfiles("not-production")
public class ServiceConfigurationNotProductionProfileTest {

    @Autowired ServiceResponseLogger serviceResponseLogger;

    @Test
    public void shouldUseProductionBeanWhenNotTheNotProductionProfile() {
        assertThat(serviceResponseLogger).isNotNull();
        assertThat(serviceResponseLogger).isInstanceOf(IncomeRecordServiceNotProductionResponseLogger.class);
    }

}

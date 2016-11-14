package apidocs.v1;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.gov.digital.ho.proving.income.acl.EarningsService;
import uk.gov.digital.ho.proving.income.acl.IndividualService;

@Profile("test")
@EnableWebMvc
@Configuration
public class TestServiceConfiguration extends WebMvcConfigurerAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(TestServiceConfiguration.class);

    @Bean
    @Primary
    public EarningsService getRevenueService() {
        return Mockito.mock(EarningsService.class);
    }

    @Bean
    @Primary
    public IndividualService getApplicantService() {
        return Mockito.mock(IndividualService.class);
    }


}

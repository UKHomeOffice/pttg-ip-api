package uk.gov.digital.ho.proving.income.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.*;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.hmrc.IncomeRecordServiceNotProductionResponseLogger;
import uk.gov.digital.ho.proving.income.hmrc.IncomeRecordServiceProductionResponseLogger;
import uk.gov.digital.ho.proving.income.hmrc.ServiceResponseLogger;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@EnableWebMvc
@Configuration
@EnableRetry
public class ServiceConfiguration extends WebMvcConfigurerAdapter {

    private final String apiDocsDir;
    private final TimeoutProperties timeoutProperties;

    public ServiceConfiguration(@Value("${apidocs.dir}") String apiDocsDir, TimeoutProperties timeoutProperties) {
        this.apiDocsDir = apiDocsDir;
        this.timeoutProperties = timeoutProperties;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return b;
    }

    @Bean
    public ObjectMapper createObjectMapper() {
        ObjectMapper m = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-M-d")));
        m.registerModule(javaTimeModule);
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        m.enable(SerializationFeature.INDENT_OUTPUT);
        m.writer().withDefaultPrettyPrinter();

        return m;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/" + apiDocsDir + "/**")
            .addResourceLocations("classpath:/" + apiDocsDir + "/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry
            .addViewController("/" + apiDocsDir + "/")
            .setViewName("redirect:/" + apiDocsDir + "/index.html");
        registry
            .addViewController("/")
            .setViewName("redirect:/" + apiDocsDir + "/index.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(createRequestData());
    }

    @Bean
    RequestData createRequestData() {
        return new RequestData();
    }

    @Bean
    RestTemplate createHmrcRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .setReadTimeout(timeoutProperties.getHmrcService().getReadMs())
            .setConnectTimeout(timeoutProperties.getHmrcService().getConnectMs())
            .build();
    }

    @Bean
    RestTemplate createAuditRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .setReadTimeout(timeoutProperties.getAuditService().getReadMs())
            .setConnectTimeout(timeoutProperties.getAuditService().getConnectMs())
            .build();
    }

    @Bean
    @ConditionalOnProperty(value = "feature.logging.not.production", havingValue = "true")
    public ServiceResponseLogger notProdServiceResponseLogger() {
        return new IncomeRecordServiceNotProductionResponseLogger(createObjectMapper());
    }

    @Bean
    @ConditionalOnProperty(value = "feature.logging.not.production", havingValue = "false", matchIfMissing = true)
    public ServiceResponseLogger serviceResponseLogger() {
        return new IncomeRecordServiceProductionResponseLogger();
    }

    @Bean
    public Clock createClock() {
        return Clock.systemDefaultZone();
    }
}

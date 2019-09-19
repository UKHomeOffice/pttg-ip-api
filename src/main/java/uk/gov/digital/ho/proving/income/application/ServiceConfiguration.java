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
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import uk.gov.digital.ho.proving.income.api.RequestData;
import uk.gov.digital.ho.proving.income.hmrc.IncomeRecordServiceNotProductionResponseLogger;
import uk.gov.digital.ho.proving.income.hmrc.IncomeRecordServiceProductionResponseLogger;
import uk.gov.digital.ho.proving.income.hmrc.ServiceResponseLogger;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@EnableWebMvc
@Configuration
@EnableRetry
public class ServiceConfiguration extends WebMvcConfigurerAdapter {

    private final String apiDocsDir;
    private final int restTemplateReadTimeoutInMillis;
    private final int restTemplateConnectTimeoutInMillis;
    private final int hmrcRetryAttempts;
    private final int hmrcRetryDelay;
    private final int auditRetryAttempts;
    private final int auditRetryDelay;

    public ServiceConfiguration(@Value("${apidocs.dir}") String apiDocsDir,
                                @Value("${resttemplate.timeout.read:30000}") int restTemplateReadTimeoutInMillis,
                                @Value("${resttemplate.timeout.connect:30000}") int restTemplateConnectTimeoutInMillis,
                                @Value("#{${hmrc.service.retry.attempts}}") int hmrcRetryAttempts,
                                @Value("#{${hmrc.service.retry.delay}}") int hmrcRetryDelay,
                                @Value("#{${audit.service.retry.attempts}}") int auditRetryAttempts,
                                @Value("#{${audit.service.retry.delay}}") int auditRetryDelay) {
        this.apiDocsDir = apiDocsDir;
        this.restTemplateReadTimeoutInMillis = restTemplateReadTimeoutInMillis;
        this.restTemplateConnectTimeoutInMillis = restTemplateConnectTimeoutInMillis;
        this.hmrcRetryAttempts = hmrcRetryAttempts;
        this.hmrcRetryDelay = hmrcRetryDelay;
        this.auditRetryAttempts = auditRetryAttempts;
        this.auditRetryDelay = auditRetryDelay;
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
    public RequestData createRequestData() {
        return new RequestData();
    }

    @Bean
    public RestTemplate createRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .setReadTimeout(restTemplateReadTimeoutInMillis)
            .setConnectTimeout(restTemplateConnectTimeoutInMillis)
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

    @Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setContentNegotiationManager(manager);

        viewResolver.setViewResolvers(singletonList(new CsvViewResolver()));
        return viewResolver;
    }

    @Bean
    public RetryTemplate hmrcRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(hmrcRetryDelay);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(hmrcRetryAttempts, singletonMap(HttpServerErrorException.class, true));
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public RetryTemplate auditRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(auditRetryDelay);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(auditRetryAttempts, singletonMap(RestClientException.class, true));
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
}

package uk.gov.digital.ho.proving.income;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.*;
import uk.gov.digital.ho.proving.income.acl.EarningsService;
import uk.gov.digital.ho.proving.income.acl.IndividualService;
import uk.gov.digital.ho.proving.income.acl.MongodbBackedApplicantService;
import uk.gov.digital.ho.proving.income.acl.MongodbBackedEarningsService;
import uk.gov.digital.ho.proving.income.logging.LoggingInterceptor;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@EnableWebMvc
@Configuration
public class ServiceConfiguration extends WebMvcConfigurerAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(ServiceConfiguration.class);

    @Value("${mongodb.ssl}")
    private boolean ssl;

    @Value("${mongodb.service}")
    private String mongodbService;

    @Value("${mongodb.connect.timeout.millis}")
    private int mongodbConnectTimeout = 30000;

    @Value("${apidocs.dir}")
    private String apiDocsDir;


    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return b;
    }

    @Bean
    public ObjectMapper getMapper() {
        ObjectMapper m = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-M-d")));
        m.registerModule(javaTimeModule);
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        m.enable(SerializationFeature.INDENT_OUTPUT);
        return m;
    }

    @Bean
    public EarningsService getRevenueService() {
        return new MongodbBackedEarningsService();
    }

    @Bean
    public IndividualService getApplicantService() {
        return new MongodbBackedApplicantService();
    }

    @Bean(name = "applicationsCollection")
    public DBCollection getApplicationsCollection() {
        MongoClient mongoClient = getMongoClient();
        return mongoClient.getDB("test").getCollection("applications");
    }

    @Bean(name = "applicantCollection")
    public DBCollection getApplicantCollection() {
        MongoClient mongoClient = getMongoClient();
        return mongoClient.getDB("test").getCollection("applicants");
    }

    private MongoClient getMongoClient() {
        boolean useHost = (mongodbService != null && !mongodbService.isEmpty());
        MongoClient client;

        if (useHost) {
            final int port = ssl ? 443 : 27017;

            client = new MongoClient(
                new ServerAddress(mongodbService, port),
                MongoClientOptions.builder()
                    .connectTimeout(mongodbConnectTimeout)
                    .serverSelectionTimeout(mongodbConnectTimeout)
                    .sslEnabled(ssl)
                    .build());

            LOGGER.info("MongoClient invoked using [" + mongodbService + "] and port [" + port + "]");
        } else {
            LOGGER.info("MongoClient invoked using default host and port");
            client = new MongoClient();
        }
        return client;
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
        registry.addInterceptor(new LoggingInterceptor());
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}

package apidocs.v1;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.digital.ho.proving.income.ServiceRunner;
import uk.gov.digital.ho.proving.income.acl.EarningsService;
import uk.gov.digital.ho.proving.income.acl.IndividualService;
import uk.gov.digital.ho.proving.income.domain.Income;
import uk.gov.digital.ho.proving.income.domain.IncomeProvingResponse;
import uk.gov.digital.ho.proving.income.domain.Individual;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;
import static org.springframework.restdocs.snippet.Attributes.key;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= RANDOM_PORT, classes = {ServiceRunner.class})
public class ApiDocumentation {

    private static final String BASEPATH = "/incomeproving/v1";

    private static final String EMPLOYER_ONE = "Pizza Hut";
    private static final String EMPLOYER_TWO = "Burger King";

    @Rule
    public JUnitRestDocumentation restDocumentationRule = new JUnitRestDocumentation("build/generated-snippets");

    @Value("${local.server.port}")
    private int port;

    @MockBean
    private EarningsService earningsService;

    @MockBean
    private IndividualService individualService;

    private RequestSpecification documentationSpec;

    private RequestSpecification requestSpec;

    private RestDocumentationFilter document =
        document("{method-name}",
            preprocessRequest(
                prettyPrint(),
                modifyUris()
                    .scheme("https")
                    .host("api.host.address")
                    .removePort()
            ),
            preprocessResponse(
                prettyPrint(),
                removeHeaders("Date", "Connection", "Transfer-Encoding")
            )
        );

    private FieldDescriptor[] individualModelFields = new FieldDescriptor[]{
        fieldWithPath("individual").description("The individual corresponding to this request"),
        fieldWithPath("individual.title").description("The individual's title eg Mrs"),
        fieldWithPath("individual.forename").description("The individual's forename"),
        fieldWithPath("individual.surname").description("The individual's surname"),
        fieldWithPath("individual.nino").description("The individual's NINO")
    };

    private FieldDescriptor[] categoryCheckModelFields = new FieldDescriptor[]{
        fieldWithPath("categoryCheck").description("The financial status category check details"),
        fieldWithPath("categoryCheck.category").description("The category that was checked"),
        fieldWithPath("categoryCheck.passed").description("True if this individual meets the financial status requirements for the given Category, otherwise false"),
        fieldWithPath("categoryCheck.applicationRaisedDate").description("Date of the application"),
        fieldWithPath("categoryCheck.assessmentStartDate").description("Start date of the financial status check based on the application raised date minus 182 days"),
        fieldWithPath("categoryCheck.failureReason").description("Description of the failure reason when passed is not true - see <<Glossary>>")
    };

    private FieldDescriptor[] statusModelFields = new FieldDescriptor[]{
        fieldWithPath("status").description("The result status"),
        fieldWithPath("status.code").description("A numeric code identifying the error condition - see <<Errors>>"),
        fieldWithPath("status.message").description("Details to further explain the error condition")
    };

    private FieldDescriptor[] incomeModelFields = new FieldDescriptor[]{
        fieldWithPath("incomes[]").description("A list of zero or more income payment details, where each entry has the fields described next"),
        fieldWithPath("incomes[].income").description("Amount of this income payment in pounds"),
        fieldWithPath("incomes[].employer").description("Name of employer that was the source of this income payment"),
        fieldWithPath("incomes[].payDate").description("Date of this income payment in the format yyyy-mm-dd"),
        fieldWithPath("total").description("Total of the incomes listed")
    };

    // todo - how to reuse this block in the incomeModelFields using a prefix?
    private FieldDescriptor[] incomeEntryFields = new FieldDescriptor[]{
        fieldWithPath("income").description("Amount of this income payment in pounds"),
        fieldWithPath("employer").description("Name of employer that was the source of this income payment"),
        fieldWithPath("payDate").description("Date of this income payment in the format yyyy-mm-dd"),
    };


    @Before
    public void setUp() {

        RestAssured.port = this.port;
        RestAssured.basePath = BASEPATH;

        requestSpec = new RequestSpecBuilder()
            .setAccept(ContentType.JSON)
            .build();

        this.documentationSpec =
            new RequestSpecBuilder()
                .addFilter(documentationConfiguration(this.restDocumentationRule))
                .addFilter(document)
                .build();


        // Will need something smarter if new tests require different responses
        Individual anIndividual = new Individual("Mr", "Duncan", "Sinclair", "AA123456A");
        String aTotal = "9600";
        String aPayFrequency = "M1";

        when(individualService.lookup(any(String.class), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(new IncomeProvingResponse(anIndividual, someIncomes(), aTotal, aPayFrequency));

        when(earningsService.lookup(any(String.class), any(LocalDate.class)))
            .thenReturn(() -> anIndividual);
    }


    private List<Income> someIncomes() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(LocalDate.of(2015, Month.JANUARY, 15), EMPLOYER_ONE, "1400"));
        incomes.add(new Income(LocalDate.of(2015, Month.MAY, 15), EMPLOYER_ONE, "1600"));
        incomes.add(new Income(LocalDate.of(2015, Month.JUNE, 15), EMPLOYER_ONE, "1600"));
        incomes.add(new Income(LocalDate.of(2015, Month.APRIL, 15), EMPLOYER_ONE, "1600"));
        incomes.add(new Income(LocalDate.of(2015, Month.JULY, 15), EMPLOYER_ONE, "1600"));
        incomes.add(new Income(LocalDate.of(2015, Month.FEBRUARY, 15), EMPLOYER_TWO, "1600"));
        incomes.add(new Income(LocalDate.of(2015, Month.AUGUST, 15), EMPLOYER_ONE, "1600"));
        incomes.add(new Income(LocalDate.of(2015, Month.SEPTEMBER, 15), EMPLOYER_ONE, "1600"));
        return incomes;
    }

    @Test
    public void commonHeaders() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("applicationRaisedDate", "2015-09-23")
            .filter(document.snippets(
                requestHeaders(
                    headerWithName("Accept").description("The requested media type eg application/json. See <<Schema>> for supported media types.")
                ),
                responseHeaders(
                    headerWithName("Content-Type").description("The Content-Type of the payload, e.g. `application/json`")
                )
            ))

            .when().get("/individual/{nino}/financialstatus", "AA123456A")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void missingParameterError() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .filter(document.snippets(
                responseFields(
                    fieldWithPath("status.code").description("A numeric code categorising this error"),
                    fieldWithPath("status.message").description("A description of the error, in this case identifying the missing mandatory parameter")
                )
            ))

            .when().get("/individual/{nino}/financialstatus", "AA123456A")
            .then().assertThat().statusCode(is(400));
    }

    @Test
    public void financialStatus() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("applicationRaisedDate", "2015-09-23")
            .param("dependants", "2")
            .filter(document.snippets(
                responseFields(individualModelFields)
                    .and(categoryCheckModelFields)
                    .and(statusModelFields),
                requestParameters(
                    parameterWithName("applicationRaisedDate")
                        .description("Date of the application. Formatted as `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("dependants")
                        .description("Number of dependants. Optional. Minimum 0, maximum 99.")
//                                        .optional()
//                                         to do - remove following when springrestdocs fixes support for documenting optional
                        .attributes(key("optional").value(true))
                ),
                pathParameters(
                    parameterWithName("nino")
                        .description("The applicant's NINO")
                )
            ))

            .when().get("/individual/{nino}/financialstatus", "AA123456A")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void incomeDetailsEntry() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2015-01-01")
            .param("toDate", "2016-01-15")
            .filter(document.snippets(
                responseFields(
                    fieldWithPath("individual").ignored(),
                    fieldWithPath("total").ignored()
                ).andWithPrefix("incomes[].", incomeEntryFields)
            ))

            .when().get("/individual/{nino}/income", "AA123456A")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void incomeDetailsDateRange() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2015-01-01")
            .param("toDate", "2016-01-15")
            .filter(document.snippets(
                responseFields(individualModelFields)
                    .and(incomeModelFields),
                requestParameters(
                    parameterWithName("fromDate")
                        .description("Earliest date for income details (inclusive). Formatted as `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("toDate")
                        .description("Latest date for income details (inclusive). Formatted as `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false))
                ),
                pathParameters(
                    parameterWithName("nino").description("The indiviudal's NINO")
                )
            ))

            .when().get("/individual/{nino}/income", "QQ654321A")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void incomeDetailsDateRangeEmptyList() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2015-01-01")
            .param("toDate", "2015-01-01")
            .filter(document.snippets(
                responseFields(individualModelFields)
                    .and(fieldWithPath("incomes").description("Zero matching income details entries"),
                        fieldWithPath("total").description("Total of the incomes listed"))
            ))

            .when().get("/individual/{nino}/income", "QQ654321A")
            .then().assertThat().statusCode(is(200));
    }
}

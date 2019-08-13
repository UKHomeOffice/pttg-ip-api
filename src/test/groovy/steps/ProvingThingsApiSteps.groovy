package steps

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.google.gson.Gson
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang.StringUtils
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.servlet.DispatcherServlet
import uk.gov.digital.ho.proving.income.ServiceRunner
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator
import uk.gov.digital.ho.proving.income.audit.AuditClient
import uk.gov.digital.ho.proving.income.hmrc.HmrcClient
import uk.gov.digital.ho.proving.income.hmrc.domain.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static com.jayway.jsonpath.JsonPath.read
import static com.jayway.restassured.RestAssured.given

@ContextConfiguration
@SpringBootTest(classes = [ServiceRunner.class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProvingThingsApiSteps implements ApplicationContextAware {

    private WireMockServer wireMockServer

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private AuditClient auditClient

    @Autowired
    private HmrcClient hmrcClient

    @Autowired
    private IncomeThresholdCalculator incomeThresholdCalculatorNew;

    @Value('${local.server.port}')
    private int port

    @Value('${hmrc.service.port}')
    private int hmrcServicePort

    @Value('${pttg.audit.port}')
    private int auditServicePort

    private static boolean SuiteSetupDone = false

    private static String APP_HOST

    @Before
    void before() throws Exception {
        if (!SuiteSetupDone) {
            APP_HOST = "http://localhost:" + port + "/incomeproving"
            wireMockServer = new WireMockServer(options().dynamicPort())
            wireMockServer.start()
            configureFor(wireMockServer.port())
            overrideClientPorts(wireMockServer.port())
            SuiteSetupDone = true
        } else {
            resetAllScenarios()
        }
    }

    def overrideClientPorts(int newPort) {
        String hmrcUrl = ReflectionTestUtils.getField(hmrcClient, "hmrcServiceEndpoint")
        String auditUrl = ReflectionTestUtils.getField(auditClient, "auditEndpoint")
        ReflectionTestUtils.setField(hmrcClient, "hmrcServiceEndpoint", hmrcUrl.replace(hmrcServicePort.toString(), newPort.toString()))
        ReflectionTestUtils.setField(auditClient, "auditEndpoint", auditUrl.replace(auditServicePort.toString(), newPort.toString()))
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //required for @controllerAdvice to work
        DispatcherServlet ds = applicationContext.getBean("dispatcherServlet")
        ds.setThrowExceptionIfNoHandlerFound(true)
    }

    @Managed
    public Response resp
    String jsonAsString
    String nino1
    String nino2
    String dependants = ""
    String applicationRaisedDate
    String fromDate = ""
    String toDate = ""


    String tocamelcase(String g) {
        StringBuilder sbl = new StringBuilder()

        String firstString
        String nextString
        String finalString = null
        char firstChar

        String[] f = g.split(" ")

        for (int e = 0; e < f.length; e++) {

            if (e == 0) {
                firstString = f[0].toLowerCase()
                sbl.append(firstString)

            }

            if (e > 0) {
                nextString = f[e].toLowerCase()
                firstChar = nextString.charAt(0)
                nextString = nextString.replaceFirst(firstChar.toString(), firstChar.toString().toUpperCase())
                sbl.append(nextString)
            }
            finalString = sbl.toString()

        }
        return finalString

    }

    def String getTableData(DataTable arg) {
        //TODO refactor to reject\identify unrecognised keys

        Map<String, String> entries = arg.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        for (String s : tableKey) {

            if (s.equalsIgnoreCase("application raised date")) {
                applicationRaisedDate = entries.get(s)
            }
            if (s.equalsIgnoreCase("nino - applicant")) {
                nino1 = entries.get(s)
            }
            if (s.equalsIgnoreCase("nino - partner")) {
                nino2 = entries.get(s)
            }
            if (s.equalsIgnoreCase("dependants")) {
                dependants = entries.get(s)
            }
            if (s.equalsIgnoreCase("From Date")) {
                fromDate = entries.get(s)

            }
            if (s.equalsIgnoreCase("To Date")) {
                toDate = entries.get(s)
            }
        }
    }

    //function to loop through three column table
    def checkIncome(DataTable table) {

        List<List<String>> rawData = table.raw()
        def incomes = read(jsonAsString, "incomes")
        assert (incomes.size() >= rawData.size() - 1)

        String total = read(jsonAsString, "total")

        int index = 0

        for (List<String> row : rawData) {

            if (!row.get(0).startsWith("Total")) {
                assert (row.get(0).equals(incomes.get(index).get("payDate")))
                assert (row.get(1).equals(incomes.get(index).get("employer")))
                assert (row.get(2).equals(incomes.get(index).get("income")))
            } else {
                assert (row.get(2).equals(total))
            }
            index++
        }
    }

    /**
     prerequisites:
     - key name has been added to FeatureKeyMapper.java
     - Date values are in the format yyyy-mm-dd
     - boolean values are lowercase
     */
    void validateJsonResult(DataTable dataTable) {
        StepAssertor.validateJsonResult(resp, jsonAsString, dataTable);

     }

    @Given("^A service is consuming the Income Proving TM Family API\$")
    void a_service_is_consuming_the_Income_Proving_TM_Family_API() {
        stubFor(WireMock.post(urlMatching("/audit.*")).
            willReturn(aResponse().withStatus(200)))
    }


    @When("^the Income Proving v3 TM Family API is invoked with the following:\$")
    void theIncomeProvingVTMFamilyAPIIsInvokedWithTheFollowing(DataTable params) throws Throwable {
        getTableData(params)
        Map<String, String> jsonRequest = new HashMap<>();
        jsonRequest.put("applicationRaisedDate", applicationRaisedDate);
        jsonRequest.put("dependants", dependants);

        def applicants = getSingleApplicantJson(nino1)
        if (nino2 != null && !nino2.isEmpty()) {
            applicants.addAll(getSingleApplicantJson(nino2, "Marie", "Surname", "1980-02-15"))
        }
        jsonRequest.put("individuals", applicants)

        resp = given().contentType(ContentType.JSON).body(new Gson().toJson(jsonRequest)).post(APP_HOST + "/v3/individual/financialstatus")

        jsonAsString = resp.asString()
        println "HMRC Json" + jsonAsString
    }

    def getSingleApplicantJson(nino = "nino", forename = "Mark", surname = "Surname", dateOfBirth = "1980-01-13") {
        Map<String, String> applicant = new HashMap<>()
        applicant.put("nino", nino)
        applicant.put("forename", forename)
        applicant.put("surname", surname)
        applicant.put("dateOfBirth", dateOfBirth)

        List<Map<String, String>> applicants = new ArrayList<>()
        applicants.add(applicant)

        return applicants
    }

    @Then("^The Income Proving TM Family API provides the following result:\$")
    void the_Income_Proving_TM_Family_API_provides_the_following_result(DataTable arg1) {
        validateJsonResult(arg1)

    }

    // TODO this is not used?
    //For generic Tool
    @When("^the Income Proving v2 API is invoked with the following:\$")
    void the_Income_Proving_v2_API_is_invoked_with_the_following(DataTable arg1) throws Throwable {
        getTableData(arg1)

        Map<String, String> jsonRequest = new HashMap<>();
        jsonRequest.put("nino", nino1);
        jsonRequest.put("forename", "Mark");
        jsonRequest.put("surname", "Surname");
        jsonRequest.put("dateOfBirth", "1980-01-13");
        jsonRequest.put("fromDate", fromDate);
        jsonRequest.put("toDate", toDate);

        resp = given().contentType(ContentType.JSON).body(new Gson().toJson(jsonRequest)).post(APP_HOST + "/v2/individual/income")

        jsonAsString = resp.asString()
        println "" + jsonAsString
    }


    @Then("^The API provides the following Individual details:\$")
    void the_API_provides_the_following_Individual_details(DataTable arg1) throws Throwable {
        validateJsonResult(arg1)
    }

    @Then("^The API provides the following result:\$")
    void the_API_provides_the_following_details(DataTable expectedResult) throws Throwable {

        checkIncome(expectedResult)
    }

    @Given("^HMRC has the following Self Assessment Returns for nino (.+?):\$")
    void hmrcHasTheFollowingSelfAssessmentReturnsForNino(String nino, DataTable dataTable) {
        def selfAssessmentReturns = dataTable.asList(AnnualSelfAssessmentTaxReturn.class)

        def individual = new HmrcIndividual("Joe", "Bloggs", nino, null)
        def incomeRecord = new IncomeRecord([], selfAssessmentReturns, [], individual)

        def responseData = objectMapper.writeValueAsString(incomeRecord)
        stubFor(post(urlMatching("/income.*"))
            .withRequestBody(equalToJson("{\"nino\":\"" + nino + "\"}", true, true))
            .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(responseData))
        )

        stubFor(post(urlMatching("/audit.*")).
            willReturn(aResponse().withStatus(200)))
    }

    @Given("^HMRC has the following income records:\$")
    void hmrcHasTheFollowingIncomeRecords(DataTable incomeRecords) throws Throwable {

        stubFor(post(urlMatching("/audit.*")).
            willReturn(aResponse().withStatus(200)))

        List<Income> income = incomeRecords.
            raw().
            stream().
            skip(1).
            map({ row -> toIncome(row) }).
            collect()

        List<Employments> employments = incomeRecords.
            raw().
            stream().
            skip(1).
            map({ row -> toEmployment(row) }).
            collect().
            unique { e1, e2 -> e1.employer.payeReference <=> e2.employer.payeReference }

        IncomeRecord incomeRecord = new IncomeRecord(income, new ArrayList<AnnualSelfAssessmentTaxReturn>(), employments, new HmrcIndividual("Joe", "Bloggs", "NE121212A", LocalDate.now()))
        String data = objectMapper.writeValueAsString(incomeRecord)
        stubFor(post(urlMatching("/income.*")).
            inScenario("hmrc applicants").
            whenScenarioStateIs(Scenario.STARTED).
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withBody(data)).
            willSetStateTo("main applicant returned"))
        incomeRecords.raw()
    }

    @Given("^the applicants partner has the following income records:\$")
    void applicantsPartnerHasTheFollowingIncomeRecords(DataTable incomeRecords) throws Throwable {

        stubFor(post(urlMatching("/audit.*")).
            willReturn(aResponse().withStatus(200)))

        List<Income> income = incomeRecords.
            raw().
            stream().
            skip(1).
            map({ row -> toIncome(row) }).
            collect()

        List<Employments> employments = incomeRecords.
            raw().
            stream().
            skip(1).
            map({ row -> toEmployment(row) }).
            collect().
            unique { e1, e2 -> e1.employer.payeReference <=> e2.employer.payeReference }

        IncomeRecord incomeRecord = new IncomeRecord(income, new ArrayList<AnnualSelfAssessmentTaxReturn>(), employments, new HmrcIndividual("Jane", "Bloggs", "OP232323B", LocalDate.now()))
        String data = objectMapper.writeValueAsString(incomeRecord)
        stubFor(post(urlMatching("/income.*")).
            inScenario("hmrc applicants").
            whenScenarioStateIs("main applicant returned").
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withBody(data)).
            willSetStateTo("finished"))
        incomeRecords.raw()
    }

    def toIncome(row) {
        BigDecimal payment = new BigDecimal(row.get(1))
        LocalDate paymentDate = LocalDate.parse(row.get(0), DateTimeFormatter.ofPattern("yyyy-M-d"))
        Integer monthPayNumber = StringUtils.isBlank(row.get(3)) ? null : new Integer(row.get(3))
        Integer weekPayNumber = StringUtils.isBlank(row.get(2)) ? null : new Integer(row.get(2))
        String employerPayeReference = row.get(4)
        new Income(payment, paymentDate, monthPayNumber, weekPayNumber, employerPayeReference)
    }

    def toEmployment(row) {
        String employerPayeReference = row.get(4)
        String employerName = row.get(5)
        new Employments(new Employer(employerName, employerPayeReference))
    }

    @Given("^HMRC has no matching record\$")
    void hmrcHasNoMatchingRecord() throws Throwable {
        stubFor(WireMock.post(urlMatching("/income.*")).
            inScenario("hmrc applicants").
            whenScenarioStateIs(Scenario.STARTED).
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withStatus(404)).
            willSetStateTo("main applicant returned"))

    }

    @Given("^the applicants partner has no matching record\$")
    void partnerHasNoMatchingRecord() throws Throwable {
        stubFor(WireMock.post(urlMatching("/income.*")).
            inScenario("hmrc applicants").
            whenScenarioStateIs("main applicant returned").
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withStatus(404)).
            willSetStateTo("finished"))

    }

    @Given("^The yearly threshold is configured to (.*?):\$")
    void setYearlyThreshold(int threshold) {
        ReflectionTestUtils.setField(incomeThresholdCalculatorNew, "baseThreshold", BigDecimal.valueOf(threshold))
    }

    @Given("^The single dependant yearly threshold is configured to (.*?):\$")
    void setSingleDepedantThreshold(int threshold) {
        ReflectionTestUtils.setField(incomeThresholdCalculatorNew, "oneDependantThreshold", BigDecimal.valueOf(threshold))
    }

    @Given("^The remaining dependants increment is configured to (.*?):\$")
    void setRemainingDependantIncrement(int increment) {
        ReflectionTestUtils.setField(incomeThresholdCalculatorNew, "remainingDependantsIncrement", BigDecimal.valueOf(increment))
    }

}

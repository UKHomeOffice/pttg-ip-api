package steps

import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.json.JSONObject
import org.springframework.beans.BeansException
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.servlet.DispatcherServlet
import uk.gov.digital.ho.proving.income.ServiceConfiguration
import uk.gov.digital.ho.proving.income.ServiceRunner
import uk.gov.digital.ho.proving.income.ApiExceptionHandler

import java.text.DecimalFormat

import static com.jayway.jsonpath.JsonPath.read
import static com.jayway.restassured.RestAssured.get


@SpringApplicationConfiguration(classes = [ServiceConfiguration.class, ServiceRunner.class, ApiExceptionHandler.class])
@WebAppConfiguration
@IntegrationTest()
class ProvingThingsApiSteps implements ApplicationContextAware{

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //required for @controllerAdvice to work
        DispatcherServlet ds = applicationContext.getBean("dispatcherServlet");
        ds.setThrowExceptionIfNoHandlerFound(true)
    }

    @Managed
    public Response resp
    String jsonAsString
    String nino
    String dependants = ""
    String applicationRaisedDate
    String fromDate = ""
    String toDate =""

    public void validateResult(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class);
        String[] tableKey = entries.keySet()
        List<String> allKeys = new ArrayList()
        List<String> allJsonValue = new ArrayList()
        List<String> tableFieldValue = new ArrayList()
        List<String> tableFieldCamelCase = new ArrayList()
        JSONObject json = new JSONObject(jsonAsString);
        DecimalFormat df = new DecimalFormat("#.00")
        double value
        String innerJsonValue
        String jsonValue

        Iterator<String> jasonKey = json.keys()

        while (jasonKey.hasNext()) {
            String key = (String) jasonKey.next();

            if (json.get(key) instanceof JSONObject) {

                String innerValue = json.get(key)
                println "AAAAAAAAAAA" + innerValue
                JSONObject json2 = new JSONObject(innerValue)
                Iterator<String> feild = json2.keys()

                while (feild.hasNext()) {
                    String key2 = (String) feild.next()
                    allKeys.add(key2)
                    println "BBBBBBBBB" + key2
                    if (!(json2.get(key2) instanceof String)) {
                        boolean values = json2.get(key2)
                       // innerJsonValue = String.valueOf(df.format(values))
                        allJsonValue.add(values)
                        println "DDDDDDDDDDD" + innerJsonValue
                    }
                    innerJsonValue = json2.get(key2)
                    if ((key2 != "code") && (key2 != "message")) {
                        allJsonValue.add(innerJsonValue)
                    }
                }
            }
            if (!(json.get(key) instanceof JSONObject)) {

                jsonValue = json.get(key)
                if ((key == "minimum") || (key == "threshold")) {
                    value = json.getDouble(key)
                    jsonValue = String.valueOf(df.format(value))
                    allJsonValue.add(jsonValue)
                }
                allKeys.add(key)
                if (!(allJsonValue.contains(jsonValue))) {
                    allJsonValue.add(jsonValue)
                }
            }
        }
        for (String s : tableKey) {
            if (s != "HTTP Status") {
                tableFieldCamelCase.add(tocamelcase(s))
                tableFieldValue.add(entries.get(s))
            }
        }
        assert allKeys.containsAll(tableFieldCamelCase)
        assert allJsonValue.containsAll(tableFieldValue)
    }

    public String tocamelcase(String g) {
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
            if (s.equalsIgnoreCase("nino")) {
                nino = entries.get(s)
            }
            if (s.equalsIgnoreCase("dependants")) {
                dependants = entries.get(s)
            }
            if (s.equalsIgnoreCase("From Date")) {
                fromDate = entries.get(s)

            }
            if(s.equalsIgnoreCase("To Date")){
                toDate = entries.get(s)
            }
        }
    }

    //function to loop through three column table
    def checkIncome(DataTable table){

        List<List<String>> rawData = table.raw()
        def incomes = read(jsonAsString, "incomes")
        assert(incomes.size() >= rawData.size() -1)

        String total = read(jsonAsString, "total")

        int index =0

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
     - BDD key can be transformed to valid jsonpath OR key name has been added to FeatureKeyMapper.java
     - Date values are in the format yyyy-mm-dd
     - boolean values are lowercase
     */
    public void validateJsonResult(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class);
        String[] tableKey = entries.keySet();

        for (String key : tableKey) {
            switch (key) {
                case "HTTP Status":
                    assert entries.get(key) == resp.getStatusCode().toString();
                    break;
                default:
                    String jsonPath = FeatureKeyMapper.buildJsonPath(key);
                    assert entries.get(key) == read(jsonAsString, jsonPath).toString();
                    println " :" + jsonPath
                    println " :" + jsonAsString
                    println " :" + entries.get(key)

            }
        }
    }

    @Given("^A service is consuming the Income Proving TM Family API\$")
    public void a_service_is_consuming_the_Income_Proving_TM_Family_API() {

    }

    @When("^the Income Proving TM Family API is invoked with the following:\$")
    public void the_Income_Proving_TM_Family_API_is_invoked_with_the_following(DataTable expectedResult) {


        getTableData(expectedResult)
        resp = get("http://localhost:8081/incomeproving/v1/individual/{nino}/financialstatus?applicationRaisedDate={applicationRaisedDate}&dependants={dependants}", nino, applicationRaisedDate, dependants);
        jsonAsString = resp.asString();
        println "Generic Tool Json" + jsonAsString
    }

    @Then("^The Income Proving TM Family API provides the following result:\$")
    public void the_Income_Proving_TM_Family_API_provides_the_following_result(DataTable arg1) {
        validateJsonResult(arg1)

    }

    //For generic Tool
    @When("^the Income Proving API is invoked with the following:\$")
    public void the_Income_Proving_API_is_invoked_with_the_following(DataTable arg1) throws Throwable {
        getTableData(arg1)
        resp = get("http://localhost:8081/incomeproving/v1/individual/{nino}/income?fromDate={fromDate}&toDate={toDate}", nino, fromDate, toDate)

        jsonAsString = resp.asString();
        println "" + jsonAsString
    }

    @Then("^The API provides the following Individual details:\$")
    public void the_API_provides_the_following_Individual_details(DataTable arg1) throws Throwable {
        validateJsonResult(arg1)
    }

    @Then("^The API provides the following result:\$")
    public void the_API_provides_the_following_details(DataTable expectedResult) throws Throwable {

        checkIncome(expectedResult)
    }






}

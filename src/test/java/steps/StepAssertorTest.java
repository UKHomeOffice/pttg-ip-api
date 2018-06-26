package steps;

import com.jayway.restassured.response.Response;
import cucumber.api.DataTable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StepAssertorTest {

    @Mock
    private Response response;

    private String json;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        json = getJson();
    }

    @Test
    public void thatHttpStatusIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("http response", "http status", "200"));
        DataTable dataTable = DataTable.create(rawData);

        when(response.getStatusCode()).thenReturn(200);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatResponseStatusCodeIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("status", "code", "100"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatResponseStatusMessageIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("status", "message", "OK"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatEmptyResponseObjectFails() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("", "key", "expected"));
        DataTable dataTable = DataTable.create(rawData);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No response object provided");

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatUnknownResponseObjectFails() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("unknown response object", "code", "expected"));
        DataTable dataTable = DataTable.create(rawData);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The data to test was not recognised");

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatUnknownKeyFails() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Category X test", "unknown key", "expected"));
        DataTable dataTable = DataTable.create(rawData);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The key was not recognised");

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatMixedCaseDataTableIsHandled() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("StAtUs", "MeSsaGE", "oK"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatApplicantNinoIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Applicant", "National Insurance Number", "AA345678A"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatPartnerNinoIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Partner", "National Insurance Number", "BB345678B"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatCatBNonSalariedPassedIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Category B non salaried", "financial requirement met", "true"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatCatBNonSalariedApplicationRaisedDateIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Category B non salaried", "application raised date", "2018-05-23"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatCatBNonSalariedAssessmentStartDateIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Category B non salaried", "assessment start date", "2017-05-23"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatCatBNonSalariedFailureReasonIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Category B non salaried", "failure reason", "catb_non_salaried_passed"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatCatBNonSalariedThresholdIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Category B non salaried", "threshold", "18600"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatCatBNonSalariedApplicantEmployersIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Category B non salaried", "employer name - applicant", "Flying Pizza Ltd, Flowers 4U Ltd"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }

    @Test
    public void thatCatBNonSalariedPartnerEmployersIsTested() {

        List<List<String>> rawData = Arrays.asList(Arrays.asList("Category B non salaried", "employer name - partner", "Flying Pizza Ltd"));
        DataTable dataTable = DataTable.create(rawData);

        StepAssertor.validateJsonResult(response, json, dataTable);
    }


    private String getJson() {
        return "{\n" +
            "    \"status\": {\n" +
            "        \"code\": \"100\",\n" +
            "        \"message\": \"OK\"\n" +
            "    },\n" +
            "    \"individuals\": [\n" +
            "        {\n" +
            "            \"title\": \"\",\n" +
            "            \"forename\": \"Joe\",\n" +
            "            \"surname\": \"Bloggs\",\n" +
            "            \"nino\": \"AA345678A\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"\",\n" +
            "            \"forename\": \"Jane\",\n" +
            "            \"surname\": \"Bloggs\",\n" +
            "            \"nino\": \"BB345678B\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"categoryChecks\": [\n" +
            "        {\n" +
            "            \"category\": \"A\",\n" +
            "            \"calculationType\": \"Category A Monthly Salary\",\n" +
            "            \"passed\": false,\n" +
            "            \"applicationRaisedDate\": \"2018-05-23\",\n" +
            "            \"assessmentStartDate\": \"2017-11-22\",\n" +
            "            \"failureReason\": \"MONTHLY_VALUE_BELOW_THRESHOLD\",\n" +
            "            \"threshold\": \"1550.00\",\n" +
            "            \"individuals\": [\n" +
            "                {\n" +
            "                    \"nino\": \"AA345678A\",\n" +
            "                    \"employers\": [\n" +
            "                        \"Flying Pizza Ltd\",\n" +
            "                        \"Flowers 4U Ltd\"\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"category\": \"B\",\n" +
            "            \"calculationType\": \"Category B non salaried\",\n" +
            "            \"passed\": true,\n" +
            "            \"applicationRaisedDate\": \"2018-05-23\",\n" +
            "            \"assessmentStartDate\": \"2017-05-23\",\n" +
            "            \"failureReason\": \"CATB_NON_SALARIED_PASSED\",\n" +
            "            \"threshold\": \"18600\",\n" +
            "            \"individuals\": [\n" +
            "                {\n" +
            "                    \"nino\": \"AA345678A\",\n" +
            "                    \"employers\": [\n" +
            "                        \"Flying Pizza Ltd\",\n" +
            "                        \"Flowers 4U Ltd\"\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"nino\": \"BB345678B\",\n" +
            "                    \"employers\": [\n" +
            "                        \"Flying Pizza Ltd\"\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}\n";
    }
}

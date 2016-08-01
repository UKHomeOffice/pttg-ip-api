package steps;

import cucumber.api.DataTable;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class ProvingThingsApiStepsTest {

    public static final String FINANCIAL_REQUIREMENT_JSON = "{\"individual\":{\"nino\":\"AA121212A\",\"forename\":\"Lucy\"}, \"categoryCheck\":{\"passed\":false,\"failureReason\":\"ALL_WRONG\", \"assessmentStartDate\":\"2014-04-05\"}}";

    ProvingThingsApiSteps test = new ProvingThingsApiSteps();

    @Test
    public void validateResultJavaSuccess() throws Exception {

        withFinancialRequirementResponse();

        DataTable dataTable = withFeatureTableData();

        test.validateJsonResult(dataTable);
    }

    @Test(expected = com.jayway.jsonpath.PathNotFoundException.class)
    public void validateResultUnmatchedKey() throws Exception {

        withFinancialRequirementResponse();

        DataTable dataTable = withUnmatchedKeyFeatureTableData();

        test.validateJsonResult(dataTable);
    }


    private DataTable withFeatureTableData() {
        List<List<String>> infoInTheRaw = Arrays.asList(Arrays.asList("National Insurance Number", "AA121212A"),
                Arrays.asList("Individual forename", "Lucy"), Arrays.asList("Financial requirement met", "false"), Arrays.asList("Failure reason", "ALL_WRONG"), Arrays.asList("Application Raised to date", "2014-04-05"));
        return DataTable.create(infoInTheRaw);
    }

    private DataTable withUnmatchedKeyFeatureTableData() {
        List<List<String>> infoInTheRaw = Arrays.asList(Arrays.asList("This is not in the json", "AA121212A"),
                Arrays.asList("Individual forename", "Lucy"), Arrays.asList("Financial requirement met", "false"), Arrays.asList("Failure reason", "ALL_WRONG"), Arrays.asList("Application received to date", "2014-04-05"));
        return DataTable.create(infoInTheRaw);
    }

    private void withFinancialRequirementResponse() {
        test.setJsonAsString(FINANCIAL_REQUIREMENT_JSON);
    }
}

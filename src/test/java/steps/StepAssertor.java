package steps;

import com.jayway.restassured.response.Response;
import cucumber.api.DataTable;
import gherkin.formatter.model.DataTableRow;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.JsonPath.read;

public class StepAssertor {

    private final static Map<String, String> KEY_MAP;

    static {
        KEY_MAP = new HashMap<>();
        KEY_MAP.put("http status", "http status");
        KEY_MAP.put("code", "code");
        KEY_MAP.put("message", "message");
        KEY_MAP.put("national insurance number", "nino");
        KEY_MAP.put("assessment start date", "assessmentStartDate");
        KEY_MAP.put("application raised date", "applicationRaisedDate");
        KEY_MAP.put("financial requirement met", "passed");
        KEY_MAP.put("failure reason", "failureReason");
        KEY_MAP.put("threshold", "threshold");
        KEY_MAP.put("employer name - applicant", "individuals[0].employers");
        KEY_MAP.put("employer name - partner", "individuals[1].employers");

    }

    public static void validateJsonResult(Response response, String json, DataTable assertions) {

        for (DataTableRow row : assertions.getGherkinRows()) {

            List<String> cells = row.getCells();
            String responseObject = cells.get(0).trim();
            String key = cells.get(1).trim().toLowerCase();
            String expected = cells.get(2).trim().toLowerCase();

            validateTestParameters(responseObject, key, expected);

            if (testHttpResponse(response, responseObject, key, expected)) continue;

            if (testResponseStatus(json, responseObject, key, expected)) continue;

            if (testApplicants(json, responseObject, key, expected)) continue;

            if (testApplicantEmployees(json, responseObject, key, expected)) continue;

            if (testCategoryCheck(json, responseObject, key, expected)) continue;

            throw new IllegalArgumentException(String.format("%s %s %s: The data to test was not recognised", responseObject, key, expected));
        }

    }

    private static void assertTrue(boolean test, String errorMessage) {
        if(!test) {
            throw new AssertionError(errorMessage);
        }
    }

    private static boolean testCategoryCheck(String json, String responseObject, String key, String expected) {
        if (responseObject.startsWith("Category ")) {
            String jsonPath = "$.categoryChecks..[?(@.calculationType == '" + responseObject + "')]." + KEY_MAP.get(key);
            JSONArray jsonData = read(json, jsonPath);
            assertTrue(jsonData.size() > 0, String.format("%s %s: Unable to find category check\nJSON: %s", responseObject, key, json));
            String actual = jsonData.get(0).toString().toLowerCase();
            assertTrue(expected.equals(actual), String.format("%s %s: expected %s but actual was %s\nJSON: %s", responseObject, key, expected, actual, json));
            return true;
        }
        return false;
    }

    private static boolean testApplicantEmployees(String json, String responseObject, String key, String expected) {
        if (key.contains("employer name")) {
            String jsonPath = "$.categoryChecks..[?(@.calculationType == '" + responseObject + "')]." + KEY_MAP.get(key);
            JSONArray employersWrapper = read(json, jsonPath);
            List<String> employers = new ArrayList<>();
            Iterator it = ((JSONArray) employersWrapper.get(0)).iterator();
            while (it.hasNext()) {
                employers.add(it.next().toString().toLowerCase());
            }
            List<String> requiredEmployers = Arrays.stream(expected.split(",")).map(String::trim).collect(Collectors.toList());
            assertTrue(employers.size() == requiredEmployers.size(), String.format("%s %s: expected %d employers but actual was %d\nJSON: %s", responseObject, key, requiredEmployers.size(), employers.size(), json));
            assertTrue(employers.containsAll(requiredEmployers), String.format("%s %s: expected employers %s but actual was %s\nJSON: %s", responseObject, key, requiredEmployers, employers, json));
            return true;
        }
        return false;
    }

    private static boolean testApplicants(String json, String responseObject, String key, String expected) {
        if (responseObject.toLowerCase().equals("applicant") || responseObject.toLowerCase().equals("partner")) {
            String jsonPath = "$.individuals";
            jsonPath += responseObject.toLowerCase().equals("applicant") ? "[0]." : "[1].";
            jsonPath += KEY_MAP.get(key);
            String actual = read(json, jsonPath).toString().toLowerCase();
            assertTrue(expected.equals(actual), String.format("%s %s: expected %s but actual was %s\nJSON: %s", responseObject, key, expected, actual, json));
            return true;
        }
        return false;
    }

    private static boolean testResponseStatus(String json, String responseObject, String key, String expected) {
        if (responseObject.toLowerCase().equals("status")) {
            String jsonPath = "$.status." + key;
            String actual = read(json, jsonPath).toString().toLowerCase();
            assertTrue(expected.equals(actual), String.format("Response status %s: expected %s but actual was %s\nJSON: %s", key, expected, actual, json));
            return true;
        }
        return false;
    }

    private static boolean testHttpResponse(Response response, String responseObject, String key, String expected) {
        if (responseObject.toLowerCase().equals("http response")) {
            if (key.toLowerCase().equals("http status")) {
                String actual = Integer.valueOf(response.getStatusCode()).toString();
                assertTrue(expected.equals(actual), String.format("Http Status: expected %s but actual was %s", expected, actual));
                return true;
            }
        }
        return false;
    }

    private static void validateTestParameters(String responseObject, String key, String expected) {
        if (responseObject.equals("")) {
            throw new IllegalArgumentException("No response object provided for key " + key + " with data " + expected);
        }

        if (!KEY_MAP.containsKey(key)) {
            throw new IllegalArgumentException(String.format("The key was not recognised: %s", key));
        }
    }
}

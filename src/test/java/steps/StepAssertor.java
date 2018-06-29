package steps;

import com.jayway.restassured.response.Response;
import cucumber.api.DataTable;
import gherkin.formatter.model.DataTableRow;
import net.minidev.json.JSONArray;

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
        KEY_MAP.put("dependants", "dependants");

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

            if (testNinos(json, responseObject, key, expected.toUpperCase())) continue;

            if (testApplicantEmployees(json, responseObject, key, expected)) continue;

            if (testCategoryCheckNinos(json, responseObject, key, expected.toUpperCase())) continue;

            if (testCategoryCheck(json, responseObject, key, expected)) continue;

            throw new IllegalArgumentException(String.format("%s %s %s: The data to test was not recognised", responseObject, key, expected));
        }

    }

    private static void assertTrue(boolean test, String errorMessage) {
        if(!test) {
            throw new AssertionError(errorMessage);
        }
    }

    private static boolean testHttpResponse(Response response, String responseObject, String key, String expected) {
        if (responseObject.equalsIgnoreCase("http response")) {
            if (key.equalsIgnoreCase("http status")) {
                String actual = Integer.valueOf(response.getStatusCode()).toString();
                assertTrue(expected.equals(actual), String.format("Http Status: expected %s but actual was %s", expected, actual));
                return true;
            }
        }
        return false;
    }

    private static boolean testResponseStatus(String json, String responseObject, String key, String expected) {
        if (responseObject.equalsIgnoreCase("status")) {
            String jsonPath = "$.status." + key;
            String actual = read(json, jsonPath).toString();
            assertTrue(expected.equalsIgnoreCase(actual), String.format("Response status %s: expected %s but actual was %s\nJSON: %s", key, expected, actual, json));
            return true;
        }
        return false;
    }

    private static boolean testNinos(String json, String responseObject, String key, String expected) {
        if (responseObject.equalsIgnoreCase("applicant") || responseObject.equalsIgnoreCase("partner")) {
            String jsonPath = getIndividualNinoJsonPath(expected);
            JSONArray jsonData = read(json, jsonPath);
            assertTrue(!jsonData.isEmpty(), String.format("%s %s: Unable to find nino %s\nJSON: %s", responseObject, key, expected, json));
            return true;
        }
        return false;
    }

    private static boolean testApplicantEmployees(String json, String responseObject, String key, String expected) {
        if (key.contains("employer name")) {
            String nino = key.replace("employer name -", "").trim().toUpperCase();
            String jsonPath = getApplicantEmployeesJsonPath(responseObject, nino);
            JSONArray employersWrapper = read(json, jsonPath);

            List<String> employers = getEmployersList(employersWrapper);
            List<String> requiredEmployers = Arrays.stream(expected.split(",")).map(String::trim).collect(Collectors.toList());

            assertTrue(employers.size() == requiredEmployers.size(),
                String.format("%s %s: expected %d employers but actual was %d\nJSON: %s", responseObject, key, requiredEmployers.size(), employers.size(), json));
            assertTrue(employers.containsAll(requiredEmployers),
                String.format("%s %s: expected employers %s but actual was %s\nJSON: %s", responseObject, key, requiredEmployers, employers, json));
            return true;
        }
        return false;
    }

    private static boolean testCategoryCheckNinos(String json, String responseObject, String key, String expected) {
        if (responseObject.startsWith("Category ") && key.contains("nino")) {
            String jsonPath = getCategoryCheckNinoJsonPath(responseObject, expected);
            JSONArray jsonData = read(json, jsonPath);
            assertTrue(!jsonData.isEmpty(), String.format("%s %s: Unable to find category check nino %s\nJSON: %s", responseObject, key, expected, json));
            return true;
        }
        return false;
    }

    private static boolean testCategoryCheck(String json, String responseObject, String key, String expected) {
        if (responseObject.startsWith("Category ")) {
            String jsonPath = getCategoryCheckPropertyJsonPath(responseObject, key);
            JSONArray jsonData = read(json, jsonPath);
            String actual = jsonData.get(0).toString();

            assertTrue(!jsonData.isEmpty(), String.format("%s %s: Unable to find category check\nJSON: %s", responseObject, key, json));
            assertTrue(expected.equalsIgnoreCase(actual), String.format("%s %s: expected %s but actual was %s\nJSON: %s", responseObject, key, expected, actual, json));
            return true;
        }
        return false;
    }


    private static List<String> getEmployersList(JSONArray employersWrapper) {
        return ((JSONArray) employersWrapper.get(0))
            .stream()
            .map(it -> it.toString().toLowerCase())
            .collect(Collectors.toList());
    }


    private static String getCategoryCheckPropertyJsonPath(String responseObject, String key) {
        return "$.categoryChecks..[?(@.calculationType == '" + responseObject + "')]." + KEY_MAP.get(key);
    }

    private static String getApplicantEmployeesJsonPath(String responseObject, String nino) {
        return "$.categoryChecks..[?(@.calculationType == '" + responseObject + "')].individuals..[?(@.nino == '" + nino + "')].employers";
    }

    private static String getIndividualNinoJsonPath(String expected) {
        return "$.individuals..[?(@.nino == '" + expected + "')].nino";
    }

    private static String getCategoryCheckNinoJsonPath(String responseObject, String expected) {
        return "$.categoryChecks..[?(@.calculationType == '" + responseObject + "')].individuals..[?(@.nino == '" + expected + "')]";
    }


    private static void validateTestParameters(String responseObject, String key, String expected) {
        if (responseObject.equals("")) {
            throw new IllegalArgumentException("No response object provided for key " + key + " with data " + expected);
        }
    }

}

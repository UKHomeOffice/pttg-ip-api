package steps;

import java.util.HashMap;
import java.util.Map;

/* BDD keys can be mapped directly to jsonpath if they are in the format "<keyword> <keyword>"  (separated by a space)
   If a more readable name is required in the automated test  - add an entry to the KEY_MAP.

   Collections are not currently supported
 */

public class FeatureKeyMapper {

    private final static Map<String, String> KEY_MAP;

    static {
        KEY_MAP = new HashMap<>();
        KEY_MAP.put("National Insurance Number", "individuals[0] nino");
        KEY_MAP.put("Assessment start date", "categoryChecks[0] assessmentStartDate");
        KEY_MAP.put("Application Raised date", "categoryChecks[0] applicationRaisedDate");
        KEY_MAP.put("Financial requirement met", "categoryChecks[0] passed");
        KEY_MAP.put("Failure reason", "categoryChecks[0] failureReason");
        KEY_MAP.put("Threshold", "categoryChecks[0] threshold");
        KEY_MAP.put("Employer Name", "categoryChecks[0] individuals[0] employers");

    }
    public static String buildJsonPath(final String key) {
        String resolvedKey = KEY_MAP.getOrDefault(key, key);
        StringBuilder sb = new StringBuilder(resolvedKey.replaceAll(" ", "."));
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        sb.insert(0,"$.");
        return sb.toString();
    }
}

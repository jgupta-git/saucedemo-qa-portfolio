package com.saucedemo.automation.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hooks to post test results to TestRail after each scenario.
 * Reads the @Cxx tag (e.g. @C1, @C2) and posts PASS/FAIL status.
 *
 * Configuration (system properties via Maven -D flags OR environment variables):
 *   TESTRAIL_URL = https://your-domain.testrail.io (no trailing slash)
 *   TESTRAIL_USER = your@email.com
 *   TESTRAIL_API_KEY = your-api-key (get from TestRail account settings)
 *   TESTRAIL_RUN_ID = 3 (the numeric ID of your active test run in TestRail)
 *   TESTRAIL_ENABLED = true (set to false to disable posting, e.g. after trial ends)
 *
 * TestRail API Reference:
 *   POST /index.php?/api/v2/add_result_for_case/{run_id}/{case_id}
 *   https://support.testrail.com/hc/en-us/articles/15758390538260
 *
 * Jenkins Usage:
 *   Maven Goals: clean verify -P saucedemo \
 *     -DTESTRAIL_URL="${TESTRAIL_URL}" \
 *     -DTESTRAIL_USER="${TESTRAIL_USER}" \
 *     -DTESTRAIL_RUN_ID="${TESTRAIL_RUN_ID}" \
 *     -DTESTRAIL_ENABLED="${TESTRAIL_ENABLED}"
 *   Build Environment: Bind TESTRAIL_API_KEY from Jenkins Credentials
 */
public class TestRailReportingHook {

    private static final String TESTRAIL_URL = getConfigValue("TESTRAIL_URL", null);
    private static final String TESTRAIL_USER = getConfigValue("TESTRAIL_USER", null);
    private static final String TESTRAIL_API_KEY = getConfigValue("TESTRAIL_API_KEY", null);
    private static final String TESTRAIL_RUN_ID = getConfigValue("TESTRAIL_RUN_ID", null);
    private static final boolean TESTRAIL_ENABLED = !"false".equalsIgnoreCase(getConfigValue("TESTRAIL_ENABLED", "true"));

    /**
     * Get configuration value from system properties (Maven -D flags) or environment variables.
     * System properties take precedence over environment variables.
     */
    private static String getConfigValue(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        String envValue = System.getenv(key);
        if (envValue != null) {
            return envValue;
        }
        return defaultValue;
    }

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @After
    public void reportToTestRail(Scenario scenario) {
        // Debug logging
        System.out.println("[TestRail Debug] TESTRAIL_ENABLED=" + TESTRAIL_ENABLED);
        System.out.println("[TestRail Debug] TESTRAIL_URL=" + TESTRAIL_URL);
        System.out.println("[TestRail Debug] TESTRAIL_USER=" + TESTRAIL_USER);
        System.out.println("[TestRail Debug] TESTRAIL_API_KEY=" + (TESTRAIL_API_KEY != null ? "SET" : "NULL"));
        System.out.println("[TestRail Debug] TESTRAIL_RUN_ID=" + TESTRAIL_RUN_ID);

        if (!TESTRAIL_ENABLED) {
            System.out.println("[TestRail Debug] SKIPPED: TESTRAIL_ENABLED is false");
            return;
        }
        if (TESTRAIL_URL == null) {
            System.out.println("[TestRail Debug] SKIPPED: TESTRAIL_URL is null");
            return;
        }
        if (TESTRAIL_API_KEY == null) {
            System.out.println("[TestRail Debug] SKIPPED: TESTRAIL_API_KEY is null");
            return;
        }
        if (TESTRAIL_RUN_ID == null) {
            System.out.println("[TestRail Debug] SKIPPED: TESTRAIL_RUN_ID is null");
            return;
        }

        // Extract @Cxx tag from scenario (e.g. @C1, @C5, @C13)
        String caseId = extractCaseId(scenario);
        if (caseId == null) {
            // Scenario doesn't have @Cxx tag, skip
            return;
        }

        // Determine status: 1 = passed, 5 = failed
        int statusId = scenario.isFailed() ? 5 : 1;
        String comment = scenario.isFailed() ? "Test failed" : "Test passed";

        // Post the result
        postTestResult(caseId, statusId, comment, scenario);
    }

    /**
     * Extract the numeric case ID from @Cxx tag (e.g. @C2 -> "2", @C13 -> "13").
     */
    private String extractCaseId(Scenario scenario) {
        Pattern pattern = Pattern.compile("@C(\\d+)");
        for (String tag : scenario.getSourceTagNames()) {
            Matcher matcher = pattern.matcher(tag);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    /**
     * POST to TestRail API: add_result_for_case
     * Endpoint: POST /index.php?/api/v2/add_result_for_case/{run_id}/{case_id}
     *
     * NOTE: This endpoint takes only run_id and case_id — NOT project_id.
     */
    private void postTestResult(String caseId, int statusId, String comment, Scenario scenario) {
        try {
            // Construct TestRail API endpoint
            // Correct format: /api/v2/add_result_for_case/{run_id}/{case_id}
            String apiUrl = String.format(
                "%s/index.php?/api/v2/add_result_for_case/%s/%s",
                TESTRAIL_URL,
                TESTRAIL_RUN_ID,
                caseId
            );

            // Log the URL for debugging
            System.out.println("[TestRail Debug] POST " + apiUrl);

            // Build JSON body
            String jsonBody = String.format(
                "{\"status_id\": %d, \"comment\": \"%s\"}",
                statusId,
                escapeJsonString(comment)
            );

            // Build HTTP request with Basic auth
            String auth = Base64.getEncoder().encodeToString(
                (TESTRAIL_USER + ":" + TESTRAIL_API_KEY).getBytes()
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(apiUrl))
                .header("Authorization", "Basic " + auth)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            // Send request
            HttpResponse<String> response = HTTP_CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 400) {
                System.err.println("TestRail API error: " + response.statusCode() + " - " + response.body());
            } else {
                System.out.println("TestRail: Case C" + caseId + " reported as " + (statusId == 1 ? "PASSED" : "FAILED"));
            }
        } catch (Exception e) {
            System.err.println("Failed to report to TestRail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Escape special characters in JSON strings.
     */
    private String escapeJsonString(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

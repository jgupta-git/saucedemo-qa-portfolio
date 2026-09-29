package com.saucedemo.automation.hooks;

import com.saucedemo.automation.util.ScreenshotHelper;
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
 * Reads the @Cxx tag (e.g. @C1, @C2) and posts PASS/FAIL status + screenshot.
 *
 * Environment variables:
 *   TESTRAIL_URL = https://your-domain.testrail.io (no trailing slash)
 *   TESTRAIL_USER = your@email.com
 *   TESTRAIL_API_KEY = your-api-key (get from TestRail account settings)
 *   TESTRAIL_PROJECT_ID = 1 (the numeric ID of your SauceDemo project in TestRail)
 *   TESTRAIL_ENABLED = true (set to false to disable posting, e.g. after trial ends)
 */
public class TestRailReportingHook {

    private static final String TESTRAIL_URL = System.getenv("TESTRAIL_URL");
    private static final String TESTRAIL_USER = System.getenv("TESTRAIL_USER");
    private static final String TESTRAIL_API_KEY = System.getenv("TESTRAIL_API_KEY");
    private static final String TESTRAIL_PROJECT_ID = System.getenv().getOrDefault("TESTRAIL_PROJECT_ID", "1");
    private static final boolean TESTRAIL_ENABLED = !"false".equalsIgnoreCase(System.getenv().getOrDefault("TESTRAIL_ENABLED", "true"));

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @After
    public void reportToTestRail(Scenario scenario) {
        if (!TESTRAIL_ENABLED || TESTRAIL_URL == null || TESTRAIL_API_KEY == null) {
            return; // TestRail reporting disabled or not configured
        }

        // Extract @Cxx tag from scenario (e.g. @C1, @C5, @C13)
        String caseId = extractCaseId(scenario);
        if (caseId == null) {
            // Scenario doesn't have @Cxx tag, skip
            return;
        }

        // Determine status: 1 = passed, 5 = failed
        int statusId = scenario.isFailed() ? 5 : 1;
        String comment = scenario.isFailed()
            ? "Test failed: " + scenario.getError().getMessage()
            : "Test passed";

        // Post the result
        postTestResult(caseId, statusId, comment, scenario);
    }

    /**
     * Extract the @Cxx tag (e.g. @C1, @C12) from scenario tags.
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
     * https://docs.testrail.com/reference/add-result-for-case
     */
    private void postTestResult(String caseId, int statusId, String comment, Scenario scenario) {
        try {
            // Construct TestRail API endpoint
            String apiUrl = String.format(
                "%s/index.php?/api/v2/add_result_for_case/%s/%s",
                TESTRAIL_URL,
                TESTRAIL_PROJECT_ID,
                caseId
            );

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

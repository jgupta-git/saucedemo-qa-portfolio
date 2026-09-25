package com.saucedemo.automation.steps;

import com.saucedemo.automation.config.CredentialsReader;
import com.saucedemo.automation.dto.LoginCredentials;
import com.saucedemo.automation.services.LoginService;
import com.saucedemo.automation.services.ProductCatalogService;
import com.saucedemo.automation.services.SessionService;
import com.saucedemo.automation.util.ImageComparator;
import com.saucedemo.automation.util.ScreenshotHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Element-level visual regression, scoped to a single deterministic image
 * rather than a full-page screenshot. visual_user's prices are randomized
 * per session (verified live), which would make a full-page pixel diff
 * flaky; the backpack image, by contrast, is consistently broken for that
 * account, so it's the one stable target for this technique.
 *
 * No saved baseline file: standard_user's image is captured live within
 * the same scenario and compared in memory. That avoids a whole class of
 * baseline-file problems (missing file, stale file, separate capture step
 * to maintain) - it only works here because a known-good account
 * (standard_user) is available to compare against live; a more general
 * "did the rendering change from last time" check would still need a
 * persisted baseline.
 */
public class VisualRegressionSteps {

    private static final double MAX_FRACTION_DIFFERENT = 0.02;

    private final ProductCatalogService productCatalogService = new ProductCatalogService();
    private final SessionService sessionService = new SessionService();
    private final LoginService loginService = new LoginService();
    private byte[] knownGoodImage;

    @And("I capture the backpack product image as the known-good image")
    public void i_capture_the_backpack_image_as_the_known_good_image() {
        knownGoodImage = productCatalogService.backpackImageLocator().screenshot();
        ScreenshotHelper.attachFile(knownGoodImage, "image/png", "Known-good backpack image (standard_user)");
    }

    @When("I log out and log in to SauceDemo as {string}")
    public void i_log_out_and_log_in_as(String username) {
        sessionService.logout();
        String password = CredentialsReader.loadCredentials().getPassword();
        loginService.login(new LoginCredentials(username, password));
    }

    @Then("the backpack product image should match the known-good image")
    public void the_backpack_image_should_match_the_known_good_image() {
        byte[] actual = productCatalogService.backpackImageLocator().screenshot();
        ScreenshotHelper.attachFile(actual, "image/png", "Actual backpack image (standard_user)");
        ScreenshotHelper.log("Pixel diff <= " + (MAX_FRACTION_DIFFERENT * 100) + "% => MATCH");
        assertTrue("Backpack image does not match its own known-good capture from moments earlier",
                ImageComparator.imagesMatch(actual, knownGoodImage, MAX_FRACTION_DIFFERENT));
    }

    @Then("the backpack product image should not match the known-good image")
    public void the_backpack_image_should_not_match_the_known_good_image() {
        byte[] actual = productCatalogService.backpackImageLocator().screenshot();
        ScreenshotHelper.attachFile(actual, "image/png", "Actual backpack image (visual_user)");
        ScreenshotHelper.log("visual_user's image differs from known-good => EXPECTED MISMATCH");
        assertFalse("Expected visual_user's backpack image to visibly differ from the known-good capture, "
                        + "but it matched - has this bug been fixed?",
                ImageComparator.imagesMatch(actual, knownGoodImage, MAX_FRACTION_DIFFERENT));
    }
}

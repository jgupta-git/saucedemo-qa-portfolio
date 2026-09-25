package com.saucedemo.automation.steps;

import com.saucedemo.automation.services.SessionService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertFalse;

public class SessionManagementSteps {

    private final SessionService sessionService = new SessionService();

    @When("I add {string} to my cart")
    public void i_add_one_item_to_my_cart(String productName) {
        sessionService.addItemToCart(productName);
    }

    @And("I reset the app state via the hamburger menu")
    public void i_reset_the_app_state_via_the_hamburger_menu() {
        sessionService.resetAppState();
    }

    @Then("the cart badge should be cleared")
    public void the_cart_badge_should_be_cleared() {
        assertFalse("Expected the cart badge to be cleared after Reset App State", sessionService.cartBadgeVisible());
    }

    @When("I log out")
    public void i_log_out() {
        sessionService.logout();
    }

    @And("I go back in the browser")
    public void i_go_back_in_the_browser() {
        sessionService.goBackInBrowser();
    }
}

package com.saucedemo.automation.steps;

import com.saucedemo.automation.config.CredentialsReader;
import com.saucedemo.automation.dto.LoginCredentials;
import com.saucedemo.automation.services.LoginService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginSteps {

    private final LoginService loginService = new LoginService();

    @Given("I am on the SauceDemo login page")
    public void i_am_on_the_saucedemo_login_page() {
        loginService.openLoginPage();
    }

    @When("I log in with username {string} and password {string}")
    public void i_log_in_with_username_and_password(String username, String password) {
        loginService.login(new LoginCredentials(username, password));
    }

    /** For scenarios that need the real, working password without spelling it out in the feature file. */
    @When("I log in with username {string} using the universal password")
    public void i_log_in_with_username_using_the_universal_password(String username) {
        String password = CredentialsReader.loadCredentials().getPassword();
        loginService.login(new LoginCredentials(username, password));
    }

    @Then("I should be logged in successfully")
    public void i_should_be_logged_in_successfully() {
        assertTrue("Expected to land on the inventory page after login", loginService.loginSucceeded());
    }

    @Then("I should see the login error {string}")
    public void i_should_see_the_login_error(String expectedError) {
        assertTrue("Expected a login error message to be shown", loginService.errorShown());
        assertEquals(expectedError, loginService.errorText());
    }
}

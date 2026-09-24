package com.saucedemo.automation.steps;

import com.saucedemo.automation.config.CredentialsReader;
import com.saucedemo.automation.services.LoginService;
import io.cucumber.java.en.Given;

/**
 * Step shared by more than one feature file. Cucumber's glue is scanned
 * package-wide, so this single definition serves every runner whose glue
 * includes com.saucedemo.automation.steps.
 */
public class CommonSteps {

    private final LoginService loginService = new LoginService();

    @Given("I am logged in to SauceDemo")
    public void i_am_logged_in_to_saucedemo() {
        loginService.openLoginPage();
        loginService.login(CredentialsReader.loadCredentials());
    }
}

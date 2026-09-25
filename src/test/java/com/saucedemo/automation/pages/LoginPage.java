package com.saucedemo.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.saucedemo.automation.config.TestConfig;
import com.saucedemo.automation.dto.LoginCredentials;

/**
 * Page Object for the SauceDemo login page. Locators use the app's own
 * data-test attributes (verified live) rather than text/CSS - SauceDemo
 * tags nearly every element with data-test specifically for automation,
 * so it's the most stable selector strategy available here.
 */
public class LoginPage extends BasePage {

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        super(page);
        this.usernameInput = page.locator("[data-test='username']");
        this.passwordInput = page.locator("[data-test='password']");
        this.loginButton = page.locator("[data-test='login-button']");
        this.errorMessage = page.locator("[data-test='error']");
    }

    public LoginPage open() {
        page.navigate(TestConfig.baseUrl() + "/");
        usernameInput.waitFor();
        return this;
    }

    public void login(LoginCredentials credentials) {
        usernameInput.fill(credentials.getUsername());
        passwordInput.fill(credentials.getPassword());
        loginButton.click();
    }

    public boolean isErrorMessageShown() {
        try {
            errorMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            return errorMessage.isVisible();
        } catch (TimeoutError e) {
            return false;
        }
    }

    public String errorMessageText() {
        return errorMessage.innerText();
    }
}

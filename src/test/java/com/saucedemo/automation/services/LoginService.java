package com.saucedemo.automation.services;

import com.saucedemo.automation.driver.DriverManager;
import com.saucedemo.automation.dto.LoginCredentials;
import com.saucedemo.automation.pages.InventoryPage;
import com.saucedemo.automation.pages.LoginPage;

/** Service layer for login. Step Definitions call this, never the Page Object directly. */
public class LoginService {

    private final LoginPage loginPage;

    public LoginService() {
        this.loginPage = new LoginPage(DriverManager.getPage());
    }

    public void openLoginPage() {
        loginPage.open();
    }

    public void login(LoginCredentials credentials) {
        loginPage.login(credentials);
    }

    public boolean errorShown() {
        return loginPage.isErrorMessageShown();
    }

    public String errorText() {
        return loginPage.errorMessageText();
    }

    public boolean loginSucceeded() {
        return new InventoryPage(DriverManager.getPage()).isDisplayed();
    }
}

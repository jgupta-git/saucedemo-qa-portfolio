package com.saucedemo.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.automation.dto.CheckoutInfo;

/** Page Object for checkout step one - "Your Information" (checkout-step-one.html). */
public class CheckoutStepOnePage extends BasePage {

    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator postalCodeInput;
    private final Locator continueButton;

    public CheckoutStepOnePage(Page page) {
        super(page);
        this.firstNameInput = page.locator("[data-test='firstName']");
        this.lastNameInput = page.locator("[data-test='lastName']");
        this.postalCodeInput = page.locator("[data-test='postalCode']");
        this.continueButton = page.locator("[data-test='continue']");
    }

    public CheckoutStepTwoPage fillInfoAndContinue(CheckoutInfo info) {
        firstNameInput.fill(info.getFirstName());
        lastNameInput.fill(info.getLastName());
        postalCodeInput.fill(info.getZipCode());
        continueButton.click();
        return new CheckoutStepTwoPage(page);
    }
}

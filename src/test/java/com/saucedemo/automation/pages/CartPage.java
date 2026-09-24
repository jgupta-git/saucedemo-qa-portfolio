package com.saucedemo.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Page Object for the SauceDemo cart page (cart.html). */
public class CartPage extends BasePage {

    private final Locator checkoutButton;

    public CartPage(Page page) {
        super(page);
        this.checkoutButton = page.locator("[data-test='checkout']");
    }

    public CheckoutStepOnePage checkout() {
        checkoutButton.click();
        return new CheckoutStepOnePage(page);
    }
}

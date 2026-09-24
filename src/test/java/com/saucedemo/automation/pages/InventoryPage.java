package com.saucedemo.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the SauceDemo product listing (inventory.html).
 * Add-to-cart buttons are tagged data-test="add-to-cart-<slug>" where
 * <slug> is the product name lowercased with spaces turned into hyphens
 * (verified live, e.g. "Sauce Labs Backpack" -> "sauce-labs-backpack").
 */
public class InventoryPage extends BasePage {

    private final Locator cartLink;

    public InventoryPage(Page page) {
        super(page);
        this.cartLink = page.locator("[data-test='shopping-cart-link']");
    }

    public InventoryPage addToCart(String productName) {
        page.locator("[data-test='add-to-cart-" + slug(productName) + "']").click();
        return this;
    }

    public CartPage openCart() {
        cartLink.click();
        return new CartPage(page);
    }

    private String slug(String productName) {
        return productName.toLowerCase().replaceAll("\\s+", "-");
    }
}

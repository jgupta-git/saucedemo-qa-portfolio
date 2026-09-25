package com.saucedemo.automation.services;

import com.saucedemo.automation.driver.DriverManager;
import com.saucedemo.automation.pages.InventoryPage;
import com.microsoft.playwright.Locator;

import java.math.BigDecimal;
import java.util.List;

/** Service layer for product catalog sorting and image integrity. */
public class ProductCatalogService {

    private final InventoryPage inventoryPage;

    public ProductCatalogService() {
        this.inventoryPage = new InventoryPage(DriverManager.getPage());
    }

    public void sortBy(String optionLabel) {
        inventoryPage.sortBy(optionLabel);
    }

    public List<BigDecimal> productPrices() {
        return inventoryPage.productPricesInOrder();
    }

    public List<String> productImageSources() {
        return inventoryPage.productImageSources();
    }

    /**
     * Exposes the raw Locator (rather than a plain value) since Playwright's
     * screenshot assertion operates directly on a Locator - the Step layer
     * calls the assertion itself, same as it calls JUnit's assertEquals.
     */
    public Locator backpackImageLocator() {
        return inventoryPage.backpackImage();
    }
}

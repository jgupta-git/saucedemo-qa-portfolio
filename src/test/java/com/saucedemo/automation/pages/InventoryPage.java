package com.saucedemo.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.saucedemo.automation.util.ScreenshotHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the SauceDemo product listing (inventory.html).
 * Add-to-cart buttons are tagged data-test="add-to-cart-<slug>" where
 * <slug> is the product name lowercased with spaces turned into hyphens
 * (verified live, e.g. "Sauce Labs Backpack" -> "sauce-labs-backpack").
 * Product images use the same slug pattern: data-test="inventory-item-<slug>-img".
 */
public class InventoryPage extends BasePage {

    private final Locator cartLink;
    private final Locator cartBadge;
    private final Locator pageTitle;
    private final Locator sortDropdown;
    private final Locator itemNames;
    private final Locator itemPrices;
    private final Locator itemImages;
    private final Locator backpackImage;

    public InventoryPage(Page page) {
        super(page);
        this.cartLink = page.locator("[data-test='shopping-cart-link']");
        this.cartBadge = page.locator("[data-test='shopping-cart-badge']");
        this.pageTitle = page.locator("[data-test='title']");
        this.sortDropdown = page.locator("[data-test='product-sort-container']");
        this.itemNames = page.locator("[data-test='inventory-item-name']");
        this.itemPrices = page.locator("[data-test='inventory-item-price']");
        this.itemImages = page.locator("img[data-test^='inventory-item-']");
        this.backpackImage = page.locator("[data-test='inventory-item-sauce-labs-backpack-img']");
    }

    /**
     * Generous timeout: performance_glitch_user is deliberately slow to
     * reach this page after login (verified live), so a short wait would
     * produce a false negative for that user specifically.
     */
    public boolean isDisplayed() {
        try {
            pageTitle.waitFor(new Locator.WaitForOptions().setTimeout(15000));
            ScreenshotHelper.capture("Successful Login");
            return "Products".equals(pageTitle.innerText());
        } catch (TimeoutError e) {
            return false;
        }
    }

    public InventoryPage addToCart(String productName) {
        page.locator("[data-test='add-to-cart-" + slug(productName) + "']").click();
        ScreenshotHelper.capture("add to cart");
        return this;
    }

    public CartPage openCart() {
        cartLink.click();
        return new CartPage(page);
    }

    public boolean isCartBadgeVisible() {
    	ScreenshotHelper.capture("cart");
        return cartBadge.isVisible();
    }

    /** Verified live: the dropdown's visible option text is a plain label, e.g. "Price (high to low)". */
    public InventoryPage sortBy(String optionLabel) {
        sortDropdown.selectOption(new SelectOption().setLabel(optionLabel));
        page.waitForLoadState(LoadState.NETWORKIDLE);
    	ScreenshotHelper.capture("Product Image");
        return this;
    }

    public List<String> productNamesInOrder() {
        return itemNames.allInnerTexts();
    }

    public List<BigDecimal> productPricesInOrder() {
    	page.waitForLoadState(LoadState.NETWORKIDLE);
    	ScreenshotHelper.capture("Product Image");
        return itemPrices.allInnerTexts().stream()
                .map(text -> new BigDecimal(text.replace("$", "")))
                .collect(Collectors.toList());
    }

    public List<String> productImageSources() {
    	page.waitForLoadState(LoadState.NETWORKIDLE);
    	ScreenshotHelper.capture("Product Image");
    	return itemImages.all().stream()
                .map(image -> image.getAttribute("src"))
                .collect(Collectors.toList());
    }

    /** Verified live: consistently broken for visual_user (always the same wrong image), unlike the randomized prices. */
    public Locator backpackImage() {
        return backpackImage;
    }

    private String slug(String productName) {
        return productName.toLowerCase().replaceAll("\\s+", "-");
    }
}

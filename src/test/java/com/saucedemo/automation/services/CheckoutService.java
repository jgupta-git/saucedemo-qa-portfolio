package com.saucedemo.automation.services;

import com.saucedemo.automation.driver.DriverManager;
import com.saucedemo.automation.dto.CheckoutInfo;
import com.saucedemo.automation.dto.ReceiptSummary;
import com.saucedemo.automation.pages.CartPage;
import com.saucedemo.automation.pages.CheckoutCompletePage;
import com.saucedemo.automation.pages.CheckoutStepOnePage;
import com.saucedemo.automation.pages.CheckoutStepTwoPage;
import com.saucedemo.automation.pages.InventoryPage;
import com.saucedemo.automation.util.PdfReceiptParser;
import com.saucedemo.automation.util.ScreenshotHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Service layer for the cart -> checkout -> PDF receipt flow. Orchestrates
 * the Page Objects and owns the page-to-page state (each checkout step
 * hands back the next page); Step Definitions call this, never the Page
 * Objects directly.
 */
public class CheckoutService {

    private final InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutStepTwoPage checkoutStepTwoPage;
    private CheckoutCompletePage checkoutCompletePage;
    private ReceiptSummary onScreenSummary;
    private Path downloadedPdfPath;

    public CheckoutService() {
        this.inventoryPage = new InventoryPage(DriverManager.getPage());
    }

    public void addItemsToCart(List<String> productNames) {
        for (String name : productNames) {
            inventoryPage.addToCart(name);
        }
    }

    public void openCart() {
        CartPage cartPage = inventoryPage.openCart();
        ScreenshotHelper.capture("Cart page");
        this.cartPage = cartPage;
    }

    public void proceedThroughCheckout(CheckoutInfo info) {
        CheckoutStepOnePage stepOne = cartPage.checkout();
        ScreenshotHelper.capture("Checkout - your information");
        checkoutStepTwoPage = stepOne.fillInfoAndContinue(info);
        ScreenshotHelper.capture("Checkout - order summary");
        onScreenSummary = checkoutStepTwoPage.readSummary();
    }

    public boolean orderCompleted() {
    	checkoutCompletePage = checkoutStepTwoPage.finish();
        ScreenshotHelper.capture("Checkout complete");
        return checkoutCompletePage.isDisplayed();
    }

    public ReceiptSummary onScreenSummary() {
        return onScreenSummary;
    }

    public ReceiptSummary downloadAndParsePdfReceipt(Path targetDir) {
        downloadedPdfPath = checkoutCompletePage.generatePdfOrder(targetDir);
        ScreenshotHelper.capture("Checkout complete - before PDF parse");
        try {
            byte[] pdfBytes = Files.readAllBytes(downloadedPdfPath);
            ScreenshotHelper.attachFile(pdfBytes, "application/pdf", "Downloaded receipt PDF");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read PDF for report attachment", e);
        }
        return PdfReceiptParser.parse(downloadedPdfPath);
    }

    public Path downloadedPdfPath() {
        return downloadedPdfPath;
    }
}

package com.saucedemo.automation.pages;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Page Object for the checkout completion page (checkout-complete.html).
 *
 * "Generate PDF order" doesn't hit the server - verified live, it loads
 * @react-pdf/renderer in the browser and builds the PDF client-side, then
 * triggers a normal browser file download via a blob URL. Playwright's
 * download event fires the same way it would for a server-served file, so
 * no special handling is needed beyond the standard waitForDownload pattern.
 */
public class CheckoutCompletePage extends BasePage {

    private final Locator completeHeader;
    private final Locator generatePdfButton;

    public CheckoutCompletePage(Page page) {
        super(page);
        this.completeHeader = page.locator("[data-test='complete-header']");
        this.generatePdfButton = page.locator("[data-test='generate-pdf-order']");
    }

    public boolean isDisplayed() {
        return completeHeader.isVisible();
    }

    /** Clicks "Generate PDF order", waits for the resulting download, and saves it under targetDir. */
    public Path generatePdfOrder(Path targetDir) {
        Download download = page.waitForDownload(generatePdfButton::click);
        Path destination = targetDir.resolve(download.suggestedFilename());
        download.saveAs(destination);
        return destination;
    }
}

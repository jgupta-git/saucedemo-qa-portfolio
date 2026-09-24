package com.saucedemo.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.automation.dto.LineItem;
import com.saucedemo.automation.dto.ReceiptSummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Page Object for checkout step two - "Overview" (checkout-step-two.html). */
public class CheckoutStepTwoPage extends BasePage {

    private static final Pattern MONEY = Pattern.compile("\\$(\\d+\\.\\d{2})");

    private final Locator itemNames;
    private final Locator itemPrices;
    private final Locator subtotalLabel;
    private final Locator taxLabel;
    private final Locator totalLabel;
    private final Locator finishButton;

    public CheckoutStepTwoPage(Page page) {
        super(page);
        this.itemNames = page.locator("[data-test='inventory-item-name']");
        this.itemPrices = page.locator("[data-test='inventory-item-price']");
        this.subtotalLabel = page.locator("[data-test='subtotal-label']");
        this.taxLabel = page.locator("[data-test='tax-label']");
        this.totalLabel = page.locator("[data-test='total-label']");
        this.finishButton = page.locator("[data-test='finish']");
    }

    /** The order overview as shown on screen - the "expected" side of the PDF comparison. */
    public ReceiptSummary readSummary() {
        List<LineItem> items = new ArrayList<>();
        List<String> names = itemNames.allInnerTexts();
        List<String> prices = itemPrices.allInnerTexts();
        for (int i = 0; i < names.size(); i++) {
            items.add(new LineItem(names.get(i), extractMoney(prices.get(i))));
        }

        BigDecimal itemTotal = extractMoney(subtotalLabel.innerText());
        BigDecimal tax = extractMoney(taxLabel.innerText());
        BigDecimal total = extractMoney(totalLabel.innerText());
        return new ReceiptSummary(items, itemTotal, tax, total);
    }

    public CheckoutCompletePage finish() {
        finishButton.click();
        return new CheckoutCompletePage(page);
    }

    private BigDecimal extractMoney(String text) {
        Matcher matcher = MONEY.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No dollar amount found in: \"" + text + "\"");
        }
        return new BigDecimal(matcher.group(1));
    }
}

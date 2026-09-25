package com.saucedemo.automation.steps;

import com.saucedemo.automation.dto.CheckoutInfo;
import com.saucedemo.automation.dto.LineItem;
import com.saucedemo.automation.dto.ReceiptSummary;
import com.saucedemo.automation.services.CheckoutService;
import com.saucedemo.automation.util.ScreenshotHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderPdfReceiptSteps {

    private final CheckoutService checkoutService = new CheckoutService();
    private List<String> cartItemNames;
    private ReceiptSummary pdfSummary;

    @When("I add {string} and {string} to my cart")
    public void i_add_two_items_to_my_cart(String item1, String item2) {
        cartItemNames = List.of(item1, item2);
        checkoutService.addItemsToCart(cartItemNames);
    }

    @And("I click on the cart")
    public void i_click_on_the_cart() {
        checkoutService.openCart();
    }

    @And("I complete checkout with valid information")
    public void i_complete_checkout_with_valid_information() {
        checkoutService.proceedThroughCheckout(new CheckoutInfo("Jigyasa", "Gupta", "90210"));
    }

    @Then("the order should complete successfully")
    public void the_order_should_complete_successfully() {
        assertTrue("Expected the order-complete page to be displayed", checkoutService.orderCompleted());
    }

    @Then("the on-screen order summary should show item total {string}, tax {string}, and total {string}")
    public void the_on_screen_summary_should_show(String expectedItemTotal, String expectedTax, String expectedTotal) {
        ReceiptSummary onScreen = checkoutService.onScreenSummary();
        ScreenshotHelper.log("Expected: ItemTotal=$" + expectedItemTotal + " Tax=$" + expectedTax + " Total=$" + expectedTotal
                + " | Actual: ItemTotal=$" + onScreen.getItemTotal() + " Tax=$" + onScreen.getTax() + " Total=$" + onScreen.getTotal());
        assertEquals("Item total does not match the expected price - did a product price change?",
                new BigDecimal(expectedItemTotal), onScreen.getItemTotal());
        assertEquals("Tax does not match the expected amount",
                new BigDecimal(expectedTax), onScreen.getTax());
        assertEquals("Total does not match the expected amount",
                new BigDecimal(expectedTotal), onScreen.getTotal());
    }

    @And("the on-screen order total should equal item total plus tax")
    public void the_on_screen_order_total_should_equal_item_total_plus_tax() {
        ReceiptSummary onScreen = checkoutService.onScreenSummary();
        BigDecimal expectedTotal = onScreen.getItemTotal().add(onScreen.getTax());
        ScreenshotHelper.log("$" + onScreen.getItemTotal() + " + $" + onScreen.getTax() + " = $" + expectedTotal
                + " | On-Screen Total=$" + onScreen.getTotal() + " => " + (expectedTotal.compareTo(onScreen.getTotal()) == 0 ? "MATCH" : "MISMATCH"));
        assertEquals("On-screen total does not equal item total + tax - arithmetic bug in the checkout overview",
                expectedTotal, onScreen.getTotal());
    }

    @And("I download the PDF order receipt")
    public void i_download_the_pdf_order_receipt() throws IOException {
        Path targetDir = Paths.get("target", "downloads");
        Files.createDirectories(targetDir);
        pdfSummary = checkoutService.downloadAndParsePdfReceipt(targetDir);
    }

    @Then("the PDF receipt should show item total {string}, tax {string}, and total {string}")
    public void the_pdf_receipt_should_show(String expectedItemTotal, String expectedTax, String expectedTotal) {
        assertEquals("PDF item total does not match the expected price - did a product price change?",
                new BigDecimal(expectedItemTotal), pdfSummary.getItemTotal());
        assertEquals("PDF tax does not match the expected amount",
                new BigDecimal(expectedTax), pdfSummary.getTax());
        assertEquals("PDF total does not match the expected amount",
                new BigDecimal(expectedTotal), pdfSummary.getTotal());
    }

    @And("the PDF receipt's item total, tax, and total should match the on-screen summary")
    public void the_pdf_receipt_should_match_the_on_screen_summary() {
        ReceiptSummary expected = checkoutService.onScreenSummary();
        ScreenshotHelper.log("On-Screen: ItemTotal=$" + expected.getItemTotal() + " Tax=$" + expected.getTax() + " Total=$" + expected.getTotal()
                + " | PDF: ItemTotal=$" + pdfSummary.getItemTotal() + " Tax=$" + pdfSummary.getTax() + " Total=$" + pdfSummary.getTotal());
        assertEquals("Item total mismatch between PDF and on-screen summary",
                expected.getItemTotal(), pdfSummary.getItemTotal());
        assertEquals("Tax mismatch between PDF and on-screen summary",
                expected.getTax(), pdfSummary.getTax());
        assertEquals("Total mismatch between PDF and on-screen summary",
                expected.getTotal(), pdfSummary.getTotal());
    }

    @And("the PDF receipt's total should equal item total plus tax")
    public void the_pdf_total_should_equal_item_total_plus_tax() {
        BigDecimal expectedTotal = pdfSummary.getItemTotal().add(pdfSummary.getTax());
        ScreenshotHelper.log("$" + pdfSummary.getItemTotal() + " + $" + pdfSummary.getTax() + " = $" + expectedTotal
                + " | PDF Total=$" + pdfSummary.getTotal() + " => " + (expectedTotal.compareTo(pdfSummary.getTotal()) == 0 ? "MATCH" : "MISMATCH"));
        assertEquals("PDF total does not equal item total + tax - arithmetic bug in the receipt",
                expectedTotal, pdfSummary.getTotal());
    }

    @And("the PDF receipt's line items should match what was in the cart")
    public void the_pdf_line_items_should_match_the_cart() {
        List<String> pdfItemNames = pdfSummary.getItems().stream()
                .map(LineItem::getName)
                .collect(Collectors.toList());
        ScreenshotHelper.log("Cart: " + cartItemNames + " | PDF: " + pdfItemNames);

        for (String expectedName : cartItemNames) {
            assertTrue("Expected PDF to contain line item \"" + expectedName + "\", but PDF items were: " + pdfItemNames,
                    pdfItemNames.stream().anyMatch(name -> name.contains(expectedName)));
        }
    }
}

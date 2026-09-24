package com.saucedemo.automation.steps;

import com.saucedemo.automation.dto.CheckoutInfo;
import com.saucedemo.automation.dto.LineItem;
import com.saucedemo.automation.dto.ReceiptSummary;
import com.saucedemo.automation.services.CheckoutService;
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

    @And("I complete checkout with valid information")
    public void i_complete_checkout_with_valid_information() {
        checkoutService.proceedThroughCheckout(new CheckoutInfo("Jigyasa", "Gupta", "90210"));
    }

    @Then("the order should complete successfully")
    public void the_order_should_complete_successfully() {
        assertTrue("Expected the order-complete page to be displayed", checkoutService.orderCompleted());
    }

    @And("I download the PDF order receipt")
    public void i_download_the_pdf_order_receipt() throws IOException {
        Path targetDir = Paths.get("target", "downloads");
        Files.createDirectories(targetDir);
        pdfSummary = checkoutService.downloadAndParsePdfReceipt(targetDir);
    }

    @And("the PDF receipt's item total, tax, and total should match the on-screen summary")
    public void the_pdf_receipt_should_match_the_on_screen_summary() {
        ReceiptSummary expected = checkoutService.onScreenSummary();
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
        assertEquals("PDF total does not equal item total + tax - arithmetic bug in the receipt",
                expectedTotal, pdfSummary.getTotal());
    }

    @And("the PDF receipt's line items should match what was in the cart")
    public void the_pdf_line_items_should_match_the_cart() {
        List<String> pdfItemNames = pdfSummary.getItems().stream()
                .map(LineItem::getName)
                .collect(Collectors.toList());

        for (String expectedName : cartItemNames) {
            assertTrue("Expected PDF to contain line item \"" + expectedName + "\", but PDF items were: " + pdfItemNames,
                    pdfItemNames.stream().anyMatch(name -> name.contains(expectedName)));
        }
    }
}

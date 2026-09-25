package com.saucedemo.automation.steps;

import com.saucedemo.automation.services.ProductCatalogService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProductSortSteps {

    private final ProductCatalogService productCatalogService = new ProductCatalogService();

    @When("I sort products by {string}")
    public void i_sort_products_by(String sortOptionLabel) {
        productCatalogService.sortBy(sortOptionLabel);
    }

    @Then("the products should be listed in {word} price order")
    public void the_products_should_be_listed_in_price_order(String direction) {
        List<BigDecimal> actual = productCatalogService.productPrices();
        List<BigDecimal> expected = sortedCopy(actual, direction);
        assertEquals("Products are not actually listed in " + direction + " price order: " + actual,
                expected, actual);
    }

    @Then("the products should not be listed in descending price order")
    public void the_products_should_not_be_listed_in_descending_price_order() {
        List<BigDecimal> actual = productCatalogService.productPrices();
        List<BigDecimal> descending = sortedCopy(actual, "descending");
        assertNotEquals("Expected problem_user's known broken sort (selecting a sort option has no effect), "
                        + "but the list came back correctly sorted - has this bug been fixed?",
                descending, actual);
    }

    private List<BigDecimal> sortedCopy(List<BigDecimal> prices, String direction) {
        List<BigDecimal> copy = new ArrayList<>(prices);
        copy.sort("ascending".equals(direction) ? Comparator.naturalOrder() : Comparator.reverseOrder());
        return copy;
    }
}

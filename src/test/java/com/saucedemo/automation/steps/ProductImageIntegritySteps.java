package com.saucedemo.automation.steps;

import com.saucedemo.automation.services.ProductCatalogService;
import io.cucumber.java.en.Then;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ProductImageIntegritySteps {

    private final ProductCatalogService productCatalogService = new ProductCatalogService();

    @Then("every product image should be unique")
    public void every_product_image_should_be_unique() {
        List<String> sources = productCatalogService.productImageSources();
        Set<String> distinct = new HashSet<>(sources);
        assertEquals("Expected every product to have a distinct image, but found duplicates: " + sources,
                sources.size(), distinct.size());
    }

    @Then("every product should show the same broken image")
    public void every_product_should_show_the_same_broken_image() {
        List<String> sources = productCatalogService.productImageSources();
        Set<String> distinct = new HashSet<>(sources);
        assertEquals("Expected problem_user's known image bug (every product sharing one broken image), "
                        + "but images were distinct - has this bug been fixed?",
                1, distinct.size());
    }
}

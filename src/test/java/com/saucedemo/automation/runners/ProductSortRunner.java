package com.saucedemo.automation.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/product_sort.feature",
        glue = {"com.saucedemo.automation.steps", "com.saucedemo.automation.hooks"},
        plugin = {"pretty", "summary", "json:target/cucumber-reports/product_sort.json",
        		//"html:target/cucumber-reports/product-sort-local-report.html"		
        }
)
public class ProductSortRunner {
}

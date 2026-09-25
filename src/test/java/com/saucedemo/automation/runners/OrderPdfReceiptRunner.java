package com.saucedemo.automation.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/order_pdf_receipt.feature",
        glue = {"com.saucedemo.automation.steps", "com.saucedemo.automation.hooks"},
        plugin = {"pretty", "summary", "json:target/cucumber-reports/order_pdf_receipt.json",
        		//"html:target/cucumber-reports/order-local-report.html"		
        }
)
public class OrderPdfReceiptRunner {
}

package com.saucedemo.automation.hooks;

import com.saucedemo.automation.driver.DriverManager;
import com.saucedemo.automation.util.ScreenshotUtil;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Scenario lifecycle: fresh browser/context/page per scenario, plus a
 * screenshot after every step (pass or fail), attached to the Cucumber
 * report and saved under target/screenshots/.
 */
public class Hooks {

    private int stepIndex;

    @Before
    public void setUp() {
        DriverManager.initDriver();
        stepIndex = 0;
    }

    @AfterStep
    public void captureStepScreenshot(Scenario scenario) {
        stepIndex++;
        byte[] png = DriverManager.getPage().screenshot(new Page.ScreenshotOptions().setFullPage(false));
        scenario.attach(png, "image/png", "step-" + stepIndex);
        ScreenshotUtil.save(png, scenario.getName(), scenario.getId(), stepIndex, scenario.getStatus().name());
    }

    @After
    public void tearDown() {
        DriverManager.quitDriver();
    }
}

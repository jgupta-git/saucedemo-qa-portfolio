package com.saucedemo.automation.hooks;

import com.saucedemo.automation.driver.DriverManager;
import com.saucedemo.automation.util.ScreenshotHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        DriverManager.initDriver();
        ScreenshotHelper.setScenario(scenario);
    }

    @After
    public void tearDown() {
        DriverManager.quitDriver();
    }
}

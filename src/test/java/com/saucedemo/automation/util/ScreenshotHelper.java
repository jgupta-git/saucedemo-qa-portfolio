package com.saucedemo.automation.util;

import com.saucedemo.automation.driver.DriverManager;
import com.microsoft.playwright.Page;
import io.cucumber.java.Scenario;

public final class ScreenshotHelper {

    private static final ThreadLocal<Scenario> scenario = new ThreadLocal<>();

    public static void setScenario(Scenario s) {
        scenario.set(s);
    }

    public static void capture(String description) {
        byte[] png = DriverManager.getPage().screenshot(
                new Page.ScreenshotOptions().setFullPage(true));
        scenario.get().attach(png, "image/png", description);
    }

    public static void attachFile(byte[] content, String mediaType, String description) {
        scenario.get().attach(content, mediaType, description);
    }

    public static void log(String text) {
        scenario.get().log(text);
    }

    private ScreenshotHelper() {
    }
}

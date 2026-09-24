package com.saucedemo.automation.config;

/**
 * Central place for environment-driven settings, read once so no other
 * class touches System.getProperty directly.
 */
public final class TestConfig {

    public static final String BASE_URL = "https://www.saucedemo.com";

    public static boolean isHeadless() {
        return !"false".equalsIgnoreCase(System.getProperty("headless", "true"));
    }

    public static String browserName() {
        return System.getProperty("browser", "chromium");
    }

    private TestConfig() {
    }
}

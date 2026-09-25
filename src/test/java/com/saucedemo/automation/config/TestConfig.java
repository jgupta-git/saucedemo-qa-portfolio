package com.saucedemo.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Central place for environment-driven settings, backed by
 * saucedemo-credentials.properties (same file CredentialsReader uses - these
 * values aren't secret, just kept alongside for convenience) so no other
 * class touches System.getProperty or hardcodes these values. Any setting
 * can still be overridden per-run without editing the file, e.g.:
 * mvn test -Dheadless=false -Dbrowser=firefox -DbaseUrl=...
 */
public final class TestConfig {

    private static final String FILE_NAME = "saucedemo-credentials.properties";
    private static final Properties PROPERTIES = load();

    public static String baseUrl() {
        return System.getProperty("baseUrl", PROPERTIES.getProperty("base.url"));
    }

    public static boolean isHeadless() {
        return !"false".equalsIgnoreCase(System.getProperty("headless", PROPERTIES.getProperty("headless", "true")));
    }

    public static String browserName() {
        return System.getProperty("browser", PROPERTIES.getProperty("browser", "chromium"));
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = TestConfig.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (in == null) {
                throw new IllegalStateException(FILE_NAME + " not found on the classpath. It should be "
                        + "checked into src/test/resources/ - check the build actually pulled it in.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + FILE_NAME, e);
        }
        return props;
    }

    private TestConfig() {
    }
}

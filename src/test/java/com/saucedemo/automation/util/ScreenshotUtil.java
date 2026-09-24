package com.saucedemo.automation.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Writes step screenshots to disk under target/screenshots/&lt;scenario&gt;/,
 * one subfolder per scenario run so Scenario Outline examples don't
 * overwrite each other.
 */
public final class ScreenshotUtil {

    private static final Path SCREENSHOT_ROOT = Paths.get("target", "screenshots");

    public static Path save(byte[] pngBytes, String scenarioName, String scenarioId, int stepIndex, String statusSoFar) {
        try {
            Path dir = SCREENSHOT_ROOT.resolve(sanitize(scenarioName) + "_" + shortHash(scenarioId));
            Files.createDirectories(dir);

            String base = String.format("%02d-%s", stepIndex, sanitize(statusSoFar));
            Path file = dir.resolve(truncate(base, 140) + ".png");
            Files.write(file, pngBytes);
            return file;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save screenshot for scenario: " + scenarioName, e);
        }
    }

    private static String sanitize(String text) {
        return text.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    private static String shortHash(String id) {
        return Integer.toHexString(id.hashCode());
    }

    private static String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }

    private ScreenshotUtil() {
    }
}

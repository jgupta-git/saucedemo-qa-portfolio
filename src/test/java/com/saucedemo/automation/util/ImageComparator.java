package com.saucedemo.automation.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Minimal pixel-diff image comparison. Playwright Java's LocatorAssertions
 * has no hasScreenshot() in this setup (verified by decompiling the actual
 * class - that API needs Playwright's own JUnit5 test-runner integration,
 * which Cucumber+JUnit4 doesn't use), so this does the comparison directly:
 * decode both PNGs and count how many pixels differ.
 *
 * A small tolerance absorbs minor anti-aliasing/rendering noise between
 * runs while still catching a genuinely different image (e.g. a completely
 * wrong product photo, which differs across nearly every pixel).
 */
public final class ImageComparator {

    public static boolean imagesMatch(byte[] actualPng, byte[] expectedPng, double maxFractionDifferent) {
        BufferedImage actual = decode(actualPng);
        BufferedImage expected = decode(expectedPng);

        if (actual.getWidth() != expected.getWidth() || actual.getHeight() != expected.getHeight()) {
            return false;
        }

        long totalPixels = (long) actual.getWidth() * actual.getHeight();
        long differentPixels = 0;
        for (int y = 0; y < actual.getHeight(); y++) {
            for (int x = 0; x < actual.getWidth(); x++) {
                if (actual.getRGB(x, y) != expected.getRGB(x, y)) {
                    differentPixels++;
                }
            }
        }

        double fractionDifferent = (double) differentPixels / totalPixels;
        return fractionDifferent <= maxFractionDifferent;
    }

    private static BufferedImage decode(byte[] png) {
        try {
            return ImageIO.read(new ByteArrayInputStream(png));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to decode PNG for image comparison", e);
        }
    }

    private ImageComparator() {
    }
}

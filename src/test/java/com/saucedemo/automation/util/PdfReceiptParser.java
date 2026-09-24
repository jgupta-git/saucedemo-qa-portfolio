package com.saucedemo.automation.util;

import com.saucedemo.automation.dto.LineItem;
import com.saucedemo.automation.dto.ReceiptSummary;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts a ReceiptSummary from a downloaded PDF order receipt.
 *
 * NOTE: this was written without ever inspecting the PDF's actual internal
 * text layout (the receipt is generated client-side via @react-pdf/renderer
 * and there was no way to intercept that blob outside of a real Playwright
 * run). It assumes the PDF mirrors the on-screen checkout overview's wording
 * ("Item total: $X", "Tax: $X", "Total: $X", one product name + price per
 * line) - a very common implementation pattern, but unverified. If the
 * first real run throws the IllegalStateException below, the message
 * includes the raw extracted text so the label-matching logic here can be
 * corrected against what the PDF actually says.
 */
public final class PdfReceiptParser {

    private static final Pattern MONEY = Pattern.compile("\\$(\\d+\\.\\d{2})");

    public static ReceiptSummary parse(Path pdfFile) {
        String text;
        try (PDDocument document = Loader.loadPDF(pdfFile.toFile())) {
            text = new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read PDF: " + pdfFile, e);
        }

        List<LineItem> items = new ArrayList<>();
        BigDecimal itemTotal = null;
        BigDecimal tax = null;
        BigDecimal total = null;

        for (String rawLine : text.split("\\r?\\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            Matcher matcher = MONEY.matcher(line);
            if (!matcher.find()) {
                continue;
            }
            BigDecimal amount = new BigDecimal(matcher.group(1));
            String lower = line.toLowerCase();

            if (lower.contains("item total") || lower.contains("subtotal")) {
                itemTotal = amount;
            } else if (lower.contains("tax")) {
                tax = amount;
            } else if (lower.contains("total")) {
                total = amount;
            } else {
                String name = line.substring(0, matcher.start()).trim();
                if (!name.isEmpty()) {
                    items.add(new LineItem(name, amount));
                }
            }
        }

        if (itemTotal == null || tax == null || total == null) {
            throw new IllegalStateException(
                    "Could not find Item total / Tax / Total lines in the PDF using the expected wording. "
                            + "Raw extracted text was:\n" + text);
        }

        return new ReceiptSummary(items, itemTotal, tax, total);
    }

    private PdfReceiptParser() {
    }
}

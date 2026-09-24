package com.saucedemo.automation.driver;

import com.saucedemo.automation.config.TestConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Owns the Playwright/Browser/Context/Page lifecycle behind ThreadLocals so
 * scenarios can run in parallel (one full stack per thread) without any
 * class outside this one touching Playwright directly. Step -> Service ->
 * Page Object all reach the live Page through here.
 *
 * This is the same shape a Selenium port would take with
 * ThreadLocal&lt;WebDriver&gt; instead - only this class and the Page
 * Objects' internals would need to change.
 */
public final class DriverManager {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver() {
        Playwright playwright = Playwright.create();
        PLAYWRIGHT.set(playwright);

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(TestConfig.isHeadless());
        Browser browser;
        switch (TestConfig.browserName().toLowerCase()) {
            case "firefox":
                browser = playwright.firefox().launch(options);
                break;
            case "webkit":
                browser = playwright.webkit().launch(options);
                break;
            default:
                browser = playwright.chromium().launch(options);
        }
        BROWSER.set(browser);

        // acceptDownloads defaults to true on modern Playwright, set explicitly since
        // the PDF-receipt scenario depends on it.
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 800)
                .setAcceptDownloads(true));
        CONTEXT.set(context);
        PAGE.set(context.newPage());
    }

    public static Page getPage() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("No Page for this thread - was DriverManager.initDriver() called in a @Before hook?");
        }
        return page;
    }

    public static void quitDriver() {
        if (CONTEXT.get() != null) {
            CONTEXT.get().close();
        }
        if (BROWSER.get() != null) {
            BROWSER.get().close();
        }
        if (PLAYWRIGHT.get() != null) {
            PLAYWRIGHT.get().close();
        }
        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }
}

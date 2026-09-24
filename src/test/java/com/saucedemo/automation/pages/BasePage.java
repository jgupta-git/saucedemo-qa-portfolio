package com.saucedemo.automation.pages;

import com.microsoft.playwright.Page;

/**
 * Common handle every Page Object needs. Concrete pages extend this instead
 * of each holding their own Page field.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    public String currentUrl() {
        return page.url();
    }
}

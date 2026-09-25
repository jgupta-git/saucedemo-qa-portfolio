package com.saucedemo.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object for the hamburger side menu present on every authenticated
 * page. data-test="open-menu" sits on the inner &lt;img&gt;, but its
 * wrapping &lt;button&gt; intercepts pointer events (verified live -
 * clicking the data-test locator directly times out), so the trigger is
 * targeted by role/name instead.
 */
public class NavigationMenu extends BasePage {

    private final Locator openMenuButton;
    private final Locator resetAppStateLink;
    private final Locator logoutLink;

    public NavigationMenu(Page page) {
        super(page);
        this.openMenuButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Open Menu"));
        this.resetAppStateLink = page.locator("[data-test='reset-sidebar-link']");
        this.logoutLink = page.locator("[data-test='logout-sidebar-link']");
    }

    public void resetAppState() {
        openMenuButton.click();
        resetAppStateLink.click();
    }

    public LoginPage logout() {
        openMenuButton.click();
        logoutLink.click();
        return new LoginPage(page);
    }
}

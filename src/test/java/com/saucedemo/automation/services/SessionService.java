package com.saucedemo.automation.services;

import com.saucedemo.automation.driver.DriverManager;
import com.saucedemo.automation.pages.InventoryPage;
import com.saucedemo.automation.pages.NavigationMenu;

/** Service layer for session/app-state management (hamburger menu actions). */
public class SessionService {

    private final InventoryPage inventoryPage;
    private final NavigationMenu navigationMenu;

    public SessionService() {
        this.inventoryPage = new InventoryPage(DriverManager.getPage());
        this.navigationMenu = new NavigationMenu(DriverManager.getPage());
    }

    public void addItemToCart(String productName) {
        inventoryPage.addToCart(productName);
    }

    public boolean cartBadgeVisible() {
        return inventoryPage.isCartBadgeVisible();
    }

    public void resetAppState() {
        navigationMenu.resetAppState();
    }

    public void logout() {
        navigationMenu.logout();
    }

    public void goBackInBrowser() {
        DriverManager.getPage().goBack();
    }
}

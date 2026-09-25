@session @functional
Feature: Session and app-state management
  As a QA engineer
  I want the hamburger menu's session actions to work correctly
  So that cart state and access control behave as expected

  Background:
    Given I am logged in to SauceDemo

  @Scenario-1 @positive
  Scenario: Reset App State clears the cart without refreshing the page
    When I add "Sauce Labs Backpack" to my cart
    And I reset the app state via the hamburger menu
    Then the cart badge should be cleared

  @Scenario-2 @negative
  Scenario: Logging out blocks access to the inventory page via the browser back button
    When I log out
    And I go back in the browser
    Then I should see the login error "Epic sadface: You can only access '/inventory.html' when you are logged in."

@checkout @functional
Feature: PDF order receipt
  As a customer who just completed checkout
  I want to download a PDF of my order
  So that I have a record that actually matches what I was charged

  @Scenario-1 @positive
  Scenario Outline: The on-screen and PDF receipt totals match the expected price
    Given I am on the SauceDemo login page
    And I log in with username "<username>" using the universal password
    When I add "Sauce Labs Backpack" and "Sauce Labs Bike Light" to my cart
    And I click on the cart
    And I complete checkout with valid information
    Then the on-screen order summary should show item total "<itemTotal>", tax "<tax>", and total "<total>"
    And the on-screen order total should equal item total plus tax
    And the order should complete successfully
    When I download the PDF order receipt
    Then the PDF receipt should show item total "<itemTotal>", tax "<tax>", and total "<total>"
    And the PDF receipt's item total, tax, and total should match the on-screen summary
    And the PDF receipt's total should equal item total plus tax
    And the PDF receipt's line items should match what was in the cart

    Examples:
      | itemTotal | tax  | total | username      |
      | 39.98     | 3.20 | 43.18 | standard_user |

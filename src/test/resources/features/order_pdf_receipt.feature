Feature: PDF order receipt
  As a customer who just completed checkout
  I want to download a PDF of my order
  So that I have a record that actually matches what I was charged

  Background:
    Given I am logged in to SauceDemo

  Scenario: The downloaded PDF receipt matches the on-screen order summary
    When I add "Sauce Labs Backpack" and "Sauce Labs Bike Light" to my cart
    And I complete checkout with valid information
    Then the order should complete successfully
    And I download the PDF order receipt
    And the PDF receipt's item total, tax, and total should match the on-screen summary
    And the PDF receipt's total should equal item total plus tax
    And the PDF receipt's line items should match what was in the cart

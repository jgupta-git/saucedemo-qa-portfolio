@catalog @functional
Feature: Visual regression on the product catalog
  As a QA engineer
  I want to catch rendering bugs a text-only assertion would miss
  So that a visually broken product image doesn't silently ship

  @Scenario-1 @positive
  Scenario: The backpack product image is visually stable
    Given I am logged in to SauceDemo
    And I capture the backpack product image as the known-good image
    Then the backpack product image should match the known-good image

  @Scenario-2 @negative
  Scenario: visual_user's backpack image differs from the correct rendering
    Given I am logged in to SauceDemo
    And I capture the backpack product image as the known-good image
    When I log out and log in to SauceDemo as "visual_user"
    Then the backpack product image should not match the known-good image

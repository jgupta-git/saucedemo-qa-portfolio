@catalog @functional
Feature: Product catalog sorting
  As a QA engineer
  I want sorting to actually reorder the product list
  So that a broken sort control doesn't silently ship

  @Scenario-1 @positive
  Scenario Outline: Sorting actually reorders the product list
    Given I am logged in to SauceDemo
    When I sort products by "<sortOption>"
    Then the products should be listed in <expectedOrder> price order

    Examples:
      | sortOption          | expectedOrder |
      | Price (low to high) | ascending     |
      | Price (high to low) | descending    |

  @Scenario-2 @negative
  Scenario: problem_user's price sort is broken
    Given I am logged in to SauceDemo as "problem_user"
    When I sort products by "Price (high to low)"
    Then the products should not be listed in descending price order

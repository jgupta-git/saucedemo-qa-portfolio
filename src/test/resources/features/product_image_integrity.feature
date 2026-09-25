@catalog @functional
Feature: Product image integrity
  As a QA engineer
  I want every product to show its own distinct image
  So that a broken image pipeline doesn't silently ship

  @Scenario-1 @positive
  Scenario: Every product shows a distinct image
    Given I am logged in to SauceDemo
    Then every product image should be unique

  @Scenario-2 @negative
  Scenario: problem_user sees the same broken image for every product
    Given I am logged in to SauceDemo as "problem_user"
    Then every product should show the same broken image

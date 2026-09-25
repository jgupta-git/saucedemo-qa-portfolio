@login @functional
Feature: Login page validation
  As a QA engineer
  I want every one of SauceDemo's built-in test accounts and error paths covered
  So that the login page's behavior is fully verified, not just the happy path

  Background:
    Given I am on the SauceDemo login page

  @Scenario-1 @positive @smoke
  Scenario Outline: All non-restricted users can log in successfully
    When I log in with username "<username>" using the universal password
    Then I should be logged in successfully

    Examples:
      | username                |
      | standard_user           |
      | problem_user            |
      | performance_glitch_user |
      | error_user              |
      | visual_user             |

  @Scenario-2 @negative
  Scenario: A locked-out user is refused login
    When I log in with username "locked_out_user" using the universal password
    Then I should see the login error "Epic sadface: Sorry, this user has been locked out."

  @Scenario-3 @negative
  Scenario Outline: An empty or unknown username is rejected even with the correct password
    When I log in with username "<username>" using the universal password
    Then I should see the login error "<expectedError>"

    Examples:
      | username     | expectedError                                                              |
      |              | Epic sadface: Username is required                                        |
      | invalid_user | Epic sadface: Username and password do not match any user in this service |

  @Scenario-4 @negative
  Scenario Outline: An empty or wrong password is rejected even with a valid username
    When I log in with username "<username>" and password "<password>"
    Then I should see the login error "<expectedError>"

    Examples:
      | username      | password       | expectedError                                                              |
      | standard_user |                | Epic sadface: Password is required                                        |
      | standard_user | wrong_password | Epic sadface: Username and password do not match any user in this service |

## Tech Stack

Java 11 | Playwright for Java 1.63.0 | Cucumber 7.34.9 | JUnit 4.13.2 | Apache PDFBox 3.0.8 | maven-cucumber-reporting 5.8.2 | Maven

## Overview

Test Target: [SauceDemo](https://www.saucedemo.com) (public e-commerce demo)
Testing Framework: Playwright for Java with Cucumber (BDD) on JUnit 4
Architecture: Layered (Steps → Service → Page Object), with DTOs for test data ([architecture diagram (PDF)](https://github.com/jgupta-git/saucedemo-qa-portfolio/blob/main/architecture-diagram.pdf))
Reporting: Cucumber HTML report with screenshots, plus TestRail results from a custom hook
Tagging System: `@positive`, `@negative`, `@smoke`, and one `@Scenario-N` tag per scenario

---

## Test Scenarios

13 test cases, mapped to TestRail cases C2 to C14.

### Login Validation (`login_validation.feature`)

| Scenario | Tags | TestRail | What it checks |
|---|---|---|---|
| All non-restricted users log in (Scenario Outline, 5 accounts) | @positive @smoke | C2 | `standard_user`, `problem_user`, `performance_glitch_user`, `error_user`, `visual_user` each reach the logged-in page |
| Locked-out user is refused | @negative | C3 | `locked_out_user` gets "Epic sadface: Sorry, this user has been locked out." |
| Empty or unknown username is rejected | @negative | C4 | Empty username gives "Username is required"; `invalid_user` gives "Username and password do not match any user in this service" |
| Empty or wrong password is rejected | @negative | C5 | Empty password gives "Password is required"; wrong password gives the same "do not match" message |

All login scenarios also carry `@login @functional` at feature level.

### Order PDF Receipt (`order_pdf_receipt.feature`)

| Scenario | Tags | TestRail | What it checks |
|---|---|---|---|
| Receipt totals match the cart and the PDF | @positive | C6 | Item total $39.98, tax $3.20, total $43.18 on screen, in the PDF, and in the arithmetic; line items match the cart |

### Product Images (`product_image_integrity.feature`)

| Scenario | Tags | TestRail | What it checks |
|---|---|---|---|
| `standard_user` sees distinct product images | @positive | C7 | Every product shows a different image |
| `problem_user` sees the known broken image | @negative | C8 | Known defect: all products share one image (test asserts the defect is present) |

### Product Sort (`product_sort.feature`)

| Scenario | Tags | TestRail | What it checks |
|---|---|---|---|
| `standard_user` sorts by price | @positive | C9 | Ascending and descending price sorts are correct |
| `problem_user` price sort is broken | @negative | C10 | Known defect: price sort is not descending (test asserts the defect is present) |

### Session Management (`session_management.feature`)

| Scenario | Tags | TestRail | What it checks |
|---|---|---|---|
| Reset App State clears the cart | @positive | C11 | Cart badge is cleared |
| Back button after logout is blocked | @negative | C12 | Access-denied message is shown after logout |

### Visual Regression (`visual_regression.feature`)

| Scenario | Tags | TestRail | What it checks |
|---|---|---|---|
| Backpack image is consistent | @positive | C13 | Pixel comparison of one product image, within 2 percent tolerance |
| `visual_user` image differs | @negative | C14 | The comparison detects a real difference |

---

## Project Structure

```
saucedemo-qa-portfolio/
├── pom.xml
├── architecture-diagram.html
├── README.md
├── reports/
│   └── overview-features.html
└── src/test/
    ├── java/com/saucedemo/automation/
    │   ├── runners/
    │   │   ├── LoginValidationRunner.java
    │   │   ├── OrderPdfReceiptRunner.java
    │   │   ├── ProductImageIntegrityRunner.java
    │   │   ├── ProductSortRunner.java
    │   │   ├── SessionManagementRunner.java
    │   │   └── VisualRegressionRunner.java
    │   ├── hooks/
    │   │   ├── Hooks.java
    │   │   └── TestRailReportingHook.java
    │   ├── steps/
    │   │   ├── CommonSteps.java
    │   │   ├── LoginSteps.java
    │   │   ├── OrderPdfReceiptSteps.java
    │   │   ├── ProductImageIntegritySteps.java
    │   │   ├── ProductSortSteps.java
    │   │   ├── SessionManagementSteps.java
    │   │   └── VisualRegressionSteps.java
    │   ├── services/
    │   │   ├── CheckoutService.java
    │   │   ├── LoginService.java
    │   │   ├── ProductCatalogService.java
    │   │   └── SessionService.java
    │   ├── pages/
    │   │   ├── BasePage.java
    │   │   ├── CartPage.java
    │   │   ├── CheckoutCompletePage.java
    │   │   ├── CheckoutStepOnePage.java
    │   │   ├── CheckoutStepTwoPage.java
    │   │   ├── InventoryPage.java
    │   │   ├── LoginPage.java
    │   │   └── NavigationMenu.java
    │   ├── dto/
    │   │   ├── CheckoutInfo.java
    │   │   ├── LineItem.java
    │   │   ├── LoginCredentials.java
    │   │   └── ReceiptSummary.java
    │   ├── driver/
    │   │   └── DriverManager.java
    │   ├── config/
    │   │   ├── CredentialsReader.java
    │   │   └── TestConfig.java
    │   └── util/
    │       ├── ImageComparator.java
    │       ├── PdfReceiptParser.java
    │       ├── ScreenshotHelper.java
    │       └── ScreenshotUtil.java
    └── resources/
        ├── saucedemo-credentials.properties
        └── features/
            ├── login_validation.feature
            ├── order_pdf_receipt.feature
            ├── product_image_integrity.feature
            ├── product_sort.feature
            ├── session_management.feature
            └── visual_regression.feature
```

## Key Files

### Layers

* **Feature files** (`src/test/resources/features/`) — Gherkin scenarios, written as behaviour.
* **Runners** (`runners/`) — one per feature file. Each runner sets the Cucumber glue and tags.
* **Steps** (`steps/`) — thin glue. Each step calls one service method and asserts on its result.
* **Services** (`services/`) — business flows such as "log in as user", "check out", "sort by price".
* **Pages** (`pages/`) — locators and UI actions. SauceDemo's `data-test` attributes are used wherever they exist.
* **DTOs** (`dto/`) — typed data (for example `ReceiptSummary`, `LineItem`) so steps compare numbers, not text.

### Infrastructure

* **DriverManager** — keeps the Playwright objects in thread-locals, so each scenario has its own stack.
* **Hooks** — start the browser before each scenario and close it after.
* **TestRailReportingHook** — reads the `@Cxx` tag and posts PASSED or FAILED to the TestRail run.

## Configuration

Settings live in `src/test/resources/saucedemo-credentials.properties`:

| Key | Current value | Purpose |
|---|---|---|
| `saucedemo.username` | `standard_user` | Default login account |
| `saucedemo.password` | (in file) | Default password |
| `base.url` | `https://www.saucedemo.com` | Site under test |
| `browser` | `chromium` | Browser to launch |
| `headless` | `false` | Run with a visible browser window |

Each value can be overridden per run with `-D`, without editing the file:

```bash
mvn test -Dheadless=true -Dbrowser=firefox
```

These are SauceDemo's public test accounts, listed on its login page, so the file is checked in on purpose.

> **Note:** the file sets `headless=false`, so the default run opens a visible browser. On a CI agent with no display, pass `-Dheadless=true`.

## Quick Start

### 1\. Prerequisites

* ✅ Java 11+
* ✅ Maven 3.6+
* ✅ Internet connection (tests run against the live SauceDemo site)

### 2\. Install Playwright browsers

```bash
mvn compile exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

### 3\. Run tests

```bash
# All runners configured in the pom
mvn test

# The saucedemo profile (the list Jenkins uses)
mvn test -P saucedemo

# Only smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Only negative tests
mvn test -Dcucumber.filter.tags="@negative"
```

### 4\. View the report

The report is generated at the `verify` phase:

```
target/cucumber-html-reports/
```

The published copy, with screenshots, is in the repo at `reports/overview-features.html`.

## Test Execution Report

[View Cucumber Report with Screenshots](https://htmlpreview.github.io/?https://github.com/jgupta-git/saucedemo-qa-portfolio/blob/main/reports/overview-features.html)

## Jenkins and TestRail

The Jenkins job `saucedemo_run` is a Maven project. Its build step runs the `saucedemo` profile with the TestRail settings passed as system properties:

```bash
mvn clean verify -P saucedemo -DTESTRAIL_URL="${TESTRAIL_URL}" -DTESTRAIL_USER="${TESTRAIL_USER}" -DTESTRAIL_RUN_ID="${TESTRAIL_RUN_ID}" -DTESTRAIL_PROJECT_ID="${TESTRAIL_PROJECT_ID}" -DTESTRAIL_ENABLED="${TESTRAIL_ENABLED}"
```

**Job parameters**

| Parameter | Purpose | Default |
|---|---|---|
| `TESTRAIL_URL` | TestRail instance URL | Set in the job |
| `TESTRAIL_USER` | TestRail login email | Set in the job |
| `TESTRAIL_PROJECT_ID` | TestRail project ID | `1` |
| `TESTRAIL_ENABLED` | Turn TestRail reporting on or off | `true` |
| `TESTRAIL_RUN_ID` | TestRail run that results are posted to | `2` |

**Post-build steps**

* **Run regardless of build result:** the report is published even when tests fail.
* **Publish HTML reports:** publishes `target/cucumber-html-reports` with `index.html` as the index page and the title "Cucumber Report".

For each scenario tagged `@Cxx`, the TestRail hook posts:

| Result | TestRail status |
|---|---|
| Passed | 1 (Passed) |
| Failed | 5 (Failed) |

## Test Execution Flow

1. `Hooks` starts Playwright, a browser context and a page (1280 × 800, downloads enabled).
2. The steps run through the service layer into the page objects.
3. Assertions run against the values read from the UI, the PDF or the image comparison.
4. Screenshots and logs are attached to the Cucumber report.
5. `Hooks` closes the browser.
6. `TestRailReportingHook` posts the result for the `@Cxx` tag.

## Architecture

```
src/test/resources/features/    one .feature file per area
src/test/java/com/saucedemo/automation/
  runners/     One @RunWith(Cucumber.class) class per feature file
  hooks/       Browser lifecycle and TestRail reporting
  steps/       Gherkin glue; calls the service layer only
  services/    Business flows; calls the page objects
  pages/       Locators and UI actions; no assertions
  dto/         Typed test data
  driver/      DriverManager (ThreadLocal Playwright lifecycle)
  util/        PDF parsing, image comparison, screenshots
  config/      Credentials and run settings
```

See `architecture-diagram.html` for the diagram.

## References

* Playwright for Java: [playwright.dev/java](https://playwright.dev/java)
* Cucumber: [cucumber.io](https://cucumber.io)
* SauceDemo: [saucedemo.com](https://www.saucedemo.com)
* Apache PDFBox: [pdfbox.apache.org](https://pdfbox.apache.org)

## GitHub Repository

```
https://github.com/jgupta-git/saucedemo-qa-portfolio
```

---

Last Updated: 2026-11-09 | Built with Playwright for Java • Cucumber • JUnit 4

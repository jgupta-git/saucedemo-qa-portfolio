# SauceDemo QA Automation

Automation portfolio project against https://www.saucedemo.com/ (Sauce Labs'
purpose-built e-commerce demo site for automation practice). Built with
Playwright for Java + Cucumber + JUnit4, structured as a 3-layer framework
(Step Definitions -> Service -> Page Object) with DTOs for test data -


## One-time setup

```
mvn compile exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

## Running the tests

```
mvn test
```

Runs headless Chromium by default. Overrides: `-Dheadless=false`, `-Dbrowser=firefox`.

## Architecture

```
src/test/resources/features/
  order_pdf_receipt.feature

src/test/java/com/saucedemo/automation/
  runners/     One @RunWith(Cucumber.class) class per feature file.
  hooks/       Hooks.java - @Before/@After lifecycle + per-step screenshots.
  steps/       Step Definitions - Gherkin glue, calls the Service layer only.
  services/    Service layer - orchestrates Page Object calls into business
               actions (LoginService, CheckoutService).
  pages/       Page Object Model - raw UI locators/actions, no assertions.
               LoginPage / InventoryPage / CartPage / CheckoutStepOnePage /
               CheckoutStepTwoPage / CheckoutCompletePage
  dto/         Plain data holders (getters/setters): LoginCredentials,
               CheckoutInfo, LineItem, ReceiptSummary.
  driver/      DriverManager - ThreadLocal Playwright/Browser/Context/Page
               lifecycle (parallel-safe; the one class a Selenium port
               would swap out).
  util/        PdfReceiptParser (Apache PDFBox) and ScreenshotUtil.
  config/      TestConfig (base URL/headless/browser) and CredentialsReader.
```

## Credentials

`saucedemo-credentials.properties` (gitignored) holds the login used by
`CommonSteps`. SauceDemo's credentials are publicly documented on its own
login page, so nothing here is actually secret - the file exists to
demonstrate the same never-hardcode-test-data pattern used for real,
sensitive credentials elsewhere in this portfolio. Copy
`saucedemo-credentials.properties.example` to get started.

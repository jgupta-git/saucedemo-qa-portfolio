# SauceDemo QA Automation

Automation portfolio project against https://www.saucedemo.com/ (Sauce Labs'
purpose-built e-commerce demo site for automation practice). Built with
Playwright for Java + Cucumber + JUnit4, structured as a 3-layer framework
(Step Definitions -> Service -> Page Object) with DTOs for test data -
the same architecture as the Mercury Insurance project, applied to a second
site as further interview-prep practice.

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

Locators use SauceDemo's own `data-test` attributes wherever available -
this app tags nearly every element specifically for automation, making
`data-test` far more stable than text or CSS selectors.

## Credentials

`saucedemo-credentials.properties` (gitignored) holds the login used by
`CommonSteps`. SauceDemo's credentials are publicly documented on its own
login page, so nothing here is actually secret - the file exists to
demonstrate the same never-hardcode-test-data pattern used for real,
sensitive credentials elsewhere in this portfolio. Copy
`saucedemo-credentials.properties.example` to get started.

## What's covered so far

**`order_pdf_receipt.feature`** - the "Generate PDF order" button on the
checkout-complete page doesn't hit the server; it builds the PDF entirely
client-side via `@react-pdf/renderer` and triggers a browser download. This
test:
1. Adds two known items to the cart and completes checkout
2. Downloads the resulting PDF (via Playwright's download event)
3. Parses the PDF's text (Apache PDFBox) and compares it against the
   on-screen order summary - not just "did a file download," but "does the
   file's own numbers agree with what the UI showed"
4. Independently verifies Item total + Tax == Total inside the PDF itself
5. Verifies the PDF's line items actually match what was added to the cart

**Caveat:** the PDF-parsing logic in `PdfReceiptParser` was written without
ever seeing the PDF's actual internal text (there was no way to intercept
the client-side-generated blob outside of a real Playwright run). It
assumes the PDF mirrors the on-screen wording ("Item total: $X", "Tax: $X",
"Total: $X"). If the first real run fails with `IllegalStateException`, the
message includes the raw extracted PDF text needed to correct the parser.

## Next steps (planned)

From the broader 6-idea test plan for this site: authentication suite
across SauceDemo's special test users (`locked_out_user`, `problem_user`,
`performance_glitch_user`, `error_user`, `visual_user`), product sort/data
integrity (including a test that demonstrably catches `problem_user`'s
broken sort), cart and product-image integrity (catches `problem_user`'s
duplicate-image bug), and session/app-state management via the hamburger
menu (Reset App State, Logout + back-button auth check).

Same long-term plan as the Mercury project: once solid on Playwright, port
to Selenium - only `DriverManager` and the Page Objects' internals change.

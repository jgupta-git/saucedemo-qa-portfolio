# SauceDemo QA Automation

Automation portfolio project against https://www.saucedemo.com/. Built with
Playwright for Java + Cucumber + JUnit4, structured as a 3-layer framework
(Step Definitions -> Service -> Page Object) with DTOs for test data -
same architecture as the Mercury Insurance project.

## Setup & running

```
mvn compile exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
mvn test
```

Headless Chromium by default. Override: `-Dheadless=false`, `-Dbrowser=firefox`.

The `saucedemo` Maven profile explicitly lists all 6 runners - this is the
Jenkins entry point: `mvn test -P saucedemo`.

## Architecture

```
src/test/resources/features/    one .feature file per area (see below)
src/test/java/com/saucedemo/automation/
  runners/     One @RunWith(Cucumber.class) class per feature file.
  hooks/       @Before/@After lifecycle + per-step screenshots.
  steps/       Gherkin glue - calls the Service layer only.
  services/    Orchestrates Page Object calls into business actions.
  pages/       Raw UI locators/actions, no assertions.
  dto/         Plain data holders (getters/setters).
  driver/      DriverManager - ThreadLocal Playwright lifecycle (the one
               class a Selenium port would swap out).
  util/        PdfReceiptParser (Apache PDFBox), ScreenshotUtil, ImageComparator.
  config/      TestConfig, CredentialsReader.
```

Locators use SauceDemo's own `data-test` attributes wherever available.
Tags: feature-level `@functional`/area tag, scenario-level `@Scenario-N`
plus `@positive`/`@negative`/`@smoke` - filterable via
`-Dcucumber.filter.tags="@negative"`.

## Configuration

`saucedemo-credentials.properties` holds login + `base.url` / `browser` /
`headless`. Checked into the repo since SauceDemo's test credentials are
publicly documented on its own login page - nothing here is actually
secret. Override any value per-run, e.g. `mvn test -Dheadless=false`.

## What's covered

**`login_validation.feature`**
- All 5 non-restricted test accounts log in successfully; `locked_out_user`
  is refused with its specific message.
- A validation matrix distinguishes SauceDemo's 3 different error messages
  (empty username, empty password, unknown credentials).

**`order_pdf_receipt.feature`**
- On-screen checkout total matches an explicit expected price
  ($39.98/$3.20/$43.18), plus item total + tax = total.
- Downloaded PDF (parsed via PDFBox) matches that same expected price, the
  on-screen summary, its own arithmetic, and the cart's line items.

**`product_sort.feature`**
- `standard_user`'s sort actually reorders by price (ascending/descending).
- `problem_user`'s price sort is genuinely broken - the test fails the sort
  if it ever gets fixed, proving it catches a real regression.

**`product_image_integrity.feature`**
- `standard_user` shows 6 distinct product images.
- `problem_user` shows the identical broken image for every product -
  a real, verified bug this test would catch.

**`session_management.feature`**
- Reset App State clears the cart badge.
- After Logout, the browser back button is blocked with a specific
  access-denied message (reuses `login_validation`'s error-check step).

**`visual_regression.feature`**
- Element-level pixel-diff on the backpack image (`visual_user`'s one
  consistently broken image; prices are randomized, so a full-page diff
  would be flaky) via a custom `ImageComparator` - no `hasScreenshot()` API
  in this Playwright/JUnit4 setup.
- No saved baseline: `standard_user`'s image is captured live and compared
  in memory against `visual_user`'s within the same scenario.

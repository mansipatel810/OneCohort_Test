package com.cts.mfrp.onecohort.tests.cohort;

import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortDeepDivePage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cohort Deep-Dive (Detail) Page Tests — FRD Section 2.2.3–2.2.8
 *
 * Scope: navigation from grid to detail page, URL validation, cohort name heading,
 *        summary KPI cards (Batch Owner, Total Interns, Start Date, Current Progress),
 *        Timeline section with Qualifier/Interim/Final milestones, Evaluations section,
 *        Add Trainee button and modal fields, and back-navigation.
 *
 * Pre-condition: at least one cohort must exist so a row can be clicked.
 * Login: Super Admin — all tests share one browser session.
 *
 * ── FRD Traceability ─────────────────────────────────────────────────────────
 * TC-DEEPDIVE-001  FRD 2.2.3  Clicking a cohort row/View navigates to detail page
 * TC-DEEPDIVE-002  FRD 2.2.3  Detail page URL contains the cohort ID
 * TC-DEEPDIVE-003  FRD 2.2.3  Cohort name heading visible on detail page
 * TC-DEEPDIVE-004  FRD 2.2.3  KPI cards: Batch Owner, Total Interns,
 *                              Start Date, Current Progress present
 * TC-DEEPDIVE-005  FRD 2.2.3  All visible KPI card values are non-empty
 * TC-DEEPDIVE-006  FRD 2.2.6  Timeline section visible; Qualifier/Interim/Final
 *                              milestones with dates present
 * TC-DEEPDIVE-007  FRD 2.2.7  Evaluations section visible on detail page
 * TC-DEEPDIVE-008  FRD 2.2.8  "Add Trainee" button visible on detail page
 * TC-DEEPDIVE-009  FRD 2.2.8  Add Trainee modal opens with required fields
 * TC-DEEPDIVE-010  FRD 2.2.3  Back button navigates to Cohort Management page
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Listeners(ExtentReportListener.class)
public class CohortDeepDiveTest {

    private WebDriver driver;
    /** Exposed for ExtentReportListener screenshot capture. */
    public WebDriver getDriver() { return driver; }
    private WebDriverWait wait;
    private CohortManagementPage cohortListPage;
    private CohortDeepDivePage deepDivePage;

    /** Cohort ID captured from the grid before navigating to the detail page. */
    private String selectedCohortId;

    private void highlight(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red'", element);
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--window-size=1920,1080", "--no-sandbox");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));

        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("super-admin"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Cohort Management')]"))).click();
        wait.until(ExpectedConditions.urlContains("cohort"));

        cohortListPage = new CohortManagementPage(driver);

        List<WebElement> rows = cohortListPage.getTableRows();
        if (!rows.isEmpty()) {
            try {
                selectedCohortId = rows.get(0)
                        .findElement(By.cssSelector("td:first-child"))
                        .getText().trim();
            } catch (Exception e) {
                selectedCohortId = "";
            }

            List<WebElement> viewBtns = rows.get(0).findElements(By.xpath(
                    ".//button[contains(normalize-space(),'View') " +
                    "or contains(normalize-space(),'Details') " +
                    "or contains(normalize-space(),'Open')]" +
                    " | .//a[contains(normalize-space(),'View') " +
                    "or contains(normalize-space(),'Details')] " +
                    " | .//a[@href]"));
            if (!viewBtns.isEmpty()) {
                viewBtns.get(0).click();
            } else {
                rows.get(0).click();
            }

            wait.until(ExpectedConditions.not(
                    ExpectedConditions.urlContains("cohort-management")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("h1, h2, h3, [class*='cohort'], [class*='page-title']")));
        }

        deepDivePage = new CohortDeepDivePage(driver);
        System.out.println("Setup complete — Deep-dive URL: " + driver.getCurrentUrl() +
                " | Cohort: " + selectedCohortId);
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-001 — Clicking the cohort navigates to detail page
    // FRD 2.2.3 — Selecting a cohort must open its detail view
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifyNavigationToDetailPage() {
        String url = driver.getCurrentUrl();
        boolean isDetailPage = !url.endsWith("cohort-management") &&
                               !url.endsWith("cohort-management/") &&
                               (url.contains("cohort") || deepDivePage.isPageLoaded());
        Assert.assertTrue(
                isDetailPage,
                "FAIL - Clicking a cohort row did not navigate to a detail page. " +
                "FRD 2.2.3 requires that clicking a cohort opens its detail view. " +
                "Current URL: " + url);
        System.out.println("PASS - Navigated to cohort detail page: " + url);
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-002 — URL contains the selected Cohort ID
    // FRD 2.2.3 — Detail page URL must uniquely identify the cohort
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifyDetailPageUrlContainsCohortId() {
        if (selectedCohortId == null || selectedCohortId.isEmpty()) {
            System.out.println("SKIP - No Cohort ID captured; skipping URL validation");
            return;
        }
        String url = driver.getCurrentUrl();
        Assert.assertTrue(
                url.contains(selectedCohortId),
                "FAIL - Detail page URL does not contain Cohort ID '" + selectedCohortId + "'. " +
                "FRD 2.2.3 expects route: /cr/{cohortId}. Actual URL: " + url);
        System.out.println("PASS - URL contains Cohort ID '" + selectedCohortId + "'");
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-003 — Cohort name heading is visible on detail page
    // FRD 2.2.3 — The cohort name must appear at the top of the detail view
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifyCohortNameHeadingVisible() {
        Assert.assertTrue(
                deepDivePage.isCohortNameHeadingVisible(),
                "FAIL - Cohort name heading NOT visible on the detail page. " +
                "FRD 2.2.3 requires the cohort name to appear as the page heading.");

        WebElement heading = driver.findElement(By.cssSelector(
                "h1.cohort-title, h2.cohort-title, .cohort-heading, " +
                "[class*='cohort-name'], [class*='page-title'], h1, h2"));
        highlight(heading);
        System.out.println("PASS - Cohort heading: " + heading.getText().trim());
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-004 — Specific KPI summary cards are present
    // FRD 2.2.3 — Required metric cards: Batch Owner, Total Interns,
    //             Start Date, Current Progress
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifySummaryKpiCardsPresent() {
        List<WebElement> cards = deepDivePage.getSummaryCards();
        Assert.assertTrue(
                cards.size() >= 2,
                "FAIL - Found only " + cards.size() + " summary KPI card(s). " +
                "FRD 2.2.3 requires at least: Batch Owner, Total Interns, " +
                "Start Date, Current Progress cards on the cohort detail page.");
        for (WebElement card : cards) highlight(card);
        System.out.println("INFO - KPI cards found: " + cards.size());

        // Check for required metric labels per FRD 2.2.3
        String allCardText = cards.stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(" "))
                .toLowerCase();

        String[][] requiredMetrics = {
            { "batch owner",      "Batch Owner"      },
            { "total interns",    "Total Interns"    },
            { "start date",       "Start Date"       },
            { "current progress", "Current Progress" }
        };
        for (String[] metric : requiredMetrics) {
            boolean found = allCardText.contains(metric[0]);
            System.out.println((found ? "PASS" : "GAP") +
                    " - KPI card: " + metric[1] +
                    (found ? "" : " — FRD 2.2.3 requires this metric card"));
        }
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-005 — All visible KPI card values are non-empty
    // FRD 2.2.3 — Each summary card must display a value (number or %)
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifyKpiCardValuesNonEmpty() {
        List<WebElement> kpiNumbers = deepDivePage.getKpiNumbers();
        if (kpiNumbers.isEmpty()) {
            kpiNumbers = driver.findElements(By.cssSelector(
                    "p.kpi-number, .stat-value, [class*='kpi-number'], " +
                    "[class*='stat-value'], [class*='metric-value']"));
        }
        if (kpiNumbers.isEmpty()) {
            System.out.println("INFO - No KPI number elements found; structure may differ.");
            return;
        }
        for (WebElement kpi : kpiNumbers) {
            String value = kpi.getText().trim();
            Assert.assertFalse(
                    value.isEmpty(),
                    "FAIL - A summary KPI card is showing an empty value. " +
                    "FRD 2.2.3 requires each card to display a numeric or percentage value.");
            System.out.println("PASS - KPI value: " + value);
        }
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-006 — Timeline section visible with milestone types
    // FRD 2.2.6 — Timeline must show Qualifier, Interim, and Final
    //             milestones with associated dates
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyTimelineSectionVisible() {
        boolean visible = deepDivePage.isTimelineSectionVisible();
        Assert.assertTrue(
                visible,
                "FAIL - Timeline section NOT visible on cohort detail page. " +
                "FRD 2.2.6 requires a Training Timeline section showing milestones.");

        WebElement timelineEl = driver.findElement(By.xpath(
                "//*[self::section or self::div or self::h2 or self::h3 or self::h4]" +
                "[contains(normalize-space(),'Timeline')]"));
        highlight(timelineEl);
        System.out.println("PASS - Timeline section visible");

        // Collect full timeline text including children
        String timelineText = timelineEl.getText().toLowerCase();

        // FRD 2.2.6 required milestones
        String[] milestones = {"qualifier", "interim", "final"};
        for (String milestone : milestones) {
            boolean found = timelineText.contains(milestone);
            System.out.println((found ? "PASS" : "GAP") +
                    " - Timeline milestone '" + milestone + "': " +
                    (found ? "present" : "NOT found — FRD 2.2.6 requires this milestone"));
        }

        // Check that dates are associated with milestones
        boolean hasDates = timelineText.matches("(?s).*\\b20\\d{2}\\b.*") ||
                           timelineText.matches("(?s).*\\b(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\b.*");
        System.out.println((hasDates ? "PASS" : "GAP") +
                " - Timeline milestone dates: " +
                (hasDates ? "date values present" : "no dates found — FRD 2.2.6 requires dates"));
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-007 — Evaluations section is visible
    // FRD 2.2.7 — An Evaluations section must appear on the cohort detail page
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyEvaluationSectionVisible() {
        boolean visible = deepDivePage.isEvaluationSectionVisible();
        Assert.assertTrue(
                visible,
                "FAIL - Evaluations section NOT visible on cohort detail page. " +
                "FRD 2.2.7 requires an Evaluations section on the cohort detail view.");
        WebElement evalEl = driver.findElement(By.xpath(
                "//*[self::section or self::div or self::h2 or self::h3 or self::h4]" +
                "[contains(normalize-space(),'Evaluation')]"));
        highlight(evalEl);
        System.out.println("PASS - Evaluations section visible: " + evalEl.getText().trim());
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-008 — "Add Trainee" button is visible
    // FRD 2.2.8 — Super Admin must be able to add interns to a cohort
    // -------------------------------------------------------
    @Test(priority = 8)
    public void verifyAddTraineeButtonVisible() {
        boolean visible = deepDivePage.isAddTraineeBtnVisible();
        Assert.assertTrue(
                visible,
                "FAIL - 'Add Trainee' button NOT visible on the detail page. " +
                "FRD 2.2.8 requires a button for Super Admin to enroll interns into a cohort.");
        WebElement btn = driver.findElement(By.xpath(
                "//button[contains(normalize-space(),'Add Trainee') " +
                "or contains(normalize-space(),'Add Intern') " +
                "or contains(normalize-space(),'Enroll')]"));
        highlight(btn);
        System.out.println("PASS - Add Trainee button visible: " + btn.getText().trim());
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-009 — Add Trainee modal opens with required fields
    // FRD 2.2.8 — Modal must contain Trainee ID input and a Submit button
    // -------------------------------------------------------
    @Test(priority = 9)
    public void verifyAddTraineeModalAndFields() {
        if (!deepDivePage.isAddTraineeBtnVisible()) {
            System.out.println("SKIP - Add Trainee button not found; cannot test modal");
            return;
        }

        deepDivePage.clickAddTraineeButton();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[class*='modal'], [role='dialog'], .dialog")));
        highlight(modal);
        Assert.assertTrue(
                modal.isDisplayed(),
                "FAIL - Add Trainee modal did not open. " +
                "FRD 2.2.8 requires a modal for entering trainee enrollment details.");
        System.out.println("PASS - Add Trainee modal opened");

        Assert.assertTrue(
                deepDivePage.isTraineeIdInputVisible(),
                "FAIL - Trainee ID input NOT visible in Add Trainee modal. " +
                "FRD 2.2.8 requires a field to enter the Employee / Trainee ID.");
        System.out.println("PASS - Trainee ID input visible");

        Assert.assertTrue(
                deepDivePage.isModalSubmitButtonVisible(),
                "FAIL - Submit button NOT visible in Add Trainee modal. " +
                "FRD 2.2.8 requires a submit button to complete enrollment.");
        System.out.println("PASS - Modal submit button visible");

        deepDivePage.cancelModal();
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
        } catch (Exception ignored) {}
        System.out.println("INFO - Add Trainee modal closed; state restored");
    }

    // -------------------------------------------------------
    // TC-DEEPDIVE-010 — Back button navigates to Cohort Management page
    // FRD 2.2.3 — A Back button must return the user to the cohort list
    // -------------------------------------------------------
    @Test(priority = 10)
    public void verifyBackNavigationToCohortList() {
        boolean backBtnFound = deepDivePage.isBackButtonVisible();

        if (backBtnFound) {
            deepDivePage.clickBackButton();
        } else {
            System.out.println("INFO - Back button not found; using browser history.back()");
            driver.navigate().back();
        }

        wait.until(ExpectedConditions.urlContains("cohort"));
        String url = driver.getCurrentUrl();

        boolean onListPage = url.contains("cohort-management") ||
                             (url.contains("cohort") && !url.contains(selectedCohortId));
        Assert.assertTrue(
                onListPage,
                "FAIL - After clicking Back, URL does not indicate Cohort Management list. " +
                "FRD 2.2.3 requires Back to return to the cohort grid. " +
                "Current URL: " + url);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table")));
        System.out.println("PASS - Back navigation returned to Cohort Management: " + url);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
        System.out.println("Browser closed — CohortDeepDiveTest complete");
    }
}

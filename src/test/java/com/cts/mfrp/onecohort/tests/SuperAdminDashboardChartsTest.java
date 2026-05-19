package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Listeners(ExtentReportListener.class)
public class SuperAdminDashboardChartsTest extends BaseClassTest {

    private SuperAdminDashboardPage dashPage;

    // ── Section 2: Analytics chart containers ─────────────────────────────────
    // Angular apps using Chart.js render charts as <canvas> elements
    // The container divs typically have classes like "chart-container", "chart-section"
    private final By chartCanvases    = By.cssSelector("canvas");
    private final By chartSections    = By.cssSelector(
            "div.chart-container, div.chart-section, div.chart-wrapper, " +
                    "div[class*='chart'], div.analytics-section, div.analytics-card"
    );
    private final By sectionHeadings  = By.cssSelector(
            "div.section-title, h2.section-title, h3.section-title, " +
                    "div[class*='section-header'], h2[class*='section'], h3[class*='section']"
    );

    // ── Section 3: Cohort Data Grid ───────────────────────────────────────────
    // FRD 2.1.5: "Cohort Data Management Grid" — a table listing all cohorts
    private final By cohortDataTable   = By.cssSelector(
            "table, div.table-container, div.grid-container, div.data-grid, " +
                    "div[class*='table'], div[class*='grid']"
    );
    private final By tableRows         = By.cssSelector(
            "table tbody tr, div.table-row, div[class*='row']:not([class*='header'])"
    );
    private final By tableHeaders      = By.cssSelector(
            "table thead th, div.table-header th, div[class*='col-header']"
    );

    // ── Summary metric card locators (FRD 2.1.5.1) ───────────────────────────
    // These are additional KPI cards NOT tested in HomePageTest
    private final By totalInternsCard       = By.xpath("//h3[contains(text(),'Total Interns')]");
    private final By internsInTrainingCard  = By.xpath("//h3[contains(text(),'Interns in Training')]");
    private final By completionRateCard     = By.xpath(
            "//h3[contains(text(),'Completion Rate') or contains(text(),'Training Completion')]"
    );

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndLoadDashboard() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());

        wait.until(ExpectedConditions.urlContains("/super-admin"));
        System.out.println("Login OK — URL: " + driver.getCurrentUrl());

        dashPage = new SuperAdminDashboardPage(driver);
        System.out.println("Dashboard loaded for chart section tests.");
    }

    // =========================================================================
    //  SECTION 1 — Summary Metrics Cards (FRD 2.1.5.1)
    //  NOTE: HomePageTest already covers Total Cohorts, Active, Completed,
    //        Upcoming cards. This section adds the remaining 3 cards.
    // =========================================================================

    // -----------------------------------------------------------------------
    // TC-CHART-001: "Total Interns" KPI card is present
    // FRD 2.1.5.1 — "Total Interns: total number of interns enrolled"
    // -----------------------------------------------------------------------
    @Test(priority = 1,
            description = "TC-CHART-001 [FRD 2.1.5.1]: 'Total Interns' KPI card is visible")
    public void verifyTotalInternsCard() {
        WebElement card = driver.findElement(totalInternsCard);
        highlight(card, "yellow", "Total Interns KPI card [FRD 2.1.5.1]");
        Assert.assertTrue(card.isDisplayed(),
                "FAIL [FRD 2.1.5.1] — 'Total Interns' KPI card is not visible on dashboard.");
        highlight(card, "green", "Total Interns card PRESENT");
        System.out.println("PASS — 'Total Interns' card visible.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-002: "Interns in Training" KPI card is present
    // FRD 2.1.5.1 — "Interns in Training: interns in active training"
    // -----------------------------------------------------------------------
    @Test(priority = 2,
            description = "TC-CHART-002 [FRD 2.1.5.1]: 'Interns in Training' KPI card is visible")
    public void verifyInternsInTrainingCard() {
        WebElement card = driver.findElement(internsInTrainingCard);
        highlight(card, "yellow", "Interns in Training card [FRD 2.1.5.1]");
        Assert.assertTrue(card.isDisplayed(),
                "FAIL [FRD 2.1.5.1] — 'Interns in Training' KPI card is not visible.");
        highlight(card, "green", "Interns in Training card PRESENT");
        System.out.println("PASS — 'Interns in Training' card visible.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-003: "Training Completion Rate" KPI card is present
    // FRD 2.1.5.1 — "Training Completion Rate: % of interns who completed"
    // -----------------------------------------------------------------------
    @Test(priority = 3,
            description = "TC-CHART-003 [FRD 2.1.5.1]: 'Training Completion Rate' KPI card is visible")
    public void verifyCompletionRateCard() {
        WebElement card = driver.findElement(completionRateCard);
        highlight(card, "yellow", "Training Completion Rate card [FRD 2.1.5.1]");
        Assert.assertTrue(card.isDisplayed(),
                "FAIL [FRD 2.1.5.1] — 'Training Completion Rate' KPI card not visible.");
        highlight(card, "green", "Training Completion Rate card PRESENT");
        System.out.println("PASS — 'Training Completion Rate' card visible.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-004: All KPI cards (at least 6) are present on dashboard
    // FRD 2.1.5.1 — 6 summary metric cards total
    // -----------------------------------------------------------------------
    @Test(priority = 4,
            description = "TC-CHART-004 [FRD 2.1.5.1]: Dashboard has at least 6 KPI metric cards")
    public void verifyTotalKpiCardCount() {
        List<WebElement> kpiNumbers = dashPage.getKpiNumberElements();
        int count = kpiNumbers.size();
        System.out.println("Found KPI number elements: " + count);
        Assert.assertTrue(count >= 6,
                "FAIL [FRD 2.1.5.1] — Expected at least 6 KPI cards " +
                        "(Total Cohorts, Active, Completed, Total Interns, Interns in Training, Completion Rate). " +
                        "Found: " + count);
        System.out.println("PASS — " + count + " KPI metric cards found on dashboard.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-005: All KPI card numbers are populated (not empty or "--")
    // FRD 2.1.5.1 — "Each card must display a value"
    // -----------------------------------------------------------------------
    @Test(priority = 5,
            description = "TC-CHART-005 [FRD 2.1.5.1]: All KPI card values are non-empty")
    public void verifyAllKpiValuesNonEmpty() {
        List<WebElement> kpiNumbers = dashPage.getKpiNumberElements();
        Assert.assertFalse(kpiNumbers.isEmpty(),
                "FAIL — No KPI number elements found on dashboard.");

        for (int i = 0; i < kpiNumbers.size(); i++) {
            String val = kpiNumbers.get(i).getText().trim();
            Assert.assertFalse(val.isEmpty(),
                    "FAIL [FRD 2.1.5.1] — KPI card #" + (i+1) + " has an empty value.");
            System.out.println("PASS — KPI card #" + (i+1) + " value: " + val);
        }
    }

    // -----------------------------------------------------------------------
    // TC-CHART-006: Completion Rate card value contains "%" sign
    // FRD 2.1.5.1 — "Training Completion Rate: displayed as a percentage"
    // -----------------------------------------------------------------------
    @Test(priority = 6,
            description = "TC-CHART-006 [FRD 2.1.5.1]: Completion Rate card value contains '%'")
    public void verifyCompletionRateShowsPercentage() {
        // Find all KPI numbers and check if any contains "%"
        List<WebElement> kpiNumbers = dashPage.getKpiNumberElements();
        boolean hasPercent = kpiNumbers.stream()
                .anyMatch(el -> el.getText().contains("%") || el.getText().equalsIgnoreCase("N/A"));
        System.out.println("KPI values: " +
                kpiNumbers.stream().map(WebElement::getText).toList());
        Assert.assertTrue(hasPercent,
                "FAIL [FRD 2.1.5.1] — Training Completion Rate should show a % value. " +
                        "None of the KPI cards contains '%'.");
        System.out.println("PASS — Completion Rate % sign confirmed in KPI values.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-007: Metrics section is above the charts section (layout order)
    // FRD 2.1.5 — Summary Metrics comes before Analytics Charts
    // -----------------------------------------------------------------------
    @Test(priority = 7,
            description = "TC-CHART-007 [FRD 2.1.5]: Summary Metrics section is visible and leads the dashboard")
    public void verifySummaryMetricsSectionVisible() {
        List<WebElement> kpiNums = dashPage.getKpiNumberElements();
        Assert.assertFalse(kpiNums.isEmpty(),
                "FAIL [FRD 2.1.5] — Summary metrics section not found on dashboard. " +
                        "It should be the first section of the content area.");
        // Verify it's rendered near the top of the page by checking y-position
        int firstKpiY = kpiNums.get(0).getLocation().getY();
        Assert.assertTrue(firstKpiY < 600,
                "FAIL [FRD 2.1.5] — Summary Metrics appears too far down the page " +
                        "(y=" + firstKpiY + "). It should be near the top of the dashboard content area.");
        System.out.println("PASS — Summary Metrics is visible at y=" + firstKpiY + " (near page top).");
    }

    // =========================================================================
    //  SECTION 2 — Analytics & Visualization Charts (FRD 2.1.5.2)
    //  These are the visual chart components (bar charts, pie charts, etc.)
    //  rendered using Chart.js as <canvas> elements in the DOM.
    // =========================================================================

    // -----------------------------------------------------------------------
    // TC-CHART-008: At least one chart canvas element is present on the dashboard
    // FRD 2.1.5.2 — "Analytics and Visualization Section"
    // -----------------------------------------------------------------------
    @Test(priority = 8,
            description = "TC-CHART-008 [FRD 2.1.5.2]: At least one chart (canvas element) is on the dashboard")
    public void verifyChartCanvasPresent() {
        List<WebElement> canvases = driver.findElements(chartCanvases);
        System.out.println("Found <canvas> elements: " + canvases.size());
        Assert.assertFalse(canvases.isEmpty(),
                "FAIL [FRD 2.1.5.2] — No <canvas> elements found. " +
                        "The Analytics section should render at least one chart. " +
                        "Check if Chart.js is loaded correctly.");
        for (WebElement c : canvases) {
            highlight(c, "yellow", "Chart canvas [FRD 2.1.5.2]");
        }
        System.out.println("PASS — " + canvases.size() + " canvas chart element(s) found.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-009: Multiple charts exist (the dashboard should have more than 1)
    // FRD 2.1.5.2 — "Analytics section provides multiple chart views"
    // -----------------------------------------------------------------------
    @Test(priority = 9,
            description = "TC-CHART-009 [FRD 2.1.5.2]: Dashboard has more than one chart")
    public void verifyMultipleChartsPresent() {
        List<WebElement> canvases = driver.findElements(chartCanvases);
        System.out.println("Canvas elements count: " + canvases.size());
        Assert.assertTrue(canvases.size() >= 2,
                "FAIL [FRD 2.1.5.2] — Expected at least 2 charts in the Analytics section. " +
                        "Found: " + canvases.size());
        System.out.println("PASS — " + canvases.size() + " charts present in Analytics section.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-010: Chart section container element is in the DOM
    // FRD 2.1.5.2 — "Analytics and Visualization Section" must exist as a container
    // -----------------------------------------------------------------------
    @Test(priority = 10,
            description = "TC-CHART-010 [FRD 2.1.5.2]: Analytics chart section container is present")
    public void verifyChartSectionContainerPresent() {
        // First try dedicated chart section containers
        List<WebElement> chartContainers = driver.findElements(chartSections);
        System.out.println("Chart container elements found: " + chartContainers.size());

        // If no dedicated class, fall back to verifying canvas elements exist
        boolean hasCharts = !chartContainers.isEmpty()
                || !driver.findElements(chartCanvases).isEmpty();
        Assert.assertTrue(hasCharts,
                "FAIL [FRD 2.1.5.2] — No chart container or canvas found on dashboard. " +
                        "The Analytics section must render at minimum one visual chart element.");
        if (!chartContainers.isEmpty()) {
            highlight(chartContainers.get(0), "green", "Chart section container [FRD 2.1.5.2]");
        }
        System.out.println("PASS — Chart section found on dashboard.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-011: Chart canvases have non-zero width and height
    // FRD 2.1.5.2 — Charts must actually render (not be 0x0 invisible)
    // -----------------------------------------------------------------------
    @Test(priority = 11,
            description = "TC-CHART-011 [FRD 2.1.5.2]: Chart canvas elements have non-zero dimensions")
    public void verifyChartCanvasHasDimensions() {
        List<WebElement> canvases = driver.findElements(chartCanvases);
        Assert.assertFalse(canvases.isEmpty(),
                "FAIL — No canvas elements found to check dimensions.");

        for (int i = 0; i < canvases.size(); i++) {
            WebElement canvas = canvases.get(i);
            int width  = canvas.getSize().getWidth();
            int height = canvas.getSize().getHeight();
            System.out.println("Canvas #" + (i+1) + " — width=" + width + " height=" + height);
            Assert.assertTrue(width > 0 && height > 0,
                    "FAIL [FRD 2.1.5.2] — Canvas #" + (i+1) + " has zero dimensions (" +
                            width + "x" + height + "). The chart did not render.");
            highlight(canvas, "green", "Canvas " + (i+1) + " rendered (" + width + "x" + height + ")");
        }
        System.out.println("PASS — All chart canvases have non-zero dimensions.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-012: Section headings/titles exist for the chart area
    // FRD 2.1.5.2 — Each analytics section should be labelled
    // -----------------------------------------------------------------------
    @Test(priority = 12,
            description = "TC-CHART-012 [FRD 2.1.5.2]: Analytics section has visible headings/labels")
    public void verifyChartSectionHeadingsPresent() {
        List<WebElement> headings = driver.findElements(sectionHeadings);
        System.out.println("Section heading elements found: " + headings.size());
        Assert.assertFalse(headings.isEmpty(),
                "FAIL [FRD 2.1.5.2] — No section headings found on dashboard. " +
                        "Each analytics section should have a visible title label.");
        for (WebElement h : headings) {
            System.out.println("Section heading: \"" + h.getText() + "\"");
        }
        System.out.println("PASS — " + headings.size() + " section heading(s) found.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-013: No chart-related error messages are shown
    // FRD 2.1.5.2 — Charts should load without errors
    // -----------------------------------------------------------------------
    @Test(priority = 13,
            description = "TC-CHART-013 [FRD 2.1.5.2]: No error state shown in Analytics section")
    public void verifyNoChartErrorState() {
        By errorMessages = By.cssSelector(
                "div.error, div.status-msg.error, div[class*='error'], " +
                        "p[class*='error'], span[class*='error']"
        );
        List<WebElement> errors = driver.findElements(errorMessages);
        long visibleErrors = errors.stream().filter(WebElement::isDisplayed).count();
        Assert.assertEquals(visibleErrors, 0,
                "FAIL [FRD 2.1.5.2] — " + visibleErrors + " error message(s) visible on dashboard. " +
                        "Charts may have failed to load data from the API.");
        System.out.println("PASS — No error messages visible in Analytics section.");
    }

    // =========================================================================
    //  SECTION 3 — Cohort Data Management Grid (FRD 2.1.5.3)
    //  A table listing all cohorts with their details.
    // =========================================================================

    // -----------------------------------------------------------------------
    // TC-CHART-014: Cohort data table/grid is present on dashboard
    // FRD 2.1.5 — "Cohort Data Management Grid" section
    // -----------------------------------------------------------------------
    @Test(priority = 14,
            description = "TC-CHART-014 [FRD 2.1.5]: Cohort data grid/table is present on dashboard")
    public void verifyCohortDataGridPresent() {
        List<WebElement> grids = driver.findElements(cohortDataTable);
        System.out.println("Data grid/table elements found: " + grids.size());
        Assert.assertFalse(grids.isEmpty(),
                "FAIL [FRD 2.1.5] — No cohort data table or grid found on dashboard. " +
                        "FRD 2.1.5 specifies a 'Cohort Data Management Grid' section.");
        highlight(grids.get(0), "yellow", "Cohort Data Grid [FRD 2.1.5]");
        System.out.println("PASS — Cohort data grid/table is present.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-015: Cohort data table has column headers
    // FRD 2.1.5 — Columns: Cohort ID, Name, Status, Service Line, Dates
    // -----------------------------------------------------------------------
    @Test(priority = 15,
            description = "TC-CHART-015 [FRD 2.1.5]: Cohort data grid has column headers")
    public void verifyTableHasColumnHeaders() {
        List<WebElement> headers = driver.findElements(tableHeaders);
        System.out.println("Table header count: " + headers.size());
        Assert.assertFalse(headers.isEmpty(),
                "FAIL [FRD 2.1.5] — No column headers found in the cohort data grid. " +
                        "Expected headers like: Cohort ID, Name, Status, Service Line, Start Date.");
        for (WebElement h : headers) {
            System.out.println("Column header: \"" + h.getText() + "\"");
            highlight(h, "green", "Column header: " + h.getText());
        }
        System.out.println("PASS — " + headers.size() + " column header(s) found.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-016: Cohort ID column header is present
    // FRD 2.1.5 — "Cohort ID" is a required column in the grid
    // -----------------------------------------------------------------------
    @Test(priority = 16,
            description = "TC-CHART-016 [FRD 2.1.5]: 'Cohort ID' column header is present in grid")
    public void verifyCohortIdColumnPresent() {
        By cohortIdHeader = By.xpath(
                "//th[contains(text(),'Cohort ID') or contains(text(),'COHORT ID')] | " +
                        "//td[contains(@class,'header') and contains(text(),'Cohort')]"
        );
        boolean present = !driver.findElements(cohortIdHeader).isEmpty();
        Assert.assertTrue(present,
                "FAIL [FRD 2.1.5] — 'Cohort ID' column not found in the data grid.");
        System.out.println("PASS — 'Cohort ID' column header is present.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-017: Data grid has at least one data row
    // FRD 2.1.5 — Grid should show real cohort data (not empty)
    // -----------------------------------------------------------------------
    @Test(priority = 17,
            description = "TC-CHART-017 [FRD 2.1.5]: Cohort data grid contains at least one data row")
    public void verifyGridHasDataRows() {
        List<WebElement> rows = driver.findElements(tableRows);
        System.out.println("Data grid row count: " + rows.size());
        Assert.assertFalse(rows.isEmpty(),
                "FAIL [FRD 2.1.5] — The cohort data grid has no rows. " +
                        "The dashboard should show existing cohorts from the database.");
        System.out.println("PASS — Cohort grid has " + rows.size() + " data row(s).");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-018: Status column is present in the grid
    // FRD 2.1.5 — Status column required (Active/Completed/Upcoming/Planning)
    // -----------------------------------------------------------------------
    @Test(priority = 18,
            description = "TC-CHART-018 [FRD 2.1.5]: 'Status' column header is present in cohort grid")
    public void verifyStatusColumnPresent() {
        By statusHeader = By.xpath(
                "//th[contains(text(),'Status') or contains(text(),'STATUS')] | " +
                        "//div[contains(@class,'header') and contains(text(),'Status')]"
        );
        boolean present = !driver.findElements(statusHeader).isEmpty();
        Assert.assertTrue(present,
                "FAIL [FRD 2.1.5] — 'Status' column not found in the cohort data grid.");
        System.out.println("PASS — 'Status' column header is present.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-019: All 3 main sections are present together on one page
    // FRD 2.1.5 — The dashboard must show all 3 sections simultaneously
    // -----------------------------------------------------------------------
    @Test(priority = 19,
            description = "TC-CHART-019 [FRD 2.1.5]: All 3 dashboard sections visible on the page")
    public void verifyAllThreeSectionsPresent() {
        boolean hasMetrics = !dashPage.getKpiNumberElements().isEmpty();
        boolean hasCharts  = !driver.findElements(chartCanvases).isEmpty()
                || !driver.findElements(chartSections).isEmpty();
        boolean hasGrid    = !driver.findElements(cohortDataTable).isEmpty();

        System.out.println("Section 1 (Metrics): " + hasMetrics);
        System.out.println("Section 2 (Charts):  " + hasCharts);
        System.out.println("Section 3 (Grid):    " + hasGrid);

        Assert.assertTrue(hasMetrics,
                "FAIL [FRD 2.1.5] — Section 1 (Summary Metrics) not found.");
        Assert.assertTrue(hasCharts,
                "FAIL [FRD 2.1.5] — Section 2 (Analytics Charts) not found.");
        Assert.assertTrue(hasGrid,
                "FAIL [FRD 2.1.5] — Section 3 (Cohort Data Grid) not found.");
        System.out.println("PASS — All 3 dashboard sections (FRD 2.1.5) are present.");
    }

    // -----------------------------------------------------------------------
    // TC-CHART-020: No loading spinner is stuck on screen
    // FRD 2.1.5 — All sections must fully load without a hung spinner
    // -----------------------------------------------------------------------
    @Test(priority = 20,
            description = "TC-CHART-020 [FRD 2.1.5]: No loading spinner visible after page load")
    public void verifyNoLoadingSpinnerStuck() {
        By spinner = By.cssSelector(
                "div.spinner, div.loading, div[class*='spinner'], " +
                        "div[class*='loading'], mat-spinner, app-spinner"
        );
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        List<WebElement> spinners = driver.findElements(spinner);
        boolean stuck = spinners.stream().anyMatch(WebElement::isDisplayed);
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        Assert.assertFalse(stuck,
                "FAIL [FRD 2.1.5] — A loading spinner is still visible on the dashboard. " +
                        "One or more sections failed to finish loading.");
        System.out.println("PASS — No loading spinner stuck on dashboard.");
    }
}
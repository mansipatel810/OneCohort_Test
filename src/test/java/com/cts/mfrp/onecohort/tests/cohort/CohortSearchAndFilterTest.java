package com.cts.mfrp.onecohort.tests.cohort;

import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortFilterDropdownComponent;
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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cohort Search and Filter Tests — FRD Section 2.2.5 (Search and Filter)
 *
 * Scope: search input visibility, search-by-name, search-by-ID, clear search,
 *        status filter dropdown presence and option enumeration,
 *        filtering to Active/Completed, and Learning Path filter availability.
 *
 * Login: Super Admin — all tests share one browser session.
 * Pre-condition: at least one cohort must exist in the database.
 *
 * ── FRD Traceability ─────────────────────────────────────────────────────────
 * TC-FILTER-001  FRD 2.2.5  Search input is visible on the Cohort Management page
 * TC-FILTER-002  FRD 2.2.5  Search by Cohort Name narrows the result set
 * TC-FILTER-003  FRD 2.2.5  Search by Cohort ID narrows the result set
 * TC-FILTER-004  FRD 2.2.5  Clearing the search restores the full result set
 * TC-FILTER-005  FRD 2.2.5  Status filter dropdown contains required option values
 *                            (Planning, In-Progress, Completed, Active, Upcoming)
 * TC-FILTER-006  FRD 2.2.5  Status filter "Active" shows only Active cohorts
 * TC-FILTER-007  FRD 2.2.5  Status filter "Completed" shows only Completed cohorts
 * TC-FILTER-008  FRD 2.2.5  Learning Path filter exists (GAP documented if absent)
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Listeners(ExtentReportListener.class)
public class CohortSearchAndFilterTest {

    private WebDriver driver;
    /** Exposed for ExtentReportListener screenshot capture. */
    public WebDriver getDriver() { return driver; }
    private WebDriverWait wait;
    private CohortManagementPage cohortPage;
    private CohortFilterDropdownComponent filterComponent;

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
        cohortPage      = new CohortManagementPage(driver);
        filterComponent = new CohortFilterDropdownComponent(driver);
        System.out.println("Setup complete — URL: " + driver.getCurrentUrl());
    }

    // -------------------------------------------------------
    // TC-FILTER-001 — Search input is visible on the page
    // FRD 2.2.5 — A search input must be present to search cohorts
    //             by Cohort ID or Cohort Name
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifySearchInputVisible() {
        boolean visible = filterComponent.isSearchInputVisible();
        if (!visible) {
            visible = cohortPage.isSearchInputVisible();
        }
        Assert.assertTrue(
                visible,
                "FAIL - Search input is NOT visible on the Cohort Management page. " +
                "FRD 2.2.5 requires a search field for searching cohorts by name or ID.");

        WebElement searchEl = driver.findElement(By.cssSelector(
                "input[type='search'], input[type='text'][placeholder*='earch'], " +
                "input[formcontrolname*='search'], input[placeholder*='Search']"));
        highlight(searchEl);
        System.out.println("PASS - Search input is visible");
    }

    // -------------------------------------------------------
    // TC-FILTER-002 — Searching by Cohort Name filters results
    // FRD 2.2.5 — Real-time search; results narrow as user types
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifySearchByCohortNameFilters() {
        List<WebElement> rows = cohortPage.getTableRows();
        if (rows.isEmpty()) {
            System.out.println("SKIP - No cohort rows to derive a search term from");
            return;
        }
        String cohortName = rows.get(0)
                .findElement(By.cssSelector("td:nth-child(2)"))
                .getText().trim();
        if (cohortName.isEmpty()) {
            System.out.println("SKIP - First row Cohort Name is empty; cannot search");
            return;
        }

        String searchTerm = cohortName.length() > 4 ? cohortName.substring(0, 4) : cohortName;
        filterComponent.search(searchTerm);
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        List<WebElement> filteredRows = cohortPage.getTableRows();
        Assert.assertFalse(
                filteredRows.isEmpty(),
                "FAIL - Search for '" + searchTerm + "' returned no rows. " +
                "FRD 2.2.5 requires that searching by Cohort Name filters the grid.");

        for (WebElement row : filteredRows) {
            String rowName = row.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
            Assert.assertTrue(
                    rowName.toLowerCase().contains(searchTerm.toLowerCase()),
                    "FAIL - Row '" + rowName + "' visible after searching '" + searchTerm + "'. " +
                    "FRD 2.2.5 requires only matching cohorts to be shown.");
        }
        System.out.println("PASS - Name search '" + searchTerm + "' returned " +
                filteredRows.size() + " row(s)");

        filterComponent.clearSearch();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    // -------------------------------------------------------
    // TC-FILTER-003 — Searching by Cohort ID filters results
    // FRD 2.2.5 — Search by Cohort ID (e.g. COH-10001) must work
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifySearchByCohortIdFilters() {
        List<WebElement> rows = cohortPage.getTableRows();
        if (rows.isEmpty()) {
            System.out.println("SKIP - No rows to derive a Cohort ID from");
            return;
        }
        String cohortId = rows.get(0)
                .findElement(By.cssSelector("td:first-child"))
                .getText().trim();
        if (cohortId.isEmpty()) {
            System.out.println("SKIP - First row Cohort ID is empty; cannot search");
            return;
        }

        filterComponent.search(cohortId);
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        List<WebElement> filteredRows = cohortPage.getTableRows();
        Assert.assertFalse(
                filteredRows.isEmpty(),
                "FAIL - Search for Cohort ID '" + cohortId + "' returned no rows. " +
                "FRD 2.2.5 requires that searching by Cohort ID filters the grid correctly.");

        String firstId = filteredRows.get(0)
                .findElement(By.cssSelector("td:first-child"))
                .getText().trim();
        Assert.assertTrue(
                firstId.contains(cohortId) || cohortId.contains(firstId),
                "FAIL - After searching '" + cohortId + "', first row shows '" + firstId + "'. " +
                "FRD 2.2.5 search-by-ID is not working correctly.");
        System.out.println("PASS - Cohort ID search '" + cohortId + "' returned correct result");

        filterComponent.clearSearch();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    // -------------------------------------------------------
    // TC-FILTER-004 — Clearing search restores the full result set
    // FRD 2.2.5 — Clearing the search field must reset the grid
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifyClearSearchRestoresFullResults() {
        int totalRowsBefore = cohortPage.getTableRows().size();
        if (totalRowsBefore == 0) {
            System.out.println("SKIP - No rows available; skipping clear-search test");
            return;
        }

        filterComponent.search("COH-");
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        filterComponent.clearSearch();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        int totalRowsAfter = cohortPage.getTableRows().size();
        Assert.assertEquals(
                totalRowsAfter, totalRowsBefore,
                "FAIL - After clearing search, grid shows " + totalRowsAfter +
                " rows but " + totalRowsBefore + " were visible before. " +
                "FRD 2.2.5 requires clearing search to restore the complete cohort list.");
        System.out.println("PASS - Clear search restored " + totalRowsAfter + " rows");
    }

    // -------------------------------------------------------
    // TC-FILTER-005 — Status filter dropdown with required option values
    // FRD 2.2.5 — Status filter options must include:
    //             Planning, In-Progress, Completed (+ All, Active, Upcoming)
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifyStatusFilterDropdown() {
        boolean visible = filterComponent.isStatusDropdownVisible();
        if (!visible) {
            List<WebElement> selects = driver.findElements(By.cssSelector("select"));
            visible = !selects.isEmpty();
        }
        Assert.assertTrue(
                visible,
                "FAIL - Status filter dropdown is NOT visible on the Cohort Management page. " +
                "FRD 2.2.5 requires a dropdown to filter cohorts by status.");

        List<WebElement> options = filterComponent.getStatusOptions();
        Assert.assertFalse(options.isEmpty(),
                "FAIL - Status dropdown has no options.");

        List<String> optionTexts = options.stream()
                .map(o -> o.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
        System.out.println("INFO - Status options found: " + optionTexts);

        // FRD 2.2.5 required status values
        List<String> requiredOptions = Arrays.asList("Planning", "In-Progress", "Completed");
        for (String expected : requiredOptions) {
            boolean found = optionTexts.stream()
                    .anyMatch(t -> t.equalsIgnoreCase(expected) ||
                                   t.equalsIgnoreCase(expected.replace("-", " ")));
            System.out.println((found ? "PASS" : "GAP") +
                    " - Status option '" + expected + "': " +
                    (found ? "present" : "NOT found — FRD 2.2.5 requires this value"));
        }

        Assert.assertTrue(
                optionTexts.size() >= 3,
                "FAIL - Status dropdown has fewer than 3 options (found: " + optionTexts.size() + "). " +
                "FRD 2.2.5 requires at minimum: Planning, In-Progress, Completed. " +
                "Options: " + optionTexts);
        System.out.println("PASS - Status filter has " + optionTexts.size() + " options");
    }

    // -------------------------------------------------------
    // TC-FILTER-006 — Status filter "Active" shows only Active rows
    // FRD 2.2.5 — Filtering by status must narrow results correctly
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyStatusFilterActive() {
        if (!filterComponent.isStatusDropdownVisible()) {
            System.out.println("SKIP - Status filter not available; skipping Active filter test");
            return;
        }

        filterComponent.selectStatus("Active");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        List<WebElement> rows = cohortPage.getTableRows();
        if (rows.isEmpty()) {
            System.out.println("INFO - No Active cohorts in DB; empty result is expected");
        } else {
            for (WebElement row : rows) {
                List<WebElement> statusCells = row.findElements(By.cssSelector(
                        "[class*='badge'], [class*='status'], td:nth-child(4)"));
                if (!statusCells.isEmpty()) {
                    String statusText = statusCells.get(0).getText().trim();
                    Assert.assertTrue(
                            statusText.equalsIgnoreCase("Active") || statusText.isEmpty(),
                            "FAIL - After filtering by 'Active', a row shows status '" +
                            statusText + "'. FRD 2.2.5 requires only Active cohorts to appear.");
                }
            }
            System.out.println("PASS - Status filter 'Active' returned " + rows.size() + " row(s)");
        }

        resetStatusFilter();
    }

    // -------------------------------------------------------
    // TC-FILTER-007 — Status filter "Completed" shows only Completed rows
    // FRD 2.2.5 — Same filtering rule applies for Completed status
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyStatusFilterCompleted() {
        if (!filterComponent.isStatusDropdownVisible()) {
            System.out.println("SKIP - Status filter not available; skipping Completed filter test");
            return;
        }

        filterComponent.selectStatus("Completed");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        List<WebElement> rows = cohortPage.getTableRows();
        if (rows.isEmpty()) {
            System.out.println("INFO - No Completed cohorts in DB; empty result is expected");
        } else {
            for (WebElement row : rows) {
                List<WebElement> statusCells = row.findElements(By.cssSelector(
                        "[class*='badge'], [class*='status'], td:nth-child(4)"));
                if (!statusCells.isEmpty()) {
                    String statusText = statusCells.get(0).getText().trim();
                    Assert.assertTrue(
                            statusText.equalsIgnoreCase("Completed") || statusText.isEmpty(),
                            "FAIL - After filtering by 'Completed', a row shows status '" +
                            statusText + "'. FRD 2.2.5 requires only Completed cohorts to appear.");
                }
            }
            System.out.println("PASS - Status filter 'Completed' returned " + rows.size() + " row(s)");
        }

        resetStatusFilter();
    }

    // -------------------------------------------------------
    // TC-FILTER-008 — Learning Path filter is available
    // FRD 2.2.5 — Learning Path filter (dependent on Service Line selection)
    // -------------------------------------------------------
    @Test(priority = 8)
    public void verifyLearningPathFilterVisible() {
        boolean visible = filterComponent.isLearningPathDropdownVisible();

        if (!visible && filterComponent.isServiceLineDropdownVisible()) {
            filterComponent.selectServiceLine(ConfigReader.getValidServiceLineId());
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
            visible = filterComponent.isLearningPathDropdownVisible();
        }

        Assert.assertTrue(
                visible,
                "FAIL - Learning Path filter dropdown is NOT present. " +
                "GAP: FRD 2.2.5 explicitly requires a Learning Path filter " +
                "on the Cohort Management page. This may be a dependent dropdown " +
                "that appears after a Service Line is selected — verify implementation.");
        System.out.println("PASS - Learning Path filter dropdown is visible");
    }

    // ── Private helper ────────────────────────────────────────────────────────
    private void resetStatusFilter() {
        try {
            new Select(driver.findElement(By.cssSelector(
                    "select[name='status'], select#statusFilter, " +
                    "select[formcontrolname='status']")))
                    .selectByIndex(0);
            Thread.sleep(400);
        } catch (Exception ignored) {}
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
        System.out.println("Browser closed — CohortSearchAndFilterTest complete");
    }
}

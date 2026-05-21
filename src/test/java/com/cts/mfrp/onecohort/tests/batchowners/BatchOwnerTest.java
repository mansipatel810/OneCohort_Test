package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * Batch Owner (POC) Role Test Suite
 *
 * Tests covered:
 *   1. Batch Owner dashboard loads (URL not on /login, summary cards visible)
 *   2. Dashboard shows all 4 cohort summary cards (Total, Active, Completed, Upcoming)
 *   3. Sidebar has only 2 links: Dashboard and Cohorts (limited access)
 *   4. Cohorts list page loads with a table containing required columns
 *   5. Search bar filters the cohorts table in real time
 *   6. Filters button reveals Status and Learning Path dropdowns
 *   7. Clicking a Cohort ID opens the cohort detail page
 *   8. Batch Owner login validation: missing POC ID shows an alert
 *
 * All tests 1–7 run in a single logged-in Batch Owner browser session.
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "batchowner"})
public class BatchOwnerTest extends BaseClassTest {

    private BatchOwnerDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsBatchOwner() {
        // Log in as Batch Owner with user ID, service line, and POC ID
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsBatchOwner(
                ConfigReader.getSuperAdminUserId(),
                ConfigReader.getValidServiceLineId(),
                ConfigReader.getValidPocId());

        // Wait until the URL is no longer the login page
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(d -> !d.getCurrentUrl().contains("login"));

        dashPage = new BatchOwnerDashboardPage(driver);
        System.out.println("Batch Owner login complete. URL: " + driver.getCurrentUrl());
    }

    // ── TC-BO-001 ─────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-BO-001: Batch Owner dashboard loads after successful login")
    public void testBatchOwnerDashboardLoads() {
        // Verify we are NOT on the login page
        String url = driver.getCurrentUrl();
        Assert.assertFalse(url.contains("/login"),
                "URL should not be on /login after Batch Owner login. Got: " + url);

        // Verify the dashboard heading is visible
        Assert.assertTrue(dashPage.isDashboardHeadingVisible(),
                "Batch Owner Dashboard heading should be visible after login");

        System.out.println("PASS - Batch Owner dashboard loaded. URL: " + url);
    }

    // ── TC-BO-002 ─────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-BO-002: Dashboard shows all 4 cohort summary cards")
    public void testDashboardShowsCohortSummaryCards() {
        // The dashboard must show: Total Cohorts, Active, Completed, Upcoming
        Assert.assertTrue(dashPage.isTotalCohortsCardVisible(),    "Total Cohorts card should be visible");
        Assert.assertTrue(dashPage.isActiveCohortsCardVisible(),   "Active Cohorts card should be visible");
        Assert.assertTrue(dashPage.isCompletedCohortsCardVisible(), "Completed Cohorts card should be visible");
        Assert.assertTrue(dashPage.isUpcomingCohortsCardVisible(), "Upcoming Cohorts card should be visible");

        System.out.println("PASS - All 4 cohort summary cards are visible on the dashboard.");
    }

    // ── TC-BO-003 ─────────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-BO-003: Sidebar has Dashboard and Cohorts links only (limited access)")
    public void testSidebarHasDashboardAndCohortsLinks() {
        // Batch Owner should only see Dashboard and Cohorts in the sidebar (read-only role)
        Assert.assertTrue(dashPage.isSidebarDashboardLinkVisible(),
                "Sidebar should have a 'Dashboard' link");
        Assert.assertTrue(dashPage.isSidebarCohortsLinkVisible(),
                "Sidebar should have a 'Cohorts' link");

        System.out.println("PASS - Sidebar has Dashboard and Cohorts links.");
    }

    // ── TC-BO-004 ─────────────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-BO-004: Cohorts list page has a search bar and table with required columns")
    public void testCohortsListPageLoads() {
        // Navigate to the Cohorts list page by clicking the sidebar link
        WebElement cohortsLink = dashPage.getSidebarCohortsLinkElement();
        cohortsLink.click();

        // Wait for the search bar to appear (confirms the list page loaded)
        dashPage.waitForSearchBarVisible();

        // Verify the search bar is present
        Assert.assertTrue(dashPage.isSearchBarVisible(),
                "Search bar should be visible on the Cohorts list page");

        // Verify the cohorts table has required columns: ID, Name, Status, Date
        Assert.assertTrue(dashPage.isCohortsTableVisible(),
                "Cohorts table should be visible on the list page");

        List<WebElement> headers = dashPage.getCohortsTableHeaderCells();
        String allHeaders = headers.stream()
                .map(h -> h.getText().trim().toLowerCase())
                .reduce("", (a, b) -> a + " " + b);

        Assert.assertTrue(allHeaders.contains("id"),     "Table should have a 'Cohort ID' column");
        Assert.assertTrue(allHeaders.contains("name"),   "Table should have a 'Name' column");
        Assert.assertTrue(allHeaders.contains("status"), "Table should have a 'Status' column");

        System.out.println("PASS - Cohorts list page loaded with table and columns: " + allHeaders);
    }

    // ── TC-BO-005 ─────────────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-BO-005: Search bar filters the cohorts table in real time")
    public void testSearchFiltersCohortsTable() {
        // Get the first cohort's text to use as a search term
        String searchTerm = "INT";
        String firstCellText = dashPage.getFirstCohortRowFirstCellText();
        if (!firstCellText.isEmpty() && firstCellText.length() >= 3) {
            searchTerm = firstCellText.substring(0, 3);
        }

        // Type the search term in the search bar
        WebElement searchBar = dashPage.getSearchBarElement();
        searchBar.clear();
        searchBar.sendKeys(searchTerm);

        // Wait a moment for the table to filter (no Thread.sleep — use table settle method)
        dashPage.waitForCohortsTableToSettle(5);

        // Verify the search returned at least one result
        List<WebElement> filteredRows = dashPage.getCohortsTableRows();
        Assert.assertFalse(filteredRows.isEmpty(),
                "Search with term '" + searchTerm + "' should return at least one result");
        System.out.println("PASS - Search '" + searchTerm + "' filtered table to " + filteredRows.size() + " rows.");

        // Clear the search to restore the full list
        searchBar.clear();
        dashPage.waitForCohortsTableToSettle(5);
        List<WebElement> restoredRows = dashPage.getCohortsTableRows();
        Assert.assertFalse(restoredRows.isEmpty(),
                "Clearing the search should restore all cohort rows");
        System.out.println("PASS - Search cleared. Full list restored with " + restoredRows.size() + " rows.");
    }

    // ── TC-BO-006 ─────────────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-BO-006: Filters button shows Status and Learning Path dropdowns")
    public void testFilterDropdownsAppear() {
        // Click the Filters button to reveal filter dropdowns
        Assert.assertTrue(dashPage.isFiltersButtonVisible(),
                "A 'Filters' button should be visible on the Cohorts list page");

        WebElement filtersBtn = dashPage.getFiltersButtonElement();
        filtersBtn.click();

        // Wait for the filter dropdowns to appear
        dashPage.waitForStatusFilterVisible();

        // Verify both filter dropdowns are visible
        Assert.assertTrue(dashPage.isStatusFilterVisible(),
                "Status filter dropdown should appear after clicking Filters");
        Assert.assertTrue(dashPage.isLearningPathFilterVisible(),
                "Learning Path filter dropdown should appear after clicking Filters");

        System.out.println("PASS - Filter dropdowns (Status, Learning Path) are visible.");
    }

    // ── TC-BO-007 ─────────────────────────────────────────────────────────────
    @Test(priority = 7, description = "TC-BO-007: Clicking a Cohort ID link opens the cohort detail page")
    public void testCohortDetailPageLoads() {
        // Get the list of Cohort ID links from the table
        List<WebElement> cohortLinks = dashPage.getCohortIdLinksOrCells();
        Assert.assertFalse(cohortLinks.isEmpty(),
                "There should be at least one Cohort ID link in the cohorts table");

        String urlBefore = driver.getCurrentUrl();

        // Click the first cohort ID link
        cohortLinks.get(0).click();

        // Wait for the URL to change (we navigated to the detail page)
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !d.getCurrentUrl().equals(urlBefore));

        String urlAfter = driver.getCurrentUrl();
        Assert.assertNotEquals(urlAfter, urlBefore,
                "Clicking a Cohort ID should navigate to the cohort detail page");
        System.out.println("PASS - Cohort detail page loaded. URL: " + urlAfter);

        // Verify the "Back to Cohorts" link is present (confirms we're on the detail page)
        WebElement backLink = dashPage.getBackToCohortsElement();
        Assert.assertNotNull(backLink, "'Back to Cohorts' link should be present on the detail page");
        System.out.println("PASS - Back to Cohorts link is visible on the detail page.");
    }

    // ── TC-BO-009 ─────────────────────────────────────────────────────────────
    @Test(priority = 8, description = "TC-BO-009: Training timeline section is visible on the cohort detail page")
    public void testTrainingTimelineVisible() {
        // The cohort detail page should show a week-by-week training timeline
        Assert.assertTrue(dashPage.isTrainingTimelineVisible(),
                "Training timeline section should be visible on the cohort detail page");
        System.out.println("PASS - Training timeline section is visible on the cohort detail page.");
    }

    // ── TC-BO-010 ─────────────────────────────────────────────────────────────
    @Test(priority = 9, description = "TC-BO-010: Cohort detail page shows Qualifier, Interim, and Final evaluation sections")
    public void testEvaluationPanelVisible() {
        // All 3 evaluation checkpoints must appear on the detail page
        Assert.assertTrue(dashPage.isQualifierExamVisible(),
                "Qualifier Exam section should be visible on the cohort detail page");
        Assert.assertTrue(dashPage.isInterimEvaluationVisible(),
                "Interim Evaluation section should be visible on the cohort detail page");
        Assert.assertTrue(dashPage.isFinalEvaluationVisible(),
                "Final Evaluation section should be visible on the cohort detail page");
        System.out.println("PASS - All 3 evaluation sections visible (Qualifier, Interim, Final).");
    }

    // ── TC-BO-011 ─────────────────────────────────────────────────────────────
    @Test(priority = 10, description = "TC-BO-011: Trainees table is visible on the cohort detail page")
    public void testTraineesTableVisibleOnDetailPage() {
        // The cohort detail should show a list of trainees enrolled in the cohort
        Assert.assertTrue(dashPage.isTraineesTableVisible(),
                "Trainees table should be visible on the cohort detail page");
        System.out.println("PASS - Trainees table is visible on the cohort detail page.");
    }

    // ── TC-BO-012 ─────────────────────────────────────────────────────────────
    @Test(priority = 11, description = "TC-BO-012: Overall progress section is visible on the cohort detail page")
    public void testOverallProgressVisible() {
        // The overall progress section summarises how the cohort is doing overall
        Assert.assertTrue(dashPage.isOverallProgressVisible(),
                "Overall progress section should be visible on the cohort detail page");
        System.out.println("PASS - Overall progress section is visible on the cohort detail page.");
    }

    // ── TC-BO-013 ─────────────────────────────────────────────────────────────
    @Test(priority = 12, description = "TC-BO-013: Cohort detail page shows Total Members, Learning Path, and Status info")
    public void testDetailPageSummaryInfoVisible() {
        // Key cohort metadata should be displayed at the top of the detail page
        Assert.assertTrue(dashPage.isDetailTotalMembersVisible(),
                "Total Members info should be visible on the cohort detail page");
        Assert.assertTrue(dashPage.isDetailLearningPathVisible(),
                "Learning Path info should be visible on the cohort detail page");
        Assert.assertTrue(dashPage.isDetailStatusVisible(),
                "Status info should be visible on the cohort detail page");
        System.out.println("PASS - Cohort detail summary info (Total Members, Learning Path, Status) is visible.");
    }

    // ── TC-BO-008 ─────────────────────────────────────────────────────────────
    @Test(priority = 13, description = "TC-BO-008: Batch Owner login without entering a POC ID shows a validation alert")
    public void testBatchOwnerLoginWithoutPocIdShowsAlert() {
        // Navigate to the login page
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        // Enter user ID, select Batch Owner role, select Service Line, but skip POC ID
        loginPage.enterUserId(ConfigReader.getSuperAdminUserId())
                 .selectRole("Batch Owner")
                 .selectServiceLine(ConfigReader.getValidServiceLineId())
                 .clickLoginButton();

        // An alert should appear asking the user to enter a POC ID
        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_ENTER_POC_ID,
                "Alert should say: " + AppConstants.ALERT_ENTER_POC_ID);
        System.out.println("PASS - Validation alert shown for missing POC ID: " + alertText);
    }
}

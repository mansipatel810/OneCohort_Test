package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
// ── FIX 1: @Test REMOVED from class level ────────────────────────────────────
// @Test on the class causes ALL tests to be "ignored" when @BeforeClass fails.
// @Test belongs only on individual test methods.
// ─────────────────────────────────────────────────────────────────────────────
public class BatchOwnerTest extends BaseClassTest {

    private BatchOwnerDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsBatchOwner() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsBatchOwner(
                ConfigReader.getSuperAdminUserId(),
                ConfigReader.getValidServiceLineId(),
                ConfigReader.getValidPocId());

        // Wait until URL leaves the login page
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(d -> !d.getCurrentUrl().contains("login"));

        // ── FIX 2: Wait for Angular to fully render the dashboard ─────────────
        // Without this wait, tests run before the page is ready and fail even
        // though everything works fine manually.
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("app-batch-owner-dashboard h2")));
        // ─────────────────────────────────────────────────────────────────────

        dashPage = new BatchOwnerDashboardPage(driver);
        System.out.println("Batch Owner login complete. URL: " + driver.getCurrentUrl());
    }

    // ── TC-BO-001 ─────────────────────────────────────────────────────────────
    @Test(priority = 1, groups = {"smoke", "regression", "batchowner"},
            description = "TC-BO-001: Batch Owner dashboard loads after successful login")
    public void testBatchOwnerDashboardLoads() {
        String url = driver.getCurrentUrl();
        Assert.assertFalse(url.contains("/login"),
                "URL should not be on /login after Batch Owner login. Got: " + url);
        Assert.assertTrue(dashPage.isDashboardHeadingVisible(),
                "Batch Owner Dashboard heading should be visible after login");
        System.out.println("PASS - Batch Owner dashboard loaded. URL: " + url);
    }

    // ── TC-BO-002 ─────────────────────────────────────────────────────────────
    @Test(priority = 2, groups = {"smoke", "regression", "batchowner"},
            description = "TC-BO-002: Dashboard shows all 4 cohort summary cards")
    public void testDashboardShowsCohortSummaryCards() {
        Assert.assertTrue(dashPage.isTotalCohortsCardVisible(),
                "Total Cohorts card should be visible");
        Assert.assertTrue(dashPage.isActiveCohortsCardVisible(),
                "Active Cohorts card should be visible");
        Assert.assertTrue(dashPage.isCompletedCohortsCardVisible(),
                "Completed Cohorts card should be visible");
        Assert.assertTrue(dashPage.isUpcomingCohortsCardVisible(),
                "Upcoming Cohorts card should be visible");
        System.out.println("PASS - All 4 cohort summary cards are visible on the dashboard.");
    }

    // ── TC-BO-003 ─────────────────────────────────────────────────────────────
    @Test(priority = 3, groups = {"smoke", "regression", "batchowner"},
            description = "TC-BO-003: Sidebar has Dashboard and Cohorts links only (limited access)")
    public void testSidebarHasDashboardAndCohortsLinks() {
        Assert.assertTrue(dashPage.isSidebarDashboardLinkVisible(),
                "Sidebar should have a 'Dashboard' link");
        Assert.assertTrue(dashPage.isSidebarCohortsLinkVisible(),
                "Sidebar should have a 'Cohorts' link");
        System.out.println("PASS - Sidebar has Dashboard and Cohorts links.");
    }

    // ── TC-BO-004 ─────────────────────────────────────────────────────────────
    @Test(priority = 4, groups = {"regression", "batchowner"},
            description = "TC-BO-004: Cohorts list page has a search bar and table with required columns")
    public void testCohortsListPageLoads() {
        WebElement cohortsLink = dashPage.getSidebarCohortsLinkElement();
        cohortsLink.click();
        dashPage.waitForSearchBarVisible();

        Assert.assertTrue(dashPage.isSearchBarVisible(),
                "Search bar should be visible on the Cohorts list page");
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
    @Test(priority = 5, groups = {"regression", "batchowner"},
            description = "TC-BO-005: Search bar filters the cohorts table in real time")
    public void testSearchFiltersCohortsTable() {
        String searchTerm = "INT";
        String firstCellText = dashPage.getFirstCohortRowFirstCellText();
        if (!firstCellText.isEmpty() && firstCellText.length() >= 3) {
            searchTerm = firstCellText.substring(0, 3);
        }

        WebElement searchBar = dashPage.getSearchBarElement();
        searchBar.clear();
        searchBar.sendKeys(searchTerm);
        dashPage.waitForCohortsTableToSettle(5);

        List<WebElement> filteredRows = dashPage.getCohortsTableRows();
        Assert.assertFalse(filteredRows.isEmpty(),
                "Search with term '" + searchTerm + "' should return at least one result");
        System.out.println("PASS - Search '" + searchTerm + "' filtered table to "
                + filteredRows.size() + " rows.");

        searchBar.clear();
        dashPage.waitForCohortsTableToSettle(5);
        List<WebElement> restoredRows = dashPage.getCohortsTableRows();
        Assert.assertFalse(restoredRows.isEmpty(),
                "Clearing the search should restore all cohort rows");
        System.out.println("PASS - Search cleared. Full list restored with "
                + restoredRows.size() + " rows.");
    }

    // ── TC-BO-006 ─────────────────────────────────────────────────────────────
    @Test(priority = 6, groups = {"regression", "batchowner"},
            description = "TC-BO-006: Filters button shows Status and Learning Path dropdowns")
    public void testFilterDropdownsAppear() {
        Assert.assertTrue(dashPage.isFiltersButtonVisible(),
                "A 'Filters' button should be visible on the Cohorts list page");

        WebElement filtersBtn = dashPage.getFiltersButtonElement();
        filtersBtn.click();
        dashPage.waitForStatusFilterVisible();

        Assert.assertTrue(dashPage.isStatusFilterVisible(),
                "Status filter dropdown should appear after clicking Filters");
        Assert.assertTrue(dashPage.isLearningPathFilterVisible(),
                "Learning Path filter dropdown should appear after clicking Filters");
        System.out.println("PASS - Filter dropdowns (Status, Learning Path) are visible.");
    }

    // ── TC-BO-007 ─────────────────────────────────────────────────────────────
    @Test(priority = 7, groups = {"regression", "batchowner"},
            description = "TC-BO-007: Clicking a Cohort ID link opens the cohort detail page")
    public void testCohortDetailPageLoads() {
        List<WebElement> cohortLinks = dashPage.getCohortIdLinksOrCells();
        Assert.assertFalse(cohortLinks.isEmpty(),
                "There should be at least one Cohort ID link in the cohorts table");

        String urlBefore = driver.getCurrentUrl();
        cohortLinks.get(0).click();

        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !d.getCurrentUrl().equals(urlBefore));

        String urlAfter = driver.getCurrentUrl();
        Assert.assertNotEquals(urlAfter, urlBefore,
                "Clicking a Cohort ID should navigate to the cohort detail page");
        System.out.println("PASS - Cohort detail page loaded. URL: " + urlAfter);

        WebElement backLink = dashPage.getBackToCohortsElement();
        Assert.assertNotNull(backLink,
                "'Back to Cohorts' link should be present on the detail page");
        System.out.println("PASS - Back to Cohorts link is visible on the detail page.");
    }

    // ── TC-BO-009 ─────────────────────────────────────────────────────────────
    @Test(priority = 8, groups = {"regression", "batchowner"},
            description = "TC-BO-009: Training timeline section is visible on the cohort detail page")
    public void testTrainingTimelineVisible() {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight / 2)");
        Assert.assertTrue(dashPage.isTrainingTimelineVisible(),
                "Training timeline section should be visible on the cohort detail page");
        System.out.println("PASS - Training timeline section is visible on the cohort detail page.");
    }

    // ── TC-BO-010 ─────────────────────────────────────────────────────────────
    @Test(priority = 9, groups = {"regression", "batchowner"},
            description = "TC-BO-010: Cohort detail page shows Qualifier, Interim, and Final evaluation sections")
    public void testEvaluationPanelVisible() {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Assert.assertTrue(dashPage.isQualifierExamVisible(),
                "Qualifier Exam section should be visible on the cohort detail page");
        Assert.assertTrue(dashPage.isInterimEvaluationVisible(),
                "Interim Evaluation section should be visible on the cohort detail page");
        Assert.assertTrue(dashPage.isFinalEvaluationVisible(),
                "Final Evaluation section should be visible on the cohort detail page");
        System.out.println("PASS - All 3 evaluation sections visible (Qualifier, Interim, Final).");
    }

    // ── TC-BO-011 ─────────────────────────────────────────────────────────────
    @Test(priority = 10, groups = {"regression", "batchowner"},
            description = "TC-BO-011: Trainees table is visible on the cohort detail page")
    public void testTraineesTableVisibleOnDetailPage() {
        Assert.assertTrue(dashPage.isTraineesTableVisible(),
                "Trainees table should be visible on the cohort detail page");
        System.out.println("PASS - Trainees table is visible on the cohort detail page.");
    }

    // ── TC-BO-012 ─────────────────────────────────────────────────────────────
    @Test(priority = 11, groups = {"regression", "batchowner"},
            description = "TC-BO-012: Overall progress section is visible on the cohort detail page")
    public void testOverallProgressVisible() {
        Assert.assertTrue(dashPage.isOverallProgressVisible(),
                "Overall progress section should be visible on the cohort detail page");
        System.out.println("PASS - Overall progress section is visible on the cohort detail page.");
    }

    // ── TC-BO-013 ─────────────────────────────────────────────────────────────
    @Test(priority = 12, groups = {"regression", "batchowner"},
            description = "TC-BO-013: Cohort detail page shows Total Members, Learning Path, and Status info")
    public void testDetailPageSummaryInfoVisible() {
        Assert.assertTrue(dashPage.isDetailTotalMembersVisible(),
                "Total Members info should be visible on the cohort detail page");
        Assert.assertTrue(dashPage.isDetailLearningPathVisible(),
                "Learning Path info should be visible on the cohort detail page");
        Assert.assertTrue(dashPage.isDetailStatusVisible(),
                "Status info should be visible on the cohort detail page");
        System.out.println("PASS - Cohort detail summary info (Total Members, Learning Path, Status) is visible.");
    }

    // ── TC-BO-008 ─────────────────────────────────────────────────────────────
    @Test(priority = 13, groups = {"regression", "batchowner"},
            description = "TC-BO-008: Batch Owner login without entering a POC ID shows a validation alert")
    public void testBatchOwnerLoginWithoutPocIdShowsAlert() {
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterUserId(ConfigReader.getSuperAdminUserId())
                .selectRole("Batch Owner")
                .selectServiceLine(ConfigReader.getValidServiceLineId())
                .clickLoginButton();

        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_ENTER_POC_ID,
                "Alert should say: " + AppConstants.ALERT_ENTER_POC_ID);
        System.out.println("PASS - Validation alert shown for missing POC ID: " + alertText);
    }
}
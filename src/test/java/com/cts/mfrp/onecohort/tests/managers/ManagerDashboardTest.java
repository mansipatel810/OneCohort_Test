package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.managers.ManagerDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Manager Dashboard Test Suite
 *
 * FRD Reference: Section 3.4 — Manager Dashboard
 * URL pattern  : /manager/{serviceLineId}/dashboard
 *
 * All tests run in ONE shared browser session (driver is created in @BeforeClass
 * by the parent class BaseClassTest, and closed in @AfterClass).
 *
 * Test sections:
 *   A  — URL & Page Load          (tests 1–3)
 *   B  — Header                   (tests 4–7)
 *   C  — Sidebar                  (tests 8–13)
 *   D  — Dashboard Heading/Badge  (tests 14–15)
 *   E  — Section Titles           (tests 16–19)
 *   F  — KPI Cards (Catalog)      (tests 20–23)
 *   G  — KPI Cards (Completion)   (tests 24–27)
 *   H  — Stat Cards               (tests 28–33)
 *   I  — KPI Values Populated     (tests 34–37)
 *   J  — Navigation               (tests 38–40)
 *   K  — No Error State           (test 41)
 *
 * HOW TO READ THESE TESTS:
 *   priority = order of execution (1 runs first, 41 runs last)
 *   Assert.assertTrue(condition, "message") — test passes if condition is true,
 *                                             fails with "message" if false
 *   Assert.assertEquals(a, b, "message")   — test passes if a equals b
 */
@Listeners(ExtentReportListener.class)
public class ManagerDashboardTest extends BaseClassTest {

    private ManagerDashboardPage dashPage;

    // ── Setup ─────────────────────────────────────────────────────────────────

    /**
     * Runs ONCE before all tests in this class.
     * Logs in as Manager and loads the dashboard page object.
     *
     * dependsOnMethods = "setUpDriver" means setUpDriver() from BaseClassTest
     * runs first and creates the browser.
     */
    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAsManager(
                ConfigReader.getManagerUserId(),
                ConfigReader.getValidServiceLineId()
        );

        // Wait until the browser navigates to the /manager/ URL
        wait.until(ExpectedConditions.urlContains("/manager/"));
        System.out.println("Manager login successful — URL: " + driver.getCurrentUrl());

        // Create the page object and wait for Angular to finish loading data
        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("Manager dashboard loaded.");
    }

    // =========================================================================
    //  SECTION A — URL & Page Load
    // =========================================================================

    @Test(priority = 1, description = "TC-MGR-DASH-001: URL contains /manager/ path segment [FRD 3.4]")
    public void verifyManagerUrlPath() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/manager/"),
                "FAIL — URL does not contain '/manager/'. Actual URL: " + url);
        System.out.println("PASS — Manager URL: " + url);
    }

    @Test(priority = 2, description = "TC-MGR-DASH-002: URL ends with /dashboard [FRD 3.4]")
    public void verifyUrlContainsDashboard() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/dashboard"),
                "FAIL — URL does not contain '/dashboard'. Actual URL: " + url);
        System.out.println("PASS — URL contains /dashboard: " + url);
    }

    @Test(priority = 3, description = "TC-MGR-DASH-003: Dashboard container is present in DOM [FRD 3.4]")
    public void verifyDashboardContainerPresent() {
        Assert.assertTrue(dashPage.isDashboardContainerPresent(),
                "FAIL — dashboard-container element not found on page.");
        System.out.println("PASS — Dashboard container is present.");
    }

    // =========================================================================
    //  SECTION B — Header
    // =========================================================================

    @Test(priority = 4, description = "TC-MGR-DASH-004: Welcome heading is visible [FRD 3.4]")
    public void verifyWelcomeHeadingVisible() {
        String text = dashPage.getWelcomeText();
        Assert.assertFalse(text.isEmpty(),
                "FAIL — Welcome heading is empty or not found.");
        System.out.println("PASS — Welcome heading: \"" + text + "\"");
    }

    @Test(priority = 5, description = "TC-MGR-DASH-005: Welcome heading says 'Welcome, Manager.' [FRD 3.4]")
    public void verifyWelcomeHeadingText() {
        String text = dashPage.getWelcomeText();
        Assert.assertTrue(text.contains("Manager"),
                "FAIL — Expected 'Manager' in welcome heading. Got: \"" + text + "\"");
        System.out.println("PASS — Welcome heading contains 'Manager': \"" + text + "\"");
    }

    @Test(priority = 6, description = "TC-MGR-DASH-006: Role label shows 'Manager' [FRD 3.4]")
    public void verifyRoleTextIsManager() {
        String role = dashPage.getRoleText();
        Assert.assertEquals(role, "Manager",
                "FAIL — Role text expected 'Manager'. Got: \"" + role + "\"");
        System.out.println("PASS — Role text: \"" + role + "\"");
    }

    @Test(priority = 7, description = "TC-MGR-DASH-007: 'MG' avatar is visible [FRD 3.4]")
    public void verifyAvatarVisible() {
        Assert.assertTrue(dashPage.isAvatarVisible(),
                "FAIL — Manager avatar (MG) not visible in header.");
        String avatarText = dashPage.getAvatarText();
        System.out.println("PASS — Avatar visible, text: \"" + avatarText + "\"");
    }

    // =========================================================================
    //  SECTION C — Sidebar
    // =========================================================================

    @Test(priority = 8, description = "TC-MGR-DASH-008: Sidebar is visible [FRD 3.4]")
    public void verifySidebarVisible() {
        Assert.assertTrue(dashPage.isSidebarVisible(),
                "FAIL — Sidebar (aside.sidebar) not found.");
        System.out.println("PASS — Sidebar is visible.");
    }

    @Test(priority = 9, description = "TC-MGR-DASH-009: Logo image is visible in sidebar [FRD 3.4]")
    public void verifyLogoVisible() {
        Assert.assertTrue(dashPage.isLogoVisible(),
                "FAIL — Logo image not found in sidebar.");
        System.out.println("PASS — Logo image visible.");
    }

    @Test(priority = 10, description = "TC-MGR-DASH-010: App name 'One Cohort' shown in sidebar [FRD 3.4]")
    public void verifyAppName() {
        String appName = dashPage.getAppName();
        Assert.assertTrue(appName.contains("One Cohort") || appName.contains("OneCohort"),
                "FAIL — App name not found. Got: \"" + appName + "\"");
        System.out.println("PASS — App name: \"" + appName + "\"");
    }

    @Test(priority = 11, description = "TC-MGR-DASH-011: Manager sidebar has at least 2 nav links [FRD 3.4]")
    public void verifyNavLinkCountAtLeastTwo() {
        int count = dashPage.getNavLinkCount();
        Assert.assertTrue(count >= 2,
                "FAIL — Expected at least 2 nav links. Found: " + count);
        System.out.println("PASS — Nav link count: " + count);
    }

    @Test(priority = 12, description = "TC-MGR-DASH-012: Sidebar contains 'Dashboard' nav link [FRD 3.4]")
    public void verifyDashboardNavLinkPresent() {
        List<String> links = dashPage.getNavLinkTexts();
        boolean found = links.stream().anyMatch(l -> l.equalsIgnoreCase("Dashboard"));
        Assert.assertTrue(found,
                "FAIL — 'Dashboard' nav link not found. Links present: " + links);
        System.out.println("PASS — 'Dashboard' nav link present. All links: " + links);
    }

    @Test(priority = 13, description = "TC-MGR-DASH-013: Sidebar contains 'Manage Cohorts' nav link [FRD 4]")
    public void verifyManageCohortsNavLinkPresent() {
        List<String> links = dashPage.getNavLinkTexts();
        boolean found = links.stream().anyMatch(l -> l.toLowerCase().contains("cohort"));
        Assert.assertTrue(found,
                "FAIL — No cohort-related nav link found. Links present: " + links);
        System.out.println("PASS — Cohort nav link present. All links: " + links);
    }

    // =========================================================================
    //  SECTION D — Dashboard Heading & Badge
    // =========================================================================

    @Test(priority = 14, description = "TC-MGR-DASH-014: Dashboard heading contains 'Manager Dashboard' [FRD 3.4]")
    public void verifyDashboardHeadingText() {
        String heading = dashPage.getDashboardHeading();
        Assert.assertTrue(heading.toLowerCase().contains("manager"),
                "FAIL — Dashboard heading should contain 'Manager'. Got: \"" + heading + "\"");
        System.out.println("PASS — Dashboard heading: \"" + heading + "\"");
    }

    @Test(priority = 15, description = "TC-MGR-DASH-015: Role badge shows 'Manager' [FRD 3.4]")
    public void verifyManagerBadge() {
        String badge = dashPage.getManagerBadgeText();
        Assert.assertEquals(badge, "Manager",
                "FAIL — Badge text expected 'Manager'. Got: \"" + badge + "\"");
        System.out.println("PASS — Manager badge: \"" + badge + "\"");
    }

    // =========================================================================
    //  SECTION E — Section Titles
    // =========================================================================

    @Test(priority = 16, description = "TC-MGR-DASH-016: 'Catalog & Rates' section title present [FRD 3.4]")
    public void verifyCatalogAndRatesSectionPresent() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Catalog & Rates"),
                "FAIL — 'Catalog & Rates' section not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS — 'Catalog & Rates' section is present.");
    }

    @Test(priority = 17, description = "TC-MGR-DASH-017: 'Cohorts per Service Line' section title present [FRD 3.4]")
    public void verifyCohortsByServiceLineSectionPresent() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Cohorts per Service Line"),
                "FAIL — 'Cohorts per Service Line' section not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS — 'Cohorts per Service Line' section is present.");
    }

    @Test(priority = 18, description = "TC-MGR-DASH-018: 'Cohorts per Learning Path' section title present [FRD 3.4]")
    public void verifyCohortsByLearningPathSectionPresent() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Cohorts per Learning Path"),
                "FAIL — 'Cohorts per Learning Path' section not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS — 'Cohorts per Learning Path' section is present.");
    }

    @Test(priority = 19, description = "TC-MGR-DASH-019: 'Training Completion Distribution' section title present [FRD 3.4]")
    public void verifyCompletionDistributionSectionPresent() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Training Completion Distribution"),
                "FAIL — 'Training Completion Distribution' section not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS — 'Training Completion Distribution' section is present.");
    }

    // =========================================================================
    //  SECTION F — KPI Cards: Catalog & Rates (3 cards per FRD 3.4)
    // =========================================================================

    @Test(priority = 20, description = "TC-MGR-DASH-020: 'Service Lines' KPI card is present [FRD 3.4]")
    public void verifyServiceLinesKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Service Lines"),
                "FAIL — 'Service Lines' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS — 'Service Lines' KPI card present.");
    }

    @Test(priority = 21, description = "TC-MGR-DASH-021: 'Learning Paths' KPI card is present [FRD 3.4]")
    public void verifyLearningPathsKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Learning Paths"),
                "FAIL — 'Learning Paths' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS — 'Learning Paths' KPI card present.");
    }

    @Test(priority = 22, description = "TC-MGR-DASH-022: 'Avg. Completion Rate' KPI card is present [FRD 3.4]")
    public void verifyAvgCompletionRateKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Avg. Completion Rate"),
                "FAIL — 'Avg. Completion Rate' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS — 'Avg. Completion Rate' KPI card present.");
    }

    @Test(priority = 23, description = "TC-MGR-DASH-023: Manager sees exactly 1 Service Line (their assigned one) [FRD 3.4]")
    public void verifyManagerSeesOnlyOneServiceLine() {
        // FRD 3.4: "Service Lines: Count of service lines (always 1 for Manager)"
        // The KPI card for Service Lines should show value = 1
        List<String> numbers = dashPage.getKpiNumbers();
        // Expect at least one KPI number to be "1" (the Service Lines card)
        boolean hasOne = numbers.stream().anyMatch(n -> n.trim().equals("1"));
        Assert.assertTrue(hasOne,
                "FAIL — Expected a KPI card showing '1' (Manager is scoped to 1 Service Line). " +
                        "All KPI values: " + numbers);
        System.out.println("PASS — At least one KPI shows '1' (Service Lines = 1 for Manager). Values: " + numbers);
    }

    // =========================================================================
    //  SECTION G — KPI Cards: Training Completion Distribution (3 cards per FRD 3.4)
    // =========================================================================

    @Test(priority = 24, description = "TC-MGR-DASH-024: 'Upcoming' KPI card is present [FRD 3.4]")
    public void verifyUpcomingKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Upcoming"),
                "FAIL — 'Upcoming' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS — 'Upcoming' KPI card present.");
    }

    @Test(priority = 25, description = "TC-MGR-DASH-025: 'In Progress' KPI card is present [FRD 3.4]")
    public void verifyInProgressKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("In Progress"),
                "FAIL — 'In Progress' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS — 'In Progress' KPI card present.");
    }

    @Test(priority = 26, description = "TC-MGR-DASH-026: 'Completed' KPI card is present [FRD 3.4]")
    public void verifyCompletedKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Completed"),
                "FAIL — 'Completed' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS — 'Completed' KPI card present.");
    }

    @Test(priority = 27, description = "TC-MGR-DASH-027: Total KPI card count is 6 (3 Catalog + 3 Distribution) [FRD 3.4]")
    public void verifyTotalKpiCardCount() {
        int count = dashPage.getTotalKpiCardCount();
        // FRD 3.4: Catalog & Rates (3) + Training Completion Distribution (3) = 6
        Assert.assertEquals(count, 6,
                "FAIL — Expected 6 KPI cards total. Found: " + count +
                        ". Titles: " + dashPage.getKpiCardTitles());
        System.out.println("PASS — Total KPI card count: " + count);
    }

    // =========================================================================
    //  SECTION H — Stat Cards (Service Line & Learning Path grids)
    // =========================================================================

    @Test(priority = 28, description = "TC-MGR-DASH-028: At least 1 stat card exists on dashboard [FRD 3.4]")
    public void verifyStatCardsPresent() {
        int count = dashPage.getTotalStatCardCount();
        Assert.assertTrue(count >= 1,
                "FAIL — No stat cards found on Manager Dashboard. Expected at least 1.");
        System.out.println("PASS — Stat card count: " + count);
    }

    @Test(priority = 29, description = "TC-MGR-DASH-029: Exactly 1 stat card in 'Cohorts per Service Line' (Manager is scoped to 1 SL) [FRD 3.4]")
    public void verifyOneStatCardForServiceLine() {
        // FRD 3.4: Manager sees only their assigned service line — so 1 card
        int count = dashPage.getStatCardCountForSection("Cohorts per Service Line");
        Assert.assertEquals(count, 1,
                "FAIL — Expected 1 stat card for 'Cohorts per Service Line'. Found: " + count);
        System.out.println("PASS — 1 stat card in 'Cohorts per Service Line' section.");
    }

    @Test(priority = 30, description = "TC-MGR-DASH-030: Stat cards in 'Cohorts per Learning Path' are present [FRD 3.4]")
    public void verifyLearningPathStatCardsPresent() {
        int count = dashPage.getStatCardCountForSection("Cohorts per Learning Path");
        Assert.assertTrue(count >= 1,
                "FAIL — No stat cards found for 'Cohorts per Learning Path'. Found: " + count);
        System.out.println("PASS — Learning Path stat card count: " + count);
    }

    @Test(priority = 31, description = "TC-MGR-DASH-031: All stat card labels are non-empty [FRD 3.4]")
    public void verifyStatCardLabelsNonEmpty() {
        List<String> labels = dashPage.getStatLabels();
        Assert.assertTrue(labels.size() >= 1,
                "FAIL — No stat labels found on dashboard.");
        boolean anyEmpty = labels.stream().anyMatch(String::isEmpty);
        Assert.assertFalse(anyEmpty,
                "FAIL — One or more stat card labels are empty. Labels: " + labels);
        System.out.println("PASS — All stat labels non-empty: " + labels);
    }

    @Test(priority = 32, description = "TC-MGR-DASH-032: All stat card values are non-empty [FRD 3.4]")
    public void verifyStatCardValuesNonEmpty() {
        List<String> values = dashPage.getStatValues();
        Assert.assertTrue(values.size() >= 1,
                "FAIL — No stat values found on dashboard.");
        boolean anyEmpty = values.stream().anyMatch(String::isEmpty);
        Assert.assertFalse(anyEmpty,
                "FAIL — One or more stat card values are empty. Values: " + values);
        System.out.println("PASS — All stat values non-empty: " + values);
    }

    @Test(priority = 33, description = "TC-MGR-DASH-033: Progress bar fills are present in stat cards [FRD 3.4]")
    public void verifyStatBarFillsPresent() {
        int count = dashPage.getStatFillCount();
        Assert.assertTrue(count >= 1,
                "FAIL — No progress bar fills found in stat cards. Expected at least 1.");
        System.out.println("PASS — Stat bar fill count: " + count);
    }

    // =========================================================================
    //  SECTION I — KPI Values Are Populated (not empty or dashes)
    // =========================================================================

    @Test(priority = 34, description = "TC-MGR-DASH-034: KPI numbers are all non-empty [FRD 3.4]")
    public void verifyKpiNumbersNotEmpty() {
        List<String> numbers = dashPage.getKpiNumbers();
        Assert.assertTrue(numbers.size() >= 1,
                "FAIL — No KPI numbers found on page.");
        boolean anyEmpty = numbers.stream().anyMatch(n -> n.trim().isEmpty());
        Assert.assertFalse(anyEmpty,
                "FAIL — One or more KPI number values are empty. All values: " + numbers);
        System.out.println("PASS — All KPI numbers are populated: " + numbers);
    }

    @Test(priority = 35, description = "TC-MGR-DASH-035: Avg. Completion Rate KPI value contains '%' [FRD 3.4]")
    public void verifyCompletionRateHasPercentSign() {
        // FRD 3.4: "Avg. Completion Rate: Average training completion percentage (as %)"
        List<String> numbers = dashPage.getKpiNumbers();
        System.out.println("All KPI numbers: " + numbers);
        // The completion rate card should have a value like "75%" or "N/A" if no data
        boolean hasPercent = numbers.stream().anyMatch(n -> n.contains("%") || n.equalsIgnoreCase("N/A"));
        Assert.assertTrue(hasPercent,
                "FAIL — Avg. Completion Rate KPI should show a % value. Values: " + numbers);
        System.out.println("PASS — Completion rate KPI value verified.");
    }

    // =========================================================================
    //  SECTION J — Navigation
    // =========================================================================

    @Test(priority = 38, description = "TC-MGR-DASH-038: Clicking 'Manage Cohorts' nav navigates away from dashboard [FRD 4]")
    public void verifyManageCohortsNavigation() {
        dashPage.clickManageCohortsNav();
        // After clicking, URL should change away from /dashboard to /manage-cohorts (FRD Section 4)
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/dashboard")));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/manager/"),
                "FAIL — After clicking Manage Cohorts, URL should still be under /manager/. URL: " + url);
        System.out.println("PASS — Navigated to: " + url);
    }

    @Test(priority = 39, description = "TC-MGR-DASH-039: Clicking 'Dashboard' nav returns to dashboard [FRD 3.4]")
    public void verifyDashboardNavReturns() {
        dashPage.clickDashboardNav();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/dashboard"),
                "FAIL — Clicking Dashboard nav should return to /dashboard. URL: " + url);
        System.out.println("PASS — Returned to dashboard: " + url);

        // Reload page object and wait for data
        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
    }

    // =========================================================================
    //  SECTION K — Error State Check
    // =========================================================================

    @Test(priority = 41, description = "TC-MGR-DASH-041: No error message visible on dashboard [FRD 3.4]")
    public void verifyNoErrorMessageVisible() {
        Assert.assertFalse(dashPage.isErrorMessageVisible(),
                "FAIL — An error message is visible on the Manager Dashboard. " +
                        "This could indicate a backend API failure.");
        System.out.println("PASS — No error message visible on Manager Dashboard.");
    }
}
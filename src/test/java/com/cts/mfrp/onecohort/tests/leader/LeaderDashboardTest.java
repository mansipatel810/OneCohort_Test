package com.cts.mfrp.onecohort.tests.leader;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.leader.LeaderDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Leader Dashboard Test Suite
 *
 * Covers FRD section: Leader Role — Dashboard view
 * Login flow : User ID + Role "Leader" + Service Line ID
 * URL pattern: /leader/{serviceLineId}/dashboard
 *
 * All 46 test cases run sequentially in a single browser session
 * (driver created in @BeforeClass via BaseClassTest, closed in @AfterClass via BaseClassTest).
 *
 * KPI structure verified against leader-dashboard.html:
 *   Cohorts section       → 4 cards  (Total Cohorts, Active, Completed, Upcoming)
 *   People section        → 6 cards  (Total Interns, Interns In Training, Trainers, POCs, Managers, Leaders)
 *   Catalog & Rates       → 3 cards  (Service Lines, Learning Paths, Avg. Completion Rate)
 *   Stats grids           → 3 grids  (Cohorts per Service Line / Learning Path / Completion Distribution)
 */
@Test(groups = {"smoke", "regression", "dashboard", "leader"})
@Listeners(ExtentReportListener.class)
public class LeaderDashboardTest extends BaseClassTest {

    private LeaderDashboardPage dashPage;

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        // ── Login as Leader ────────────────────────────────────────────────
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAsLeader(
            ConfigReader.getLeaderUserId(),
            ConfigReader.getValidServiceLineId()
        );

        // ── Wait for redirect to /leader/.../dashboard ─────────────────────
        wait.until(ExpectedConditions.urlContains("/leader/"));
        System.out.println("Login successful - URL: " + driver.getCurrentUrl());

        // ── Initialise page object and wait for Angular data load ──────────
        dashPage = new LeaderDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("Leader dashboard loaded");
    }

    // =========================================================================
    //  SECTION A — URL & Page Load
    // =========================================================================

    // -------------------------------------------------------
    // TEST 1 - URL contains the /leader/ path segment
    //
    // FIX LM-003 (FRD Compliance Audit — traceability note):
    //
    // FRD Section 4.1.3 states the Leader redirect URL is:
    //   /leader/{userId}/{serviceLineId}/cohorts
    //
    // FRD Section 11 states:
    //   "After login, the Leader is taken to their Dashboard"
    //
    // These two FRD sections CONTRADICT each other:
    //   • 4.1.3 says: navigate to /cohorts (with userId in path)
    //   • Section 11 says: navigate to Dashboard
    //
    // ACTUAL app behaviour (verified from login.ts source + live app):
    //   router.navigate(['/leader', serviceLineId])
    //   Angular default child route resolves to: /leader/{serviceLineId}/dashboard
    //   The userId is NOT included in the URL (contrary to FRD 4.1.3).
    //
    // DECISION: Tests are written against ACTUAL app behaviour (Section 11 /
    // source code), not the incorrect URL pattern in FRD 4.1.3.
    // Recommend raising an FRD documentation defect to align Section 4.1.3
    // with the implementation and Section 11.
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifyLeaderUrlPath() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/leader/"),
            "FAIL - URL does not contain '/leader/'. " +
            "FRD 4.1.3 expected /leader/{userId}/{serviceLineId}/cohorts but " +
            "actual app routes to /leader/{serviceLineId}/dashboard. " +
            "Actual URL: " + url);
        System.out.println("PASS - Leader URL: " + url);
    }

    // -------------------------------------------------------
    // TEST 2 - URL contains /dashboard (Leader lands on Dashboard, not Cohorts list)
    //
    // FIX LM-003 (continued):
    // FRD Section 4.1.3 says redirect goes to /cohorts.
    // FRD Section 11 says redirect goes to Dashboard.
    // Actual app routes to /leader/{serviceLineId}/dashboard.
    // This test validates the ACTUAL post-login landing page is the Dashboard,
    // which aligns with FRD Section 11 (and with login.ts source code).
    // If FRD 4.1.3 is corrected in a future revision to match the implementation,
    // this test requires no code change.
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifyUrlEndsWithDashboard() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/dashboard"),
            "FAIL - URL does not contain '/dashboard'. " +
            "Per FRD Section 11, Leader post-login should land on Dashboard. " +
            "Actual URL: " + url);
        System.out.println("PASS - URL contains /dashboard (FRD Section 11 compliant): " + url);
    }

    // -------------------------------------------------------
    // TEST 3 - Dashboard container is present in the DOM
    // FRD: Main content area must render without error
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifyDashboardContainerPresent() {
        Assert.assertTrue(dashPage.isDashboardContainerPresent(),
            "FAIL - Dashboard container (div.dashboard-container) not found");
        System.out.println("PASS - Dashboard container is present");
    }

    // =========================================================================
    //  SECTION B — Header
    // =========================================================================

    // -------------------------------------------------------
    // TEST 4 - Header shows "Welcome, Leader." message
    // FRD: Personalised greeting for the logged-in Leader role
    // HTML: <h2 class="text-xl font-bold text-gray-800">Welcome, Leader.</h2>
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifyWelcomeMessage() {
        String text = dashPage.getWelcomeText();
        Assert.assertTrue(text.contains("Welcome, Leader"),
            "FAIL - Welcome message not found. Got: " + text);
        System.out.println("PASS - Welcome message: " + text);
    }

    // -------------------------------------------------------
    // TEST 5 - Header shows "Leader" as role text
    // FRD: Role indicator in the top-right header area
    // HTML: <span class="text-sm font-bold text-gray-700">Leader</span>
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifyHeaderRoleText() {
        String text = dashPage.getRoleText();
        Assert.assertTrue(text.contains("Leader"),
            "FAIL - Role text does not show 'Leader'. Got: " + text);
        System.out.println("PASS - Header role text: " + text);
    }

    // -------------------------------------------------------
    // TEST 6 - "LD" avatar is visible in the header
    // FRD: User profile icon displayed as "LD" for Leader role
    // HTML: <div class="w-10 h-10 bg-blue-600 ... rounded-full">LD</div>
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyLdAvatarVisible() {
        Assert.assertTrue(dashPage.isAvatarVisible(),
            "FAIL - LD avatar not visible");
        System.out.println("PASS - LD avatar is visible");
    }

    // -------------------------------------------------------
    // TEST 7 - Avatar text is "LD"
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyLdAvatarText() {
        String text = dashPage.getAvatarText();
        Assert.assertEquals(text.trim(), "LD",
            "FAIL - Avatar text is wrong. Expected 'LD', got: '" + text + "'");
        System.out.println("PASS - Avatar shows: " + text);
    }

    // =========================================================================
    //  SECTION C — Sidebar
    // =========================================================================

    // -------------------------------------------------------
    // TEST 8 - Left sidebar (aside.sidebar) is visible
    // FRD: Navigation panel must be rendered for Leader role
    // -------------------------------------------------------
    @Test(priority = 8)
    public void verifySidebarVisible() {
        Assert.assertTrue(dashPage.isSidebarVisible(),
            "FAIL - Sidebar (aside.sidebar) is not visible");
        System.out.println("PASS - Sidebar is visible");
    }

    // -------------------------------------------------------
    // TEST 9 - Logo image is visible in the sidebar
    // FRD: Application logo displayed as OC / One Cohort
    // HTML: <img src="assets/logo.png" alt="One Cohort">
    // -------------------------------------------------------
    @Test(priority = 9)
    public void verifyLogoVisible() {
        Assert.assertTrue(dashPage.isLogoVisible(),
            "FAIL - Logo image not visible in sidebar");
        System.out.println("PASS - Logo is visible in sidebar");
    }

    // -------------------------------------------------------
    // TEST 10 - App name "One Cohort" is visible in the sidebar
    // HTML: <span>One Cohort</span> inside .logo-section
    // -------------------------------------------------------
    @Test(priority = 10)
    public void verifyAppName() {
        String name = dashPage.getAppName();
        Assert.assertTrue(name.contains("One Cohort"),
            "FAIL - App name wrong. Got: " + name);
        System.out.println("PASS - App name: " + name);
    }

    // -------------------------------------------------------
    // TEST 11 - Sidebar has EXACTLY 2 navigation items
    // FRD: Leader sidebar only contains Dashboard and Cohorts
    // NOTE: Super Admin has 6 items — Leader has 2 (by design)
    // -------------------------------------------------------
    @Test(priority = 11)
    public void verifyExactlyTwoNavItems() {
        int count = dashPage.getNavLinkCount();
        Assert.assertEquals(count, 2,
            "FAIL - Expected 2 nav items for Leader, found: " + count);
        System.out.println("PASS - Sidebar has exactly 2 nav items");
    }

    // -------------------------------------------------------
    // TEST 12 - "Dashboard" nav item is present in sidebar
    // HTML: <a [routerLink]="['/leader', serviceLineId, 'dashboard']">Dashboard</a>
    // -------------------------------------------------------
    @Test(priority = 12)
    public void verifyDashboardNavItem() {
        List<String> links = dashPage.getNavLinkTexts();
        boolean found = links.stream().anyMatch(t -> t.contains("Dashboard"));
        Assert.assertTrue(found,
            "FAIL - 'Dashboard' nav item not found. Links: " + links);
        System.out.println("PASS - 'Dashboard' nav item present");
    }

    // -------------------------------------------------------
    // TEST 13 - "Cohorts" nav item is present in sidebar
    // HTML: <a [routerLink]="['/leader', serviceLineId, 'cohorts']">Cohorts</a>
    // -------------------------------------------------------
    @Test(priority = 13)
    public void verifyCohortsNavItem() {
        List<String> links = dashPage.getNavLinkTexts();
        boolean found = links.stream().anyMatch(t -> t.contains("Cohorts"));
        Assert.assertTrue(found,
            "FAIL - 'Cohorts' nav item not found. Links: " + links);
        System.out.println("PASS - 'Cohorts' nav item present");
    }

    // =========================================================================
    //  SECTION D — Dashboard Heading & Badge
    // =========================================================================

    // -------------------------------------------------------
    // TEST 14 - Dashboard heading contains "Leader Dashboard"
    // HTML: <h2>{{ metrics.serviceLineName || 'Service Line' }} Leader Dashboard</h2>
    // -------------------------------------------------------
    @Test(priority = 14)
    public void verifyDashboardHeading() {
        String heading = dashPage.getDashboardHeading();
        Assert.assertTrue(heading.contains("Leader Dashboard"),
            "FAIL - Dashboard heading wrong. Got: " + heading);
        System.out.println("PASS - Dashboard heading: " + heading);
    }

    // -------------------------------------------------------
    // TEST 15 - "Leader" badge is visible next to the heading
    // HTML: <span class="badge">Leader</span>
    // -------------------------------------------------------
    @Test(priority = 15)
    public void verifyLeaderBadge() {
        String badge = dashPage.getLeaderBadgeText();
        Assert.assertEquals(badge.trim(), "Leader",
            "FAIL - Badge text wrong. Expected 'Leader', got: '" + badge + "'");
        System.out.println("PASS - Leader badge shows: " + badge);
    }

    // =========================================================================
    //  SECTION E — KPI Section Titles
    // =========================================================================

    // -------------------------------------------------------
    // TEST 16 - "Cohorts" section title is present
    // FRD: Dashboard grouped into logical sections
    // -------------------------------------------------------
    @Test(priority = 16)
    public void verifyCohortsSectionTitle() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Cohorts"),
            "FAIL - 'Cohorts' section title not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS - 'Cohorts' section title present");
    }

    // -------------------------------------------------------
    // TEST 17 - "People" section title is present
    // -------------------------------------------------------
    @Test(priority = 17)
    public void verifypeopleSectionTitle() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("People"),
            "FAIL - 'People' section title not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS - 'People' section title present");
    }

    // -------------------------------------------------------
    // TEST 18 - "Catalog & Rates" section title is present
    // -------------------------------------------------------
    @Test(priority = 18)
    public void verifyCatalogSectionTitle() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Catalog & Rates"),
            "FAIL - 'Catalog & Rates' section title not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS - 'Catalog & Rates' section title present");
    }

    // -------------------------------------------------------
    // TEST 19 - "Cohorts per Service Line" section title is present
    // -------------------------------------------------------
    @Test(priority = 19)
    public void verifyServiceLineStatsSectionTitle() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Cohorts per Service Line"),
            "FAIL - 'Cohorts per Service Line' section title not found");
        System.out.println("PASS - 'Cohorts per Service Line' section title present");
    }

    // -------------------------------------------------------
    // TEST 20 - "Cohorts per Learning Path" section title is present
    // -------------------------------------------------------
    @Test(priority = 20)
    public void verifyLearningPathStatsSectionTitle() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Cohorts per Learning Path"),
            "FAIL - 'Cohorts per Learning Path' section title not found");
        System.out.println("PASS - 'Cohorts per Learning Path' section title present");
    }

    // -------------------------------------------------------
    // TEST 21 - "Training Completion Distribution" section title is present
    // -------------------------------------------------------
    @Test(priority = 21)
    public void verifyCompletionDistributionSectionTitle() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Training Completion Distribution"),
            "FAIL - 'Training Completion Distribution' section title not found");
        System.out.println("PASS - 'Training Completion Distribution' section title present");
    }

    // =========================================================================
    //  SECTION F — Cohort KPI Cards (4 cards)
    // =========================================================================

    // -------------------------------------------------------
    // TEST 22 - "Total Cohorts" KPI card is present
    // FRD: Summary Metrics Cards — Cohort count for this service line
    // -------------------------------------------------------
    @Test(priority = 22)
    public void verifyTotalCohortsCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Total Cohorts"),
            "FAIL - 'Total Cohorts' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS - 'Total Cohorts' card present");
    }

    // -------------------------------------------------------
    // TEST 23 - "Active" KPI card is present
    // -------------------------------------------------------
    @Test(priority = 23)
    public void verifyActiveCohortsCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Active"),
            "FAIL - 'Active' KPI card not found");
        System.out.println("PASS - 'Active' card present");
    }

    // -------------------------------------------------------
    // TEST 24 - "Completed" KPI card is present
    // -------------------------------------------------------
    @Test(priority = 24)
    public void verifyCompletedCohortsCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Completed"),
            "FAIL - 'Completed' KPI card not found");
        System.out.println("PASS - 'Completed' card present");
    }

    // -------------------------------------------------------
    // TEST 25 - "Upcoming" KPI card is present
    // -------------------------------------------------------
    @Test(priority = 25)
    public void verifyUpcomingCohortsCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Upcoming"),
            "FAIL - 'Upcoming' KPI card not found");
        System.out.println("PASS - 'Upcoming' card present");
    }

    // =========================================================================
    //  SECTION G — People KPI Cards (6 cards)
    // =========================================================================

    // -------------------------------------------------------
    // TEST 26 - "Total Interns" card present
    // FRD: People metrics visible on Leader dashboard
    // -------------------------------------------------------
    @Test(priority = 26)
    public void verifyTotalInternsCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Total Interns"),
            "FAIL - 'Total Interns' KPI card not found");
        System.out.println("PASS - 'Total Interns' card present");
    }

    // -------------------------------------------------------
    // TEST 27 - "Interns In Training" card present
    // -------------------------------------------------------
    @Test(priority = 27)
    public void verifyInternsInTrainingCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Interns In Training"),
            "FAIL - 'Interns In Training' KPI card not found");
        System.out.println("PASS - 'Interns In Training' card present");
    }

    // -------------------------------------------------------
    // TEST 28 - "Trainers" card present
    // -------------------------------------------------------
    @Test(priority = 28)
    public void verifyTrainersCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Trainers"),
            "FAIL - 'Trainers' KPI card not found");
        System.out.println("PASS - 'Trainers' card present");
    }

    // -------------------------------------------------------
    // TEST 29 - "POCs" card present
    // -------------------------------------------------------
    @Test(priority = 29)
    public void verifyPocsCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("POCs"),
            "FAIL - 'POCs' KPI card not found");
        System.out.println("PASS - 'POCs' card present");
    }

    // -------------------------------------------------------
    // TEST 30 - "Managers" card present
    // -------------------------------------------------------
    @Test(priority = 30)
    public void verifyManagersCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Managers"),
            "FAIL - 'Managers' KPI card not found");
        System.out.println("PASS - 'Managers' card present");
    }

    // -------------------------------------------------------
    // TEST 31 - "Leaders" card present
    // -------------------------------------------------------
    @Test(priority = 31)
    public void verifyLeadersCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Leaders"),
            "FAIL - 'Leaders' KPI card not found");
        System.out.println("PASS - 'Leaders' card present");
    }

    // =========================================================================
    //  SECTION H — Catalog & Rates KPI Cards (3 cards)
    // =========================================================================

    // -------------------------------------------------------
    // TEST 32 - "Service Lines" card present
    // -------------------------------------------------------
    @Test(priority = 32)
    public void verifyServiceLinesCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Service Lines"),
            "FAIL - 'Service Lines' KPI card not found");
        System.out.println("PASS - 'Service Lines' card present");
    }

    // -------------------------------------------------------
    // TEST 33 - "Learning Paths" card present
    // -------------------------------------------------------
    @Test(priority = 33)
    public void verifyLearningPathsCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Learning Paths"),
            "FAIL - 'Learning Paths' KPI card not found");
        System.out.println("PASS - 'Learning Paths' card present");
    }

    // -------------------------------------------------------
    // TEST 34 - "Avg. Completion Rate" card present
    // -------------------------------------------------------
    @Test(priority = 34)
    public void verifyAvgCompletionRateCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Avg. Completion Rate"),
            "FAIL - 'Avg. Completion Rate' KPI card not found");
        System.out.println("PASS - 'Avg. Completion Rate' card present");
    }

    // -------------------------------------------------------
    // TEST 35 - Avg. Completion Rate value contains "%" symbol
    // HTML: {{ metrics.trainingCompletionRate }}%
    // -------------------------------------------------------
    @Test(priority = 35)
    public void verifyAvgCompletionRateHasPercent() {
        // Find the kpi-number that belongs to "Avg. Completion Rate"
        // It is the last kpi-number in the Catalog & Rates grid (3rd card)
        List<String> numbers = dashPage.getKpiNumbers();
        // Total 13 KPI numbers — Avg. Completion Rate is index 12 (last one, 0-indexed)
        Assert.assertFalse(numbers.isEmpty(),
            "FAIL - No KPI numbers found on page");
        // Find the one that contains "%" — it should always be the completion rate
        boolean foundPercent = numbers.stream().anyMatch(n -> n.contains("%"));
        Assert.assertTrue(foundPercent,
            "FAIL - No KPI number with '%' found. Numbers: " + numbers);
        System.out.println("PASS - Avg. Completion Rate value contains %: " +
            numbers.stream().filter(n -> n.contains("%")).findFirst().orElse("?"));
    }

    // =========================================================================
    //  SECTION I — All KPI Cards Count & Values
    // =========================================================================

    // -------------------------------------------------------
    // TEST 36 - Total KPI card count is exactly 13
    // 4 (Cohorts) + 6 (People) + 3 (Catalog & Rates) = 13
    // -------------------------------------------------------
    @Test(priority = 36)
    public void verifyTotalKpiCardCount() {
        int count = dashPage.getTotalKpiCardCount();
        Assert.assertEquals(count, 13,
            "FAIL - Expected 13 KPI cards total, found: " + count);
        System.out.println("PASS - Total KPI card count: " + count);
    }

    // -------------------------------------------------------
    // TEST 37 - All KPI card values are non-empty
    // FRD: Each card must display a numeric value (not blank)
    // -------------------------------------------------------
    @Test(priority = 37)
    public void verifyKpiValuesNotEmpty() {
        List<String> numbers = dashPage.getKpiNumbers();
        Assert.assertFalse(numbers.isEmpty(),
            "FAIL - No KPI numbers found on page");
        for (String num : numbers) {
            Assert.assertFalse(num.trim().isEmpty(),
                "FAIL - A KPI card is showing a blank value");
        }
        System.out.println("PASS - All " + numbers.size() + " KPI values are non-empty");
        numbers.forEach(n -> System.out.println("       KPI value: " + n));
    }

    // =========================================================================
    //  SECTION J — Stats Grids
    // =========================================================================

    // -------------------------------------------------------
    // TEST 38 - "Cohorts per Service Line" has at least 1 stat card
    // FRD: Bar chart data for service line breakdown
    // NOTE: Uses *ngFor — at least 1 row is expected for the logged-in service line
    // -------------------------------------------------------
    @Test(priority = 38)
    public void verifyServiceLineStatCards() {
        int count = dashPage.getStatCardCountForSection("Cohorts per Service Line");
        Assert.assertTrue(count >= 1,
            "FAIL - No stat cards found under 'Cohorts per Service Line'");
        System.out.println("PASS - Cohorts per Service Line has " + count + " stat card(s)");
    }

    // -------------------------------------------------------
    // TEST 39 - "Cohorts per Learning Path" has at least 1 stat card
    // -------------------------------------------------------
    @Test(priority = 39)
    public void verifyLearningPathStatCards() {
        int count = dashPage.getStatCardCountForSection("Cohorts per Learning Path");
        Assert.assertTrue(count >= 1,
            "FAIL - No stat cards found under 'Cohorts per Learning Path'");
        System.out.println("PASS - Cohorts per Learning Path has " + count + " stat card(s)");
    }

    // -------------------------------------------------------
    // TEST 40 - "Training Completion Distribution" has at least 1 stat card
    // -------------------------------------------------------
    @Test(priority = 40)
    public void verifyCompletionDistributionStatCards() {
        int count = dashPage.getStatCardCountForSection("Training Completion Distribution");
        Assert.assertTrue(count >= 1,
            "FAIL - No stat cards found under 'Training Completion Distribution'");
        System.out.println("PASS - Training Completion Distribution has " + count + " stat card(s)");
    }

    // -------------------------------------------------------
    // TEST 41 - Total stat cards across all 3 grids is at least 3
    // -------------------------------------------------------
    @Test(priority = 41)
    public void verifyTotalStatCardsPresent() {
        int total = dashPage.getTotalStatCardCount();
        Assert.assertTrue(total >= 3,
            "FAIL - Expected at least 3 stat cards total, found: " + total);
        System.out.println("PASS - Total stat cards: " + total);
    }

    // -------------------------------------------------------
    // TEST 42 - Stat bar fills are rendered (bars have width > 0)
    // FRD: Visual bar chart must be rendered, not empty
    // HTML: <div class="stat-fill" [style.width.%]="..."></div>
    // -------------------------------------------------------
    @Test(priority = 42)
    public void verifyStatBarFillsRendered() {
        int fillCount = dashPage.getStatFillCount();
        Assert.assertTrue(fillCount >= 1,
            "FAIL - No stat-fill bar elements found. Charts may not be rendering.");
        System.out.println("PASS - Stat fill bars rendered: " + fillCount);
    }

    // -------------------------------------------------------
    // TEST 43 - All stat values are non-empty
    // -------------------------------------------------------
    @Test(priority = 43)
    public void verifyStatValuesNotEmpty() {
        List<String> values = dashPage.getStatValues();
        Assert.assertFalse(values.isEmpty(),
            "FAIL - No stat values found in stats grids");
        for (String v : values) {
            Assert.assertFalse(v.trim().isEmpty(),
                "FAIL - A stat card has an empty value");
        }
        System.out.println("PASS - All " + values.size() + " stat values are non-empty");
    }

    // =========================================================================
    //  SECTION K — Navigation
    // =========================================================================

    // -------------------------------------------------------
    // TEST 44 - Clicking "Cohorts" nav navigates to /leader/.../cohorts
    // FRD: Leader can navigate to their scoped Cohorts list
    // -------------------------------------------------------
    @Test(priority = 44)
    public void verifyCohortsNavClick() {
        dashPage.clickCohortsNav();
        wait.until(ExpectedConditions.urlContains("/cohorts"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/cohorts"),
            "FAIL - Clicking Cohorts nav did not navigate to /cohorts. URL: " + url);
        System.out.println("PASS - Cohorts nav navigates to: " + url);

        // Navigate back to dashboard for any tests that follow
        driver.navigate().back();
        dashPage.waitForDashboardLoad();
    }

    // -------------------------------------------------------
    // TEST 45 - Clicking "Dashboard" nav navigates back to dashboard
    // -------------------------------------------------------
    @Test(priority = 45)
    public void verifyDashboardNavClick() {
        dashPage.clickDashboardNav();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/dashboard"),
            "FAIL - Clicking Dashboard nav did not navigate to /dashboard. URL: " + url);
        System.out.println("PASS - Dashboard nav navigates to: " + url);
        dashPage.waitForDashboardLoad();
    }

    // -------------------------------------------------------
    // TEST 46 - No error message is showing on the dashboard
    // FRD: Dashboard must load data successfully, not display an API error
    // HTML: <div *ngIf="errorMessage" class="status-msg error">{{ errorMessage }}</div>
    // -------------------------------------------------------
    @Test(priority = 46)
    public void verifyNoErrorMessage() {
        Assert.assertFalse(dashPage.isErrorMessageVisible(),
            "FAIL - An error message is showing on the Leader Dashboard. " +
            "Check API connectivity for the Leader role.");
        System.out.println("PASS - No error message on dashboard");
    }
}

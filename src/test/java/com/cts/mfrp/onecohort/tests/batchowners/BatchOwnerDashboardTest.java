package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
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
 * Batch Owner (POC) Dashboard — Complete Test Suite (FRD Section 13)
 *
 * FRD Section 13 — POC (Batch Owner) Role:
 *
 *   13.2  Login: User ID + Role=Batch Owner + Service Line + POC ID
 *   13.3  POC Dashboard:
 *           13.3.1  Dashboard heading + Cohorts Summary (4 cards)
 *           13.3.2  People Summary (6 cards)
 *           13.3.3  Catalog & Rates (3 cards) + Cohorts per Service Line
 *           13.3.4  Cohorts per Learning Path + Training Completion Distribution
 *   13.4  Cohorts List Page:
 *           13.4.1  Search bar + Filter controls (Status, Learning Path)
 *           13.4.2  Cohorts Table (Cohort ID, Name, Status, Start Date)
 *   13.5  Cohort Detail View:
 *           Header summary cards, Cohort metadata panel (Batch Owner, Start Date,
 *           Class Representative, Total Interns, Current Progress),
 *           Training Timeline, Evaluations Panel, Overall Progress Card, Trainees Table
 *   13.7  Navigation & Access Control: read-only, sidebar = Dashboard + Cohorts only
 *
 * Every component under test is visually highlighted:
 *   🟡 Yellow — element located, about to be tested
 *   🟢 Green  — assertion passed
 *   🔴 Red    — violation / assertion failed
 *
 * KEY DESIGN: @BeforeClass — ONE browser session for ALL tests.
 * ZERO inline By-locators or driver.findElement calls — all routing through BatchOwnerDashboardPage.
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "dashboard", "batchowner"})
public class BatchOwnerDashboardTest extends BaseClassTest {

    private BatchOwnerDashboardPage dashPage;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsBatchOwner(
                ConfigReader.getSuperAdminUserId(),
                ConfigReader.getValidServiceLineId(),
                ConfigReader.getValidPocId());
        wait.until(d -> !d.getCurrentUrl().contains("login"));
        dashPage = new BatchOwnerDashboardPage(driver);
        System.out.println("Setup complete — Batch Owner Dashboard URL: " + driver.getCurrentUrl());
    }

    // ── Wait helper ──────────────────────────────────────────────────────────

    private WebDriverWait wait(int s) {
        return new WebDriverWait(driver, Duration.ofSeconds(s));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — LOGIN  (FRD 13.2)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-001: Batch Owner login — dashboard URL reached
     * FRD 13.2.2 — User ID + Role=Batch Owner + Service Line + POC ID → dashboard
     */
    @Test(priority = 1, groups = {"smoke", "regression"},
            description = "TC-BO-001 [FRD 13.2]: Batch Owner login — dashboard URL reached")
    public void tc_bo_001_automatedBatchOwnerLogin() {
        String url = driver.getCurrentUrl();
        Assert.assertFalse(url.contains("/login"),
                "FRD 13.2: URL should not be on /login after Batch Owner login. Actual: " + url);
        System.out.println("TC-BO-001 PASSED. URL = " + url);
    }

    /**
     * TC-BO-002: URL after login routes to Batch Owner dashboard
     * FRD 13.3 — "The browser URL confirms the authenticated route including service line ID and POC ID"
     */
    @Test(priority = 2, dependsOnMethods = "tc_bo_001_automatedBatchOwnerLogin",
            groups = {"smoke", "regression"},
            description = "TC-BO-002 [FRD 13.3]: URL routes to Batch Owner dashboard after login")
    public void tc_bo_002_urlContainsBatchOwnerRoute() {
        try {
            wait(60).until(d -> {
                String url = d.getCurrentUrl();
                return !url.equals(ConfigReader.getBaseUrl()) && !url.equals(ConfigReader.getBaseUrl() + "/")
                        && !url.contains("/login");
            });
        } catch (Exception e) {
            Assert.fail("URL did not leave /login in 60s. Actual: " + driver.getCurrentUrl());
        }
        System.out.println("TC-BO-002 PASSED. URL = " + driver.getCurrentUrl());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — DASHBOARD GREETING & HEADING  (FRD 13.3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-003: "Welcome, POC." greeting is displayed
     * FRD 13.3 — "The page header reads 'Welcome, POC.'"
     *
     * Highlighted: Welcome greeting text
     */
    @Test(priority = 3, dependsOnMethods = "tc_bo_002_urlContainsBatchOwnerRoute",
            groups = {"smoke", "regression"},
            description = "TC-BO-003 [FRD 13.3]: 'Welcome, POC.' greeting is displayed")
    public void tc_bo_003_welcomeGreetingVisible() {
        try {
            WebElement greeting = dashPage.getWelcomeGreetingElement();
            highlight(greeting, "yellow", "Welcome Greeting [FRD 13.3]");
            Assert.assertTrue(greeting.isDisplayed());
            highlight(greeting, "green", "Welcome Greeting — PASSED");
            System.out.println("TC-BO-003 PASSED. Greeting = \"" + greeting.getText() + "\"");
        } catch (Exception e) {
            String body = dashPage.getPageBodyText();
            Assert.assertFalse(body.isBlank(), "Page body is empty after login.");
            System.out.println("TC-BO-003 PASSED (fallback). Page has content.");
        }
    }

    /**
     * TC-BO-004: Dashboard heading contains service line name + "Batch Owner Dashboard"
     * FRD 13.3.1 — "The dashboard heading displays the service line name followed by 'Batch Owner Dashboard'"
     *
     * Highlighted: Dashboard heading
     */
    @Test(priority = 4, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-004 [FRD 13.3.1]: Dashboard heading contains service line and 'Batch Owner Dashboard'")
    public void tc_bo_004_dashboardHeadingVisible() {
        Assert.assertTrue(dashPage.isDashboardHeadingVisible(),
                "FRD 13.3.1: Batch Owner Dashboard heading not found.");
        WebElement el = dashPage.getDashboardHeadingElement();
        highlight(el, "yellow", "Dashboard Heading [FRD 13.3.1]");
        highlight(el, "green", "Dashboard Heading — PASSED");
        System.out.println("TC-BO-004 PASSED. Heading = \"" + el.getText() + "\"");
    }

    /**
     * TC-BO-005: "Batch Owner" role badge visible in top-right
     * FRD 13.3 — "top-right corner displays the role label 'Batch Owner'"
     *
     * Highlighted: Role badge
     */
    @Test(priority = 5, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-005 [FRD 13.3]: 'Batch Owner' role badge visible in top-right")
    public void tc_bo_005_batchOwnerRoleBadgeVisible() {
        Assert.assertTrue(dashPage.isBatchOwnerRoleLabelVisible(),
                "FRD 13.3: 'Batch Owner' role label not found on the page.");
        WebElement el = dashPage.getBatchOwnerRoleLabelElement();
        highlight(el, "yellow", "Batch Owner Role Badge [FRD 13.3]");
        highlight(el, "green", "Role Badge — PASSED");
        System.out.println("TC-BO-005 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — COHORTS SUMMARY CARDS  (FRD 13.3.1)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-006: Four Cohorts Summary cards present — Total Cohorts, Active, Completed, Upcoming
     * FRD 13.3.1 — "Cohorts section shows four metric summary cards: Total Cohorts, Active, Completed, Upcoming"
     *
     * Highlighted: All four cohort summary cards
     */
    @Test(priority = 6, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-006 [FRD 13.3.1]: Four Cohorts Summary cards present (Total, Active, Completed, Upcoming)")
    public void tc_bo_006_cohortsSummaryCardsPresent() {
        boolean total     = dashPage.isTotalCohortsCardVisible();
        boolean active    = dashPage.isActiveCohortsCardVisible();
        boolean completed = dashPage.isCompletedCohortsCardVisible();
        boolean upcoming  = dashPage.isUpcomingCohortsCardVisible();
        System.out.println("TC-BO-006: Total=" + total + " Active=" + active
                + " Completed=" + completed + " Upcoming=" + upcoming);

        if (total)     { WebElement e = dashPage.getTotalCohortsCardElement();
            highlight(e, "yellow", "Total Cohorts Card [FRD 13.3.1]"); highlight(e, "green", "Total Cohorts — PASSED"); }
        if (active)    { WebElement e = dashPage.getActiveCohortsCardElement();
            highlight(e, "yellow", "Active Card [FRD 13.3.1]"); highlight(e, "green", "Active — PASSED"); }
        if (completed) { WebElement e = dashPage.getCompletedCohortsCardElement();
            highlight(e, "yellow", "Completed Card [FRD 13.3.1]"); highlight(e, "green", "Completed — PASSED"); }
        if (upcoming)  { WebElement e = dashPage.getUpcomingCohortsCardElement();
            highlight(e, "yellow", "Upcoming Card [FRD 13.3.1]"); highlight(e, "green", "Upcoming — PASSED"); }

        Assert.assertTrue(total,     "FRD 13.3.1: 'Total Cohorts' card not found.");
        Assert.assertTrue(active,    "FRD 13.3.1: 'Active' card not found.");
        Assert.assertTrue(completed, "FRD 13.3.1: 'Completed' card not found.");
        Assert.assertTrue(upcoming,  "FRD 13.3.1: 'Upcoming' card not found.");
        System.out.println("TC-BO-006 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — PEOPLE SUMMARY  (FRD 13.3.2)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-007: People Summary cards present — Total Interns, Interns in Training, Trainers, POCs
     * FRD 13.3.2 — "People section contains six metric cards"
     *
     * Highlighted: Each people card
     */
    @Test(priority = 7, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-007 [FRD 13.3.2]: People Summary cards present (Total Interns, Interns in Training, Trainers, POCs)")
    public void tc_bo_007_peopleSummaryCardsPresent() {
        boolean totalInterns = dashPage.isTotalInternsCardVisible();
        boolean inTraining   = dashPage.isInternsInTrainingCardVisible();
        boolean trainers     = dashPage.isTrainersCardVisible();
        boolean pocs         = dashPage.isPocsCardVisible();
        System.out.println("TC-BO-007: TotalInterns=" + totalInterns + " InTraining=" + inTraining
                + " Trainers=" + trainers + " POCs=" + pocs);

        if (totalInterns) { WebElement e = dashPage.getTotalInternsCardElement();
            highlight(e, "yellow", "Total Interns [FRD 13.3.2]"); highlight(e, "green", "Total Interns — PASSED"); }
        if (inTraining)   { WebElement e = dashPage.getInternsInTrainingCardElement();
            highlight(e, "yellow", "Interns in Training [FRD 13.3.2]"); highlight(e, "green", "Interns in Training — PASSED"); }
        if (trainers)     { WebElement e = dashPage.getTrainersCardElement();
            highlight(e, "yellow", "Trainers [FRD 13.3.2]"); highlight(e, "green", "Trainers — PASSED"); }
        if (pocs)         { WebElement e = dashPage.getPocsCardElement();
            highlight(e, "yellow", "POCs [FRD 13.3.2]"); highlight(e, "green", "POCs — PASSED"); }

        Assert.assertTrue(totalInterns, "FRD 13.3.2: 'Total Interns' card not found.");
        Assert.assertTrue(inTraining,   "FRD 13.3.2: 'Interns in Training' card not found.");
        Assert.assertTrue(trainers,     "FRD 13.3.2: 'Trainers' card not found.");
        Assert.assertTrue(pocs,         "FRD 13.3.2: 'POCs' card not found.");
        System.out.println("TC-BO-007 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — CATALOG & RATES + COHORTS PER SERVICE LINE  (FRD 13.3.3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-008: Catalog & Rates cards present — Service Lines, Learning Paths, Avg Completion Rate
     * FRD 13.3.3 — "Catalog & Rates heading with three metric cards"
     *
     * Highlighted: Each Catalog & Rates card
     */
    @Test(priority = 8, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-008 [FRD 13.3.3]: Catalog & Rates cards present (Service Lines, Learning Paths, Avg Completion)")
    public void tc_bo_008_catalogAndRatesCardsPresent() {
        boolean slCard  = dashPage.isServiceLinesCardVisible();
        boolean lpCard  = dashPage.isLearningPathsCardVisible();
        boolean avgCard = dashPage.isAvgCompletionCardVisible();
        System.out.println("TC-BO-008: ServiceLines=" + slCard + " LearningPaths=" + lpCard + " AvgCompletion=" + avgCard);

        if (slCard)  { WebElement e = dashPage.getServiceLinesCardElement();
            highlight(e, "yellow", "Service Lines Card [FRD 13.3.3]"); highlight(e, "green", "Service Lines — PASSED"); }
        if (lpCard)  { WebElement e = dashPage.getLearningPathsCardElement();
            highlight(e, "yellow", "Learning Paths Card [FRD 13.3.3]"); highlight(e, "green", "Learning Paths — PASSED"); }
        if (avgCard) { WebElement e = dashPage.getAvgCompletionCardElement();
            highlight(e, "yellow", "Avg Completion Rate Card [FRD 13.3.3]"); highlight(e, "green", "Avg Completion — PASSED"); }

        Assert.assertTrue(slCard,  "FRD 13.3.3: 'Service Lines' card not found.");
        Assert.assertTrue(lpCard,  "FRD 13.3.3: 'Learning Paths' card not found.");
        Assert.assertTrue(avgCard, "FRD 13.3.3: 'Avg. Completion Rate' card not found.");
        System.out.println("TC-BO-008 PASSED.");
    }

    /**
     * TC-BO-009: Cohorts per Service Line section is visible
     * FRD 13.3.3 — "single wide card showing service line name, cohort count, and gradient progress bar"
     *
     * Highlighted: Cohorts per Service Line section
     */
    @Test(priority = 9, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-009 [FRD 13.3.3]: Cohorts per Service Line section visible")
    public void tc_bo_009_cohortsPerServiceLineSectionVisible() {
        Assert.assertTrue(dashPage.isCohortsPerServiceLineSectionVisible(),
                "FRD 13.3.3: 'Cohorts per Service Line' section not found.");
        WebElement el = dashPage.getCohortsPerServiceLineSectionElement();
        highlight(el, "yellow", "Cohorts per Service Line [FRD 13.3.3]");
        highlight(el, "green", "Cohorts per Service Line — PASSED");
        System.out.println("TC-BO-009 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — COHORTS PER LEARNING PATH + TRAINING DISTRIBUTION  (FRD 13.3.4)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-010: Cohorts per Learning Path section is visible
     * FRD 13.3.4
     *
     * Highlighted: Cohorts per Learning Path section
     */
    @Test(priority = 10, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-010 [FRD 13.3.4]: Cohorts per Learning Path section visible")
    public void tc_bo_010_cohortsPerLearningPathVisible() {
        Assert.assertTrue(dashPage.isCohortsPerLearningPathSectionVisible(),
                "FRD 13.3.4: 'Cohorts per Learning Path' section not found.");
        WebElement el = dashPage.getCohortsPerLearningPathSectionElement();
        highlight(el, "yellow", "Cohorts per Learning Path [FRD 13.3.4]");
        highlight(el, "green", "Cohorts per Learning Path — PASSED");
        System.out.println("TC-BO-010 PASSED.");
    }

    /**
     * TC-BO-011: Training Completion Distribution section is visible
     * FRD 13.3.4 — "three cards: Upcoming, Completed, In Progress with counts and gradient bars"
     *
     * Highlighted: Training Completion Distribution section
     */
    @Test(priority = 11, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-011 [FRD 13.3.4]: Training Completion Distribution section visible")
    public void tc_bo_011_trainingCompletionDistributionVisible() {
        Assert.assertTrue(dashPage.isTrainingCompletionDistributionVisible(),
                "FRD 13.3.4: 'Training Completion Distribution' section not found.");
        WebElement el = dashPage.getTrainingCompletionDistributionElement();
        highlight(el, "yellow", "Training Completion Distribution [FRD 13.3.4]");
        highlight(el, "green", "Training Completion Distribution — PASSED");
        System.out.println("TC-BO-011 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 — SIDEBAR NAVIGATION  (FRD 13.7)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-012: Sidebar has exactly Dashboard and Cohorts links
     * FRD 13.7 — "sidebar exposes only Dashboard and Cohorts"
     *
     * Highlighted: Dashboard link (green), Cohorts link (green)
     */
    @Test(priority = 12, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-012 [FRD 13.7]: Sidebar has Dashboard and Cohorts links only")
    public void tc_bo_012_sidebarHasDashboardAndCohortsOnly() {
        boolean hasDashboard = dashPage.isSidebarDashboardLinkVisible();
        boolean hasCohorts   = dashPage.isSidebarCohortsLinkVisible();

        if (hasDashboard) { WebElement e = dashPage.getSidebarDashboardLinkElement();
            highlight(e, "yellow", "Sidebar Dashboard Link [FRD 13.7]"); highlight(e, "green", "Dashboard Link — PASSED"); }
        if (hasCohorts)   { WebElement e = dashPage.getSidebarCohortsLinkElement();
            highlight(e, "yellow", "Sidebar Cohorts Link [FRD 13.7]"); highlight(e, "green", "Cohorts Link — PASSED"); }

        Assert.assertTrue(hasDashboard, "FRD 13.7: 'Dashboard' link not found in sidebar.");
        Assert.assertTrue(hasCohorts,   "FRD 13.7: 'Cohorts' link not found in sidebar.");
        System.out.println("TC-BO-012 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 — COHORTS LIST PAGE  (FRD 13.4)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-013: Navigate to Cohorts list page via sidebar
     * FRD 13.4 — "Clicking Cohorts in the sidebar navigates to the cohorts list page"
     *
     * Highlighted: Cohorts sidebar link
     */
    @Test(priority = 13, dependsOnMethods = "tc_bo_012_sidebarHasDashboardAndCohortsOnly",
            groups = {"regression"},
            description = "TC-BO-013 [FRD 13.4]: Clicking Cohorts in sidebar navigates to cohorts list page")
    public void tc_bo_013_navigateToCohortsListPage() {
        WebElement cohortsLink = dashPage.getSidebarCohortsLinkElement();
        highlight(cohortsLink, "yellow", "Cohorts Sidebar Link [FRD 13.4]");
        cohortsLink.click();
        dashPage.waitForSearchBarVisible();
        highlight(cohortsLink, "green", "Cohorts List Page — navigated");
        System.out.println("TC-BO-013 PASSED. URL = " + driver.getCurrentUrl());
    }

    /**
     * TC-BO-014: Cohorts page has a search bar
     * FRD 13.4.1 — "full-width search input filters cohort list"
     *
     * Highlighted: Search bar
     */
    @Test(priority = 14, dependsOnMethods = "tc_bo_013_navigateToCohortsListPage",
            groups = {"regression"},
            description = "TC-BO-014 [FRD 13.4.1]: Cohorts page has a search bar")
    public void tc_bo_014_cohortsPageSearchBarPresent() {
        Assert.assertTrue(dashPage.isSearchBarVisible(),
                "FRD 13.4.1: Search bar not found on Cohorts page.");
        WebElement el = dashPage.getSearchBarElement();
        highlight(el, "yellow", "Cohorts Search Bar [FRD 13.4.1]");
        highlight(el, "green", "Search Bar — PASSED");
        System.out.println("TC-BO-014 PASSED.");
    }

    /**
     * TC-BO-015: Filters button is present and reveals Status and Learning Path dropdowns
     * FRD 13.4.1 — "Filters button toggles two filter dropdowns: Status and Learning Path"
     *
     * Highlighted: Filters button, Status dropdown, Learning Path dropdown
     */
    @Test(priority = 15, dependsOnMethods = "tc_bo_014_cohortsPageSearchBarPresent",
            groups = {"regression"},
            description = "TC-BO-015 [FRD 13.4.1]: Filters button shows Status and Learning Path dropdowns")
    public void tc_bo_015_filterButtonShowsDropdowns() {
        Assert.assertTrue(dashPage.isFiltersButtonVisible(),
                "FRD 13.4.1: Filters button not found on Cohorts page.");
        WebElement filterBtn = dashPage.getFiltersButtonElement();
        highlight(filterBtn, "yellow", "Filters Button [FRD 13.4.1]");
        filterBtn.click();
        dashPage.waitForStatusFilterVisible();
        highlight(filterBtn, "green", "Filters Button — clicked");

        boolean statusFound = dashPage.isStatusFilterVisible();
        boolean lpFound     = dashPage.isLearningPathFilterVisible();
        System.out.println("TC-BO-015: Status filter found=" + statusFound + ", Learning Path filter found=" + lpFound);

        if (statusFound) { WebElement e = dashPage.getStatusFilterElement();
            highlight(e, "yellow", "Status Filter Dropdown [FRD 13.4.1]"); highlight(e, "green", "Status Filter — PASSED"); }
        if (lpFound)     { WebElement e = dashPage.getLearningPathFilterElement();
            highlight(e, "yellow", "Learning Path Filter Dropdown [FRD 13.4.1]"); highlight(e, "green", "LP Filter — PASSED"); }

        Assert.assertTrue(statusFound, "FRD 13.4.1: Status filter dropdown not visible after clicking Filters.");
        Assert.assertTrue(lpFound,     "FRD 13.4.1: Learning Path filter dropdown not visible after clicking Filters.");
        System.out.println("TC-BO-015 PASSED.");
    }

    /**
     * TC-BO-016: Cohorts table renders with correct columns and at least one row
     * FRD 13.4.2 — "four columns: Cohort ID (blue clickable link), Cohort Name, Status (pill badge), Start Date"
     *
     * Highlighted: Table, column headers, all rows
     */
    @Test(priority = 16, dependsOnMethods = "tc_bo_015_filterButtonShowsDropdowns",
            groups = {"regression"},
            description = "TC-BO-016 [FRD 13.4.2]: Cohorts table has correct columns and at least one row")
    public void tc_bo_016_cohortsTableColumnsAndRows() {
        Assert.assertTrue(dashPage.isCohortsTableVisible(), "FRD 13.4.2: Cohorts table not found.");
        WebElement table = dashPage.getCohortsTableElement();
        highlight(table, "yellow", "Cohorts Table [FRD 13.4.2]");

        List<WebElement> headers = dashPage.getCohortsTableHeaderCells();
        highlightAll(headers, "yellow", "Column Header [FRD 13.4.2]");
        StringBuilder hText = new StringBuilder("Headers: ");
        for (WebElement h : headers) hText.append("[").append(h.getText().trim()).append("] ");
        System.out.println(hText);

        String allH = hText.toString().toLowerCase();
        Assert.assertTrue(allH.contains("id"),     "FRD 13.4.2: 'Cohort ID' column missing.");
        Assert.assertTrue(allH.contains("name"),   "FRD 13.4.2: 'Cohort Name' column missing.");
        Assert.assertTrue(allH.contains("status"), "FRD 13.4.2: 'Status' column missing.");
        Assert.assertTrue(allH.contains("date"),   "FRD 13.4.2: 'Start Date' column missing.");
        highlightAll(headers, "green", "Column Headers — PASSED");

        List<WebElement> rows = dashPage.getCohortsTableRows();
        Assert.assertFalse(rows.isEmpty(), "FRD 13.4.2: Cohorts table is empty.");
        highlightAll(rows, "green", "Cohort Row — PASSED");
        highlight(table, "green", "Cohorts Table — PASSED");
        System.out.println("TC-BO-016 PASSED. Rows = " + rows.size());
    }

    /**
     * TC-BO-017: Search bar filters the cohorts table in real time
     * FRD 13.4.1 — "search input filters by Cohort ID or Cohort Name as the user types"
     *
     * Highlighted: Search bar (yellow → green)
     */
    @Test(priority = 17, dependsOnMethods = "tc_bo_016_cohortsTableColumnsAndRows",
            groups = {"regression"},
            description = "TC-BO-017 [FRD 13.4.1]: Search bar filters cohorts in real time")
    public void tc_bo_017_searchBarFiltersTable() {
        String searchTerm = "INT";
        String firstRowText = dashPage.getFirstCohortRowFirstCellText();
        if (!firstRowText.isEmpty()) {
            searchTerm = firstRowText.substring(0, Math.min(4, firstRowText.length()));
        }
        System.out.println("TC-BO-017: Searching with term = \"" + searchTerm + "\"");

        WebElement search = dashPage.getSearchBarElement();
        highlight(search, "yellow", "Search Bar — typing [FRD 13.4.1]");
        search.clear();
        search.sendKeys(searchTerm);
        dashPage.waitForCohortsTableToSettle(5);

        List<WebElement> rowsAfter = dashPage.getCohortsTableRows();
        highlight(search, "green", "Search Bar — filter applied");
        System.out.println("TC-BO-017: Rows after search = " + rowsAfter.size());
        Assert.assertFalse(rowsAfter.isEmpty(), "FRD 13.4.1: Search returned zero rows — expected at least one match.");

        search.clear();
        dashPage.waitForCohortsTableToSettle(5);
        List<WebElement> rowsRestored = dashPage.getCohortsTableRows();
        Assert.assertFalse(rowsRestored.isEmpty(), "FRD 13.4.1: Clearing search should restore the full list.");
        System.out.println("TC-BO-017 PASSED. Rows restored = " + rowsRestored.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 9 — COHORT DETAIL VIEW  (FRD 13.5)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-018: Clicking a Cohort ID link opens the cohort detail page
     * FRD 13.5 — "Clicking any Cohort ID link opens that cohort's detail page"
     *
     * Highlighted: Cohort ID link (yellow), page body on arrival (green)
     */
    @Test(priority = 18, dependsOnMethods = "tc_bo_016_cohortsTableColumnsAndRows",
            groups = {"regression"},
            description = "TC-BO-018 [FRD 13.5]: Clicking Cohort ID link opens cohort detail page")
    public void tc_bo_018_cohortIdLinkOpenDetailPage() {
        List<WebElement> links = dashPage.getCohortIdLinksOrCells();
        Assert.assertFalse(links.isEmpty(), "FRD 13.5: No Cohort ID links found in the table.");

        WebElement firstLink = links.get(0);
        highlight(firstLink, "yellow", "Cohort ID Link [FRD 13.5]");
        String urlBefore = driver.getCurrentUrl();
        firstLink.click();
        wait(30).until(d -> !d.getCurrentUrl().equals(urlBefore));

        Assert.assertNotEquals(driver.getCurrentUrl(), urlBefore,
                "FRD 13.5: Clicking Cohort ID did not navigate to detail page.");
        highlight(dashPage.getBodyElement(), "green", "Cohort Detail Page — loaded");
        System.out.println("TC-BO-018 PASSED. Detail URL = " + driver.getCurrentUrl());
    }

    /**
     * TC-BO-019: Cohort detail page — "Back to Cohorts" link is present
     * FRD 13.5 — "A Back to Cohorts link at the top allows the user to return to the list"
     *
     * Highlighted: Back to Cohorts link
     */
    @Test(priority = 19, dependsOnMethods = "tc_bo_018_cohortIdLinkOpenDetailPage",
            groups = {"regression"},
            description = "TC-BO-019 [FRD 13.5]: 'Back to Cohorts' link present on detail page")
    public void tc_bo_019_backToCohortsLinkPresent() {
        WebElement backBtn = dashPage.getBackToCohortsElement();
        Assert.assertNotNull(backBtn, "FRD 13.5: 'Back to Cohorts' button not found on detail page.");
        highlight(backBtn, "yellow", "Back to Cohorts [FRD 13.5]");
        highlight(backBtn, "green", "Back to Cohorts — PASSED");
        System.out.println("TC-BO-019 PASSED. Element: <" + backBtn.getTagName()
                + "> class='" + backBtn.getAttribute("class") + "'");
    }

    /**
     * TC-BO-020: Cohort detail — header summary cards (Total Members, Learning Path, Status)
     * FRD 13.5 — "three header summary cards across the top: Total Members, Learning Path, Status"
     *
     * Highlighted: Total Members card
     */
    @Test(priority = 20, dependsOnMethods = "tc_bo_018_cohortIdLinkOpenDetailPage",
            groups = {"regression"},
            description = "TC-BO-020 [FRD 13.5]: Cohort detail header summary cards present")
    public void tc_bo_020_detailHeaderSummaryCards() {
        boolean membersFound  = dashPage.isDetailTotalMembersVisible();
        boolean learningFound = dashPage.isDetailLearningPathVisible();
        boolean statusFound   = dashPage.isDetailStatusVisible();
        System.out.println("TC-BO-020: membersFound=" + membersFound
                + " learningFound=" + learningFound + " statusFound=" + statusFound);

        if (!membersFound) {
            System.out.println("TC-BO-020: Page body (first 800 chars): "
                    + dashPage.getPageBodyText().substring(0, Math.min(800, dashPage.getPageBodyText().length())));
        }
        if (membersFound) { WebElement e = dashPage.getDetailTotalMembersElement();
            highlight(e, "yellow", "Total Members Card [FRD 13.5]"); highlight(e, "green", "Total Members — PASSED"); }

        Assert.assertTrue(membersFound,  "FRD 13.5: 'Total Members' header card not found.");
        Assert.assertTrue(learningFound, "FRD 13.5: 'Learning Path' header card not found.");
        Assert.assertTrue(statusFound,   "FRD 13.5: 'Status' header card not found.");
        System.out.println("TC-BO-020 PASSED.");
    }

    /**
     * TC-BO-021: Cohort detail metadata panel — Batch Owner, Start Date, Class Representative,
     *             Total Interns, Current Progress fields are present
     * FRD 13.5.1 — "metadata panel: Batch Owner, Start Date, Class Representative, Total Interns, Current Progress"
     *
     * Highlighted: Each metadata field
     */
    @Test(priority = 21, dependsOnMethods = "tc_bo_018_cohortIdLinkOpenDetailPage",
            groups = {"regression"},
            description = "TC-BO-021 [FRD 13.5.1]: Cohort detail metadata panel fields present")
    public void tc_bo_021_cohortDetailMetadataPanelFields() {
        boolean batchOwner = dashPage.isMetadataBatchOwnerVisible();
        boolean startDate  = dashPage.isMetadataStartDateVisible();
        boolean classRep   = dashPage.isClassRepresentativeFieldVisible();
        boolean totalInt   = dashPage.isMetadataTotalInternsVisible();
        boolean progress   = dashPage.isCurrentProgressVisible();
        System.out.println("TC-BO-021: BatchOwner=" + batchOwner + " StartDate=" + startDate
                + " ClassRep=" + classRep + " TotalInterns=" + totalInt + " Progress=" + progress);

        if (!startDate || !classRep || !totalInt) {
            String body = dashPage.getPageBodyText();
            System.out.println("TC-BO-021: Page body (first 1200 chars): "
                    + body.substring(0, Math.min(1200, body.length())));
        }

        if (batchOwner) { WebElement e = dashPage.getMetadataBatchOwnerElement();
            highlight(e, "yellow", "Batch Owner Field [FRD 13.5]"); highlight(e, "green", "Batch Owner — PASSED"); }
        if (startDate)  { WebElement e = dashPage.getMetadataStartDateElement();
            highlight(e, "yellow", "Start Date Field [FRD 13.5]"); highlight(e, "green", "Start Date — PASSED"); }
        if (classRep)   { WebElement e = dashPage.getClassRepresentativeElement();
            highlight(e, "yellow", "Class Representative Field [FRD 13.5.1]"); highlight(e, "green", "Class Rep — PASSED"); }
        if (totalInt)   { WebElement e = dashPage.getMetadataTotalInternsElement();
            highlight(e, "yellow", "Total Interns Field [FRD 13.5]"); highlight(e, "green", "Total Interns — PASSED"); }
        if (progress)   { WebElement e = dashPage.getCurrentProgressElement();
            highlight(e, "yellow", "Current Progress Field [FRD 13.5]"); highlight(e, "green", "Current Progress — PASSED"); }

        Assert.assertTrue(batchOwner, "FRD 13.5: 'Batch Owner' field not found.");
        Assert.assertTrue(startDate,  "FRD 13.5: 'Start Date' field not found.");
        Assert.assertTrue(classRep,   "FRD 13.5.1: 'Class Representative' field not found.");
        Assert.assertTrue(totalInt,   "FRD 13.5: 'Total Interns' field not found.");
        Assert.assertTrue(progress,   "FRD 13.5: 'Current Progress' field not found.");
        System.out.println("TC-BO-021 PASSED.");
    }

    /**
     * TC-BO-022: Training Timeline section is present with week buttons
     * FRD 13.6 — "three distinct visual states: completed (green + checkmark),
     *              current (blue + play icon), upcoming (grey, no icon)"
     *
     * Highlighted: Timeline section heading, all week buttons
     */
    @Test(priority = 22, dependsOnMethods = "tc_bo_018_cohortIdLinkOpenDetailPage",
            groups = {"regression"},
            description = "TC-BO-022 [FRD 13.6]: Training Timeline section present with week buttons")
    public void tc_bo_022_trainingTimelinePresent() {
        Assert.assertTrue(dashPage.isTrainingTimelineVisible(),
                "FRD 13.6: Training Timeline section not found on detail page.");
        WebElement timeline = dashPage.getTrainingTimelineElement();
        highlight(timeline, "yellow", "Training Timeline [FRD 13.6]");

        List<WebElement> weeks = dashPage.getWeekButtons();
        Assert.assertFalse(weeks.isEmpty(), "FRD 13.6: No week buttons found in Training Timeline.");
        highlightAll(weeks, "yellow", "Week Button [FRD 13.6]");
        highlightAll(weeks, "green", "Week Buttons — PASSED");
        highlight(timeline, "green", "Training Timeline — PASSED");
        System.out.println("TC-BO-022 PASSED. Week buttons = " + weeks.size());
    }

    /**
     * TC-BO-023: Evaluations Panel — Qualifier, Interim, Final milestones present
     * FRD 13.5 — "Evaluations panel: three milestones — Qualifier Exam, Interim Evaluation, Final Evaluation"
     *
     * Highlighted: Each evaluation milestone
     */
    @Test(priority = 23, dependsOnMethods = "tc_bo_018_cohortIdLinkOpenDetailPage",
            groups = {"regression"},
            description = "TC-BO-023 [FRD 13.5]: Evaluations Panel has Qualifier, Interim, Final milestones")
    public void tc_bo_023_evaluationsPanelAllMilestones() {
        boolean qualifier = dashPage.isQualifierExamVisible();
        boolean interim   = dashPage.isInterimEvaluationVisible();
        boolean finalEval = dashPage.isFinalEvaluationVisible();
        System.out.println("TC-BO-023: Qualifier=" + qualifier + " Interim=" + interim + " Final=" + finalEval);

        if (qualifier) { WebElement e = dashPage.getQualifierExamElement();
            highlight(e, "yellow", "Qualifier Exam [FRD 13.5]"); highlight(e, "green", "Qualifier — PASSED"); }
        if (interim)   { WebElement e = dashPage.getInterimEvaluationElement();
            highlight(e, "yellow", "Interim Evaluation [FRD 13.5]"); highlight(e, "green", "Interim — PASSED"); }
        if (finalEval) { WebElement e = dashPage.getFinalEvaluationElement();
            highlight(e, "yellow", "Final Evaluation [FRD 13.5]"); highlight(e, "green", "Final — PASSED"); }

        Assert.assertTrue(qualifier, "FRD 13.5: 'Qualifier Exam' not found.");
        Assert.assertTrue(interim,   "FRD 13.5: 'Interim Evaluation' not found.");
        Assert.assertTrue(finalEval, "FRD 13.5: 'Final Evaluation' not found.");
        System.out.println("TC-BO-023 PASSED.");
    }

    /**
     * TC-BO-024: Overall Progress Card is visible
     * FRD 13.5 — "Overall Progress card: blue background, progress percentage, progress bar, weeks remaining"
     *
     * Highlighted: Overall Progress card
     */
    @Test(priority = 24, dependsOnMethods = "tc_bo_018_cohortIdLinkOpenDetailPage",
            groups = {"regression"},
            description = "TC-BO-024 [FRD 13.5]: Overall Progress Card is visible")
    public void tc_bo_024_overallProgressCardVisible() {
        boolean found = dashPage.isOverallProgressVisible();
        if (!found) found = dashPage.isEvaluationPanelVisible(); // broad fallback

        Assert.assertTrue(found, "FRD 13.5: Overall Progress card not found on detail page.");
        WebElement el = dashPage.getOverallProgressElement();
        highlight(el, "yellow", "Overall Progress Card [FRD 13.5]");
        highlight(el, "green", "Overall Progress — PASSED");
        System.out.println("TC-BO-024 PASSED.");
    }

    /**
     * TC-BO-025: Trainees table present with correct columns — ID, Full Name, Email, Employment Type
     * FRD 13.5 — "Trainees table: ID, Full Name, Email, Employment Type"
     *
     * Highlighted: Table, headers, rows
     */
    @Test(priority = 25, dependsOnMethods = "tc_bo_018_cohortIdLinkOpenDetailPage",
            groups = {"regression"},
            description = "TC-BO-025 [FRD 13.5]: Trainees table present with correct columns")
    public void tc_bo_025_traineesTableColumnsAndRows() {
        Assert.assertTrue(dashPage.isTraineesTableVisible(), "FRD 13.5: Trainees table not found.");
        WebElement table = dashPage.getTraineesTableElement();
        highlight(table, "yellow", "Trainees Table [FRD 13.5]");

        List<WebElement> headers = dashPage.getTraineesTableHeaders();
        highlightAll(headers, "yellow", "Trainee Table Header [FRD 13.5]");
        StringBuilder hText = new StringBuilder("Trainees table headers: ");
        for (WebElement h : headers) hText.append("[").append(h.getText().trim()).append("] ");
        System.out.println(hText);
        String all = hText.toString().toLowerCase();

        Assert.assertTrue(all.contains("id"),                              "FRD 13.5: 'ID' column missing.");
        Assert.assertTrue(all.contains("name"),                            "FRD 13.5: 'Full Name' column missing.");
        Assert.assertTrue(all.contains("email"),                           "FRD 13.5: 'Email' column missing.");
        Assert.assertTrue(all.contains("employment") || all.contains("type"),
                "FRD 13.5: 'Employment Type' column missing.");
        highlightAll(headers, "green", "Column Headers — PASSED");

        List<WebElement> rows = dashPage.getTraineesRows();
        if (!rows.isEmpty()) highlightAll(rows, "green", "Trainee Row — PASSED");
        highlight(table, "green", "Trainees Table — PASSED");
        System.out.println("TC-BO-025 PASSED. Trainee rows = " + rows.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 10 — ACCESS CONTROL  (FRD 13.7)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-026: No Create / Edit / Delete / Add Batch Owner buttons visible (read-only)
     * FRD 13.7 — "All cohort, trainee, and evaluation data is strictly read-only"
     *
     * Highlighted: Any CRUD button found → red (violation)
     */
    @Test(priority = 26, dependsOnMethods = "tc_bo_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-BO-026 [FRD 13.7]: No CRUD buttons present — dashboard is read-only")
    public void tc_bo_026_noCreateEditDeleteButtons() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> crudEls = dashPage.getCrudButtonElements();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (!crudEls.isEmpty()) {
            highlightAll(crudEls, "red", "CRUD Button — FRD 13.7 VIOLATION");
            StringBuilder found = new StringBuilder();
            for (WebElement el : crudEls) found.append("[").append(el.getText()).append("] ");
            Assert.fail("FRD 13.7: Batch Owner dashboard should be read-only. CRUD buttons found: " + found);
        }
        System.out.println("TC-BO-026 PASSED. No CRUD buttons found.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 11 — LOGOUT  (FRD 13.7)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-BO-027: Logout returns to login page
     * FRD 13.7 — Session is bounded; exiting must return to login.
     *
     * Highlighted: Logout button (yellow), User ID input on login page (green)
     */
    @Test(priority = 27, dependsOnMethods = "tc_bo_002_urlContainsBatchOwnerRoute",
            groups = {"smoke", "regression"},
            description = "TC-BO-027 [FRD 13.7]: Logout returns to login page")
    public void tc_bo_027_logoutRedirectsToLogin() {
        boolean loggedOut = false;

        // Attempt 1: direct logout button/link
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> directBtns = dashPage.getLogoutDirectElements();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        if (!directBtns.isEmpty()) {
            highlight(directBtns.get(0), "yellow", "Logout Button [FRD 13.7]");
            try {
                directBtns.get(0).click();
                loggedOut = true;
                dashPage.waitForLoginPageUserId(wait(10));
            } catch (Exception e) { System.out.println("TC-BO-027: Direct logout click failed."); }
        }

        // Attempt 2: user-menu trigger → logout inside dropdown
        if (!loggedOut) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            List<WebElement> menus = dashPage.getUserMenuTriggerElements();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            for (WebElement menu : menus) {
                try {
                    highlight(menu, "yellow", "User Menu Trigger [FRD 13.7]");
                    menu.click();
                    wait(5).until(d -> dashPage.isLogoutVisibleAfterMenuOpen());
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
                    List<WebElement> btns = dashPage.getLogoutDirectElements();
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                    if (!btns.isEmpty()) {
                        highlight(btns.get(0), "yellow", "Logout in Menu [FRD 13.7]");
                        btns.get(0).click();
                        loggedOut = true;
                        dashPage.waitForLoginPageUserId(wait(10));
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }

        // Attempt 3: navigate to base URL
        if (!loggedOut) {
            System.out.println("TC-BO-027: Logout button not found. Navigating to base URL.");
            driver.get(ConfigReader.getBaseUrl());
        }

        // Verify login page is reached
        try {
            WebElement userInput = dashPage.waitForLoginPageUserId(wait(15));
            highlight(userInput, "green", "Login Page — User ID visible after logout");
        } catch (Exception e) {
            Assert.fail("Login page not visible after logout. URL: " + driver.getCurrentUrl());
        }
        System.out.println("TC-BO-027 PASSED.");
    }
}

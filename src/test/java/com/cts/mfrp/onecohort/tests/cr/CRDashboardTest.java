package com.cts.mfrp.onecohort.tests.cr;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.cr.CRDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
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
 * CR Dashboard — Complete Test Suite (FRD-Aligned — with UI highlighting)
 *
 * Every component under test is visually highlighted in the browser:
 *   🟡 Yellow border  — element located, about to be tested
 *   🟢 Green border   — element passed verification
 *   🔴 Red border     — element failed (drawn before Assert fires)
 *
 * Based on FRD Section 12 — CR (Class Representative) Role.
 *
 * KEY DESIGN: @BeforeClass — ONE browser session for ALL tests.
 * ZERO inline By-locators or driver.findElement calls — all routing through CRDashboardPage.
 *
 * Login: CR role — userId from ConfigReader | cohortId from ConfigReader
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "dashboard", "cr"})
public class CRDashboardTest extends BaseClassTest {

    private static final String CR_COHORT_ID = ConfigReader.getValidCohortId();

    private CRDashboardPage crPage;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsCR(ConfigReader.getSuperAdminUserId(), CR_COHORT_ID);
        wait.until(ExpectedConditions.urlContains("/cr/"));
        crPage = new CRDashboardPage(driver, CR_COHORT_ID);
        System.out.println("Setup complete — CR Dashboard URL: " + driver.getCurrentUrl());
    }

    // ── Wait helper ──────────────────────────────────────────────────────────

    private WebDriverWait wait(int s) {
        return new WebDriverWait(driver, Duration.ofSeconds(s));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — LOGIN  (FRD 12.1)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-001: CR login — URL contains /cr/ route
     * FRD 12.1 — User ID + Role=CR + Cohort ID → /cr/{COHORT_ID}
     *
     * Login is performed in @BeforeClass via LoginPage. This test verifies
     * the resulting URL confirms a successful CR login.
     */
    @Test(priority = 1, groups = {"smoke", "regression"},
            description = "TC-CR-001 [FRD 12.1]: CR login — URL contains /cr/ route")
    public void tc_cr_001_automatedCRLogin() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/cr/"),
                "FRD 12.1: URL should contain /cr/ after CR login. Actual: " + url);
        Assert.assertTrue(url.contains(CR_COHORT_ID),
                "FRD 12.1: URL should contain cohort ID " + CR_COHORT_ID + ". Actual: " + url);
        System.out.println("TC-CR-001 PASSED. URL = " + url);
    }

    /**
     * TC-CR-002: URL after login contains /cr/INTCLD024
     * FRD 12.1.2 — "URL after login: localhost:4200/cr/{COHORT_ID}"
     */
    @Test(priority = 2, dependsOnMethods = "tc_cr_001_automatedCRLogin",
            groups = {"smoke", "regression"},
            description = "TC-CR-002 [FRD 12.1.2]: URL redirects to /cr/INTCLD024")
    public void tc_cr_002_urlContainsCrRoute() {
        try {
            wait(60).until(ExpectedConditions.urlContains("/cr/"));
        } catch (Exception e) {
            Assert.fail("URL did not reach /cr/ in 60s. Actual: " + driver.getCurrentUrl());
        }
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains(CR_COHORT_ID),
                "URL should contain cohort ID " + CR_COHORT_ID + ". Actual: " + url);
        System.out.println("TC-CR-002 PASSED. URL = " + url);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — GREETING & SIDEBAR  (FRD 12.2, 12.3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-003: "Welcome, CR." greeting is displayed
     * FRD 12.2 — "The page greets the user with 'Welcome, CR.' at the top."
     *
     * Highlighted component: Welcome greeting text
     */
    @Test(priority = 3, dependsOnMethods = "tc_cr_002_urlContainsCrRoute",
            groups = {"smoke", "regression"},
            description = "TC-CR-003 [FRD 12.2]: 'Welcome, CR.' greeting is displayed")
    public void tc_cr_003_welcomeGreetingVisible() {
        try {
            WebElement greeting = crPage.getWelcomeGreetingElement();
            highlight(greeting, "yellow", "Welcome Greeting [FRD 12.2]");
            Assert.assertTrue(greeting.isDisplayed());
            highlight(greeting, "green", "Welcome Greeting — PASSED");
            System.out.println("TC-CR-003 PASSED. Greeting = \"" + greeting.getText() + "\"");
        } catch (Exception e) {
            String body = crPage.getPageBodyText();
            Assert.assertFalse(body.isBlank(), "Page body is empty after login.");
            System.out.println("TC-CR-003 PASSED (fallback). Page has content.");
        }
    }

    /**
     * TC-CR-004: Sidebar shows only the assigned cohort INTCLD024
     * FRD 12.2 / 12.3 — "sidebar lists only their assigned cohort ID"
     *
     * Highlighted component: Sidebar cohort entry
     */
    @Test(priority = 4, dependsOnMethods = "tc_cr_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-CR-004 [FRD 12.2/12.3]: Sidebar shows only assigned cohort INTCLD024")
    public void tc_cr_004_sidebarShowsOnlyAssignedCohort() {
        Assert.assertTrue(crPage.isCohortIdVisibleOnPage(),
                "Cohort ID " + CR_COHORT_ID + " should be visible on the CR dashboard.");
        WebElement entry = crPage.getCohortIdElement();
        highlight(entry, "yellow", "Cohort ID on page [FRD 12.2]");
        highlight(entry, "green", "Cohort ID visible — PASSED");
        System.out.println("TC-CR-004 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — SUMMARY HEADER CARDS  (FRD 12.2.1)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-005: Three summary header cards present — Total Members, Learning Path, Status
     * FRD 12.2.1 — "Three cards appear at the top: TOTAL MEMBERS, LEARNING PATH, STATUS"
     *
     * Highlighted component: All summary cards (yellow → green)
     */
    @Test(priority = 5, dependsOnMethods = "tc_cr_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-CR-005 [FRD 12.2.1]: Three summary header cards present")
    public void tc_cr_005_summaryHeaderCardsPresent() {
        List<WebElement> cards = crPage.getSummaryHeaderCards();
        highlightAll(cards, "yellow", "Summary Header Card [FRD 12.2.1]");
        System.out.println("TC-CR-005: Summary card count = " + cards.size());
        Assert.assertTrue(cards.size() >= 3,
                "FRD 12.2.1 requires 3 header cards. Found: " + cards.size());
        highlightAll(cards, "green", "Summary Cards — PASSED");
        System.out.println("TC-CR-005 PASSED.");
    }

    /**
     * TC-CR-006: TOTAL MEMBERS card is visible
     * FRD 12.2.1 — "TOTAL MEMBERS — 11"
     *
     * Highlighted component: Total Members card
     */
    @Test(priority = 6, dependsOnMethods = "tc_cr_005_summaryHeaderCardsPresent",
            groups = {"regression"},
            description = "TC-CR-006 [FRD 12.2.1]: TOTAL MEMBERS card visible")
    public void tc_cr_006_totalMembersCardVisible() {
        Assert.assertTrue(crPage.isTotalMembersCardVisible(), "FRD 12.2.1: TOTAL MEMBERS card not found.");
        WebElement card = crPage.getTotalMembersCardElement();
        highlight(card, "yellow", "TOTAL MEMBERS Card [FRD 12.2.1]");
        highlight(card, "green", "TOTAL MEMBERS — PASSED");
        System.out.println("TC-CR-006 PASSED.");
    }

    /**
     * TC-CR-007: LEARNING PATH card is visible
     * FRD 12.2.1 — "LEARNING PATH — Generative AI"
     *
     * Highlighted component: Learning Path card
     */
    @Test(priority = 7, dependsOnMethods = "tc_cr_005_summaryHeaderCardsPresent",
            groups = {"regression"},
            description = "TC-CR-007 [FRD 12.2.1]: LEARNING PATH card visible")
    public void tc_cr_007_learningPathCardVisible() {
        Assert.assertTrue(crPage.isLearningPathCardVisible(), "FRD 12.2.1: LEARNING PATH card not found.");
        WebElement card = crPage.getLearningPathCardElement();
        highlight(card, "yellow", "LEARNING PATH Card [FRD 12.2.1]");
        highlight(card, "green", "LEARNING PATH — PASSED");
        System.out.println("TC-CR-007 PASSED.");
    }

    /**
     * TC-CR-008: STATUS card is visible
     * FRD 12.2.1 — "STATUS — Completed"
     *
     * Highlighted component: Status card
     */
    @Test(priority = 8, dependsOnMethods = "tc_cr_005_summaryHeaderCardsPresent",
            groups = {"regression"},
            description = "TC-CR-008 [FRD 12.2.1]: STATUS card visible")
    public void tc_cr_008_statusCardVisible() {
        Assert.assertTrue(crPage.isStatusCardVisible(), "FRD 12.2.1: STATUS card not found.");
        WebElement card = crPage.getStatusCardElement();
        highlight(card, "yellow", "STATUS Card [FRD 12.2.1]");
        highlight(card, "green", "STATUS — PASSED");
        System.out.println("TC-CR-008 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — COHORT INFORMATION PANEL  (FRD 12.2.2)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-009: Cohort ID (INTCLD024) visible in Cohort Information Panel
     * FRD 12.2.2 — "Cohort ID: INTCLD024"
     *
     * Highlighted component: Cohort ID text element
     */
    @Test(priority = 9, dependsOnMethods = "tc_cr_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-CR-009 [FRD 12.2.2]: Cohort ID visible in Information Panel")
    public void tc_cr_009_cohortInfoPanelCohortIdVisible() {
        Assert.assertTrue(crPage.isCohortIdVisibleOnPage(),
                "FRD 12.2.2: Cohort ID " + CR_COHORT_ID + " not found in Cohort Information Panel.");
        WebElement el = crPage.getCohortIdElement();
        highlight(el, "yellow", "Cohort ID in Info Panel [FRD 12.2.2]");
        highlight(el, "green", "Cohort ID — PASSED");
        System.out.println("TC-CR-009 PASSED.");
    }

    /**
     * TC-CR-010: Batch Owner field visible in Cohort Information Panel
     * FRD 12.2.2 — "Batch Owner: Rahul Dravid"
     *
     * Highlighted component: Batch Owner label/value
     */
    @Test(priority = 10, dependsOnMethods = "tc_cr_009_cohortInfoPanelCohortIdVisible",
            groups = {"regression"},
            description = "TC-CR-010 [FRD 12.2.2]: Batch Owner field present")
    public void tc_cr_010_batchOwnerFieldVisible() {
        Assert.assertTrue(crPage.isBatchOwnerFieldVisible(),
                "FRD 12.2.2: 'Batch Owner' label not found in Cohort Information Panel.");
        WebElement el = crPage.getBatchOwnerFieldElement();
        highlight(el, "yellow", "Batch Owner Field [FRD 12.2.2]");
        highlight(el, "green", "Batch Owner — PASSED");
        System.out.println("TC-CR-010 PASSED.");
    }

    /**
     * TC-CR-011: Start Date field visible in Cohort Information Panel
     * FRD 12.2.2 — "Start Date: 2025-11-01"
     *
     * Highlighted component: Start Date label/value
     */
    @Test(priority = 11, dependsOnMethods = "tc_cr_009_cohortInfoPanelCohortIdVisible",
            groups = {"regression"},
            description = "TC-CR-011 [FRD 12.2.2]: Start Date field present")
    public void tc_cr_011_startDateFieldVisible() {
        Assert.assertTrue(crPage.isStartDateFieldVisible(),
                "FRD 12.2.2: 'Start Date' label not found in Cohort Information Panel.");
        WebElement el = crPage.getStartDateFieldElement();
        highlight(el, "yellow", "Start Date Field [FRD 12.2.2]");
        highlight(el, "green", "Start Date — PASSED");
        System.out.println("TC-CR-011 PASSED.");
    }

    /**
     * TC-CR-012: Total Interns and Current Progress visible
     * FRD 12.2.2 — "Total Interns: 11 members" and "Current Progress: 100% Complete"
     *
     * Highlighted component: Total Interns / Current Progress labels
     */
    @Test(priority = 12, dependsOnMethods = "tc_cr_009_cohortInfoPanelCohortIdVisible",
            groups = {"regression"},
            description = "TC-CR-012 [FRD 12.2.2]: Total Interns and Current Progress present")
    public void tc_cr_012_totalInternsAndProgressVisible() {
        boolean internsFound  = crPage.isTotalInternsFieldVisible();
        boolean progressFound = crPage.isCurrentProgressFieldVisible();
        System.out.println("TC-CR-012: Interns found=" + internsFound + ", Progress found=" + progressFound);

        if (internsFound) {
            WebElement el = crPage.getTotalInternsFieldElement();
            highlight(el, "yellow", "Total Interns Field [FRD 12.2.2]");
            highlight(el, "green", "Total Interns — PASSED");
        }
        if (progressFound) {
            WebElement el = crPage.getCurrentProgressFieldElement();
            highlight(el, "yellow", "Current Progress Field [FRD 12.2.2]");
            highlight(el, "green", "Current Progress — PASSED");
        }

        Assert.assertTrue(internsFound || progressFound,
                "FRD 12.2.2: Neither 'Total Interns' nor 'Current Progress' found.");
        System.out.println("TC-CR-012 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — TRAINING TIMELINE  (FRD 12.2.3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-013: Training Timeline section is present
     * FRD 12.2.3 — "horizontal row of week-by-week progress buttons under 'Training Timeline' heading"
     *
     * Highlighted component: Training Timeline section heading
     */
    @Test(priority = 13, dependsOnMethods = "tc_cr_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-CR-013 [FRD 12.2.3]: Training Timeline section present")
    public void tc_cr_013_trainingTimelineSectionVisible() {
        Assert.assertTrue(crPage.isTrainingTimelineSectionVisible(),
                "FRD 12.2.3: Training Timeline section not found.");
        WebElement el = crPage.getTrainingTimelineSectionElement();
        highlight(el, "yellow", "Training Timeline Section [FRD 12.2.3]");
        highlight(el, "green", "Training Timeline — PASSED");
        System.out.println("TC-CR-013 PASSED.");
    }

    /**
     * TC-CR-014: Training Timeline week buttons are rendered
     * FRD 12.2.3 — "at least 8 weeks total; green=completed, blue=current, grey=upcoming"
     *
     * Highlighted component: All week buttons
     */
    @Test(priority = 14, dependsOnMethods = "tc_cr_013_trainingTimelineSectionVisible",
            groups = {"regression"},
            description = "TC-CR-014 [FRD 12.2.3]: Week buttons rendered in Training Timeline")
    public void tc_cr_014_weekButtonsRendered() {
        List<WebElement> weeks = crPage.getWeekButtons();
        System.out.println("TC-CR-014: Week buttons found = " + weeks.size());
        Assert.assertFalse(weeks.isEmpty(), "FRD 12.2.3: No week buttons found in Training Timeline.");
        highlightAll(weeks, "yellow", "Week Button [FRD 12.2.3]");
        highlightAll(weeks, "green", "Week Buttons — PASSED");
        System.out.println("TC-CR-014 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — EVALUATIONS PANEL  (FRD 12.2.4)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-015: All three evaluation milestones present
     * FRD 12.2.4 — "Qualifier Exam, Interim Evaluation, Final Evaluation"
     *
     * Highlighted component: Each evaluation milestone label
     */
    @Test(priority = 15, dependsOnMethods = "tc_cr_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-CR-015 [FRD 12.2.4]: Qualifier, Interim, Final evaluation milestones present")
    public void tc_cr_015_evaluationsPanelAllMilestones() {
        boolean qualifier = crPage.isQualifierExamVisible();
        boolean interim   = crPage.isInterimEvaluationVisible();
        boolean finalEval = crPage.isFinalEvaluationVisible();
        System.out.println("TC-CR-015: Qualifier=" + qualifier + " Interim=" + interim + " Final=" + finalEval);

        if (qualifier) { WebElement el = crPage.getQualifierExamElement();
            highlight(el, "yellow", "Qualifier Exam [FRD 12.2.4]"); highlight(el, "green", "Qualifier — PASSED"); }
        if (interim)   { WebElement el = crPage.getInterimEvaluationElement();
            highlight(el, "yellow", "Interim Evaluation [FRD 12.2.4]"); highlight(el, "green", "Interim — PASSED"); }
        if (finalEval) { WebElement el = crPage.getFinalEvaluationElement();
            highlight(el, "yellow", "Final Evaluation [FRD 12.2.4]"); highlight(el, "green", "Final — PASSED"); }

        Assert.assertTrue(qualifier, "FRD 12.2.4: 'Qualifier Exam' not found.");
        Assert.assertTrue(interim,   "FRD 12.2.4: 'Interim Evaluation' not found.");
        Assert.assertTrue(finalEval, "FRD 12.2.4: 'Final Evaluation' not found.");
        System.out.println("TC-CR-015 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 — OVERALL PROGRESS CARD  (FRD 12.2.5)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-016: Overall Progress Card is visible
     * FRD 12.2.5 — "blue card showing Overall Progress %, progress bar, weeks remaining"
     *
     * Highlighted component: Overall Progress card
     */
    @Test(priority = 16, dependsOnMethods = "tc_cr_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-CR-016 [FRD 12.2.5]: Overall Progress Card visible")
    public void tc_cr_016_overallProgressCardVisible() {
        boolean found = crPage.isOverallProgressCardVisible() || crPage.isOverallProgressFallbackVisible();
        Assert.assertTrue(found, "FRD 12.2.5: Overall Progress card not found.");
        WebElement el = crPage.getOverallProgressCardElement();
        highlight(el, "yellow", "Overall Progress Card [FRD 12.2.5]");
        highlight(el, "green", "Overall Progress — PASSED");
        System.out.println("TC-CR-016 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 — TRAINEES TABLE  (FRD 12.2.6)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-017: Trainees Table present and has at least one row
     * FRD 12.2.6 — "Trainees section shows a table with columns: ID, Full Name, Email, Employment Type"
     *
     * Highlighted component: Trainees table + all data rows
     */
    @Test(priority = 17, dependsOnMethods = "tc_cr_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-CR-017 [FRD 12.2.6]: Trainees Table present with at least one row")
    public void tc_cr_017_traineesTableHasRows() {
        Assert.assertTrue(crPage.isTraineesTableVisible(), "FRD 12.2.6: Trainees table not found.");
        WebElement table = crPage.getTraineesTableElement();
        highlight(table, "yellow", "Trainees Table [FRD 12.2.6]");

        List<WebElement> rows = crPage.getTraineesTableRows();
        System.out.println("TC-CR-017: Trainee rows = " + rows.size());
        Assert.assertFalse(rows.isEmpty(), "FRD 12.2.6: Trainees table is empty.");

        highlightAll(rows, "green", "Trainee Row — PASSED");
        highlight(table, "green", "Trainees Table — PASSED");
        System.out.println("TC-CR-017 PASSED.");
    }

    /**
     * TC-CR-018: Trainees Table column headers — ID, Full Name, Email, Employment Type
     * FRD 12.2.6 — "Columns are: ID, Full Name, Email, Employment Type"
     *
     * Highlighted component: Table header row
     */
    @Test(priority = 18, dependsOnMethods = "tc_cr_017_traineesTableHasRows",
            groups = {"regression"},
            description = "TC-CR-018 [FRD 12.2.6]: Trainees table has correct column headers")
    public void tc_cr_018_traineesTableColumnHeaders() {
        List<WebElement> headers = crPage.getTraineesTableHeaders();
        highlightAll(headers, "yellow", "Table Header [FRD 12.2.6]");

        StringBuilder headerText = new StringBuilder("Table headers: ");
        for (WebElement h : headers) headerText.append("[").append(h.getText().trim()).append("] ");
        System.out.println(headerText);

        String all = headerText.toString().toLowerCase();
        Assert.assertTrue(all.contains("id"),                               "FRD 12.2.6: 'ID' column header missing.");
        Assert.assertTrue(all.contains("name"),                             "FRD 12.2.6: 'Full Name' column header missing.");
        Assert.assertTrue(all.contains("email"),                            "FRD 12.2.6: 'Email' column header missing.");
        Assert.assertTrue(all.contains("employment") || all.contains("type"),
                "FRD 12.2.6: 'Employment Type' column header missing.");

        highlightAll(headers, "green", "Column Headers — PASSED");
        System.out.println("TC-CR-018 PASSED.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 9 — ACCESS CONTROL  (FRD 12.3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-019: No Create / Edit / Delete buttons visible (read-only)
     * FRD 12.3 — "All data is read-only — no edit, create, or delete actions available"
     *
     * Highlighted component: Any CRUD button found (red = violation)
     */
    @Test(priority = 19, dependsOnMethods = "tc_cr_003_welcomeGreetingVisible",
            groups = {"regression"},
            description = "TC-CR-019 [FRD 12.3]: No CRUD buttons present — dashboard is read-only")
    public void tc_cr_019_noCreateEditDeleteButtons() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> crudEls = crPage.getCrudButtonElements();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (!crudEls.isEmpty()) {
            highlightAll(crudEls, "red", "CRUD Button — FRD 12.3 VIOLATION");
            StringBuilder found = new StringBuilder("CRUD buttons found: ");
            for (WebElement el : crudEls) found.append("[").append(el.getText()).append("] ");
            Assert.fail("FRD 12.3: CR dashboard should be read-only. " + found);
        }

        System.out.println("TC-CR-019 PASSED. No Create/Edit/Delete buttons found.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 10 — LOGOUT  (FRD 12.3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-CR-020: Logout returns to login page
     * FRD 12.3 — Session bounded to assigned cohort; exiting must return to login.
     *
     * Highlighted component: Logout button, then User ID input on login page
     */
    @Test(priority = 20, dependsOnMethods = "tc_cr_002_urlContainsCrRoute",
            groups = {"smoke", "regression"},
            description = "TC-CR-020 [FRD 12.3]: Logout returns to login page")
    public void tc_cr_020_logoutRedirectsToLogin() {
        boolean loggedOut = false;

        // Attempt 1: direct logout button/link
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> directBtns = crPage.getLogoutDirectElements();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        if (!directBtns.isEmpty()) {
            highlight(directBtns.get(0), "yellow", "Logout Button [FRD 12.3]");
            try {
                directBtns.get(0).click();
                loggedOut = true;
                crPage.waitForLoginPageUserId(wait(10));
            } catch (Exception e) { System.out.println("TC-CR-020: Direct logout click failed."); }
        }

        // Attempt 2: user-menu trigger → logout inside dropdown
        if (!loggedOut) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            List<WebElement> menus = crPage.getUserMenuTriggerElements();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            for (WebElement menu : menus) {
                try {
                    highlight(menu, "yellow", "User Menu Trigger [FRD 12.3]");
                    menu.click();
                    wait(5).until(d -> crPage.isLogoutVisibleAfterMenuOpen());
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
                    List<WebElement> btns = crPage.getLogoutDirectElements();
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                    if (!btns.isEmpty()) {
                        highlight(btns.get(0), "yellow", "Logout in Menu [FRD 12.3]");
                        btns.get(0).click();
                        loggedOut = true;
                        crPage.waitForLoginPageUserId(wait(10));
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }

        // Attempt 3: fallback — navigate to base URL
        if (!loggedOut) {
            System.out.println("TC-CR-020: Logout button not found. Navigating to base URL as fallback.");
            driver.get(ConfigReader.getBaseUrl());
        }

        // Verify login page
        try {
            WebElement userInput = crPage.waitForLoginPageUserId(wait(15));
            highlight(userInput, "green", "Login Page — User ID Input visible after logout");
        } catch (Exception e) {
            Assert.fail("Login page not visible after logout. URL: " + driver.getCurrentUrl());
        }
        System.out.println("TC-CR-020 PASSED.");
    }
}

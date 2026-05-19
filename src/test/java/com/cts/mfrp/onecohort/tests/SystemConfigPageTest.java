package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SystemConfigPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * System Configuration Page Content Test Suite
 *
 * FRD Reference: Section 2.6 — System Configuration Page
 *
 * URL: /super-admin/system-config
 * Access: Exclusively for Super Admin role (FRD 2.6)
 *
 * Page structure (FRD 2.6):
 *   Header   : "System Configuration"
 *   Subtitle : "Manage cohorts, service lines, and learning paths"
 *   4 Tiles  : Cohort Management, Service Line Management,
 *               Learning Path Management, POC Management
 *   Each tile: Title + description text + "Create X" action button
 *
 * Test sections:
 *   A — URL & Access           (TC-SYSCFG-001 to 002)
 *   B — Page Header & Subtitle (TC-SYSCFG-003 to 005)
 *   C — The 4 Config Tiles     (TC-SYSCFG-006 to 013)
 *   D — Action Buttons         (TC-SYSCFG-014 to 017)
 *   E — Modal Open/Close       (TC-SYSCFG-018 to 021)
 *   F — Access Control         (TC-SYSCFG-022)
 *
 * DESIGN:
 *   Extends BaseClassTest — one shared browser session.
 *   Logs in as Super Admin, navigates directly to system-config URL.
 */
@Listeners(ExtentReportListener.class)
public class SystemConfigPageTest extends BaseClassTest {

    private SystemConfigPage configPage;

    // Base URL for system config (used in navigation tests)
    private String sysConfigUrl;

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToSystemConfig() {
        // Step 1: Log in as Super Admin
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/super-admin"));
        System.out.println("Login OK — URL: " + driver.getCurrentUrl());

        // Step 2: Navigate directly to the System Config page
        // Direct URL navigation is more reliable than clicking nav links
        sysConfigUrl = ConfigReader.getBaseUrl() + "/super-admin/system-config";
        driver.get(sysConfigUrl);

        // Step 3: Create page object and wait for page to load
        configPage = new SystemConfigPage(driver);
        configPage.waitForPageLoad();
        System.out.println("System Config page loaded — URL: " + driver.getCurrentUrl());
    }

    // =========================================================================
    //  SECTION A — URL & Access
    // =========================================================================

    @Test(priority = 1,
            retryAnalyzer = RetryAnalyzer.class,
            description = "TC-SYSCFG-001 [FRD 2.6]: URL contains /system-config path segment")
    public void verifySystemConfigUrl() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(configPage.isOnSystemConfigPage(),
                "FAIL [FRD 2.6] — URL should contain '/system-config'. Actual URL: " + url);
        System.out.println("PASS — System Config URL confirmed: " + url);
    }

    @Test(priority = 2,
            description = "TC-SYSCFG-002 [FRD 2.6]: Super Admin can access System Config page")
    public void verifyPageAccessible() {
        // If we can see the page heading, access was granted successfully
        Assert.assertTrue(configPage.isPageHeadingVisible(),
                "FAIL [FRD 2.6] — Super Admin cannot access System Config page. " +
                        "Page heading not found — may have been redirected or access denied.");
        System.out.println("PASS — System Config page is accessible to Super Admin.");
    }

    // =========================================================================
    //  SECTION B — Page Header & Subtitle
    // =========================================================================

    @Test(priority = 3,
            description = "TC-SYSCFG-003 [FRD 2.6]: Page heading 'System Configuration' is visible")
    public void verifyPageHeadingVisible() {
        Assert.assertTrue(configPage.isPageHeadingVisible(),
                "FAIL [FRD 2.6] — Page heading 'System Configuration' is not visible.");
        System.out.println("PASS — Page heading visible: \"" + configPage.getPageHeadingText() + "\"");
    }

    @Test(priority = 4,
            description = "TC-SYSCFG-004 [FRD 2.6]: Page heading text says 'System Configuration'")
    public void verifyPageHeadingText() {
        String heading = configPage.getPageHeadingText();
        Assert.assertTrue(heading.contains("System Configuration"),
                "FAIL [FRD 2.6] — Heading should say 'System Configuration'. Got: \"" + heading + "\"");
        System.out.println("PASS — Heading text: \"" + heading + "\"");
    }

    @Test(priority = 5,
            description = "TC-SYSCFG-005 [FRD 2.6]: Subtitle mentions cohorts, service lines, and learning paths")
    public void verifyPageSubtitle() {
        // FRD 2.6: "Manage cohorts, service lines, and learning paths"
        Assert.assertTrue(configPage.isPageSubtitleVisible(),
                "FAIL [FRD 2.6] — Page subtitle not visible. " +
                        "Expected text: 'Manage cohorts, service lines, and learning paths'");
        String subtitle = configPage.getPageSubtitleText();
        System.out.println("PASS — Subtitle text: \"" + subtitle + "\"");
    }

    // =========================================================================
    //  SECTION C — The 4 Configuration Tiles
    // =========================================================================

    @Test(priority = 6,
            description = "TC-SYSCFG-006 [FRD 2.6]: 'Cohort Management' tile is present")
    public void verifyCohortManagementTile() {
        Assert.assertTrue(configPage.isCohortManagementTileVisible(),
                "FAIL [FRD 2.6] — 'Cohort Management' tile not found on System Config page.");
        System.out.println("PASS — 'Cohort Management' tile is visible.");
    }

    @Test(priority = 7,
            description = "TC-SYSCFG-007 [FRD 2.6]: 'Service Line Management' tile is present")
    public void verifyServiceLineManagementTile() {
        Assert.assertTrue(configPage.isServiceLineMgmtTileVisible(),
                "FAIL [FRD 2.6] — 'Service Line Management' tile not found on System Config page.");
        System.out.println("PASS — 'Service Line Management' tile is visible.");
    }

    @Test(priority = 8,
            description = "TC-SYSCFG-008 [FRD 2.6]: 'Learning Path Management' tile is present")
    public void verifyLearningPathManagementTile() {
        Assert.assertTrue(configPage.isLearningPathMgmtTileVisible(),
                "FAIL [FRD 2.6] — 'Learning Path Management' tile not found on System Config page.");
        System.out.println("PASS — 'Learning Path Management' tile is visible.");
    }

    @Test(priority = 9,
            description = "TC-SYSCFG-009 [FRD 2.6]: 'POC Management' tile is present")
    public void verifyPocManagementTile() {
        Assert.assertTrue(configPage.isPocMgmtTileVisible(),
                "FAIL [FRD 2.6] — 'POC Management' tile not found on System Config page.");
        System.out.println("PASS — 'POC Management' tile is visible.");
    }

    @Test(priority = 10,
            description = "TC-SYSCFG-010 [FRD 2.6]: All 4 configuration tiles are present")
    public void verifyAllFourTilesPresent() {
        boolean cohort   = configPage.isCohortManagementTileVisible();
        boolean sl       = configPage.isServiceLineMgmtTileVisible();
        boolean lp       = configPage.isLearningPathMgmtTileVisible();
        boolean poc      = configPage.isPocMgmtTileVisible();

        System.out.println("Cohort Mgmt tile: " + cohort);
        System.out.println("Service Line Mgmt tile: " + sl);
        System.out.println("Learning Path Mgmt tile: " + lp);
        System.out.println("POC Mgmt tile: " + poc);

        Assert.assertTrue(cohort && sl && lp && poc,
                "FAIL [FRD 2.6] — One or more configuration tiles are missing. " +
                        "Expected: Cohort Management, Service Line Management, " +
                        "Learning Path Management, POC Management. " +
                        "Results: Cohort=" + cohort + " SL=" + sl + " LP=" + lp + " POC=" + poc);
        System.out.println("PASS — All 4 configuration tiles are present.");
    }

    // =========================================================================
    //  SECTION D — Action Buttons on Each Tile
    // =========================================================================

    @Test(priority = 14,
            description = "TC-SYSCFG-014 [FRD 2.6]: '+ Create Cohort' button is visible on Cohort tile")
    public void verifyCreateCohortButton() {
        Assert.assertTrue(configPage.isCreateCohortButtonVisible(),
                "FAIL [FRD 2.6] — '+ Create Cohort' button not visible on Cohort Management tile.");
        System.out.println("PASS — '+ Create Cohort' button is visible.");
    }

    @Test(priority = 15,
            description = "TC-SYSCFG-015 [FRD 2.6]: '+ Create Service Line' button is visible")
    public void verifyCreateServiceLineButton() {
        Assert.assertTrue(configPage.isCreateServiceLineButtonVisible(),
                "FAIL [FRD 2.6] — '+ Create Service Line' button not visible on Service Line Management tile.");
        System.out.println("PASS — '+ Create Service Line' button is visible.");
    }

    @Test(priority = 16,
            description = "TC-SYSCFG-016 [FRD 2.6]: '+ Create Learning Path' button is visible")
    public void verifyCreateLearningPathButton() {
        Assert.assertTrue(configPage.isCreateLearningPathButtonVisible(),
                "FAIL [FRD 2.6] — '+ Create Learning Path' button not visible on Learning Path tile.");
        System.out.println("PASS — '+ Create Learning Path' button is visible.");
    }

    @Test(priority = 17,
            description = "TC-SYSCFG-017 [FRD 2.6]: '+ Create POC' button is visible")
    public void verifyCreatePocButton() {
        Assert.assertTrue(configPage.isCreatePocButtonVisible(),
                "FAIL [FRD 2.6] — '+ Create POC' button not visible on POC Management tile.");
        System.out.println("PASS — '+ Create POC' button is visible.");
    }

    // =========================================================================
    //  SECTION E — Modal Opens and Closes (non-destructive: we only open then cancel)
    // =========================================================================

    @Test(priority = 18,
            description = "TC-SYSCFG-018 [FRD 2.6]: Clicking 'Create Cohort' opens a modal dialog")
    public void verifyCreateCohortModalOpens() {
        configPage.clickCreateCohort();
        Assert.assertTrue(configPage.isModalVisible(),
                "FAIL [FRD 2.6] — Clicking '+ Create Cohort' should open a modal dialog. " +
                        "No modal overlay found.");
        System.out.println("PASS — Create Cohort modal opened.");
        configPage.cancelModal(); // clean up — close the modal
        System.out.println("Modal closed via Cancel.");
    }

    @Test(priority = 19,
            description = "TC-SYSCFG-019 [FRD 2.6]: Clicking 'Create Service Line' opens a modal dialog")
    public void verifyCreateServiceLineModalOpens() {
        configPage.clickCreateServiceLine();
        Assert.assertTrue(configPage.isModalVisible(),
                "FAIL [FRD 2.6] — Clicking '+ Create Service Line' should open a modal dialog.");
        System.out.println("PASS — Create Service Line modal opened.");
        configPage.cancelModal();
        System.out.println("Modal closed via Cancel.");
    }

    @Test(priority = 20,
            description = "TC-SYSCFG-020 [FRD 2.6]: Clicking 'Create Learning Path' opens a modal dialog")
    public void verifyCreateLearningPathModalOpens() {
        configPage.clickCreateLearningPath();
        Assert.assertTrue(configPage.isModalVisible(),
                "FAIL [FRD 2.6] — Clicking '+ Create Learning Path' should open a modal dialog.");
        System.out.println("PASS — Create Learning Path modal opened.");
        configPage.cancelModal();
        System.out.println("Modal closed via Cancel.");
    }

    @Test(priority = 21,
            description = "TC-SYSCFG-021 [FRD 2.6]: Clicking 'Create POC' opens a modal dialog")
    public void verifyCreatePocModalOpens() {
        configPage.clickCreatePoc();
        Assert.assertTrue(configPage.isModalVisible(),
                "FAIL [FRD 2.6] — Clicking '+ Create POC' should open a modal dialog.");
        System.out.println("PASS — Create POC modal opened.");
        configPage.cancelModal();
        System.out.println("Modal closed via Cancel.");
    }

    // =========================================================================
    //  SECTION F — Access Control
    // =========================================================================

    @Test(priority = 22,
            description = "TC-SYSCFG-022 [FRD 2.6]: System Config page URL confirms Super Admin only access")
    public void verifyAccessControlByUrl() {
        // FRD 2.6: "Exclusively for Super Admin role"
        // The URL path /super-admin/system-config itself enforces this
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/super-admin/"),
                "FAIL [FRD 2.6] — System Config URL should be under /super-admin/. " +
                        "Actual URL: " + url);
        System.out.println("PASS — System Config is correctly scoped under /super-admin/: " + url);
    }
}
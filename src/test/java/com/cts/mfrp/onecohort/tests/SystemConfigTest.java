package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.SystemConfigPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * System Configuration Page Test Suite
 *
 * Tests covered:
 *   1.  System Config page loads at the correct URL (/system-config)
 *   2.  Page heading is visible and non-empty
 *   3.  Page subtitle is visible and non-empty
 *   4.  Exactly 4 configuration category cards are present
 *   5.  All 4 Create buttons are visible (one per config category)
 *   6.  Create Cohort button opens a modal dialog
 *   7.  Create Service Line button opens a modal dialog
 *   8.  Create Learning Path button opens a modal dialog
 *   9.  Create POC button opens a modal dialog
 *  10.  Cancel button on a modal closes it
 *  11.  All 4 config cards are individually visible (Cohort, Service Line, Learning Path, POC)
 *
 * Login: Super Admin → navigate to System Config via sidebar.
 * All tests share one browser session.
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"regression", "superadmin", "systemconfig"})
public class SystemConfigTest extends BaseClassTest {

    private SystemConfigPage systemConfigPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToSystemConfig() {
        // Log in as Super Admin
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        // Click "System Config" in the Super Admin sidebar
        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("System Config").click();

        // Wait for the System Config page to fully load
        wait.until(ExpectedConditions.urlContains("system-config"));
        systemConfigPage = new SystemConfigPage(driver);
        systemConfigPage.waitForPageLoad();

        System.out.println("Setup complete. System Config URL: " + driver.getCurrentUrl());
    }

    // ── TC-SC-001 ─────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-SC-001: System Config page loads at the correct URL")
    public void testSystemConfigPageLoads() {
        // Verify the URL confirms we are on the System Config page
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("system-config"),
                "URL should contain 'system-config'. Got: " + url);

        Assert.assertTrue(systemConfigPage.isOnSystemConfigPage(),
                "The page object should confirm we are on the System Config page");
        System.out.println("PASS - System Config page loaded at URL: " + url);
    }

    // ── TC-SC-002 ─────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-SC-002: System Config page heading is visible and non-empty")
    public void testSystemConfigHeadingVisible() {
        // The page heading should clearly label this as the System Configuration page
        Assert.assertTrue(systemConfigPage.isPageHeadingVisible(),
                "Page heading should be visible on the System Config page");

        String headingText = systemConfigPage.getPageHeadingText();
        Assert.assertFalse(headingText.trim().isEmpty(),
                "Page heading should have non-empty text");
        System.out.println("PASS - System Config page heading: " + headingText);
    }

    // ── TC-SC-003 ─────────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-SC-003: System Config page subtitle is visible and non-empty")
    public void testSystemConfigSubtitleVisible() {
        // The subtitle provides a short description of what can be configured on this page
        Assert.assertTrue(systemConfigPage.isPageSubtitleVisible(),
                "Page subtitle should be visible on the System Config page");

        String subtitleText = systemConfigPage.getPageSubtitleText();
        Assert.assertFalse(subtitleText.trim().isEmpty(),
                "Page subtitle should have non-empty text");
        System.out.println("PASS - System Config subtitle: " + subtitleText);
    }

    // ── TC-SC-004 ─────────────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-SC-004: System Config page shows exactly 4 configuration category cards")
    public void testConfigCardCountIsExactlyFour() {
        // There should be exactly 4 cards: Cohort, Service Line, Learning Path, POC
        int cardCount = systemConfigPage.getConfigCardCount();
        Assert.assertEquals(cardCount, 4,
                "System Config page should show exactly 4 config category cards. Found: " + cardCount);
        System.out.println("PASS - System Config shows exactly 4 configuration cards.");
    }

    // ── TC-SC-005 ─────────────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-SC-005: All 4 Create buttons are visible on the System Config page")
    public void testAllFourCreateButtonsVisible() {
        // Each config card has a Create button — all 4 must be accessible
        Assert.assertTrue(systemConfigPage.isCreateCohortButtonVisible(),
                "Create Cohort button should be visible");
        Assert.assertTrue(systemConfigPage.isCreateServiceLineButtonVisible(),
                "Create Service Line button should be visible");
        Assert.assertTrue(systemConfigPage.isCreateLearningPathButtonVisible(),
                "Create Learning Path button should be visible");
        Assert.assertTrue(systemConfigPage.isCreatePocButtonVisible(),
                "Create POC button should be visible");
        System.out.println("PASS - All 4 Create buttons are visible on the System Config page.");
    }

    // ── TC-SC-006 ─────────────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-SC-006: Create Cohort button opens a modal dialog")
    public void testCreateCohortModalOpens() {
        // Clicking Create Cohort should open a form modal
        systemConfigPage.clickCreateCohort();
        Assert.assertTrue(systemConfigPage.isModalVisible(),
                "A modal dialog should appear after clicking Create Cohort");
        System.out.println("PASS - Create Cohort modal opened successfully.");

        // Close the modal to restore a clean state for the next test
        systemConfigPage.cancelModal();
    }

    // ── TC-SC-007 ─────────────────────────────────────────────────────────────
    @Test(priority = 7, description = "TC-SC-007: Create Service Line button opens a modal dialog")
    public void testCreateServiceLineModalOpens() {
        // Clicking Create Service Line should open a form modal
        systemConfigPage.clickCreateServiceLine();
        Assert.assertTrue(systemConfigPage.isModalVisible(),
                "A modal dialog should appear after clicking Create Service Line");
        System.out.println("PASS - Create Service Line modal opened successfully.");

        systemConfigPage.cancelModal();
    }

    // ── TC-SC-008 ─────────────────────────────────────────────────────────────
    @Test(priority = 8, description = "TC-SC-008: Create Learning Path button opens a modal dialog")
    public void testCreateLearningPathModalOpens() {
        // Clicking Create Learning Path should open a form modal
        systemConfigPage.clickCreateLearningPath();
        Assert.assertTrue(systemConfigPage.isModalVisible(),
                "A modal dialog should appear after clicking Create Learning Path");
        System.out.println("PASS - Create Learning Path modal opened successfully.");

        systemConfigPage.cancelModal();
    }

    // ── TC-SC-009 ─────────────────────────────────────────────────────────────
    @Test(priority = 9, description = "TC-SC-009: Create POC button opens a modal dialog")
    public void testCreatePocModalOpens() {
        // Clicking Create POC should open a form modal
        systemConfigPage.clickCreatePoc();
        Assert.assertTrue(systemConfigPage.isModalVisible(),
                "A modal dialog should appear after clicking Create POC");
        System.out.println("PASS - Create POC modal opened successfully.");

        systemConfigPage.cancelModal();
    }

    // ── TC-SC-010 ─────────────────────────────────────────────────────────────
    @Test(priority = 10, description = "TC-SC-010: Cancel button closes the modal without saving")
    public void testCancelButtonClosesModal() {
        // Open a modal (using Create Cohort as the example)
        systemConfigPage.clickCreateCohort();
        Assert.assertTrue(systemConfigPage.isModalVisible(),
                "Modal should be open before testing Cancel");

        // Click Cancel — the modal should close without any data being saved
        systemConfigPage.cancelModal();

        // Wait for the modal to disappear
        wait.until(d -> !systemConfigPage.isModalVisible());

        Assert.assertFalse(systemConfigPage.isModalVisible(),
                "Modal should be closed after clicking Cancel");
        System.out.println("PASS - Modal closed correctly after clicking Cancel.");
    }

    // ── TC-SC-011 ─────────────────────────────────────────────────────────────
    @Test(priority = 11, description = "TC-SC-011: All 4 config category cards are individually visible")
    public void testAllConfigCardsVisible() {
        // Verify each config category card is visible on the page
        Assert.assertTrue(systemConfigPage.isCohortCardVisible(),
                "Cohort Management config card should be visible");
        Assert.assertTrue(systemConfigPage.isServiceLineCardVisible(),
                "Service Line Management config card should be visible");
        Assert.assertTrue(systemConfigPage.isLearningPathCardVisible(),
                "Learning Path Management config card should be visible");
        Assert.assertTrue(systemConfigPage.isPocCardVisible(),
                "POC Management config card should be visible");
        System.out.println("PASS - All 4 configuration category cards are individually visible.");
    }
}

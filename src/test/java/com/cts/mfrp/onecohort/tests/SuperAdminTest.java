package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.SystemConfigPage;
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

/**
 * Super Admin Test Suite
 *
 * Tests covered:
 *   1. Dashboard loads with KPI cards after login
 *   2. Left navigation menu has all required links
 *   3. Navigate to Cohort Management via sidebar
 *   4. System Configuration page shows 4 config cards
 *   5. System Config Create button opens a modal
 *   6. Logout redirects to the login page
 *
 * All tests run in a single browser session (one login for all).
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "superadmin"})
public class SuperAdminTest extends BaseClassTest {

    private SuperAdminDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsSuperAdmin() {
        // Navigate to the app and log in as Super Admin
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        // Wait until the URL confirms we reached the super-admin dashboard
        wait.until(ExpectedConditions.urlContains("/super-admin"));
        dashPage = new SuperAdminDashboardPage(driver);
        System.out.println("Logged in as Super Admin. URL: " + driver.getCurrentUrl());
    }

    // ── TC-SA-001 ─────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-SA-001: Super Admin dashboard loads with KPI cards")
    public void testDashboardLoadsWithKpiCards() {
        // Verify the URL is correct
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/super-admin"),
                "URL should contain /super-admin but was: " + url);

        // Verify the 4 KPI summary cards are visible on the dashboard
        WebElement totalCohorts = dashPage.getTotalCohortsCardElement();
        WebElement active       = dashPage.getActiveCardElement();
        WebElement completed    = dashPage.getCompletedCardElement();
        WebElement upcoming     = dashPage.getUpcomingCardElement();

        Assert.assertTrue(totalCohorts.isDisplayed(), "Total Cohorts card should be visible");
        Assert.assertTrue(active.isDisplayed(),       "Active card should be visible");
        Assert.assertTrue(completed.isDisplayed(),    "Completed card should be visible");
        Assert.assertTrue(upcoming.isDisplayed(),     "Upcoming card should be visible");

        System.out.println("PASS - Dashboard loaded with all 4 KPI cards.");
    }

    // ── TC-SA-002 ─────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-SA-002: Sidebar has all required navigation links")
    public void testLeftNavHasAllMenuItems() {
        // The Super Admin sidebar must have these 5 navigation items
        String[] expectedMenuItems = {
            "Dashboard",
            "Cohort Management",
            "Managers",
            "Batch Owners",
            "System Config"
        };

        for (String menuItem : expectedMenuItems) {
            boolean found = dashPage.isMenuItemVisible(menuItem);
            Assert.assertTrue(found, "Sidebar should contain menu item: '" + menuItem + "'");
            System.out.println("PASS - Sidebar menu item found: " + menuItem);
        }
    }

    // ── TC-SA-003 ─────────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-SA-003: Clicking Cohort Management in sidebar navigates to the cohort page")
    public void testNavigateToCohortManagement() {
        // Click the Cohort Management link in the sidebar
        dashPage.getMenuItemElement("Cohort Management").click();

        // Wait for the URL to change to the cohort management page
        wait.until(ExpectedConditions.urlContains("cohort"));

        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("cohort"),
                "URL should contain 'cohort' after clicking Cohort Management. Got: " + url);
        System.out.println("PASS - Navigated to Cohort Management. URL: " + url);

        // Navigate back to dashboard for the next test
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    // ── TC-SA-004 ─────────────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-SA-004: System Configuration page shows 4 config cards")
    public void testSystemConfigPageHasFourCards() {
        // Navigate to System Configuration via sidebar
        dashPage.getMenuItemElement("System Config").click();
        wait.until(ExpectedConditions.urlContains("system-config"));

        SystemConfigPage systemConfigPage = new SystemConfigPage(driver);
        systemConfigPage.waitForPageLoad();

        // Verify all 4 configuration category cards are present
        Assert.assertTrue(systemConfigPage.isCohortCardVisible(),
                "Cohort Management config card should be visible");
        Assert.assertTrue(systemConfigPage.isServiceLineCardVisible(),
                "Service Line Management config card should be visible");
        Assert.assertTrue(systemConfigPage.isLearningPathCardVisible(),
                "Learning Path Management config card should be visible");
        Assert.assertTrue(systemConfigPage.isPocCardVisible(),
                "POC Management config card should be visible");

        System.out.println("PASS - All 4 System Configuration cards are visible.");
    }

    // ── TC-SA-005 ─────────────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-SA-005: Create Cohort button on System Config page opens a modal")
    public void testSystemConfigCreateCohortModalOpens() {
        // We are already on System Config page from the previous test
        // If not, navigate there first
        if (!driver.getCurrentUrl().contains("system-config")) {
            dashPage.getMenuItemElement("System Config").click();
            wait.until(ExpectedConditions.urlContains("system-config"));
        }

        SystemConfigPage systemConfigPage = new SystemConfigPage(driver);
        systemConfigPage.waitForPageLoad();

        // Click the Create Cohort button
        systemConfigPage.clickCreateCohort();

        // Verify the modal dialog appeared
        Assert.assertTrue(systemConfigPage.isModalVisible(),
                "A modal dialog should appear after clicking Create Cohort");
        System.out.println("PASS - Create Cohort modal opened successfully.");

        // Close the modal and go back to dashboard for the logout test
        systemConfigPage.cancelModal();
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    // ── TC-SA-007 ─────────────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-SA-007: Super Admin dashboard shows the correct role badge text")
    public void testSuperAdminBadgeText() {
        // Verify the Super Admin badge text is non-empty (e.g. "Super Admin" or "SU")
        String badgeText = dashPage.getSuperUserBadgeText();
        Assert.assertFalse(badgeText.isEmpty(),
                "Super Admin badge text should not be empty. Got: '" + badgeText + "'");
        System.out.println("PASS - Super Admin badge text: " + badgeText);
    }

    // ── TC-SA-008 ─────────────────────────────────────────────────────────────
    @Test(priority = 7, description = "TC-SA-008: Dashboard KPI cards all show non-empty number values")
    public void testDashboardKpiNumbersNonEmpty() {
        // Each KPI card should show a number (even "0" is valid, but not blank)
        List<WebElement> kpiNumbers = dashPage.getKpiNumberElements();
        Assert.assertFalse(kpiNumbers.isEmpty(),
                "Dashboard should have at least one KPI number element");

        for (WebElement num : kpiNumbers) {
            String text = num.getText().trim();
            Assert.assertFalse(text.isEmpty(),
                    "Each KPI number element should have a visible value. Found blank text.");
        }
        System.out.println("PASS - All " + kpiNumbers.size() + " KPI number(s) have visible values.");
    }

    // ── TC-SA-009 ─────────────────────────────────────────────────────────────
    @Test(priority = 8, description = "TC-SA-009: Clicking 'Batch Owners' in sidebar navigates away from the dashboard")
    public void testNavigateToBatchOwners() {
        String urlBefore = driver.getCurrentUrl();

        // Click the Batch Owners menu item in the Super Admin sidebar
        dashPage.getMenuItemElement("Batch Owners").click();

        // Wait for the URL to change (navigation happened)
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlBefore)));

        String urlAfter = driver.getCurrentUrl();
        Assert.assertNotEquals(urlAfter, urlBefore,
                "Clicking 'Batch Owners' should navigate away from the dashboard. URL before: " + urlBefore);
        System.out.println("PASS - Navigated to Batch Owners page. URL: " + urlAfter);

        // Navigate back to the dashboard for the next test
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    // ── TC-SA-010 ─────────────────────────────────────────────────────────────
    @Test(priority = 9, description = "TC-SA-010: Clicking 'Managers' in sidebar navigates to the Managers page")
    public void testNavigateToManagers() {
        String urlBefore = driver.getCurrentUrl();

        // Click the Managers menu item in the Super Admin sidebar
        dashPage.getMenuItemElement("Managers").click();

        // Wait for the URL to contain "manager"
        wait.until(ExpectedConditions.urlContains("manager"));

        String urlAfter = driver.getCurrentUrl();
        Assert.assertTrue(urlAfter.contains("manager"),
                "URL should contain 'manager' after clicking Managers. Got: " + urlAfter);
        System.out.println("PASS - Navigated to Managers page. URL: " + urlAfter);

        // Navigate back to the dashboard for the logout test
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    // ── TC-SA-006 ─────────────────────────────────────────────────────────────
    @Test(priority = 10, description = "TC-SA-006: Logout redirects the user back to the login page")
    public void testLogoutRedirectsToLoginPage() {
        // Click the user avatar to open the user menu
        dashPage.clickAvatar();

        // Wait for the Logout option to appear and click it
        WebElement logoutOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[routerlink='/login'], a[href='/login']")));
        logoutOption.click();

        // Wait until the User ID input appears — this confirms we are on the login page
        // (We check for the input field directly rather than relying on URL patterns,
        //  because different environments may have different base URL formats)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));

        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isUserIdInputVisible(),
                "After logout, the login page User ID field should be visible");
        System.out.println("PASS - Logout successful. Login page is visible.");
    }
}

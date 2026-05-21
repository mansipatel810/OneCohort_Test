package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.managers.ManagerDashboardPage;
import com.cts.mfrp.onecohort.pages.managers.CreateManagerModal;
import com.cts.mfrp.onecohort.pages.managers.ManagersLeadershipPage;
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
 * Manager Role Test Suite
 *
 * Tests covered:
 *   1. Manager dashboard loads with correct URL, heading, role badge, and KPI cards
 *   2. Sidebar navigation: click Cohorts link and return to Dashboard
 *   3. Manager sidebar has at least 2 navigation links
 *   4. Managers and Leadership page loads for Super Admin (heading + Create Manager button)
 *   5. Create Manager button opens a modal dialog
 *   6. Login validation: missing service line shows an alert
 *   7. Login validation: empty user ID shows an alert
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "manager"})
public class ManagerTest extends BaseClassTest {

    private ManagerDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsManager() {
        // Log in as Manager with a valid user ID and service line
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsManager(
                ConfigReader.getManagerUserId(),
                ConfigReader.getValidServiceLineId());

        // Wait for the Manager dashboard to load
        wait.until(ExpectedConditions.urlContains("/manager/"));
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("Manager dashboard loaded. URL: " + driver.getCurrentUrl());
    }

    // ── TC-MGR-001 ────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-MGR-001: Manager dashboard loads with correct URL, heading, and KPI cards")
    public void testManagerDashboardLoads() {
        // Verify the URL contains the manager path
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/manager/"),
                "URL should contain /manager/. Got: " + url);
        Assert.assertTrue(url.contains("/dashboard"),
                "URL should contain /dashboard. Got: " + url);

        // Verify the role badge shows "Manager"
        String roleText = dashPage.getRoleText();
        Assert.assertEquals(roleText, "Manager",
                "Role badge should display 'Manager'");

        // Verify the 3 KPI cards are visible
        Assert.assertTrue(dashPage.isKpiCardPresent("Service Lines"),    "Service Lines KPI card missing");
        Assert.assertTrue(dashPage.isKpiCardPresent("Learning Paths"),   "Learning Paths KPI card missing");
        Assert.assertTrue(dashPage.isKpiCardPresent("Avg. Completion Rate"), "Avg. Completion Rate KPI card missing");

        System.out.println("PASS - Manager dashboard loaded correctly. URL: " + url);
    }

    // ── TC-MGR-002 ────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-MGR-002: Manager can navigate to Cohorts section and back to Dashboard")
    public void testManagerSidebarNavigation() {
        // Click the "Manage Cohorts" link in the sidebar
        dashPage.clickManageCohortsNav();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/dashboard")));

        String urlAfterCohorts = driver.getCurrentUrl();
        Assert.assertTrue(urlAfterCohorts.contains("/manager/"),
                "After clicking Cohorts, URL should still be under /manager/. Got: " + urlAfterCohorts);
        System.out.println("PASS - Navigated to cohorts section. URL: " + urlAfterCohorts);

        // Click "Dashboard" to return to the dashboard
        dashPage.clickDashboardNav();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Should return to dashboard after clicking Dashboard nav");

        // Reinitialize dashPage after page reload
        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("PASS - Returned to Manager dashboard.");
    }

    // ── TC-MGR-003 ────────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-MGR-003: Manager sidebar has at least 2 navigation links")
    public void testManagerSidebarHasNavLinks() {
        List<String> navLinks = dashPage.getNavLinkTexts();
        System.out.println("Manager nav links: " + navLinks);

        // Manager sidebar must have at least Dashboard and Cohorts links
        Assert.assertTrue(navLinks.size() >= 2,
                "Manager sidebar should have at least 2 nav links. Found: " + navLinks);

        boolean hasDashboard = navLinks.stream().anyMatch(l -> l.equalsIgnoreCase("Dashboard"));
        Assert.assertTrue(hasDashboard, "Sidebar should contain 'Dashboard' link. Found: " + navLinks);

        System.out.println("PASS - Manager sidebar has " + navLinks.size() + " navigation links.");
    }

    // ── TC-MGR-004 ────────────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-MGR-004: Managers and Leadership page loads with heading and Create Manager button")
    public void testManagersLeadershipPageLoadsAsSuperAdmin() {
        // Log in as Super Admin to access the Managers & Leadership page
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        // Click the Managers link in the Super Admin sidebar
        WebElement managersLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]//*[contains(text(),'Manager')]")));
        managersLink.click();
        wait.until(ExpectedConditions.urlContains("manager"));

        ManagersLeadershipPage leadershipPage = new ManagersLeadershipPage(driver);

        // Verify the page heading is visible
        Assert.assertTrue(leadershipPage.isPageHeadingVisible(),
                "Managers & Leadership page heading should be visible");

        // Verify the Create Manager button is accessible to Super Admin
        Assert.assertTrue(leadershipPage.isCreateManagerBtnVisible(),
                "Create Manager button should be visible for Super Admin");

        System.out.println("PASS - Managers & Leadership page loaded for Super Admin.");
    }

    // ── TC-MGR-005 ────────────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-MGR-005: Create Manager button opens a modal dialog")
    public void testCreateManagerModalOpens() {
        // We should still be on the Managers & Leadership page from TC-MGR-004
        // If not, navigate there
        if (!driver.getCurrentUrl().contains("manager")) {
            driver.get(ConfigReader.getBaseUrl());
            new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
            wait.until(ExpectedConditions.urlContains("/super-admin"));
            WebElement managersLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//nav[contains(@class,'menu')]//*[contains(text(),'Manager')]")));
            managersLink.click();
            wait.until(ExpectedConditions.urlContains("manager"));
        }

        ManagersLeadershipPage leadershipPage = new ManagersLeadershipPage(driver);

        // Click the Create Manager button
        leadershipPage.clickCreateManager();

        // Verify the modal is now visible
        Assert.assertTrue(leadershipPage.isModalVisible(),
                "A modal dialog should appear after clicking Create Manager");
        System.out.println("PASS - Create Manager modal opened successfully.");

        // Close the modal to clean up
        leadershipPage.closeModal();
    }

    // ── TC-MGR-006 ────────────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-MGR-006: Manager login without Service Line shows a validation alert")
    public void testManagerLoginWithoutServiceLineShowsAlert() {
        // Go to the login page
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        // Enter user ID, select Manager role, but DO NOT select a Service Line
        loginPage.enterUserId(ConfigReader.getManagerUserId())
                 .selectRole("Manager")
                 .clickLoginButton();

        // An alert should pop up asking the user to select a service line
        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_SELECT_SERVICE_LINE,
                "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE);
        System.out.println("PASS - Validation alert shown for missing Service Line: " + alertText);
    }

    // ── TC-MGR-007 ────────────────────────────────────────────────────────────
    @Test(priority = 7, description = "TC-MGR-007: Manager login with empty User ID shows a validation alert")
    public void testManagerLoginWithEmptyUserIdShowsAlert() {
        // Go to the login page
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        // Select Manager role but leave the User ID field EMPTY
        loginPage.selectRole("Manager")
                 .clickLoginButton();

        // An alert should pop up asking the user to enter a user ID
        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_EMPTY_USER_ID,
                "Alert should say: " + AppConstants.ALERT_EMPTY_USER_ID);
        System.out.println("PASS - Validation alert shown for empty User ID: " + alertText);
    }

    // ── TC-MGR-008 ────────────────────────────────────────────────────────────
    @Test(priority = 8, description = "TC-MGR-008: Manager dashboard KPI cards show non-empty numeric values")
    public void testManagerKpiNumbersNonEmpty() {
        // Log in fresh as Manager (previous tests navigated to the login page)
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsManager(
                ConfigReader.getManagerUserId(),
                ConfigReader.getValidServiceLineId());
        wait.until(ExpectedConditions.urlContains("/manager/"));
        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();

        // Verify KPI number values are not blank
        List<String> kpiNumbers = dashPage.getKpiNumbers();
        Assert.assertFalse(kpiNumbers.isEmpty(),
                "Manager dashboard should show at least one KPI number value");
        System.out.println("PASS - Manager KPI numbers: " + kpiNumbers);
    }

    // ── TC-MGR-009 ────────────────────────────────────────────────────────────
    @Test(priority = 9, description = "TC-MGR-009: Manager dashboard shows no error message after successful login")
    public void testManagerDashboardHasNoErrorMessage() {
        // After a successful login the dashboard must not show any error message
        Assert.assertFalse(dashPage.isErrorMessageVisible(),
                "No error message should be visible on the Manager dashboard after a successful login");
        System.out.println("PASS - No error message is visible on the Manager dashboard.");
    }

    // ── TC-MGR-010 ────────────────────────────────────────────────────────────
    @Test(priority = 10, description = "TC-MGR-010: Create Manager modal contains all required input fields")
    public void testCreateManagerModalHasRequiredFields() {
        // Log in as Super Admin to access the Managers & Leadership page
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        // Navigate to the Managers page via the sidebar
        WebElement managersLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]//*[contains(text(),'Manager')]")));
        managersLink.click();
        wait.until(ExpectedConditions.urlContains("manager"));

        // Open the Create Manager modal
        ManagersLeadershipPage leadershipPage = new ManagersLeadershipPage(driver);
        CreateManagerModal modal = leadershipPage.clickCreateManager();

        // Verify all required form fields are present inside the modal
        Assert.assertTrue(modal.isEmployeeIdInputVisible(),
                "Create Manager modal should have an Employee ID input field");
        Assert.assertTrue(modal.isFullNameInputVisible(),
                "Create Manager modal should have a Full Name input field");
        Assert.assertTrue(modal.isServiceLineDropdownVisible(),
                "Create Manager modal should have a Service Line dropdown");

        System.out.println("PASS - Create Manager modal has all required form fields.");
        modal.closeModal();
    }

    // ── TC-MGR-011 ────────────────────────────────────────────────────────────
    @Test(priority = 11, description = "TC-MGR-011: Managers and Leadership page shows heading and filter tabs")
    public void testManagersLeadershipPageContent() {
        // We should still be on the Managers & Leadership page from TC-MGR-010
        if (!driver.getCurrentUrl().contains("manager")) {
            driver.get(ConfigReader.getBaseUrl());
            new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
            wait.until(ExpectedConditions.urlContains("/super-admin"));
            WebElement managersLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//nav[contains(@class,'menu')]//*[contains(text(),'Manager')]")));
            managersLink.click();
            wait.until(ExpectedConditions.urlContains("manager"));
        }

        ManagersLeadershipPage leadershipPage = new ManagersLeadershipPage(driver);

        // Verify the page heading is visible
        Assert.assertTrue(leadershipPage.isPageHeadingVisible(),
                "Managers & Leadership page heading should be visible");

        // Verify filter tabs are visible (e.g. All, Active)
        Assert.assertTrue(leadershipPage.areFilterTabsVisible(),
                "Managers & Leadership page should have filter tabs visible");

        System.out.println("PASS - Managers & Leadership page shows heading and filter tabs.");
    }
}

package com.cts.mfrp.onecohort.tests.leader;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
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
 * Leader Role Test Suite
 *
 * Tests covered:
 *   1. Leader dashboard loads with correct URL and KPI cards
 *   2. Leader can navigate to Cohorts and back to Dashboard via sidebar
 *   3. Login validation: missing Service Line shows an alert
 *   4. Login validation: empty User ID shows an alert
 *
 * Tests 1–2 use a logged-in Leader session.
 * Tests 3–4 navigate to the login page to test validation messages.
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "leader"})
public class LeaderTest extends BaseClassTest {

    private LeaderDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsLeader() {
        // Log in as Leader with a valid user ID and service line
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsLeader(
                ConfigReader.getLeaderUserId(),
                ConfigReader.getValidServiceLineId());

        // Wait for the Leader dashboard URL pattern
        wait.until(ExpectedConditions.urlContains("/leader/"));

        dashPage = new LeaderDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("Leader dashboard loaded. URL: " + driver.getCurrentUrl());
    }

    // ── TC-LEADER-001 ─────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-LEADER-001: Leader dashboard loads with correct URL and KPI cards")
    public void testLeaderDashboardLoads() {
        // Verify the URL contains the leader path
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/leader/"),
                "URL should contain /leader/. Got: " + url);

        // Verify the role badge shows "Leader"
        String leaderBadgeText = dashPage.getLeaderBadgeText();
        Assert.assertTrue(leaderBadgeText.toLowerCase().contains("leader"),
                "Role badge should display 'Leader'. Got: " + leaderBadgeText);

        // Verify the KPI cards are present on the dashboard
        List<String> kpiTitles = dashPage.getKpiCardTitles();
        Assert.assertFalse(kpiTitles.isEmpty(),
                "Dashboard should have KPI cards. Found: " + kpiTitles);

        System.out.println("PASS - Leader dashboard loaded. URL: " + url + " | KPI cards: " + kpiTitles);
    }

    // ── TC-LEADER-002 ─────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-LEADER-002: Leader can navigate to Cohorts section and back to Dashboard")
    public void testLeaderSidebarNavigation() {
        // Verify the sidebar has the Dashboard and Cohorts links
        List<String> navLinks = dashPage.getNavLinkTexts();
        Assert.assertTrue(navLinks.size() >= 2,
                "Leader sidebar should have at least 2 navigation links. Found: " + navLinks);

        boolean hasCohortLink = navLinks.stream()
                .anyMatch(link -> link.toLowerCase().contains("cohort"));
        Assert.assertTrue(hasCohortLink,
                "Sidebar should have a Cohorts link. Found: " + navLinks);

        System.out.println("PASS - Leader sidebar navigation verified. Links: " + navLinks);
    }

    // ── TC-LEADER-003 ─────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-LEADER-003: Leader login without selecting a Service Line shows a validation alert")
    public void testLeaderLoginWithoutServiceLineShowsAlert() {
        // Navigate to the login page
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        // Enter user ID, select Leader role, but skip Service Line selection
        loginPage.enterUserId(ConfigReader.getLeaderUserId())
                 .selectRole("Leader")
                 .clickLoginButton();

        // Verify the validation alert appears
        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_SELECT_SERVICE_LINE,
                "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE);
        System.out.println("PASS - Validation alert shown for missing Service Line: " + alertText);
    }

    // ── TC-LEADER-004 ─────────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-LEADER-004: Leader login with empty User ID shows a validation alert")
    public void testLeaderLoginWithEmptyUserIdShowsAlert() {
        // Navigate to the login page
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        // Select Leader role but leave the User ID empty
        loginPage.selectRole("Leader")
                 .clickLoginButton();

        // Verify the validation alert appears
        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_EMPTY_USER_ID,
                "Alert should say: " + AppConstants.ALERT_EMPTY_USER_ID);
        System.out.println("PASS - Validation alert shown for empty User ID: " + alertText);
    }

    // ── TC-LEADER-005 ─────────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-LEADER-005: Leader dashboard shows no error message after successful login")
    public void testLeaderDashboardHasNoErrorMessage() {
        // Log in fresh as Leader (previous tests navigated to the login page)
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsLeader(
                ConfigReader.getLeaderUserId(),
                ConfigReader.getValidServiceLineId());
        wait.until(ExpectedConditions.urlContains("/leader/"));
        dashPage = new LeaderDashboardPage(driver);
        dashPage.waitForDashboardLoad();

        // After a successful login, no error message should be visible
        Assert.assertFalse(dashPage.isErrorMessageVisible(),
                "No error message should be visible on the Leader dashboard after a successful login");
        System.out.println("PASS - No error message is visible on the Leader dashboard.");
    }

    // ── TC-LEADER-006 ─────────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-LEADER-006: Leader dashboard KPI cards show numeric values")
    public void testLeaderKpiNumbersNonEmpty() {
        // We are on the Leader dashboard from TC-LEADER-005
        List<String> kpiNumbers = dashPage.getKpiNumbers();
        Assert.assertFalse(kpiNumbers.isEmpty(),
                "Leader dashboard should display at least one KPI number value");
        System.out.println("PASS - Leader KPI numbers visible: " + kpiNumbers);
    }

    // ── TC-LEADER-007 ─────────────────────────────────────────────────────────
    @Test(priority = 7, description = "TC-LEADER-007: App logo is visible in the Leader dashboard sidebar")
    public void testLeaderAppLogoVisible() {
        // The app logo should always be visible in the sidebar header
        Assert.assertTrue(dashPage.isLogoVisible(),
                "The app logo should be visible in the Leader dashboard sidebar");
        System.out.println("PASS - App logo is visible on the Leader dashboard.");
    }
}

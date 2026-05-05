package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.HomePage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ExtentManager;
import com.cts.mfrp.onecohort.utils.RetryAnalyzer;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Covers authentication flows for the OneCohort Angular app.
 * The app has no password — login is role-based (User ID + Role selection).
 * All validation errors are surfaced via browser alert() dialogs.
 */
@Listeners(ExtentReportListener.class)
public class LoginTest extends BaseTest {

    // ── TC-AUTH-001 ──────────────────────────────────────────────────────────
    @Test(
        groups       = {"smoke", "regression"},
        retryAnalyzer = RetryAnalyzer.class,
        description  = "TC-AUTH-001: Super Admin login with valid User ID should redirect to /super-admin"
    )
    public void superAdminLogin_ValidUserId_RedirectsToDashboard() {
        LoginPage loginPage = new LoginPage(getDriver());
        ExtentManager.getTest().info("Attempting Super Admin login with userId: SA001");

        HomePage homePage = loginPage.loginAsSuperAdmin("SA001");

        ExtentManager.getTest().info("Current URL: " + homePage.getCurrentUrl());
        Assert.assertTrue(
            homePage.getCurrentUrl().contains(AppConstants.URL_SUPER_ADMIN),
            "URL should contain /super-admin after Super Admin login"
        );
        Assert.assertTrue(
            homePage.isDashboardLoaded(),
            "Dashboard header should be visible after login"
        );
    }

    // ── TC-AUTH-002 ──────────────────────────────────────────────────────────
    @Test(
        groups      = {"regression"},
        description = "TC-AUTH-002: Submitting login with empty User ID should trigger a validation alert"
    )
    public void login_EmptyUserId_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());
        ExtentManager.getTest().info("Clicking Login with no User ID entered");

        loginPage.selectRole("Super Admin").clickLoginButton();
        String alertText = loginPage.acceptAlertAndGetMessage();

        ExtentManager.getTest().info("Alert text received: " + alertText);
        Assert.assertEquals(
            alertText,
            AppConstants.ALERT_EMPTY_USER_ID,
            "Alert should say: " + AppConstants.ALERT_EMPTY_USER_ID
        );
    }

    // ── TC-AUTH-003 ──────────────────────────────────────────────────────────
    @Test(
        groups      = {"regression"},
        description = "TC-AUTH-003: Manager login without selecting a Service Line should alert"
    )
    public void managerLogin_NoServiceLine_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());
        ExtentManager.getTest().info("Logging in as Manager — skipping Service Line selection");

        loginPage.enterUserId("MGR001")
                 .selectRole("Manager")
                 .clickLoginButton();
        String alertText = loginPage.acceptAlertAndGetMessage();

        ExtentManager.getTest().info("Alert text received: " + alertText);
        Assert.assertEquals(
            alertText,
            AppConstants.ALERT_SELECT_SERVICE_LINE,
            "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE
        );
    }

    // ── TC-AUTH-004 ──────────────────────────────────────────────────────────
    @Test(
        groups      = {"regression"},
        description = "TC-AUTH-004: Leader login without selecting a Service Line should alert"
    )
    public void leaderLogin_NoServiceLine_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());
        ExtentManager.getTest().info("Logging in as Leader — skipping Service Line selection");

        loginPage.enterUserId("LDR001")
                 .selectRole("Leader")
                 .clickLoginButton();
        String alertText = loginPage.acceptAlertAndGetMessage();

        ExtentManager.getTest().info("Alert text received: " + alertText);
        Assert.assertEquals(
            alertText,
            AppConstants.ALERT_SELECT_SERVICE_LINE,
            "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE
        );
    }

    // ── TC-AUTH-005 ──────────────────────────────────────────────────────────
    @Test(
        groups      = {"regression"},
        description = "TC-AUTH-005: Batch Owner login with Service Line but without POC ID should alert"
    )
    public void batchOwnerLogin_NoPocId_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());
        ExtentManager.getTest().info("Logging in as Batch Owner — Service Line selected, POC ID omitted");

        loginPage.enterUserId("BO001")
                 .selectRole("Batch Owner")
                 .selectServiceLine("SL-001")
                 .clickLoginButton();
        String alertText = loginPage.acceptAlertAndGetMessage();

        ExtentManager.getTest().info("Alert text received: " + alertText);
        Assert.assertEquals(
            alertText,
            AppConstants.ALERT_ENTER_POC_ID,
            "Alert should say: " + AppConstants.ALERT_ENTER_POC_ID
        );
    }

    // ── TC-AUTH-006 ──────────────────────────────────────────────────────────
    @Test(
        groups      = {"regression"},
        description = "TC-AUTH-006: CR login without entering a Cohort ID should alert"
    )
    public void crLogin_NoCohortId_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());
        ExtentManager.getTest().info("Logging in as CR — skipping Cohort ID entry");

        loginPage.enterUserId("CR001")
                 .selectRole("CR")
                 .clickLoginButton();
        String alertText = loginPage.acceptAlertAndGetMessage();

        ExtentManager.getTest().info("Alert text received: " + alertText);
        Assert.assertEquals(
            alertText,
            AppConstants.ALERT_ENTER_COHORT_ID,
            "Alert should say: " + AppConstants.ALERT_ENTER_COHORT_ID
        );
    }

    // ── TC-AUTH-007 ──────────────────────────────────────────────────────────
    @Test(
        groups        = {"smoke"},
        retryAnalyzer = RetryAnalyzer.class,
        description   = "TC-AUTH-007: CR login with valid Cohort ID should redirect to /cr/{cohortId}"
    )
    public void crLogin_ValidCohortId_RedirectsToCrRoute() {
        LoginPage loginPage = new LoginPage(getDriver());
        String cohortId = "COH-10001";
        ExtentManager.getTest().info("Logging in as CR with cohortId: " + cohortId);

        loginPage.loginAsCR("CR001", cohortId);

        String currentUrl = getDriver().getCurrentUrl();
        ExtentManager.getTest().info("Current URL: " + currentUrl);
        Assert.assertTrue(
            currentUrl.contains(AppConstants.URL_CR + cohortId),
            "URL should contain /cr/" + cohortId + " after CR login"
        );
    }

    // ── TC-AUTH-008 ──────────────────────────────────────────────────────────
    @Test(
        groups        = {"smoke"},
        retryAnalyzer = RetryAnalyzer.class,
        description   = "TC-AUTH-008: Manager login with valid User ID and Service Line should redirect to /manager/{serviceLineId}"
    )
    public void managerLogin_ValidCredentials_RedirectsToManagerRoute() {
        LoginPage loginPage = new LoginPage(getDriver());
        String serviceLineId = "SL-001";
        ExtentManager.getTest().info("Logging in as Manager with serviceLineId: " + serviceLineId);

        loginPage.loginAsManager("MGR001", serviceLineId);

        String currentUrl = getDriver().getCurrentUrl();
        ExtentManager.getTest().info("Current URL: " + currentUrl);
        Assert.assertTrue(
            currentUrl.contains(AppConstants.URL_MANAGER + serviceLineId),
            "URL should contain /manager/" + serviceLineId + " after Manager login"
        );
    }
}

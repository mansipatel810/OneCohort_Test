package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.HomePage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "TC-AUTH-001: Super Admin login with valid User ID should redirect to dashboard")
    public void superAdminLogin_ValidUserId_RedirectsToDashboard() {
        LoginPage loginPage = new LoginPage(getDriver());

        HomePage homePage = loginPage.loginAsSuperAdmin("SA001");

        Assert.assertTrue(
                homePage.getCurrentUrl().contains(AppConstants.URL_SUPER_ADMIN),
                "URL should contain /super-admin after Super Admin login"
        );
        Assert.assertTrue(
                homePage.isDashboardLoaded(),
                "Dashboard should be visible after Super Admin login"
        );
    }

    @Test(description = "TC-AUTH-002: Login with empty User ID should show a validation alert")
    public void login_EmptyUserId_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.selectRole("Super Admin");
        loginPage.clickLoginButton();

        String alertMessage = loginPage.acceptAlertAndGetMessage();

        Assert.assertEquals(
                alertMessage,
                AppConstants.ALERT_EMPTY_USER_ID,
                "Alert should say: " + AppConstants.ALERT_EMPTY_USER_ID
        );
    }

    @Test(description = "TC-AUTH-003: Manager login without Service Line should show a validation alert")
    public void managerLogin_NoServiceLine_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.enterUserId("MGR001");
        loginPage.selectRole("Manager");
        loginPage.clickLoginButton();

        String alertMessage = loginPage.acceptAlertAndGetMessage();

        Assert.assertEquals(
                alertMessage,
                AppConstants.ALERT_SELECT_SERVICE_LINE,
                "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE
        );
    }

    @Test(description = "TC-AUTH-004: Leader login without Service Line should show a validation alert")
    public void leaderLogin_NoServiceLine_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.enterUserId("LDR001");
        loginPage.selectRole("Leader");
        loginPage.clickLoginButton();

        String alertMessage = loginPage.acceptAlertAndGetMessage();

        Assert.assertEquals(
                alertMessage,
                AppConstants.ALERT_SELECT_SERVICE_LINE,
                "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE
        );
    }

    @Test(description = "TC-AUTH-005: Batch Owner login without POC ID should show a validation alert")
    public void batchOwnerLogin_NoPocId_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.enterUserId("BO001");
        loginPage.selectRole("Batch Owner");
        loginPage.selectServiceLine("Duplicate Service Line (SRV-10001)");
        loginPage.clickLoginButton();

        String alertMessage = loginPage.acceptAlertAndGetMessage();

        Assert.assertEquals(
                alertMessage,
                AppConstants.ALERT_ENTER_POC_ID,
                "Alert should say: " + AppConstants.ALERT_ENTER_POC_ID
        );
    }

    @Test(description = "TC-AUTH-006: CR login without Cohort ID should show a validation alert")
    public void crLogin_NoCohortId_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.enterUserId("CR001");
        loginPage.selectRole("CR");
        loginPage.clickLoginButton();

        String alertMessage = loginPage.acceptAlertAndGetMessage();

        Assert.assertEquals(
                alertMessage,
                AppConstants.ALERT_ENTER_COHORT_ID,
                "Alert should say: " + AppConstants.ALERT_ENTER_COHORT_ID
        );
    }

    @Test(description = "TC-AUTH-007: CR login with valid Cohort ID should redirect to /cr/{cohortId}")
    public void crLogin_ValidCohortId_RedirectsToCrRoute() {
        LoginPage loginPage = new LoginPage(getDriver());
        String cohortId = "COH-10001";

        loginPage.loginAsCR("CR001", cohortId);

        String currentUrl = getDriver().getCurrentUrl();

        Assert.assertTrue(
                currentUrl.contains(AppConstants.URL_CR + cohortId),
                "URL should contain /cr/" + cohortId + " after CR login"
        );
    }

    @Test(description = "TC-AUTH-008: Manager login with valid credentials should redirect to /manager/{serviceLineId}")
    public void managerLogin_ValidCredentials_RedirectsToManagerRoute() {
        LoginPage loginPage = new LoginPage(getDriver());
        String serviceLineId = "Duplicate Service Line (SRV-10001)";

        loginPage.loginAsManager("MR001", serviceLineId);

        String currentUrl = getDriver().getCurrentUrl();

        Assert.assertTrue(
                currentUrl.contains(AppConstants.URL_MANAGER + "SRV-10001"),
                "URL should contain /manager/" + serviceLineId + " after Manager login"
        );
    }
}
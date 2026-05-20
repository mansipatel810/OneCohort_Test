package com.cts.mfrp.onecohort.tests.leader;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;

@Listeners(ExtentReportListener.class)
public class LeaderLoginNegativeTest extends BaseTest {

    private LoginPage loginPage;
    private WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        getDriver().get(ConfigReader.getBaseUrl());
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        loginPage = new LoginPage(getDriver());
        wait.until(ExpectedConditions.visibilityOf(loginPage.getUserIdInputElement()));
    }

    @Test(priority = 1, groups = {"negative", "regression"},
            description = "TC-NEG-LDR-001 [FRD 11.1]: Empty User ID")
    public void tc_neg_ldr_001_emptyUserId() {
        loginPage.getUserIdInputElement().clear();
        loginPage.selectRole("Leader");
        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        Assert.assertTrue(loginPage.isOnLoginPage());
        Assert.assertEquals(alert, AppConstants.ALERT_EMPTY_USER_ID);
    }

    @Test(priority = 2, groups = {"negative", "regression"},
            description = "TC-NEG-LDR-002 [FRD 11.1]: Leader role with no Service Line")
    public void tc_neg_ldr_002_noServiceLine() {
        loginPage.enterUserId(ConfigReader.getLeaderUserId());
        loginPage.selectRole("Leader");
        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        Assert.assertTrue(loginPage.isOnLoginPage());
        Assert.assertEquals(alert, AppConstants.ALERT_SELECT_SERVICE_LINE);
    }

    @Test(priority = 3, groups = {"negative", "regression"},
            description = "TC-NEG-LDR-003 [FRD 11.1]: Service Line dropdown hidden check")
    public void tc_neg_ldr_003_serviceLineHiddenBeforeRoleSelect() {
        boolean isVisible = loginPage.isServiceLineDropdownVisible();
        Assert.assertFalse(isVisible);
    }

    @Test(priority = 4, groups = {"negative", "regression"},
            description = "TC-NEG-LDR-004 [FRD 11.1]: All fields blank")
    public void tc_neg_ldr_004_allFieldsBlank() {
        loginPage.getUserIdInputElement().clear();
        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        Assert.assertTrue(loginPage.isOnLoginPage());
        Assert.assertNotNull(alert);
    }
}

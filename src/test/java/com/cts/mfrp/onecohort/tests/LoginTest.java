package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.DashboardPage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ExcelUtils;
import com.cts.mfrp.onecohort.utils.ExtentManager;
import com.cts.mfrp.onecohort.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;

import java.util.Map;

@Listeners(ExtentReportListener.class)
public class LoginTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return ExcelUtils.getTestData(AppConstants.TESTDATA_PATH, AppConstants.LOGIN_DATA_SHEET);
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Verify successful login with valid credentials")
    public void validLoginTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        DashboardPage dashboard = loginPage.loginAs("Admin", "admin123");

        ExtentManager.getTest().info("Verifying dashboard is loaded");
        Assert.assertTrue(dashboard.isDashboardLoaded(), "Dashboard should be visible after login");
    }

    @Test(groups = {"regression"},
          description = "Verify error message on invalid login")
    public void invalidLoginTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.enterUsername("wrongUser").enterPassword("wrongPass").clickLogin();

        ExtentManager.getTest().info("Verifying error message is displayed");
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be shown for invalid credentials");
    }

    @Test(groups = {"regression"}, dataProvider = "loginData",
          description = "Verify login with data-driven credentials from Excel")
    public void dataDriverLoginTest(Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");
        String expected = data.get("expected");

        ExtentManager.getTest().info("Testing login with username: " + username);

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.enterUsername(username).enterPassword(password).clickLogin();

        if ("pass".equalsIgnoreCase(expected)) {
            DashboardPage dashboard = new DashboardPage(getDriver());
            Assert.assertTrue(dashboard.isDashboardLoaded(), "Dashboard should load for valid user");
        } else {
            Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should show for invalid user");
        }
    }
}

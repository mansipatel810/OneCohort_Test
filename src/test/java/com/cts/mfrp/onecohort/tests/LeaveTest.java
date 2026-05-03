package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.pages.DashboardPage;
import com.cts.mfrp.onecohort.pages.LeavePage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentManager;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListener.class)
public class LeaveTest extends BaseTest {

    private LeavePage leavePage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(getDriver());
        DashboardPage dashboard = loginPage.loginAs(
                ConfigReader.getUsername(), ConfigReader.getPassword());
        leavePage = dashboard.navigateToLeave();
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Verify leave list page loads")
    public void leaveListLoadsTest() {
        ExtentManager.getTest().info("Verifying leave table is visible");
        Assert.assertTrue(leavePage.isLeaveTableVisible() || leavePage.isNoRecordsDisplayed(),
                "Leave page should show table or no-records message");
    }

    @Test(groups = {"regression"},
          description = "Search leave by date range shows results or no records")
    public void searchLeaveByDateRangeTest() {
        leavePage.enterFromDate("2024-01-01")
                 .enterToDate("2024-12-31")
                 .clickSearch();

        ExtentManager.getTest().info("Searched leave by date range");
        boolean hasResults = leavePage.isLeaveTableVisible() || leavePage.isNoRecordsDisplayed();
        Assert.assertTrue(hasResults, "Leave search should return table or no-records message");
    }
}

package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.pages.HomePage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentManager;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListener.class)
public class EmployeeTest extends BaseTest {

    private CohortManagementPage cohortManagementPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(getDriver());
        HomePage homePage = loginPage.loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        cohortManagementPage = homePage.navigateToCohortManagement();
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Verify Cohort Management grid loads after navigation")
    public void cohortManagementGridLoadsTest() {
        ExtentManager.getTest().info("Verifying Cohort Management grid is visible");
        Assert.assertTrue(cohortManagementPage.isGridLoaded(),
                "Cohort Management data grid should be visible");
    }

    @Test(groups = {"regression"},
          description = "Verify grid shows no-data state before filter is applied")
    public void cohortGridEmptyStateTest() {
        ExtentManager.getTest().info("Verifying initial empty state before filter");
        Assert.assertTrue(cohortManagementPage.isEmptyStateDisplayed(),
                "Grid should show 'No data found' before a filter is applied");
    }

    @Test(groups = {"regression"},
          description = "Verify keyword search filters cohort grid in real time")
    public void cohortKeywordSearchTest() {
        ExtentManager.getTest().info("Searching cohorts by keyword: QEA");
        cohortManagementPage.searchByKeyword("QEA");
        Assert.assertTrue(cohortManagementPage.isGridLoaded(),
                "Grid should display filtered results for keyword 'QEA'");
    }
}

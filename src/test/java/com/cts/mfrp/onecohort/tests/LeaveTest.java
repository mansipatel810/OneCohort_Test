package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.pages.HomePage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.analytics.TrainingProgressAnalyticsPage;
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

    private TrainingProgressAnalyticsPage analyticsPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(getDriver());
        HomePage homePage = loginPage.loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        analyticsPage = homePage.navigateToTrainingAnalytics();
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Verify Training Progress Analytics section loads on the dashboard")
    public void trainingAnalyticsSectionLoadsTest() {
        ExtentManager.getTest().info("Verifying analytics bar chart is visible");
        Assert.assertTrue(analyticsPage.isProgressChartVisible(),
                "Active Cohort Progress bar chart should be visible");
    }

    @Test(groups = {"regression"},
          description = "Verify individual cohort detail cards are rendered")
    public void cohortDetailCardsRenderTest() {
        ExtentManager.getTest().info("Verifying individual cohort detail cards are visible");
        Assert.assertTrue(analyticsPage.areCohortDetailCardsVisible(),
                "Each active cohort should have a detail card with progress info");
    }

    @Test(groups = {"regression"},
          description = "Verify On Track status indicator is present for active cohorts")
    public void onTrackIndicatorPresentTest() {
        ExtentManager.getTest().info("Checking On Track status indicator");
        Assert.assertTrue(analyticsPage.isOnTrackIndicatorVisible(),
                "'On Track' status indicator should be visible for active cohorts");
    }
}

package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LeftNavPage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test(groups = {"smoke", "regression", "navigation", "superadmin"})
@Listeners(ExtentReportListener.class)
public class LeftNavPanelTest extends BaseClassTest {

    private LeftNavPage leftNavPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigate() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("dashboard"));
        leftNavPage = new LeftNavPage(driver);
    }

    @Test(priority = 1)
    public void testDashboardNav() {
        leftNavPage.clickDashboard();
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"),
                "Dashboard page not loaded!");
    }

    @Test(priority = 2)
    public void testCohortManagementNav() {
        leftNavPage.clickCohortManagement();
        Assert.assertTrue(driver.getCurrentUrl().contains("cohort"),
                "Cohort Management page not loaded!");
    }

    @Test(priority = 3)
    public void testManagersLeadershipNav() {
        leftNavPage.clickManagersLeadership();
        Assert.assertTrue(driver.getCurrentUrl().contains("leadership"),
                "Managers & Leadership page not loaded!");
    }

    @Test(priority = 4)
    public void testBatchOwnersNav() {
        leftNavPage.clickBatchOwners();
        Assert.assertTrue(driver.getCurrentUrl().contains("batch"),
                "Batch Owners page not loaded!");
    }

    @Test(priority = 5)
    public void testTrainingProgressNav() {
        leftNavPage.clickTrainingProgress();
        Assert.assertTrue(driver.getCurrentUrl().contains("training"),
                "Training Progress page not loaded!");
    }

    @Test(priority = 6)
    public void testSystemConfigNav() {
        leftNavPage.clickSystemConfig();
        Assert.assertTrue(driver.getCurrentUrl().contains("system-config"),
                "System Configuration page not loaded!");
    }
}

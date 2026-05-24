package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class SuperAdminTest extends BaseClassTest {

    SuperAdminDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsSuperAdmin() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));
        dashPage = new SuperAdminDashboardPage(driver);
        dashPage.waitForDashboardToLoad();
    }

    @Test(priority = 1, description = "TC-SA-001: Dashboard loads with all KPI cards across all three sections")
    public void testDashboardLoadsWithKpiCards() {
        Assert.assertTrue(driver.getCurrentUrl().contains("/super-admin"),
                "URL should contain /super-admin");

        Assert.assertTrue(dashPage.getTotalCohortsCardElement().isDisplayed(),
                "Total Cohorts card should be visible");
        Assert.assertTrue(dashPage.getActiveCardElement().isDisplayed(),
                "Active card should be visible");
        Assert.assertTrue(dashPage.getCompletedCardElement().isDisplayed(),
                "Completed card should be visible");
        Assert.assertTrue(dashPage.getUpcomingCardElement().isDisplayed(),
                "Upcoming card should be visible");

        Assert.assertTrue(dashPage.getTotalInternsCardElement().isDisplayed(),
                "Total Interns card should be visible");
        Assert.assertTrue(dashPage.getInternsTrainingCardElement().isDisplayed(),
                "Interns In Training card should be visible");
        Assert.assertTrue(dashPage.getTrainersCardElement().isDisplayed(),
                "Trainers card should be visible");
        Assert.assertTrue(dashPage.getPocsCardElement().isDisplayed(),
                "POCs card should be visible");
        Assert.assertTrue(dashPage.getManagersCardElement().isDisplayed(),
                "Managers card should be visible");
        Assert.assertTrue(dashPage.getLeadersCardElement().isDisplayed(),
                "Leaders card should be visible");

        Assert.assertTrue(dashPage.getServiceLinesCardElement().isDisplayed(),
                "Service Lines card should be visible");
        Assert.assertTrue(dashPage.getLearningPathsCardElement().isDisplayed(),
                "Learning Paths card should be visible");
        Assert.assertTrue(dashPage.getAvgCompletionCardElement().isDisplayed(),
                "Avg. Completion Rate card should be visible");
    }

    @Test(priority = 2, description = "TC-SA-002: KPI card numbers are all non-empty")
    public void testKpiCardNumbersNonEmpty() {
        List<WebElement> kpiNums = dashPage.getKpiNumberElements();
        Assert.assertFalse(kpiNums.isEmpty(),
                "There should be at least one KPI number on the dashboard");
        for (WebElement num : kpiNums) {
            Assert.assertFalse(num.getText().trim().isEmpty(),
                    "Each KPI number should have a visible value");
        }
    }

    @Test(priority = 3, description = "TC-SA-003: Clicking Cohort Management navigates to /super-admin/cohorts")
    public void testNavigateToCohortManagement() {
        dashPage.clickNavCohorts();
        wait.until(ExpectedConditions.urlContains("/super-admin/cohorts"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/super-admin/cohorts"),
                "URL should contain /super-admin/cohorts");
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    @Test(priority = 4, description = "TC-SA-004: Clicking Managers and Leadership navigates to /super-admin/leadership")
    public void testNavigateToManagersLeadership() {
        dashPage.clickNavLeadership();
        wait.until(ExpectedConditions.urlContains("/super-admin/leadership"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/super-admin/leadership"),
                "URL should contain /super-admin/leadership");
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    @Test(priority = 5, description = "TC-SA-005: Clicking Batch Owners navigates to /super-admin/batch-owners and page loads")
    public void testNavigateToBatchOwners() {
        dashPage.clickNavBatchOwners();
        wait.until(ExpectedConditions.urlContains("/super-admin/batch-owners"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/super-admin/batch-owners"),
                "URL should contain /super-admin/batch-owners");
        Assert.assertTrue(
                !driver.findElements(By.xpath("//h1[contains(text(),'Batch Owners')]")).isEmpty(),
                "Batch Owners page title should be visible");
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    @Test(priority = 6, description = "TC-SA-006: Clicking Training Progress navigates to /super-admin/training-progress")
    public void testNavigateToTrainingProgress() {
        dashPage.clickNavTrainingProgress();
        wait.until(ExpectedConditions.urlContains("/super-admin/training-progress"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/super-admin/training-progress"),
                "URL should contain /super-admin/training-progress");
        Assert.assertTrue(
                !driver.findElements(By.xpath("//h2[contains(text(),'Active Cohort Progress')]")).isEmpty(),
                "Training Progress heading should be visible");
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    @Test(priority = 7, description = "TC-SA-007: Clicking System Configuration navigates to /super-admin/system-config and shows 4 config cards")
    public void testNavigateToSystemConfig() {
        dashPage.clickNavSystemConfig();
        wait.until(ExpectedConditions.urlContains("/super-admin/system-config"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/super-admin/system-config"),
                "URL should contain /super-admin/system-config");
        Assert.assertTrue(
                !driver.findElements(By.xpath("//h3[normalize-space()='Cohort Management']")).isEmpty(),
                "Cohort Management config card should be visible");
        Assert.assertTrue(
                !driver.findElements(By.xpath("//h3[normalize-space()='Service Line Management']")).isEmpty(),
                "Service Line Management config card should be visible");
        Assert.assertTrue(
                !driver.findElements(By.xpath("//h3[normalize-space()='Learning Path Management']")).isEmpty(),
                "Learning Path Management config card should be visible");
        Assert.assertTrue(
                !driver.findElements(By.xpath("//h3[normalize-space()='POC Management']")).isEmpty(),
                "POC Management config card should be visible");
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/super-admin"));
    }

    @Test(priority = 8, description = "TC-SA-008: Sidebar has all 6 required navigation links")
    public void testSidebarHasAllNavLinks() {
        Assert.assertTrue(dashPage.isMenuItemVisible("Dashboard"),
                "Dashboard link should be visible");
        Assert.assertTrue(dashPage.isMenuItemVisible("Cohort Management"),
                "Cohort Management link should be visible");
        Assert.assertTrue(dashPage.isMenuItemVisible("Managers"),
                "Managers & Leadership link should be visible");
        Assert.assertTrue(dashPage.isMenuItemVisible("Batch Owners"),
                "Batch Owners link should be visible");
        Assert.assertTrue(dashPage.isMenuItemVisible("Training Progress"),
                "Training Progress link should be visible");
        Assert.assertTrue(dashPage.isMenuItemVisible("System Configuration"),
                "System Configuration link should be visible");
    }

    @Test(priority = 9, description = "TC-SA-009: Super User badge text is not empty")
    public void testSuperUserBadgeText() {
        String badgeText = dashPage.getSuperUserBadgeText().trim();
        Assert.assertFalse(badgeText.isEmpty(),
                "Super User badge text should not be empty");
    }

    @Test(priority = 10, description = "TC-SA-010: Logout redirects back to the login page")
    public void testLogoutRedirectsToLoginPage() {
        dashPage.clickAvatar();
        WebElement logoutOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/login' or @routerlink='/login']")));
        logoutOption.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='e.g. 123456']")));
        Assert.assertTrue(
                !driver.findElements(By.xpath("//input[@placeholder='e.g. 123456']")).isEmpty(),
                "Login page User ID field should be visible after logout");
    }
}
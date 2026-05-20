package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SystemConfigPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListener.class)
public class SystemConfigPageTest extends BaseClassTest {

    private SystemConfigPage configPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigate() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("super-admin"));
        driver.get("https://one-cohort-1.onrender.com/super-admin/system-config");
        configPage = new SystemConfigPage(driver);
        configPage.waitForPageLoad();

        System.out.println("System Config page loaded. URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 1,
            description = "TC-SYSCFG-001 [FRD 2.6]: URL contains /system-config")
    public void verifySystemConfigUrl() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(configPage.isOnSystemConfigPage(),
                "FAIL - URL should contain '/system-config'. Got: " + url);
        System.out.println("PASS - URL: " + url);
    }

    @Test(priority = 2,
            description = "TC-SYSCFG-002 [FRD 2.6]: URL is under /super-admin/ (Super Admin only access)")
    public void verifyAccessControlByUrl() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/super-admin/"),
                "FAIL - System Config should be under /super-admin/. Got: " + url);
        System.out.println("PASS - Access confirmed under /super-admin/: " + url);
    }

    @Test(priority = 3,
            description = "TC-SYSCFG-003 [FRD 2.6]: Page heading 'System Configuration' is visible")
    public void verifyPageHeadingVisible() {
        Assert.assertTrue(configPage.isPageHeadingVisible(),
                "FAIL - Page heading is not visible [FRD 2.6]");
        System.out.println("PASS - Page heading visible: " + configPage.getPageHeadingText());
    }

    @Test(priority = 4,
            description = "TC-SYSCFG-004 [FRD 2.6]: Page heading text reads 'System Configuration'")
    public void verifyPageHeadingText() {
        String heading = configPage.getPageHeadingText();
        Assert.assertEquals(heading, "System Configuration",
                "FAIL - Heading text mismatch. Got: " + heading);
        System.out.println("PASS - Heading text: " + heading);
    }

    @Test(priority = 5,
            description = "TC-SYSCFG-005 [FRD 2.6]: Subtitle reads 'Manage cohorts, service lines, and learning paths'")
    public void verifyPageSubtitleText() {
        Assert.assertTrue(configPage.isPageSubtitleVisible(),
                "FAIL - Page subtitle is not visible [FRD 2.6]");
        String subtitle = configPage.getPageSubtitleText();
        Assert.assertEquals(subtitle, "Manage cohorts, service lines, and learning paths",
                "FAIL - Subtitle text mismatch. Got: " + subtitle);
        System.out.println("PASS - Subtitle: " + subtitle);
    }

    @Test(priority = 6,
            description = "TC-SYSCFG-006 [FRD 2.6]: Exactly 4 configuration cards are present")
    public void verifyFourConfigCardsPresent() {
        int count = configPage.getConfigCardCount();
        Assert.assertEquals(count, 4,
                "FAIL - Expected 4 config cards but found: " + count + " [FRD 2.6]");
        System.out.println("PASS - " + count + " config cards found.");
    }

    @Test(priority = 7,
            description = "TC-SYSCFG-007 [FRD 2.6]: 'Cohort Management' card is visible")
    public void verifyCohortManagementCard() {
        Assert.assertTrue(configPage.isCohortCardVisible(),
                "FAIL - 'Cohort Management' card not found [FRD 2.6]");
        System.out.println("PASS - 'Cohort Management' card is visible.");
    }

    @Test(priority = 8,
            description = "TC-SYSCFG-008 [FRD 2.6]: 'Service Line Management' card is visible")
    public void verifyServiceLineManagementCard() {
        Assert.assertTrue(configPage.isServiceLineCardVisible(),
                "FAIL - 'Service Line Management' card not found [FRD 2.6]");
        System.out.println("PASS - 'Service Line Management' card is visible.");
    }

    @Test(priority = 9,
            description = "TC-SYSCFG-009 [FRD 2.6]: 'Learning Path Management' card is visible")
    public void verifyLearningPathManagementCard() {
        Assert.assertTrue(configPage.isLearningPathCardVisible(),
                "FAIL - 'Learning Path Management' card not found [FRD 2.6]");
        System.out.println("PASS - 'Learning Path Management' card is visible.");
    }

    @Test(priority = 10,
            description = "TC-SYSCFG-010 [FRD 2.6]: 'POC Management' card is visible")
    public void verifyPocManagementCard() {
        Assert.assertTrue(configPage.isPocCardVisible(),
                "FAIL - 'POC Management' card not found [FRD 2.6]");
        System.out.println("PASS - 'POC Management' card is visible.");
    }

    @Test(priority = 11,
            description = "TC-SYSCFG-011 [FRD 2.6]: All 4 cards are present together")
    public void verifyAllFourCardsPresent() {
        boolean cohort  = configPage.isCohortCardVisible();
        boolean sl      = configPage.isServiceLineCardVisible();
        boolean lp      = configPage.isLearningPathCardVisible();
        boolean poc     = configPage.isPocCardVisible();

        Assert.assertTrue(cohort && sl && lp && poc,
                "FAIL - One or more cards missing. "
                        + "Cohort=" + cohort + " ServiceLine=" + sl
                        + " LearningPath=" + lp + " POC=" + poc + " [FRD 2.6]");
        System.out.println("PASS - All 4 config cards are present.");
    }

    @Test(priority = 12,
            description = "TC-SYSCFG-012 [FRD 2.6]: Exactly 4 '+ Create' buttons are present")
    public void verifyFourCreateButtons() {
        int count = configPage.getCreateButtonCount();
        Assert.assertEquals(count, 4,
                "FAIL - Expected 4 create buttons but found: " + count + " [FRD 2.6]");
        System.out.println("PASS - " + count + " create buttons found.");
    }

    @Test(priority = 13,
            description = "TC-SYSCFG-013 [FRD 2.6]: '+ Create Cohort' button is visible")
    public void verifyCreateCohortButton() {
        Assert.assertTrue(configPage.isCreateCohortButtonVisible(),
                "FAIL - '+ Create Cohort' button not visible [FRD 2.6]");
        System.out.println("PASS - '+ Create Cohort' button is visible.");
    }

    @Test(priority = 14,
            description = "TC-SYSCFG-014 [FRD 2.6]: '+ Create Service Line' button is visible")
    public void verifyCreateServiceLineButton() {
        Assert.assertTrue(configPage.isCreateServiceLineButtonVisible(),
                "FAIL - '+ Create Service Line' button not visible [FRD 2.6]");
        System.out.println("PASS - '+ Create Service Line' button is visible.");
    }

    @Test(priority = 15,
            description = "TC-SYSCFG-015 [FRD 2.6]: '+ Create Learning Path' button is visible")
    public void verifyCreateLearningPathButton() {
        Assert.assertTrue(configPage.isCreateLearningPathButtonVisible(),
                "FAIL - '+ Create Learning Path' button not visible [FRD 2.6]");
        System.out.println("PASS - '+ Create Learning Path' button is visible.");
    }

    @Test(priority = 16,
            description = "TC-SYSCFG-016 [FRD 2.6]: '+ Create POC' button is visible")
    public void verifyCreatePocButton() {
        Assert.assertTrue(configPage.isCreatePocButtonVisible(),
                "FAIL - '+ Create POC' button not visible [FRD 2.6]");
        System.out.println("PASS - '+ Create POC' button is visible.");
    }

    @Test(priority = 17,
            description = "TC-SYSCFG-017 [FRD 2.6]: Clicking '+ Create Cohort' opens a modal")
    public void verifyCreateCohortModalOpens() {
        configPage.clickCreateCohort();
        Assert.assertTrue(configPage.isModalVisible(),
                "FAIL - Clicking '+ Create Cohort' did not open a modal [FRD 2.6]");
        System.out.println("PASS - Create Cohort modal opened.");
        configPage.cancelModal();
        System.out.println("Modal closed via Cancel.");
    }

    @Test(priority = 18,
            description = "TC-SYSCFG-018 [FRD 2.6]: Clicking '+ Create Service Line' opens a modal")
    public void verifyCreateServiceLineModalOpens() {
        configPage.clickCreateServiceLine();
        Assert.assertTrue(configPage.isModalVisible(),
                "FAIL - Clicking '+ Create Service Line' did not open a modal [FRD 2.6]");
        System.out.println("PASS - Create Service Line modal opened.");
        configPage.cancelModal();
        System.out.println("Modal closed via Cancel.");
    }

    @Test(priority = 19,
            description = "TC-SYSCFG-019 [FRD 2.6]: Clicking '+ Create Learning Path' opens a modal")
    public void verifyCreateLearningPathModalOpens() {
        configPage.clickCreateLearningPath();
        Assert.assertTrue(configPage.isModalVisible(),
                "FAIL - Clicking '+ Create Learning Path' did not open a modal [FRD 2.6]");
        System.out.println("PASS - Create Learning Path modal opened.");
        configPage.cancelModal();
        System.out.println("Modal closed via Cancel.");
    }

    @Test(priority = 20,
            description = "TC-SYSCFG-020 [FRD 2.6]: Clicking '+ Create POC' opens a modal")
    public void verifyCreatePocModalOpens() {
        configPage.clickCreatePoc();
        Assert.assertTrue(configPage.isModalVisible(),
                "FAIL - Clicking '+ Create POC' did not open a modal [FRD 2.6]");
        System.out.println("PASS - Create POC modal opened.");
        configPage.cancelModal();
        System.out.println("Modal closed via Cancel.");
    }
}
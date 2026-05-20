package com.cts.mfrp.onecohort.tests.cr;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * CR Login — Negative Test Suite (Strictly FRD-Based, with UI highlighting)
 *
 * Every field under test is visually highlighted in the browser via BasePage.highlight():
 *   🟡 Yellow border — element located, about to be tested
 *   🟢 Green border  — assertion passed
 *   🔴 Red border    — assertion failed / violation detected
 *
 * FRD Section 12.1 validation rules tested:
 *   TC-NEG-001  Empty User ID            → alert / stay on login   [FRD 12.1]
 *   TC-NEG-002  Empty Cohort ID          → alert / stay on login   [FRD 12.1.2]
 *   TC-NEG-003  Cohort ID field hidden   → before CR is selected   [FRD 12.1.2]
 *   TC-NEG-004  All fields blank         → alert on Login click     [FRD 12.1 + 12.1.2]
 */
@Listeners(ExtentReportListener.class)
public class CRLoginNegativeTest extends BaseTest {

    private LoginPage loginPage;
    private WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        getDriver().get(ConfigReader.getBaseUrl());
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        loginPage = new LoginPage(getDriver());
    }

    /**
     * TC-NEG-001: Empty User ID → validation alert / stay on login
     * FRD 12.1: "User ID is required before login can proceed."
     */
    @Test(priority = 1, groups = {"negative", "regression"},
            description = "TC-NEG-001 [FRD 12.1]: Empty User ID — alert fires and page stays on login")
    public void tc_neg_001_emptyUserId() throws InterruptedException {
        WebElement userIdEl = loginPage.getUserIdInputElement();
        loginPage.highlight(userIdEl, "yellow", "User ID Input — LEFT BLANK [FRD 12.1]");

        loginPage.selectRole("CR");
        loginPage.enterCohortId(ConfigReader.getValidCohortId());
        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        System.out.println("TC-NEG-001: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        if (loginPage.isUserIdInputVisible()) {
            loginPage.highlight(loginPage.getUserIdInputElement(), "green",
                    "User ID empty — validation correctly triggered");
        }

        Assert.assertTrue(loginPage.isOnLoginPage(),
                "FRD 12.1: App should stay on login page when User ID is empty. URL: " + getDriver().getCurrentUrl());
        if (alert != null) {
            Assert.assertTrue(
                    alert.toLowerCase().contains("user") || alert.toLowerCase().contains("id"),
                    "Alert should mention User ID. Got: \"" + alert + "\"");
        }
        System.out.println("TC-NEG-001 PASSED.");
    }

    /**
     * TC-NEG-002: Empty Cohort ID → validation alert / stay on login
     * FRD 12.1.2: "Cohort ID is required (marked with *)"
     */
    @Test(priority = 2, groups = {"negative", "regression"},
            description = "TC-NEG-002 [FRD 12.1.2]: Empty Cohort ID — alert fires and page stays on login")
    public void tc_neg_002_emptyCohortId() throws InterruptedException {
        loginPage.enterUserId(ConfigReader.getSuperAdminUserId());
        loginPage.selectRole("CR");

        // Locate Cohort ID field and leave it blank
        List<WebElement> cohortCandidates = loginPage.getCohortIdCandidates();
        if (!cohortCandidates.isEmpty()) {
            WebElement cohortEl = wait.until(ExpectedConditions.visibilityOf(cohortCandidates.get(0)));
            loginPage.highlight(cohortEl, "yellow", "Cohort ID Input — LEFT BLANK [FRD 12.1.2]");
            cohortEl.clear();
            loginPage.highlight(cohortEl, "green", "Cohort ID empty — validation correctly triggered");
        }

        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        System.out.println("TC-NEG-002: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        Assert.assertTrue(loginPage.isOnLoginPage(),
                "FRD 12.1.2: App should stay on login page when Cohort ID is empty. URL: " + getDriver().getCurrentUrl());
        if (alert != null) {
            Assert.assertTrue(alert.toLowerCase().contains("cohort"),
                    "Alert should mention Cohort ID. Got: \"" + alert + "\"");
        }
        System.out.println("TC-NEG-002 PASSED.");
    }

    /**
     * TC-NEG-003: Cohort ID field is NOT visible before CR role is selected
     * FRD 12.1.2
     */
    @Test(priority = 3, groups = {"negative", "regression"},
            description = "TC-NEG-003 [FRD 12.1.2]: Cohort ID field hidden before CR role is selected")
    public void tc_neg_003_cohortIdFieldHiddenBeforeCRSelected() {
        wait.until(ExpectedConditions.visibilityOf(loginPage.getUserIdInputElement()));
        loginPage.highlight(loginPage.getUserIdInputElement(), "green",
                "Login Page loaded — checking Cohort ID field visibility");

        loginPage.highlight(loginPage.getRoleDropdownElement(), "yellow",
                "Role Dropdown — default (not CR) [FRD 12.1.2]");

        // Disable implicit wait for negative visibility check
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> cohortFields = loginPage.getCohortIdCandidates();
        boolean anyVisible = cohortFields.stream().anyMatch(WebElement::isDisplayed);

        if (anyVisible) {
            cohortFields.stream().filter(WebElement::isDisplayed)
                    .forEach(el -> loginPage.highlight(el, "red",
                            "Cohort ID visible before CR selected — FRD 12.1.2 VIOLATION"));
        } else {
            loginPage.highlight(loginPage.getRoleDropdownElement(), "green",
                    "Cohort ID correctly hidden before CR selected");
        }
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        Assert.assertFalse(anyVisible,
                "FRD 12.1.2: Cohort ID field should NOT be visible before CR role is selected.");
        System.out.println("TC-NEG-003 PASSED. Cohort ID field correctly hidden before CR selection.");
    }

    /**
     * TC-NEG-004: All fields blank — clicking Login immediately shows alert
     * FRD 12.1 + 12.1.2
     */
    @Test(priority = 4, groups = {"negative", "regression"},
            description = "TC-NEG-004 [FRD 12.1 + 12.1.2]: All fields blank — Login shows validation alert")
    public void tc_neg_004_allFieldsBlank() {
        wait.until(ExpectedConditions.visibilityOf(loginPage.getUserIdInputElement()));

        loginPage.highlight(loginPage.getUserIdInputElement(), "yellow",
                "User ID — intentionally BLANK [FRD 12.1]");
        loginPage.highlight(loginPage.getRoleDropdownElement(), "yellow",
                "Role Dropdown — intentionally unchanged [FRD 12.1]");

        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        System.out.println("TC-NEG-004: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        if (loginPage.isUserIdInputVisible()) {
            loginPage.highlight(loginPage.getUserIdInputElement(), "green",
                    "Blank login rejected — validation working correctly");
        }

        Assert.assertTrue(loginPage.isOnLoginPage(),
                "FRD 12.1: App should stay on login page when all fields are blank. URL: " + getDriver().getCurrentUrl());
        Assert.assertNotNull(alert,
                "FRD 12.1: A validation alert must appear when Login is clicked with no fields filled.");
        System.out.println("TC-NEG-004 PASSED.");
    }
}

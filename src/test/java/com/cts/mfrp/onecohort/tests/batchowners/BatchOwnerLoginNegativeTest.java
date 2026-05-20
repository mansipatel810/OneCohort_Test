package com.cts.mfrp.onecohort.tests.batchowners;

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
 * Batch Owner Login — Negative Test Suite (Strictly FRD-Based, with UI highlighting)
 *
 * FRD 13.2.1 — "User ID is required"
 * FRD 13.2.2 — "Service Line is required; POC ID is required; both appear after Batch Owner selected"
 *
 * Highlighting:
 *   🟡 Yellow — element located, being tested
 *   🟢 Green  — assertion passed
 *   🔴 Red    — violation / field visible when it shouldn't be
 */
@Listeners(ExtentReportListener.class)
public class BatchOwnerLoginNegativeTest extends BaseTest {

    private LoginPage loginPage;
    private WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        getDriver().get(ConfigReader.getBaseUrl());
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        loginPage = new LoginPage(getDriver());
    }

    /**
     * TC-NEG-BO-001: Empty User ID → validation alert / stay on login
     * FRD 13.2.1 — "User ID is required"
     */
    @Test(priority = 1, groups = {"negative", "regression"},
            description = "TC-NEG-BO-001 [FRD 13.2.1]: Empty User ID — alert fires and page stays on login")
    public void tc_neg_bo_001_emptyUserId() {
        WebElement userIdEl = loginPage.getUserIdInputElement();
        loginPage.highlight(userIdEl, "yellow", "User ID Input — LEFT BLANK [FRD 13.2.1]");

        loginPage.selectRole("Batch Owner");
        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        System.out.println("TC-NEG-BO-001: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        if (loginPage.isUserIdInputVisible()) {
            loginPage.highlight(loginPage.getUserIdInputElement(), "green",
                    "User ID empty — validation correctly triggered");
        }

        Assert.assertTrue(loginPage.isOnLoginPage(),
                "FRD 13.2.1: App should stay on login when User ID is empty. URL: " + getDriver().getCurrentUrl());
        if (alert != null) {
            Assert.assertTrue(
                    alert.toLowerCase().contains("user") || alert.toLowerCase().contains("id"),
                    "Alert should mention User ID. Got: \"" + alert + "\"");
        }
        System.out.println("TC-NEG-BO-001 PASSED.");
    }

    /**
     * TC-NEG-BO-002: Empty POC ID → validation alert / stay on login
     * FRD 13.2.2 — "POC ID is required (marked with *)"
     */
    @Test(priority = 2, groups = {"negative", "regression"},
            description = "TC-NEG-BO-002 [FRD 13.2.2]: Empty POC ID — alert fires and page stays on login")
    public void tc_neg_bo_002_emptyPocId() throws InterruptedException {
        loginPage.enterUserId(ConfigReader.getSuperAdminUserId());
        loginPage.selectRole("Batch Owner");
        loginPage.selectServiceLine(ConfigReader.getValidServiceLineId());

        // Leave POC ID blank — locate it but type nothing
        List<WebElement> pocCandidates = loginPage.getPocIdCandidates();
        if (!pocCandidates.isEmpty()) {
            WebElement pocEl = wait.until(ExpectedConditions.visibilityOf(pocCandidates.get(0)));
            loginPage.highlight(pocEl, "yellow", "POC ID Input — LEFT BLANK [FRD 13.2.2]");
            pocEl.clear();
        }

        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        System.out.println("TC-NEG-BO-002: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        Assert.assertTrue(loginPage.isOnLoginPage(),
                "FRD 13.2.2: App should stay on login when POC ID is empty. URL: " + getDriver().getCurrentUrl());
        System.out.println("TC-NEG-BO-002 PASSED.");
    }

    /**
     * TC-NEG-BO-003: Service Line and POC ID fields hidden before Batch Owner role is selected
     * FRD 13.2.2
     */
    @Test(priority = 3, groups = {"negative", "regression"},
            description = "TC-NEG-BO-003 [FRD 13.2.2]: Service Line and POC ID fields hidden before Batch Owner selected")
    public void tc_neg_bo_003_fieldsHiddenBeforeBatchOwnerSelected() {
        wait.until(ExpectedConditions.visibilityOf(loginPage.getUserIdInputElement()));
        loginPage.highlight(loginPage.getUserIdInputElement(), "green",
                "Login page loaded — checking field visibility [FRD 13.2.2]");

        WebElement roleEl = loginPage.getRoleDropdownElement();
        loginPage.highlight(roleEl, "yellow", "Role Dropdown — default role (not Batch Owner) [FRD 13.2.2]");

        // Temporarily disable implicit wait for negative visibility check
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> allSelects = loginPage.getAllSelectElements();
        boolean slVisible = allSelects.stream()
                .filter(s -> !s.equals(roleEl))
                .anyMatch(WebElement::isDisplayed);

        List<WebElement> pocInputs = loginPage.getPocIdCandidates();
        boolean pocVisible = pocInputs.stream().anyMatch(WebElement::isDisplayed);
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (slVisible) {
            allSelects.stream().filter(s -> !s.equals(roleEl) && s.isDisplayed())
                    .forEach(s -> loginPage.highlight(s, "red",
                            "Service Line visible before Batch Owner — FRD 13.2.2 VIOLATION"));
        }
        if (pocVisible) {
            pocInputs.stream().filter(WebElement::isDisplayed)
                    .forEach(p -> loginPage.highlight(p, "red",
                            "POC ID visible before Batch Owner — FRD 13.2.2 VIOLATION"));
        }
        if (!slVisible && !pocVisible) {
            loginPage.highlight(roleEl, "green",
                    "Service Line & POC ID correctly hidden before Batch Owner selected");
        }

        Assert.assertFalse(slVisible,
                "FRD 13.2.2: Service Line dropdown should NOT be visible before Batch Owner is selected.");
        Assert.assertFalse(pocVisible,
                "FRD 13.2.2: POC ID field should NOT be visible before Batch Owner is selected.");
        System.out.println("TC-NEG-BO-003 PASSED. Both extra fields correctly hidden before role selection.");
    }

    /**
     * TC-NEG-BO-004: All fields blank → validation alert
     * FRD 13.2.1 + 13.2.2
     */
    @Test(priority = 4, groups = {"negative", "regression"},
            description = "TC-NEG-BO-004 [FRD 13.2.1 + 13.2.2]: All fields blank — Login shows validation alert")
    public void tc_neg_bo_004_allFieldsBlank() {
        wait.until(ExpectedConditions.visibilityOf(loginPage.getUserIdInputElement()));

        loginPage.highlight(loginPage.getUserIdInputElement(), "yellow",
                "User ID — intentionally BLANK [FRD 13.2.1]");
        loginPage.highlight(loginPage.getRoleDropdownElement(), "yellow",
                "Role Dropdown — intentionally unchanged [FRD 13.2.1]");

        loginPage.clickLoginButton();

        String alert = loginPage.acceptAlertAndGetMessage();
        System.out.println("TC-NEG-BO-004: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        if (loginPage.isUserIdInputVisible()) {
            loginPage.highlight(loginPage.getUserIdInputElement(), "green",
                    "Blank login rejected — validation working correctly");
        }

        Assert.assertTrue(loginPage.isOnLoginPage(),
                "FRD 13.2.1: App should stay on login page when all fields are blank. URL: " + getDriver().getCurrentUrl());
        Assert.assertNotNull(alert,
                "FRD 13.2.1: A validation alert must appear when Login is clicked with no fields filled.");
        System.out.println("TC-NEG-BO-004 PASSED.");
    }
}

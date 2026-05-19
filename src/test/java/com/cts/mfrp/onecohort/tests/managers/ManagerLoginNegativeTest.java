package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * Manager Login — Negative Test Suite
 *
 * FRD Reference: Section 3.3 — Manager Role Login
 *
 * Manager login requires:
 *   1. User ID     (FRD 3.3 — required text field)
 *   2. Role        = "Manager" (FRD 3.3 — dropdown, required)
 *   3. Service Line (FRD 3.3 — dropdown, appears ONLY after Manager is selected, required)
 *
 * Negative test cases (what should FAIL / show validation):
 *   TC-NEG-MGR-001  Empty User ID                  → alert "Please enter a User ID"
 *   TC-NEG-MGR-002  User ID + Manager role, no SL  → alert "Please select a Service Line"
 *   TC-NEG-MGR-003  Service Line field hidden       → field should NOT appear before Manager selected
 *   TC-NEG-MGR-004  All fields blank + click Login  → alert fires, stay on login page
 *
 * UI Highlighting colours:
 *   🟡 Yellow — element being tested / located
 *   🟢 Green  — assertion passed
 *   🔴 Red    — violation (something visible that shouldn't be)
 */
@Listeners(ExtentReportListener.class)
public class ManagerLoginNegativeTest extends BaseTest {

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * @BeforeMethod runs before EVERY @Test method.
     * This ensures each test starts fresh on the login page.
     * Because this class extends BaseTest, a new browser is also created per test.
     */
    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        getDriver().get(ConfigReader.getBaseUrl());
    }

    // ── Private Helper Methods ────────────────────────────────────────────────
    // These helpers keep the test methods clean and readable.

    /** Creates a short-lived explicit wait (for alert detection etc.). */
    private WebDriverWait wait(int seconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(seconds));
    }

    /**
     * Waits for a browser alert dialog, captures its message, then dismisses it.
     * Returns null if no alert appears within 5 seconds.
     *
     * WHY: The OneCohort app uses browser alert() for ALL validation errors.
     * There are no inline error messages — just JavaScript alert popups.
     */
    private String getAlertText() {
        try {
            wait(5).until(ExpectedConditions.alertIsPresent());
            String msg = getDriver().switchTo().alert().getText();
            getDriver().switchTo().alert().accept();
            return msg;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Checks if the browser is still on the login page.
     * Used to confirm that a failed login attempt didn't navigate away.
     */
    private boolean isOnLoginPage() {
        String url  = getDriver().getCurrentUrl();
        String base = ConfigReader.getBaseUrl();
        if (url.contains("/login") || url.equals(base) || url.equals(base + "/")) return true;
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        boolean visible = !getDriver().findElements(
                By.cssSelector("input[placeholder='e.g. 123456']")).isEmpty();
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return visible;
    }

    /**
     * Visually highlights an element in the browser during test execution.
     * 🟡 yellow = locating it | 🟢 green = passed | 🔴 red = violation
     */
    private void highlight(WebElement element, String color, String label) {
        try {
            String border = switch (color) {
                case "green" -> "3px solid #22c55e";
                case "red"   -> "3px solid #ef4444";
                default      -> "3px solid #f59e0b";
            };
            ((JavascriptExecutor) getDriver()).executeScript(
                    "arguments[0].style.border     = '" + border + "';" +
                            "arguments[0].style.boxShadow  = '0 0 6px 2px " + color + "';" +
                            "arguments[0].setAttribute('title', 'TESTING: " + label + "');",
                    element);
            Thread.sleep(400);
        } catch (Exception ignored) {}
    }

    /** Selects the "Manager" role from the role dropdown. */
    private WebElement selectManagerRole() {
        WebElement roleEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.space-y-5 select")));
        highlight(roleEl, "yellow", "Role Dropdown [FRD 3.3]");
        new Select(roleEl).selectByVisibleText("Manager");
        highlight(roleEl, "green", "Role = Manager selected");
        return roleEl;
    }

    /** Types text into the User ID field. Pass null or "" to leave it blank. */
    private WebElement enterUserId(String value) {
        WebElement el = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(el, "yellow", "User ID Input [FRD 3.3]");
        el.clear();
        if (value != null && !value.isEmpty()) el.sendKeys(value);
        return el;
    }

    /** Clicks the Login button. */
    private void clickLogin() {
        WebElement btn = wait(10).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Login']")));
        highlight(btn, "yellow", "Login Button [FRD 3.3]");
        btn.click();
    }

    // ── TEST CASES ────────────────────────────────────────────────────────────

    /**
     * TC-NEG-MGR-001: Empty User ID → validation alert + stay on login
     *
     * FRD 3.3: "User ID is required"
     *
     * What we do:
     *   1. Leave User ID blank
     *   2. Select Role = Manager  (Service Line dropdown appears)
     *   3. Click Login
     *
     * What should happen:
     *   → Browser alert: "Please enter a User ID"
     *   → Page stays on login (no redirect)
     */
    @Test(priority = 1,
            groups = {"negative", "regression"},
            description = "TC-NEG-MGR-001 [FRD 3.3]: Empty User ID — alert fires and page stays on login")
    public void tc_neg_mgr_001_emptyUserId() {
        WebElement userIdEl = enterUserId(""); // highlight yellow, leave blank
        highlight(userIdEl, "yellow", "User ID — intentionally BLANK [FRD 3.3]");

        selectManagerRole();
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-MGR-001 | Alert: \"" + alert + "\" | URL: " + getDriver().getCurrentUrl());

        // Re-highlight green: validation correctly triggered
        try {
            highlight(getDriver().findElement(By.cssSelector("input[placeholder='e.g. 123456']")),
                    "green", "Validation triggered correctly");
        } catch (Exception ignored) {}

        Assert.assertTrue(isOnLoginPage(),
                "FAIL [FRD 3.3] — Page should stay on login with empty User ID. URL: " + getDriver().getCurrentUrl());
        Assert.assertEquals(alert, AppConstants.ALERT_EMPTY_USER_ID,
                "FAIL [FRD 3.3] — Alert should be '" + AppConstants.ALERT_EMPTY_USER_ID + "'. Got: " + alert);
        System.out.println("TC-NEG-MGR-001 PASSED.");
    }

    /**
     * TC-NEG-MGR-002: User ID entered + Manager role, but no Service Line → alert
     *
     * FRD 3.3: "Service Line is required for Manager role"
     *
     * What we do:
     *   1. Enter a valid User ID
     *   2. Select Role = Manager  (Service Line dropdown appears but we skip it)
     *   3. Click Login WITHOUT selecting a Service Line
     *
     * What should happen:
     *   → Browser alert: "Please select a Service Line"
     *   → Page stays on login
     */
    @Test(priority = 2,
            groups = {"negative", "regression"},
            description = "TC-NEG-MGR-002 [FRD 3.3]: Manager role with no Service Line — alert fires")
    public void tc_neg_mgr_002_noServiceLine() {
        enterUserId(ConfigReader.getManagerUserId()); // fill User ID
        selectManagerRole();
        // intentionally DO NOT select a service line
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-MGR-002 | Alert: \"" + alert + "\" | URL: " + getDriver().getCurrentUrl());

        Assert.assertTrue(isOnLoginPage(),
                "FAIL [FRD 3.3] — Page should stay on login when Service Line is missing. URL: " + getDriver().getCurrentUrl());
        Assert.assertEquals(alert, AppConstants.ALERT_SELECT_SERVICE_LINE,
                "FAIL [FRD 3.3] — Alert should be '" + AppConstants.ALERT_SELECT_SERVICE_LINE + "'. Got: " + alert);
        System.out.println("TC-NEG-MGR-002 PASSED.");
    }

    /**
     * TC-NEG-MGR-003: Service Line field is HIDDEN before Manager role is selected
     *
     * FRD 3.3: "Service Line dropdown appears ONLY after Manager role is selected"
     * (Angular *ngIf — the element does not exist in DOM until the role changes)
     *
     * What we do:
     *   1. Navigate to login page
     *   2. Do NOT change the role (default role is shown)
     *   3. Check if Service Line dropdown is visible
     *
     * What should happen:
     *   → Service Line dropdown is NOT visible / not in DOM
     */
    @Test(priority = 3,
            groups = {"negative", "regression"},
            description = "TC-NEG-MGR-003 [FRD 3.3]: Service Line dropdown hidden before Manager role selected")
    public void tc_neg_mgr_003_serviceLineHiddenBeforeRoleSelect() {
        // Confirm login page is loaded
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(userIdEl, "green", "Login page loaded [FRD 3.3]");

        // Highlight role dropdown to show it's at its DEFAULT value (not Manager)
        WebElement roleEl = getDriver().findElement(By.cssSelector("div.space-y-5 select"));
        highlight(roleEl, "yellow", "Role Dropdown — DEFAULT (not Manager) [FRD 3.3]");

        // Check if the Service Line dropdown is visible without selecting Manager
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> allSelects = getDriver().findElements(By.cssSelector("select"));
        boolean serviceLineVisible = allSelects.stream()
                .filter(s -> !s.equals(roleEl))   // exclude the role dropdown itself
                .anyMatch(WebElement::isDisplayed);
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (serviceLineVisible) {
            // Mark it red — this is a violation
            allSelects.stream()
                    .filter(s -> !s.equals(roleEl) && s.isDisplayed())
                    .forEach(s -> highlight(s, "red", "Service Line visible before Manager selected — VIOLATION [FRD 3.3]"));
        } else {
            highlight(roleEl, "green", "Service Line correctly hidden before Manager role selected");
        }

        Assert.assertFalse(serviceLineVisible,
                "FAIL [FRD 3.3] — Service Line dropdown should NOT be visible before Manager is selected.");
        System.out.println("TC-NEG-MGR-003 PASSED. Service Line correctly hidden before role selection.");
    }

    /**
     * TC-NEG-MGR-004: All fields blank + click Login → alert fires
     *
     * FRD 3.3: All fields are required — clicking Login with nothing filled must show alert
     *
     * What we do:
     *   1. Navigate to login page
     *   2. Click Login without filling ANYTHING
     *
     * What should happen:
     *   → Browser alert fires (any validation message is acceptable here)
     *   → Page stays on login
     */
    @Test(priority = 4,
            groups = {"negative", "regression"},
            description = "TC-NEG-MGR-004 [FRD 3.3]: All fields blank — clicking Login shows validation alert")
    public void tc_neg_mgr_004_allFieldsBlank() {
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(userIdEl, "yellow", "User ID — intentionally BLANK [FRD 3.3]");

        WebElement roleEl = getDriver().findElement(By.cssSelector("div.space-y-5 select"));
        highlight(roleEl, "yellow", "Role Dropdown — not changed [FRD 3.3]");

        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-MGR-004 | Alert: \"" + alert + "\" | URL: " + getDriver().getCurrentUrl());

        try {
            highlight(getDriver().findElement(By.cssSelector("input[placeholder='e.g. 123456']")),
                    "green", "Blank login correctly rejected");
        } catch (Exception ignored) {}

        Assert.assertTrue(isOnLoginPage(),
                "FAIL [FRD 3.3] — Page should stay on login when all fields are blank. URL: " + getDriver().getCurrentUrl());
        Assert.assertNotNull(alert,
                "FAIL [FRD 3.3] — A validation alert must appear when Login clicked with no input.");
        System.out.println("TC-NEG-MGR-004 PASSED.");
    }
}
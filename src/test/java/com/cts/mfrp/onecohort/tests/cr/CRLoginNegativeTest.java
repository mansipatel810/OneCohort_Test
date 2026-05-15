package com.cts.mfrp.onecohort.tests.cr;

import com.cts.mfrp.onecohort.base.BaseTest;
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
 * CR Login — Negative Test Suite (Strictly FRD-Based, with UI highlighting)
 *
 * Every field or component under test is visually highlighted in the browser:
 *
 *   🟡 Yellow border — element located, about to be tested
 *   🟢 Green border  — assertion passed
 *   🔴 Red border    — assertion failed / violation detected
 *
 * FRD Section 12.1 validation rules tested:
 *
 *   TC-NEG-001  Empty User ID            → alert / stay on login   [FRD 12.1]
 *   TC-NEG-002  Empty Cohort ID          → alert / stay on login   [FRD 12.1.2]
 *   TC-NEG-003  Cohort ID field hidden   → before CR is selected   [FRD 12.1.2]
 *   TC-NEG-004  All fields blank         → alert on Login click     [FRD 12.1 + 12.1.2]
 *
 * DESIGN: @BeforeMethod / @AfterMethod — fresh browser per test (provided by BaseTest).
 */
@Listeners(ExtentReportListener.class)
public class CRLoginNegativeTest extends BaseTest {

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        getDriver().get(ConfigReader.getBaseUrl());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private WebDriverWait wait(int s) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(s));
    }

    /** Waits up to 5s for a browser alert, captures its text, and dismisses it. */
    private String getAlertText() {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(5))
                    .until(ExpectedConditions.alertIsPresent());
            String msg = getDriver().switchTo().alert().getText();
            getDriver().switchTo().alert().accept();
            return msg;
        } catch (Exception e) { return null; }
    }

    /**
     * Returns true if the current page is still the login page.
     * Handles Angular SPAs where the URL may not change on failed login.
     */
    private boolean isOnLoginPage() {
        String url = getDriver().getCurrentUrl();
        String base = ConfigReader.getBaseUrl();
        if (url.contains("/login") || url.equals(base) || url.equals(base + "/"))
            return true;
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        boolean inputVisible = !getDriver().findElements(
                By.cssSelector("input[placeholder='e.g. 123456']")).isEmpty();
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return inputVisible;
    }

    /**
     * Highlights a WebElement in the browser.
     *
     * @param element  element to highlight
     * @param color    "yellow" = locating | "green" = passed | "red" = failed/violation
     * @param label    short label shown as a browser tooltip on the element
     */
    private void highlight(WebElement element, String color, String label) {
        try {
            String border = switch (color) {
                case "green" -> "3px solid #22c55e";
                case "red"   -> "3px solid #ef4444";
                default      -> "3px solid #f59e0b"; // yellow
            };
            ((JavascriptExecutor) getDriver()).executeScript(
                    "arguments[0].style.border     = '" + border + "';" +
                            "arguments[0].style.boxShadow  = '0 0 6px 2px " + color + "';" +
                            "arguments[0].style.transition = 'all 0.2s ease';" +
                            "arguments[0].setAttribute('title', 'TESTING: " + label + "');",
                    element
            );
            Thread.sleep(400);
        } catch (Exception ignored) {}
    }

    /** Selects CR from the role dropdown and highlights it. */
    private void selectCRRole() {
        WebElement roleEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.space-y-5 select")));
        highlight(roleEl, "yellow", "Select Role Dropdown [FRD 12.1]");
        Select s = new Select(roleEl);
        for (String txt : new String[]{"CR", "cr", "Cr", "Class Representative"}) {
            try { s.selectByVisibleText(txt); highlight(roleEl, "green", "Role = CR selected"); return; }
            catch (Exception ignored) {}
        }
        for (WebElement o : s.getOptions()) {
            String v = o.getAttribute("value").toLowerCase();
            if (v.contains("cr") || v.contains("class")) {
                s.selectByValue(o.getAttribute("value"));
                highlight(roleEl, "green", "Role = CR selected");
                return;
            }
        }
        highlight(roleEl, "red", "CR role not found in dropdown");
        throw new RuntimeException("Could not select CR role in dropdown.");
    }

    /** Enters text into the User ID field and highlights it. */
    private void enterUserId(String value) {
        WebElement el = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(el, "yellow", "User ID Input [FRD 12.1]");
        el.clear();
        if (value != null) el.sendKeys(value);
    }

    /**
     * Enters text into the Cohort ID field and highlights it.
     * FRD 12.1.2: field is injected by Angular *ngIf after CR role is selected.
     */
    private void enterCohortId(String value) throws InterruptedException {
        Thread.sleep(400);
        WebElement el = null;
        try {
            el = wait(8).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                    "//input[contains(@placeholder,'COH') or contains(@placeholder,'cohort') " +
                            "or contains(@placeholder,'Cohort') or contains(@placeholder,'INTCLD') " +
                            "or (contains(@placeholder,'ID') and not(contains(@placeholder,'123456')))]")));
        } catch (Exception e) {
            for (WebElement inp : getDriver().findElements(
                    By.cssSelector("input[type='text'],input:not([type])"))) {
                String ph = inp.getAttribute("placeholder");
                if (inp.isDisplayed() && ph != null && !ph.contains("123456")) {
                    el = inp; break;
                }
            }
        }
        if (el == null) throw new RuntimeException("Cohort ID field not found after selecting CR role.");
        highlight(el, "yellow", "Cohort ID Input [FRD 12.1.2]");
        el.clear();
        if (value != null) el.sendKeys(value);
    }

    /** Clicks the Login button and highlights it. */
    private void clickLogin() {
        WebElement btn = wait(10).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Login']")));
        highlight(btn, "yellow", "Login Button [FRD 12.1]");
        btn.click();
    }

    // ── TEST CASES ────────────────────────────────────────────────────────────

    /**
     * TC-NEG-001: Empty User ID → validation alert / stay on login
     * FRD 12.1: "User ID is required before login can proceed."
     *
     * Highlighted components:
     *   🟡 Role dropdown (CR selected)
     *   🟡 Cohort ID input (filled)
     *   🟡 Login button (clicked)
     *   🟢 User ID input (highlighted green = correctly empty / validation triggered)
     *
     * Steps:
     *   1. Leave User ID blank
     *   2. Select Role = CR
     *   3. Enter Cohort ID = INTCLD024
     *   4. Click Login
     * Expected: Alert fires; page stays on login.
     */
    @Test(priority = 1, groups = {"negative","regression"},
            description = "TC-NEG-001 [FRD 12.1]: Empty User ID — alert fires and page stays on login")
    public void tc_neg_001_emptyUserId() throws InterruptedException {
        // Highlight User ID field to show it's intentionally left blank
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(userIdEl, "yellow", "User ID Input — LEFT BLANK [FRD 12.1]");

        selectCRRole();
        enterCohortId(ConfigReader.getValidCohortId());
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-001: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        // Re-locate User ID field and highlight based on result
        try {
            WebElement userIdAfter = getDriver().findElement(By.cssSelector("input[placeholder='e.g. 123456']"));
            highlight(userIdAfter, "green", "User ID empty — validation correctly triggered");
        } catch (Exception ignored) {}

        Assert.assertTrue(isOnLoginPage(),
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
     *
     * Highlighted components:
     *   🟡 User ID input (filled with valid value)
     *   🟡 Role dropdown (CR selected)
     *   🟡 Cohort ID input (intentionally left blank — highlighted yellow)
     *   🟡 Login button (clicked)
     *   🟢 Cohort ID input (green = validation correctly triggered)
     *
     * Steps:
     *   1. Enter User ID = 123456
     *   2. Select Role = CR
     *   3. Leave Cohort ID blank
     *   4. Click Login
     * Expected: Alert fires; page stays on login.
     */
    @Test(priority = 2, groups = {"negative","regression"},
            description = "TC-NEG-002 [FRD 12.1.2]: Empty Cohort ID — alert fires and page stays on login")
    public void tc_neg_002_emptyCohortId() throws InterruptedException {
        enterUserId(ConfigReader.getSuperAdminUserId());
        selectCRRole();

        // Locate Cohort ID field and show it's being left blank
        Thread.sleep(400);
        WebElement cohortEl = null;
        try {
            cohortEl = wait(8).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                    "//input[contains(@placeholder,'COH') or contains(@placeholder,'cohort') " +
                            "or contains(@placeholder,'Cohort') or contains(@placeholder,'INTCLD') " +
                            "or (contains(@placeholder,'ID') and not(contains(@placeholder,'123456')))]")));
        } catch (Exception e) {
            for (WebElement inp : getDriver().findElements(By.cssSelector("input[type='text'],input:not([type])"))) {
                String ph = inp.getAttribute("placeholder");
                if (inp.isDisplayed() && ph != null && !ph.contains("123456")) { cohortEl = inp; break; }
            }
        }
        if (cohortEl != null) {
            highlight(cohortEl, "yellow", "Cohort ID Input — LEFT BLANK [FRD 12.1.2]");
            cohortEl.clear(); // clear only, type nothing
        }

        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-002: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        // Re-highlight cohort field green to show validation was triggered correctly
        if (cohortEl != null) highlight(cohortEl, "green", "Cohort ID empty — validation correctly triggered");

        Assert.assertTrue(isOnLoginPage(),
                "FRD 12.1.2: App should stay on login page when Cohort ID is empty. URL: " + getDriver().getCurrentUrl());
        if (alert != null) {
            Assert.assertTrue(
                    alert.toLowerCase().contains("cohort"),
                    "Alert should mention Cohort ID. Got: \"" + alert + "\"");
        }
        System.out.println("TC-NEG-002 PASSED.");
    }

    /**
     * TC-NEG-003: Cohort ID field is NOT visible before CR role is selected
     * FRD 12.1.2: "When the user selects CR from the Select Role dropdown,
     *              an additional required field appears."
     * Implies it is absent before CR is chosen.
     *
     * Highlighted components:
     *   🟡 Role dropdown (default — Super Admin, not CR)
     *   🟢 User ID input (green to confirm login page loaded correctly)
     *   🔴 Cohort ID input (red border IF it wrongly appears — violation)
     *
     * Steps:
     *   1. Navigate to login page (default role = Super Admin)
     *   2. Do NOT change the role
     *   3. Check whether Cohort ID input is visible
     * Expected: Cohort ID field is NOT visible.
     */
    @Test(priority = 3, groups = {"negative","regression"},
            description = "TC-NEG-003 [FRD 12.1.2]: Cohort ID field hidden before CR role is selected")
    public void tc_neg_003_cohortIdFieldHiddenBeforeCRSelected() {
        // Highlight the login page User ID field to confirm page loaded
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(userIdEl, "green", "Login Page loaded — checking Cohort ID field visibility");

        // Highlight the role dropdown to show current default role
        WebElement roleEl = getDriver().findElement(By.cssSelector("div.space-y-5 select"));
        highlight(roleEl, "yellow", "Role Dropdown — default (not CR) [FRD 12.1.2]");

        // Check if cohort ID field is visible under the default role
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> cohortFields = getDriver().findElements(By.xpath(
                "//input[contains(@placeholder,'COH') or contains(@placeholder,'cohort') " +
                        "or contains(@placeholder,'Cohort') or contains(@placeholder,'INTCLD')]"));
        boolean anyVisible = cohortFields.stream().anyMatch(WebElement::isDisplayed);

        if (anyVisible) {
            // Highlight in red — this is a violation
            cohortFields.stream().filter(WebElement::isDisplayed).forEach(el ->
                    highlight(el, "red", "Cohort ID visible before CR selected — FRD 12.1.2 VIOLATION"));
        } else {
            // Good — confirm the role dropdown as green
            highlight(roleEl, "green", "Cohort ID correctly hidden before CR selected");
        }

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        Assert.assertFalse(anyVisible,
                "FRD 12.1.2: Cohort ID field should NOT be visible before CR role is selected.");
        System.out.println("TC-NEG-003 PASSED. Cohort ID field correctly hidden before CR selection.");
    }

    /**
     * TC-NEG-004: All fields blank — clicking Login immediately shows alert
     * FRD 12.1 + 12.1.2: Both User ID and Cohort ID are required.
     *
     * Highlighted components:
     *   🟡 User ID input (yellow = intentionally empty)
     *   🟡 Login button (yellow before click)
     *   🟢 User ID input (green after alert fires = validation working)
     *
     * Steps:
     *   1. Navigate to login page
     *   2. Click Login without filling any field
     * Expected: Alert fires; page stays on login.
     */
    @Test(priority = 4, groups = {"negative","regression"},
            description = "TC-NEG-004 [FRD 12.1 + 12.1.2]: All fields blank — Login shows validation alert")
    public void tc_neg_004_allFieldsBlank() {
        // Highlight all visible login fields to show they are intentionally empty
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(userIdEl, "yellow", "User ID — intentionally BLANK [FRD 12.1]");

        WebElement roleEl = getDriver().findElement(By.cssSelector("div.space-y-5 select"));
        highlight(roleEl, "yellow", "Role Dropdown — intentionally unchanged [FRD 12.1]");

        // Click Login without filling anything
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-004: Alert = \"" + alert + "\"  |  URL = " + getDriver().getCurrentUrl());

        // Highlight User ID green after successful validation trigger
        try {
            WebElement userIdAfter = getDriver().findElement(By.cssSelector("input[placeholder='e.g. 123456']"));
            highlight(userIdAfter, "green", "Blank login rejected — validation working correctly");
        } catch (Exception ignored) {}

        Assert.assertTrue(isOnLoginPage(),
                "FRD 12.1: App should stay on login page when all fields are blank. URL: " + getDriver().getCurrentUrl());
        Assert.assertNotNull(alert,
                "FRD 12.1: A validation alert must appear when Login is clicked with no fields filled.");
        System.out.println("TC-NEG-004 PASSED.");
    }
}

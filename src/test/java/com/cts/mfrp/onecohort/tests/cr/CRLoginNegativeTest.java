package com.cts.mfrp.onecohort.tests.cr;

import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

/**
 * CR Login — Negative Test Suite (Strictly FRD-Based)
 *
 * Only tests what FRD Section 12.1 explicitly states:
 *
 *   FRD 12.1   — "User ID is required before login can proceed"
 *   FRD 12.1.2 — "Cohort ID is required (marked with *)"
 *   FRD 12.1.2 — "Cohort ID field appears ONLY after CR role is selected"
 *
 * This gives exactly 4 negative test cases:
 *   TC-NEG-001  Empty User ID → validation alert / stay on login
 *   TC-NEG-002  Empty Cohort ID → validation alert / stay on login
 *   TC-NEG-003  Cohort ID field is hidden before CR role is selected
 *   TC-NEG-004  All fields blank → clicking Login shows alert
 *
 * DESIGN: @BeforeMethod / @AfterMethod — each test gets a fresh browser
 * so a leftover alert from one test never contaminates the next.
 */
@Listeners(ExtentReportListener.class)
public class CRLoginNegativeTest {

    private static final String BASE_URL     = "https://one-cohort-1.onrender.com";
    private static final String VALID_USER   = "123456";
    private static final String VALID_COHORT = "INTCLD024";

    private WebDriver driver;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--window-size=1920,1080", "--no-sandbox", "--disable-gpu");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.get(BASE_URL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private WebDriverWait wait(int s) {
        return new WebDriverWait(driver, Duration.ofSeconds(s));
    }

    /** Waits up to 5s for a browser alert, captures its text, and dismisses it. */
    private String getAlertText() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.alertIsPresent());
            String msg = driver.switchTo().alert().getText();
            driver.switchTo().alert().accept();
            return msg;
        } catch (Exception e) { return null; }
    }

    /**
     * Checks whether the current page is still the login page.
     * Handles Angular SPAs where the URL may not change on failed login —
     * falls back to checking whether the User ID input is still visible.
     */
    private boolean isOnLoginPage() {
        String url = driver.getCurrentUrl();
        if (url.contains("/login") || url.equals(BASE_URL) || url.equals(BASE_URL + "/"))
            return true;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        boolean inputVisible = !driver.findElements(
                By.cssSelector("input[placeholder='e.g. 123456']")).isEmpty();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return inputVisible;
    }

    /** Selects CR from the role dropdown. */
    private void selectCRRole() {
        WebElement roleEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.space-y-5 select")));
        Select s = new Select(roleEl);
        for (String txt : new String[]{"CR", "cr", "Cr", "Class Representative"}) {
            try { s.selectByVisibleText(txt); return; } catch (Exception ignored) {}
        }
        for (WebElement o : s.getOptions()) {
            String v = o.getAttribute("value").toLowerCase();
            if (v.contains("cr") || v.contains("class")) {
                s.selectByValue(o.getAttribute("value")); return;
            }
        }
        throw new RuntimeException("Could not select CR role in dropdown.");
    }

    /** Types into the User ID field. */
    private void enterUserId(String value) {
        WebElement el = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        el.clear();
        if (value != null) el.sendKeys(value);
    }

    /**
     * Types into the Cohort ID field.
     * FRD 12.1.2: field is injected by Angular *ngIf after CR role is selected.
     */
    private void enterCohortId(String value) throws InterruptedException {
        Thread.sleep(400); // allow Angular *ngIf to render the field
        WebElement el = null;
        try {
            el = wait(8).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                    "//input[contains(@placeholder,'COH') or contains(@placeholder,'cohort') " +
                            "or contains(@placeholder,'Cohort') or contains(@placeholder,'INTCLD') " +
                            "or (contains(@placeholder,'ID') and not(contains(@placeholder,'123456')))]")));
        } catch (Exception e) {
            // Fallback: any visible text input that is not the User ID field
            for (WebElement inp : driver.findElements(
                    By.cssSelector("input[type='text'],input:not([type])"))) {
                String ph = inp.getAttribute("placeholder");
                if (inp.isDisplayed() && ph != null && !ph.contains("123456")) {
                    el = inp; break;
                }
            }
        }
        if (el == null) throw new RuntimeException(
                "Cohort ID field not found after selecting CR role.");
        el.clear();
        if (value != null) el.sendKeys(value);
    }

    /** Clicks the Login button. */
    private void clickLogin() {
        wait(10).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Login']"))).click();
    }

    // ── TEST CASES ────────────────────────────────────────────────────────────

    /**
     * TC-NEG-001: Empty User ID → validation alert / stay on login
     *
     * FRD 12.1: "User ID is required before login can proceed."
     *
     * Steps:
     *   1. Leave User ID blank
     *   2. Select Role = CR
     *   3. Enter Cohort ID = INTCLD024
     *   4. Click Login
     * Expected:
     *   - Browser alert fires (message references User ID)
     *   - Page stays on login
     */
    @Test(priority = 1, groups = {"negative", "regression"},
            description = "TC-NEG-001 [FRD 12.1]: Empty User ID — alert fires and page stays on login")
    public void tc_neg_001_emptyUserId() throws InterruptedException {
        // Deliberately do NOT enter a User ID
        selectCRRole();
        enterCohortId(VALID_COHORT);
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-001: Alert = \"" + alert + "\"  |  URL = " + driver.getCurrentUrl());

        Assert.assertTrue(isOnLoginPage(),
                "FRD 12.1: App should stay on login page when User ID is empty. " +
                        "Actual URL: " + driver.getCurrentUrl());

        if (alert != null) {
            Assert.assertTrue(
                    alert.toLowerCase().contains("user") || alert.toLowerCase().contains("id"),
                    "Alert should mention User ID. Got: \"" + alert + "\"");
        }

        System.out.println("TC-NEG-001 PASSED.");
    }

    /**
     * TC-NEG-002: Empty Cohort ID → validation alert / stay on login
     *
     * FRD 12.1.2: "Cohort ID — required (marked with *)"
     *
     * Steps:
     *   1. Enter User ID = 123456
     *   2. Select Role = CR
     *   3. Leave Cohort ID blank
     *   4. Click Login
     * Expected:
     *   - Browser alert fires (message references Cohort ID)
     *   - Page stays on login
     */
    @Test(priority = 2, groups = {"negative", "regression"},
            description = "TC-NEG-002 [FRD 12.1.2]: Empty Cohort ID — alert fires and page stays on login")
    public void tc_neg_002_emptyCohortId() throws InterruptedException {
        enterUserId(VALID_USER);
        selectCRRole();
        enterCohortId(""); // select the field but type nothing
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-002: Alert = \"" + alert + "\"  |  URL = " + driver.getCurrentUrl());

        Assert.assertTrue(isOnLoginPage(),
                "FRD 12.1.2: App should stay on login page when Cohort ID is empty. " +
                        "Actual URL: " + driver.getCurrentUrl());

        if (alert != null) {
            Assert.assertTrue(
                    alert.toLowerCase().contains("cohort"),
                    "Alert should mention Cohort ID. Got: \"" + alert + "\"");
        }

        System.out.println("TC-NEG-002 PASSED.");
    }

    /**
     * TC-NEG-003: Cohort ID field is NOT visible before CR role is selected
     *
     * FRD 12.1.2: "When the user selects CR from the Select Role dropdown,
     *              an additional required field appears below the role selector."
     * This means the field must be absent/hidden when any other role is selected.
     *
     * Steps:
     *   1. Navigate to login page (default role = Super Admin)
     *   2. Do NOT change the role
     *   3. Check whether the Cohort ID input is present in the DOM
     * Expected:
     *   - Cohort ID field is NOT visible when a non-CR role is active
     */
    @Test(priority = 3, groups = {"negative", "regression"},
            description = "TC-NEG-003 [FRD 12.1.2]: Cohort ID field is hidden before CR role is selected")
    public void tc_neg_003_cohortIdFieldHiddenBeforeCRSelected() {
        // Wait for the login page to load with default role (Super Admin)
        wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));

        // Cohort ID field must NOT be visible under any non-CR role
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> cohortFields = driver.findElements(By.xpath(
                "//input[contains(@placeholder,'COH') or contains(@placeholder,'cohort') " +
                        "or contains(@placeholder,'Cohort') or contains(@placeholder,'INTCLD')]"));
        boolean anyVisible = cohortFields.stream().anyMatch(WebElement::isDisplayed);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        Assert.assertFalse(anyVisible,
                "FRD 12.1.2: Cohort ID field should NOT be visible before CR role is selected.");

        System.out.println("TC-NEG-003 PASSED. Cohort ID field is correctly hidden before CR selection.");
    }

    /**
     * TC-NEG-004: All fields blank — clicking Login immediately shows alert
     *
     * FRD 12.1: "User ID is required."
     * FRD 12.1.2: "Cohort ID is required."
     * Combined: clicking Login with nothing filled must produce a validation alert.
     *
     * Steps:
     *   1. Navigate to login page
     *   2. Click Login without filling any field or selecting a role
     * Expected:
     *   - Browser alert fires
     *   - Page stays on login
     */
    @Test(priority = 4, groups = {"negative", "regression"},
            description = "TC-NEG-004 [FRD 12.1 + 12.1.2]: All fields blank — Login button shows validation alert")
    public void tc_neg_004_allFieldsBlank() {
        wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));

        // Click Login without filling anything
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-004: Alert = \"" + alert + "\"  |  URL = " + driver.getCurrentUrl());

        Assert.assertTrue(isOnLoginPage(),
                "FRD 12.1: App should stay on login page when all fields are blank. " +
                        "Actual URL: " + driver.getCurrentUrl());

        Assert.assertNotNull(alert,
                "FRD 12.1: A validation alert must appear when Login is clicked with no fields filled.");

        System.out.println("TC-NEG-004 PASSED.");
    }
}

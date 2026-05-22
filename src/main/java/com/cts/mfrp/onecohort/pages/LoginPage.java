package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;

public class LoginPage extends BasePage {

    // No id/name attrs in Angular template — located by placeholder text and DOM structure
    private final By userIdInput         = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown        = By.cssSelector("div.space-y-5 select");
    // *ngIf fields — only injected into the DOM once the matching role is selected
    // The service line <select> is always the 2nd select inside div.space-y-5.
    // CSS "select + select" fails because the two <select>s live in separate sibling <div>s.
    // Angular does not render formcontrolname as a DOM attribute in this app, so those fail too.
    private final By serviceLineDropdown = By.xpath(
            "(//div[contains(@class,'space-y-5')]//select)[2]");
    private final By pocIdInput          = By.cssSelector("input[placeholder='e.g. USR-40002']");
    private final By cohortIdInput       = By.cssSelector("input[placeholder='e.g. COH-10001']");
    private final By loginButton         = By.xpath("//button[normalize-space()='Login']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ── Atomic actions ───────────────────────────────────────────────────────

    public LoginPage enterUserId(String userId) {
        type(userIdInput, userId);
        return this;
    }

    public LoginPage selectRole(String role) {
        new Select(waitForVisible(roleDropdown)).selectByVisibleText(role);
        return this;
    }

    /**
     * Waits for Angular *ngIf to inject the service line dropdown AND for the
     * backend API to populate it with options, then selects the matching entry.
     *
     * The app is hosted on render.com (free tier) which has cold-start delays —
     * the dropdown element appears immediately but options may take 10-20 s to load.
     * This method polls up to 30 s for at least one real option before selecting.
     *
     * Selection order:
     *  1. selectByValue       — exact value attribute match
     *  2. selectByVisibleText — exact visible text match
     *  3. Partial text match  — label contains serviceLineId
     *  4. Index 1             — first non-placeholder option (last resort)
     */
    public LoginPage selectServiceLine(String serviceLineId) {
        org.openqa.selenium.WebElement dropdownEl = waitForVisible(serviceLineDropdown);

        // Poll until at least one real option appears (handles render.com cold start)
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(d -> new Select(d.findElement(serviceLineDropdown))
                        .getOptions().stream()
                        .anyMatch(o -> !o.getAttribute("value").trim().isEmpty()));
        Select select = new Select(waitForVisible(serviceLineDropdown));

        // 1. Exact value attribute
        try { select.selectByValue(serviceLineId); return this; }
        catch (Exception ignored) {}

        // 2. Exact visible text
        try { select.selectByVisibleText(serviceLineId); return this; }
        catch (Exception ignored) {}

        // 3. Partial text match  (e.g. "QEA (SRV-10001)")
        Select fs = select;
        select.getOptions().stream()
                .filter(o -> o.getText().contains(serviceLineId)
                          && !o.getAttribute("value").trim().isEmpty())
                .findFirst()
                .ifPresent(o -> fs.selectByVisibleText(o.getText()));
        try {
            if (!select.getFirstSelectedOption().getAttribute("value").trim().isEmpty())
                return this;
        } catch (Exception ignored) {}

        // 4. First real option as last resort
        select.getOptions().stream()
                .filter(o -> !o.getAttribute("value").trim().isEmpty())
                .findFirst()
                .ifPresent(o -> fs.selectByValue(o.getAttribute("value")));

        return this;
    }

    public LoginPage enterPocId(String pocId) {
        type(pocIdInput, pocId);
        return this;
    }

    public LoginPage enterCohortId(String cohortId) {
        type(cohortIdInput, cohortId);
        return this;
    }

    public void clickLoginButton() {
        click(loginButton);
    }

    // ── Alert handling ───────────────────────────────────────────────────────
    // The app uses browser alert() dialogs for ALL validation errors — no inline messages.

    /** Waits for the browser alert, captures its text, then dismisses it. */
    public String acceptAlertAndGetMessage() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String message = alert.getText();
        alert.accept();
        return message;
    }

    public boolean isAlertPresent() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Page state checks ────────────────────────────────────────────────────

    /**
     * Returns true if the browser is still on the login page.
     * Handles Angular SPAs where the URL may not change on a failed login attempt.
     */
    public boolean isOnLoginPage() {
        String url = driver.getCurrentUrl();
        if (url.contains("/login") || url.endsWith("/") ||
                url.equals(com.cts.mfrp.onecohort.utils.ConfigReader.getBaseUrl()) ||
                url.equals(com.cts.mfrp.onecohort.utils.ConfigReader.getBaseUrl() + "/")) {
            return true;
        }
        // Fallback: check whether the login User ID input is still visible
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        boolean inputVisible = !driver.findElements(userIdInput).isEmpty();
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(com.cts.mfrp.onecohort.utils.ConfigReader.getImplicitWait()));
        return inputVisible;
    }

    // ── Convenience login flows ──────────────────────────────────────────────

    public HomePage loginAsSuperAdmin(String userId) {
        enterUserId(userId);
        selectRole("Super Admin");
        clickLoginButton();
        return new HomePage(driver);
    }

    public void loginAsManager(String userId, String serviceLineId) {
        enterUserId(userId);
        selectRole("Manager");
        selectServiceLine(serviceLineId);
        clickLoginButton();
    }

    public void loginAsLeader(String userId, String serviceLineId) {
        enterUserId(userId);
        selectRole("Leader");
        selectServiceLine(serviceLineId);
        clickLoginButton();
    }

    public void loginAsBatchOwner(String userId, String serviceLineId, String pocId) {
        enterUserId(userId);
        selectRole("Batch Owner");
        selectServiceLine(serviceLineId);
        enterPocId(pocId);
        clickLoginButton();
    }

    public void loginAsCR(String userId, String cohortId) {
        enterUserId(userId);
        selectRole("CR");
        enterCohortId(cohortId);
        clickLoginButton();
    }

    // ── Element visibility helpers (used by negative-login tests) ────────────

    public boolean isUserIdInputVisible() {
        return isDisplayed(userIdInput);
    }

    public boolean isRoleDropdownVisible() {
        return isDisplayed(roleDropdown);
    }

    public boolean isServiceLineDropdownVisible() {
        return isDisplayed(serviceLineDropdown);
    }

    public boolean isPocIdInputVisible() {
        return isDisplayed(pocIdInput);
    }

    public boolean isCohortIdInputVisible() {
        return isDisplayed(cohortIdInput);
    }

    public boolean isLoginButtonVisible() {
        return isDisplayed(loginButton);
    }

    /** Returns the User ID input element (for tests that need to highlight it). */
    public WebElement getUserIdInputElement() {
        return waitForVisible(userIdInput);
    }

    /** Returns the Role dropdown element. */
    public WebElement getRoleDropdownElement() {
        return waitForVisible(roleDropdown);
    }

    /** Returns all select elements visible on the page. */
    public java.util.List<org.openqa.selenium.WebElement> getAllSelectElements() {
        return driver.findElements(By.cssSelector("select"));
    }

    /** Returns all input elements matching the POC ID placeholder. */
    public java.util.List<org.openqa.selenium.WebElement> getPocIdCandidates() {
        return driver.findElements(By.xpath(
                "//input[contains(@placeholder,'USR') or contains(@placeholder,'POC') " +
                "or contains(@placeholder,'poc')]"));
    }

    /** Returns all input elements matching the Cohort ID placeholder. */
    public java.util.List<org.openqa.selenium.WebElement> getCohortIdCandidates() {
        return driver.findElements(By.xpath(
                "//input[contains(@placeholder,'COH') or contains(@placeholder,'cohort') " +
                "or contains(@placeholder,'Cohort') or contains(@placeholder,'INTCLD')]"));
    }
}

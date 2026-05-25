package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

public class LoginPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By userIdInput  = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown = By.cssSelector("div.space-y-5 select");
    private final By loginButton  = By.xpath("//button[normalize-space()='Login']");

    // Flexible locators from Git to handle varying placeholders
    private final By pocIdInput = By.cssSelector(
            "input[placeholder*='USR'], input[placeholder*='POC'], " +
            "input[placeholder*='poc'], input[placeholder*='Poc']");

    private final By cohortIdInput = By.cssSelector(
            "input[placeholder*='COH'], input[placeholder*='Cohort'], " +
            "input[placeholder*='cohort']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ── Atomic actions ────────────────────────────────────────────────────────

    public LoginPage enterUserId(String userId) {
        type(userIdInput, userId); // Using your BasePage method
        return this;
    }

    public LoginPage selectRole(String role) {
        new Select(waitForVisible(roleDropdown)).selectByVisibleText(role);
        return this;
    }

    /**
     * Git Team's Fix applied here: Finds the service line dynamically as the 2nd <select> 
     * element on the page, avoiding the TimeoutException caused by Angular builds.
     */
    public LoginPage selectServiceLine(String serviceLineId) {
        // Step 1: wait for the 2nd <select> to appear in the DOM
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(d -> d.findElements(By.cssSelector("select")).size() >= 2);

        // Step 2: wait for real options to load inside the 2nd select
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(d -> {
                    WebElement el = d.findElements(By.cssSelector("select")).get(1);
                    return new Select(el).getOptions().stream()
                            .anyMatch(o -> !o.getAttribute("value").trim().isEmpty());
                });

        // Step 3: get a fresh reference to the 2nd select
        WebElement slElement = driver.findElements(By.cssSelector("select")).get(1);
        wait.until(ExpectedConditions.visibilityOf(slElement));
        Select select = new Select(slElement);

        // 1. Exact value attribute match
        try { select.selectByValue(serviceLineId); return this; } catch (Exception ignored) {}
        
        // 2. Exact visible text match
        try { select.selectByVisibleText(serviceLineId); return this; } catch (Exception ignored) {}
        
        // 3. Partial text match e.g. "QEA (SRV-10001)"
        select.getOptions().stream()
                .filter(o -> o.getText().contains(serviceLineId)
                        && !o.getAttribute("value").trim().isEmpty())
                .findFirst()
                .ifPresent(o -> new Select(
                        driver.findElements(By.cssSelector("select")).get(1))
                        .selectByVisibleText(o.getText()));

        try {
            if (!select.getFirstSelectedOption().getAttribute("value").trim().isEmpty()) return this;
        } catch (Exception ignored) {}

        // 4. Last resort: first non-placeholder option
        select.getOptions().stream()
                .filter(o -> !o.getAttribute("value").trim().isEmpty())
                .findFirst()
                .ifPresent(o -> new Select(
                        driver.findElements(By.cssSelector("select")).get(1))
                        .selectByValue(o.getAttribute("value")));

        return this;
    }

    public LoginPage enterPocId(String pocId) {
        type(pocIdInput, pocId); // Using your BasePage method
        return this;
    }

    public LoginPage enterCohortId(String cohortId) {
        type(cohortIdInput, cohortId); // Using your BasePage method
        return this;
    }

    public void clickLoginButton() {
        click(loginButton); // Using your BasePage method
    }

    // ── Visibility checks ─────────────────────────────────────────────────────

    public boolean isUserIdInputVisible() {
        return isDisplayed(userIdInput);
    }

    // ── Alert handling ────────────────────────────────────────────────────────

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
        } catch (Exception e) { return false; }
    }

    public boolean isOnLoginPage() {
        String url = driver.getCurrentUrl();
        if (url.contains("/login") || url.endsWith("/") ||
                url.equals(com.cts.mfrp.onecohort.utils.ConfigReader.getBaseUrl()) ||
                url.equals(com.cts.mfrp.onecohort.utils.ConfigReader.getBaseUrl() + "/")) {
            return true;
        }
        return isUserIdInputVisible();
    }

    // ── Convenience login flows ───────────────────────────────────────────────

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
}
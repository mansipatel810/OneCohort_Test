package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;

public class LoginPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By userIdInput  = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown = By.cssSelector("div.space-y-5 select");
    private final By loginButton  = By.xpath("//button[normalize-space()='Login']");

    // Injected by Angular *ngIf after a role that requires Service Line is selected.
    private final By serviceLineDropdown = By.xpath(
            "(//div[contains(@class,'space-y-5')]//select)[2]");

    // Injected by Angular *ngIf for Batch Owner role (POC ID field)
    private final By pocIdInput = By.cssSelector(
            "input[placeholder*='POC'], input[placeholder*='poc'], " +
            "input[placeholder*='Poc']");

    // Injected by Angular *ngIf for CR role (Cohort ID field)
    private final By cohortIdInput = By.cssSelector(
            "input[placeholder*='COH'], input[placeholder*='Cohort'], " +
            "input[placeholder*='cohort']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ── Atomic actions ────────────────────────────────────────────────────────

    public LoginPage enterUserId(String userId) {
        type(userIdInput, userId);
        return this;
    }

    public LoginPage selectRole(String role) {
        new Select(waitForVisible(roleDropdown)).selectByVisibleText(role);
        return this;
    }

    /**
     * Waits for the service line dropdown to appear (Angular *ngIf) AND for the
     * backend API to populate its options, then selects the matching entry.
     * Polls up to 30 s for real options — handles render.com cold-start delays.
     */
    public LoginPage selectServiceLine(String serviceLineId) {
        waitForVisible(serviceLineDropdown);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(d -> new Select(d.findElement(serviceLineDropdown))
                        .getOptions().stream()
                        .anyMatch(o -> !o.getAttribute("value").trim().isEmpty()));

        Select select = new Select(waitForVisible(serviceLineDropdown));

        // 1. Exact value attribute
        try { select.selectByValue(serviceLineId); return this; } catch (Exception ignored) {}
        // 2. Exact visible text
        try { select.selectByVisibleText(serviceLineId); return this; } catch (Exception ignored) {}
        // 3. Partial text match (e.g. "QEA (SRV-10001)")
        select.getOptions().stream()
                .filter(o -> o.getText().contains(serviceLineId)
                          && !o.getAttribute("value").trim().isEmpty())
                .findFirst()
                .ifPresent(o -> new Select(driver.findElement(serviceLineDropdown))
                        .selectByVisibleText(o.getText()));
        // 4. First real option as last resort
        try {
            if (new Select(driver.findElement(serviceLineDropdown))
                    .getFirstSelectedOption().getAttribute("value").trim().isEmpty()) {
                select.getOptions().stream()
                        .filter(o -> !o.getAttribute("value").trim().isEmpty())
                        .findFirst()
                        .ifPresent(o -> new Select(driver.findElement(serviceLineDropdown))
                                .selectByValue(o.getAttribute("value")));
            }
        } catch (Exception ignored) {}
        return this;
    }

    public LoginPage enterPocId(String pocId) {
        waitForVisible(pocIdInput).sendKeys(pocId);
        return this;
    }

    public LoginPage enterCohortId(String cohortId) {
        waitForVisible(cohortIdInput).sendKeys(cohortId);
        return this;
    }

    public void clickLoginButton() {
        click(loginButton);
    }

    // ── Visibility checks ─────────────────────────────────────────────────────

    /** Returns true if the User ID input is visible — confirms login page is loaded. */
    public boolean isUserIdInputVisible() {
        return isDisplayed(userIdInput);
    }

    // ── Alert handling ────────────────────────────────────────────────────────

    /** Waits for the browser alert, captures its text, then dismisses it. */
    public String acceptAlertAndGetMessage() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String message = alert.getText();
        alert.accept();
        return message;
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

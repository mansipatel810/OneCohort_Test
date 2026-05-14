package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class LoginPage extends BasePage {

    // No id/name attrs in Angular template — located by placeholder text and DOM structure
    private final By userIdInput         = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown        = By.cssSelector("div.space-y-5 select");
    // *ngIf fields — only injected into the DOM once the matching role is selected
    private final By serviceLineDropdown = By.xpath("(//select)[2]");
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
        long deadline = System.currentTimeMillis() + 30_000;
        Select select = new Select(dropdownEl);
        while (System.currentTimeMillis() < deadline) {
            long real = select.getOptions().stream()
                    .filter(o -> !o.getAttribute("value").trim().isEmpty())
                    .count();
            if (real > 0) break;
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            select = new Select(waitForVisible(serviceLineDropdown));
        }

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
}

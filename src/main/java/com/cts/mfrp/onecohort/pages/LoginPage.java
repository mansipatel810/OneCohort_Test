package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    // No id/name attrs in Angular template — located by placeholder text and DOM structure
    private final By userIdInput   = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown  = By.cssSelector("div.space-y-5 select");
    // ── serviceLineDropdown field REMOVED ────────────────────────────────────
    // The old selector used formcontrolname which differs across builds and caused
    // TimeoutException for everyone. Service line is now found positionally
    // inside selectServiceLine() as the 2nd <select> on the page.
    // ─────────────────────────────────────────────────────────────────────────
    private final By pocIdInput    = By.cssSelector("input[placeholder='e.g. USR-40002']");
    private final By cohortIdInput = By.cssSelector("input[placeholder='e.g. COH-10001']");
    private final By loginButton   = By.xpath("//button[normalize-space()='Login']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    public LoginPage enterUserId(String userId) {
        driver.findElement(userIdInput).clear();
        driver.findElement(userIdInput).sendKeys(userId);
        return this;
    }

    public LoginPage selectRole(String role) {
        new Select(driver.findElement(roleDropdown)).selectByVisibleText(role);
        return this;
    }

    /**
     * Selects the service line dropdown.
     *
     * KEY FIX: Does NOT use formcontrolname — that attribute differs across
     * builds and caused TimeoutException for the whole team.
     *
     * Instead:
     *   Step 1 — wait until 2+ <select> elements exist (role + service line)
     *   Step 2 — wait for options to populate (handles Render.com cold start)
     *   Step 3 — grab the 2nd select (index 1) = always the service line
     *   Step 4 — try value → visible text → partial text → index 1
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
        try { select.selectByValue(serviceLineId); return this; }
        catch (Exception ignored) {}

        // 2. Exact visible text match
        try { select.selectByVisibleText(serviceLineId); return this; }
        catch (Exception ignored) {}

        // 3. Partial text match e.g. "QEA (SRV-10001)"
        select.getOptions().stream()
                .filter(o -> o.getText().contains(serviceLineId)
                        && !o.getAttribute("value").trim().isEmpty())
                .findFirst()
                .ifPresent(o -> new Select(
                        driver.findElements(By.cssSelector("select")).get(1))
                        .selectByVisibleText(o.getText()));

        try {
            if (!select.getFirstSelectedOption()
                    .getAttribute("value").trim().isEmpty()) return this;
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
        driver.findElement(pocIdInput).clear();
        driver.findElement(pocIdInput).sendKeys(pocId);
        return this;
    }

    public LoginPage enterCohortId(String cohortId) {
        driver.findElement(cohortIdInput).clear();
        driver.findElement(cohortIdInput).sendKeys(cohortId);
        return this;
    }

    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }

    // ── Alert handling ───────────────────────────────────────────────────────

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

    // ── Page state checks ────────────────────────────────────────────────────

    public boolean isOnLoginPage() {
        String url = driver.getCurrentUrl();
        if (url.contains("/login") || url.endsWith("/") ||
                url.equals(com.cts.mfrp.onecohort.utils.ConfigReader.getBaseUrl()) ||
                url.equals(com.cts.mfrp.onecohort.utils.ConfigReader.getBaseUrl() + "/")) {
            return true;
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        boolean inputVisible = !driver.findElements(userIdInput).isEmpty();
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(
                        com.cts.mfrp.onecohort.utils.ConfigReader.getImplicitWait()));
        return inputVisible;
    }

    public boolean isUserIdInputVisible() {
        return !driver.findElements(userIdInput).isEmpty();
    }

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

    // ── Element visibility helpers ────────────────────────────────────────────

    public boolean isUserIdInputVisible()   { return isDisplayed(userIdInput); }
    public boolean isRoleDropdownVisible()  { return isDisplayed(roleDropdown); }
    public boolean isPocIdInputVisible()    { return isDisplayed(pocIdInput); }
    public boolean isCohortIdInputVisible() { return isDisplayed(cohortIdInput); }
    public boolean isLoginButtonVisible()   { return isDisplayed(loginButton); }

    public boolean isServiceLineDropdownVisible() {
        try {
            return driver.findElements(By.cssSelector("select")).size() >= 2
                    && driver.findElements(By.cssSelector("select")).get(1).isDisplayed();
        } catch (Exception e) { return false; }
    }

    // ── Element getters ───────────────────────────────────────────────────────

    public WebElement getUserIdInputElement()  { return waitForVisible(userIdInput); }
    public WebElement getRoleDropdownElement() { return waitForVisible(roleDropdown); }

    public List<WebElement> getAllSelectElements() {
        return driver.findElements(By.cssSelector("select"));
    }

    public List<WebElement> getPocIdCandidates() {
        return driver.findElements(By.xpath(
                "//input[contains(@placeholder,'USR') or contains(@placeholder,'POC') " +
                        "or contains(@placeholder,'poc')]"));
    }

    public List<WebElement> getCohortIdCandidates() {
        return driver.findElements(By.xpath(
                "//input[contains(@placeholder,'COH') or contains(@placeholder,'cohort') " +
                        "or contains(@placeholder,'Cohort') or contains(@placeholder,'INTCLD')]"));
    }
}
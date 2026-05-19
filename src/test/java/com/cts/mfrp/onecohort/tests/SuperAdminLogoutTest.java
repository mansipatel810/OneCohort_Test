package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentManager;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.RetryAnalyzer;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Super Admin Logout Test Suite
 *
 * FRD Reference: Section 2.1 — Super Admin, Section 2.1.3 — Right Frame (User Controls)
 *
 * Logout flow (FRD 2.1):
 *   1. User clicks the avatar/profile icon in the top-right
 *   2. A dropdown menu appears with a "Logout" option
 *   3. User clicks "Logout"
 *   4. Session is cleared, browser redirects to /login page
 *
 * Test cases:
 *   TC-LOGOUT-001  Logout option appears in avatar dropdown       [FRD 2.1.3]
 *   TC-LOGOUT-002  Clicking Logout redirects to login page        [FRD 2.1]
 *   TC-LOGOUT-003  After logout, login form is visible            [FRD 2.1]
 *   TC-LOGOUT-004  After logout, navigating back shows login      [FRD 2.1]
 *
 * DESIGN:
 *   Extends BaseTest → fresh browser per test method.
 *   Each test logs in fresh and then tests the logout behaviour.
 *   This avoids test order dependency (each test is independent).
 */
@Listeners(ExtentReportListener.class)
public class SuperAdminLogoutTest extends BaseTest {

    // ── Locators ──────────────────────────────────────────────────────────────
    // These are the DOM elements involved in the logout flow.

    // The blue avatar circle in the top-right header (FRD 2.1.3)
    private final By avatarBtn = By.cssSelector(".bg-blue-600.rounded-full");

    // The dropdown that appears after clicking the avatar
    private final By profileDropdown = By.cssSelector("app-header ul");

    // The "Logout" link inside the dropdown
    // Tries two locator strategies: link text and anchor href
    private final By logoutLink = By.xpath(
            "//app-header//a[contains(text(),'Logout') or @routerLink='/login']" +
                    "| //ul//li[contains(text(),'Logout')]" +
                    "| //button[contains(text(),'Logout')]"
    );

    // The login page User ID input — confirms we're back on the login page
    private final By loginInput = By.cssSelector("input[placeholder='e.g. 123456']");

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Before EVERY test: open browser on login page and log in as Super Admin.
     * This ensures each logout test starts from a fully authenticated session.
     */
    @BeforeMethod(alwaysRun = true)
    public void loginAsSuperAdmin() {
        getDriver().get(ConfigReader.getBaseUrl());
        new LoginPage(getDriver()).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());

        // Wait until redirected to the dashboard
        new WebDriverWait(getDriver(), Duration.ofSeconds(15))
                .until(ExpectedConditions.urlContains("/super-admin"));
        System.out.println("Logged in — URL: " + getDriver().getCurrentUrl());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private WebDriverWait wait(int seconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(seconds));
    }

    private void highlight(WebElement el, String color, String label) {
        try {
            String border = switch (color) {
                case "green" -> "3px solid #22c55e";
                case "red"   -> "3px solid #ef4444";
                default      -> "3px solid #f59e0b";
            };
            ((JavascriptExecutor) getDriver()).executeScript(
                    "arguments[0].style.border='"+border+"';" +
                            "arguments[0].setAttribute('title','TESTING: "+label+"');",
                    el);
            Thread.sleep(400);
        } catch (Exception ignored) {}
    }

    /** Clicks the avatar to open the profile dropdown. */
    private void openProfileDropdown() {
        WebElement avatar = wait(10).until(
                ExpectedConditions.elementToBeClickable(avatarBtn));
        highlight(avatar, "yellow", "Avatar — clicking to open dropdown [FRD 2.1.3]");
        avatar.click();
    }

    // ── TEST CASES ────────────────────────────────────────────────────────────

    /**
     * TC-LOGOUT-001: Profile dropdown contains a Logout option
     *
     * FRD 2.1.3: "Profile Dropdown Menu — Contains logout and profile options"
     *
     * Steps:
     *   1. Log in as Super Admin (done in @BeforeMethod)
     *   2. Click the avatar in the top-right
     *   3. Check that a "Logout" option is visible in the dropdown
     */
    @Test(
            priority       = 1,
            groups         = {"smoke", "regression"},
            retryAnalyzer  = RetryAnalyzer.class,
            description    = "TC-LOGOUT-001 [FRD 2.1.3]: Avatar dropdown contains Logout option"
    )
    public void logoutOptionVisibleInDropdown() {
        ExtentManager.getTest().info("Clicking avatar to open profile dropdown");
        openProfileDropdown();

        // Wait for the dropdown menu to appear
        wait(5).until(ExpectedConditions.visibilityOfElementLocated(profileDropdown));
        WebElement dropdown = getDriver().findElement(profileDropdown);
        highlight(dropdown, "yellow", "Profile dropdown [FRD 2.1.3]");

        ExtentManager.getTest().info("Checking Logout option is present in dropdown");
        boolean logoutVisible = !getDriver().findElements(logoutLink).isEmpty()
                && getDriver().findElement(logoutLink).isDisplayed();

        highlight(dropdown, logoutVisible ? "green" : "red",
                logoutVisible ? "Logout option found" : "Logout option MISSING — FRD violation");

        Assert.assertTrue(logoutVisible,
                "FAIL [FRD 2.1.3] — Logout option not found in profile dropdown. " +
                        "FRD 2.1.3 requires a Logout option in the user profile dropdown.");
        System.out.println("TC-LOGOUT-001 PASSED — Logout option is visible in dropdown.");
    }

    /**
     * TC-LOGOUT-002: Clicking Logout redirects browser to the login page
     *
     * FRD 2.1: "System clears session and redirects to /login page"
     *
     * Steps:
     *   1. Log in as Super Admin
     *   2. Click avatar → Click Logout
     *   3. Verify URL contains /login or base URL (login page URL)
     */
    @Test(
            priority       = 2,
            groups         = {"smoke", "regression"},
            retryAnalyzer  = RetryAnalyzer.class,
            description    = "TC-LOGOUT-002 [FRD 2.1]: Clicking Logout redirects to login page"
    )
    public void logoutRedirectsToLoginPage() {
        ExtentManager.getTest().info("Opening avatar dropdown");
        openProfileDropdown();
        wait(5).until(ExpectedConditions.visibilityOfElementLocated(logoutLink));

        WebElement logout = getDriver().findElement(logoutLink);
        highlight(logout, "yellow", "Logout link — about to click [FRD 2.1]");
        ExtentManager.getTest().info("Clicking Logout");
        logout.click();

        // Wait for the URL to change to the login page
        wait(10).until(driver -> {
            String url = driver.getCurrentUrl();
            String base = ConfigReader.getBaseUrl();
            return url.contains("/login") || url.equals(base) || url.equals(base + "/");
        });

        String currentUrl = getDriver().getCurrentUrl();
        ExtentManager.getTest().info("After logout — URL: " + currentUrl);

        String base = ConfigReader.getBaseUrl();
        boolean onLoginPage = currentUrl.contains("/login")
                || currentUrl.equals(base)
                || currentUrl.equals(base + "/");

        Assert.assertTrue(onLoginPage,
                "FAIL [FRD 2.1] — After logout, expected to land on login page. " +
                        "Actual URL: " + currentUrl);
        System.out.println("TC-LOGOUT-002 PASSED — Redirected to: " + currentUrl);
    }

    /**
     * TC-LOGOUT-003: After logout, the login form is visible (not a blank/error page)
     *
     * FRD 2.1: "System clears session — login form should appear"
     *
     * Steps:
     *   1. Log in as Super Admin → Logout
     *   2. Verify the User ID input field is visible on the page
     *   3. Verify the Login button is visible
     */
    @Test(
            priority       = 3,
            groups         = {"regression"},
            retryAnalyzer  = RetryAnalyzer.class,
            description    = "TC-LOGOUT-003 [FRD 2.1]: After logout, login form is visible"
    )
    public void loginFormVisibleAfterLogout() {
        // Perform logout
        openProfileDropdown();
        wait(5).until(ExpectedConditions.visibilityOfElementLocated(logoutLink));
        getDriver().findElement(logoutLink).click();

        ExtentManager.getTest().info("Waiting for login form to appear after logout");

        // Wait for User ID input to appear
        WebElement userIdField = wait(15).until(
                ExpectedConditions.visibilityOfElementLocated(loginInput));
        highlight(userIdField, "green", "Login form visible after logout [FRD 2.1]");

        ExtentManager.getTest().info("Login form loaded — URL: " + getDriver().getCurrentUrl());

        Assert.assertTrue(userIdField.isDisplayed(),
                "FAIL [FRD 2.1] — User ID input not visible after logout. " +
                        "The login form should be shown after session is cleared.");

        // Also check the Login button exists
        boolean loginBtnVisible = !getDriver().findElements(
                By.xpath("//button[normalize-space()='Login']")).isEmpty();
        Assert.assertTrue(loginBtnVisible,
                "FAIL [FRD 2.1] — Login button not visible after logout.");

        System.out.println("TC-LOGOUT-003 PASSED — Login form (User ID + Login button) visible after logout.");
    }

    /**
     * TC-LOGOUT-004: After logout, pressing browser Back does not re-enter the dashboard
     *
     * FRD 2.1: "System clears session" — implies session tokens are invalidated.
     * Navigating Back should not restore the authenticated dashboard.
     *
     * Steps:
     *   1. Log in as Super Admin → Logout → Land on login page
     *   2. Press browser Back
     *   3. Verify: either login page is shown again, OR dashboard redirects back to login
     *
     * NOTE: Some SPAs (Single Page Apps) may briefly show the dashboard before
     *       the auth guard kicks in and redirects. This test checks the FINAL state.
     */
    @Test(
            priority      = 4,
            groups        = {"regression"},
            description   = "TC-LOGOUT-004 [FRD 2.1]: After logout, browser Back does not restore dashboard session"
    )
    public void backButtonAfterLogoutDoesNotRestoreDashboard() {
        // Perform logout
        openProfileDropdown();
        wait(5).until(ExpectedConditions.visibilityOfElementLocated(logoutLink));
        getDriver().findElement(logoutLink).click();

        // Confirm we are on the login page
        wait(10).until(ExpectedConditions.visibilityOfElementLocated(loginInput));
        ExtentManager.getTest().info("Confirmed on login page. Pressing browser Back...");

        // Press browser Back button
        getDriver().navigate().back();

        // Wait 3 seconds for Angular route guards to process
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        String urlAfterBack = getDriver().getCurrentUrl();
        ExtentManager.getTest().info("URL after Back: " + urlAfterBack);

        // The Angular auth guard should redirect to login even if Back was pressed
        // Accept either: still on login page, or redirected back to login
        String base = ConfigReader.getBaseUrl();
        boolean sessionProtected = urlAfterBack.contains("/login")
                || urlAfterBack.equals(base)
                || urlAfterBack.equals(base + "/");

        // If the URL still shows /super-admin, check if the User ID input appeared
        // (meaning the Angular route guard redirected them)
        if (!sessionProtected && urlAfterBack.contains("/super-admin")) {
            try {
                getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
                boolean loginInputVisible = !getDriver().findElements(loginInput).isEmpty()
                        && getDriver().findElement(loginInput).isDisplayed();
                getDriver().manage().timeouts().implicitlyWait(
                        Duration.ofSeconds(ConfigReader.getImplicitWait()));
                sessionProtected = loginInputVisible;
            } catch (Exception e) {
                getDriver().manage().timeouts().implicitlyWait(
                        Duration.ofSeconds(ConfigReader.getImplicitWait()));
            }
        }

        Assert.assertTrue(sessionProtected,
                "FAIL [FRD 2.1] — After logout + Back, dashboard is still accessible. " +
                        "Session was NOT properly cleared. URL: " + urlAfterBack);
        System.out.println("TC-LOGOUT-004 PASSED — Session protected after logout. URL: " + urlAfterBack);
    }
}
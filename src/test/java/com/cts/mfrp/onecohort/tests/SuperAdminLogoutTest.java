package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;

@Test(groups = {"regression", "auth", "superadmin"})
@Listeners(ExtentReportListener.class)
public class SuperAdminLogoutTest extends BaseTest {

    private final By avatarBtn = By.cssSelector("div.w-10.h-10.bg-blue-600");
    private final By logoutOption = By.cssSelector("a[routerlink='/login'], a[href='/login']");
    private final By userIdInput = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By loginButton = By.cssSelector("button[type='button']");

    private WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void loginAsSuperAdmin() {
        getDriver().get(ConfigReader.getBaseUrl());
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
        new LoginPage(getDriver()).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));
        System.out.println("Logged in. URL: " + getDriver().getCurrentUrl());
    }

    private void clickAvatar() {
        WebElement avatar = wait.until(ExpectedConditions.elementToBeClickable(avatarBtn));
        avatar.click();
    }

    private boolean isOnLoginPage() {
        String url  = getDriver().getCurrentUrl();
        String base = ConfigReader.getBaseUrl();
        return url.contains("/login")
                || url.equals(base)
                || url.equals(base + "/");
    }

    @Test(priority = 1,
            description = "TC-LOGOUT-001 [FRD 2.1.3]: Clicking avatar shows a Logout option in the dropdown")
    public void logoutOptionVisibleInDropdown() {
        clickAvatar();

        WebElement logout = wait.until(
                ExpectedConditions.visibilityOfElementLocated(logoutOption));

        Assert.assertTrue(logout.isDisplayed(),
                "FAIL - Logout option not visible in profile dropdown [FRD 2.1.3]");
        System.out.println("PASS - TC-LOGOUT-001: Logout option is visible in dropdown.");
    }

    @Test(priority = 2,
            description = "TC-LOGOUT-002 [FRD 2.1]: Clicking Logout redirects to the login page")
    public void logoutRedirectsToLoginPage() {
        clickAvatar();

        WebElement logout = wait.until(
                ExpectedConditions.visibilityOfElementLocated(logoutOption));
        logout.click();
        wait.until(driver -> isOnLoginPage());

        Assert.assertTrue(isOnLoginPage(),
                "FAIL - After logout, expected login page. URL: " + getDriver().getCurrentUrl());
        System.out.println("PASS - TC-LOGOUT-002: Redirected to login page. URL: " + getDriver().getCurrentUrl());
    }

    @Test(priority = 3,
            description = "TC-LOGOUT-003 [FRD 2.1]: After logout, the login form (User ID + Login button) is visible")
    public void loginFormVisibleAfterLogout() {
        clickAvatar();

        WebElement logout = wait.until(
                ExpectedConditions.visibilityOfElementLocated(logoutOption));
        logout.click();

        // Wait for the User ID input to appear
        WebElement userIdField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(userIdInput));

        Assert.assertTrue(userIdField.isDisplayed(),
                "FAIL - User ID input not visible after logout [FRD 2.1]");

        WebElement loginBtn = getDriver().findElement(loginButton);
        Assert.assertTrue(loginBtn.isDisplayed(),
                "FAIL - Login button not visible after logout [FRD 2.1]");

        System.out.println("PASS - TC-LOGOUT-003: Login form is visible after logout.");
    }

    @Test(priority = 4,
            description = "TC-LOGOUT-004 [FRD 2.1]: After logout, pressing browser Back does not restore the dashboard")
    public void backButtonAfterLogoutDoesNotRestoreDashboard() {
        clickAvatar();

        WebElement logout = wait.until(
                ExpectedConditions.visibilityOfElementLocated(logoutOption));
        logout.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(userIdInput));
        System.out.println("On login page. Now pressing Back...");

        getDriver().navigate().back();

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(userIdInput));
        } catch (Exception ignored) {}

        String urlAfterBack = getDriver().getCurrentUrl();
        System.out.println("URL after Back: " + urlAfterBack);

        Assert.assertTrue(isOnLoginPage(),
                "FAIL - After logout + Back, dashboard is still accessible. " +
                        "Session was not cleared properly. URL: " + urlAfterBack);
        System.out.println("PASS - TC-LOGOUT-004: Session protected. URL: " + urlAfterBack);
    }
}
package com.cts.mfrp.onecohort.tests.leader;

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

@Listeners(ExtentReportListener.class)
public class LeaderLoginNegativeTest extends BaseTest {


    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        getDriver().get(ConfigReader.getBaseUrl());
    }


    private WebDriverWait wait(int seconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(seconds));
    }

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

    private WebElement selectLeaderRole() {
        WebElement roleEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.space-y-5 select")));
        highlight(roleEl, "yellow", "Role Dropdown [FRD 11.1]");
        new Select(roleEl).selectByVisibleText("Leader");
        highlight(roleEl, "green", "Role = Leader selected");
        return roleEl;
    }

    private WebElement enterUserId(String value) {
        WebElement el = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(el, "yellow", "User ID Input [FRD 11.1]");
        el.clear();
        if (value != null && !value.isEmpty()) el.sendKeys(value);
        return el;
    }

    private void clickLogin() {
        WebElement btn = wait(10).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Login']")));
        highlight(btn, "yellow", "Login Button [FRD 11.1]");
        btn.click();
    }

    @Test(priority = 1,
            groups = {"negative", "regression"},
            description = "TC-NEG-LDR-001 [FRD 11.1]: Empty User ID — alert fires and page stays on login")
    public void tc_neg_ldr_001_emptyUserId() {
        WebElement userIdEl = enterUserId(""); // locate field, highlight yellow, leave blank
        highlight(userIdEl, "yellow", "User ID — intentionally BLANK [FRD 11.1]");

        selectLeaderRole();
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-LDR-001 | Alert: \"" + alert + "\" | URL: " + getDriver().getCurrentUrl());

        try {
            highlight(getDriver().findElement(By.cssSelector("input[placeholder='e.g. 123456']")),
                    "green", "Validation triggered correctly");
        } catch (Exception ignored) {}

        Assert.assertTrue(isOnLoginPage(),
                "FAIL [FRD 11.1] — Page should stay on login with empty User ID. URL: " + getDriver().getCurrentUrl());
        Assert.assertEquals(alert, AppConstants.ALERT_EMPTY_USER_ID,
                "FAIL [FRD 11.1] — Alert should be '" + AppConstants.ALERT_EMPTY_USER_ID + "'. Got: " + alert);
        System.out.println("TC-NEG-LDR-001 PASSED.");
    }

    @Test(priority = 2,
            groups = {"negative", "regression"},
            description = "TC-NEG-LDR-002 [FRD 11.1]: Leader role with no Service Line — alert fires")
    public void tc_neg_ldr_002_noServiceLine() {
        enterUserId(ConfigReader.getLeaderUserId());
        selectLeaderRole();
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-LDR-002 | Alert: \"" + alert + "\" | URL: " + getDriver().getCurrentUrl());

        Assert.assertTrue(isOnLoginPage(),
                "FAIL [FRD 11.1] — Page should stay on login when Service Line is missing. URL: " + getDriver().getCurrentUrl());
        Assert.assertEquals(alert, AppConstants.ALERT_SELECT_SERVICE_LINE,
                "FAIL [FRD 11.1] — Alert should be '" + AppConstants.ALERT_SELECT_SERVICE_LINE + "'. Got: " + alert);
        System.out.println("TC-NEG-LDR-002 PASSED.");
    }

    @Test(priority = 3,
            groups = {"negative", "regression"},
            description = "TC-NEG-LDR-003 [FRD 11.1]: Service Line dropdown hidden before Leader role selected")
    public void tc_neg_ldr_003_serviceLineHiddenBeforeRoleSelect() {
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(userIdEl, "green", "Login page loaded [FRD 11.1]");

        WebElement roleEl = getDriver().findElement(By.cssSelector("div.space-y-5 select"));
        highlight(roleEl, "yellow", "Role Dropdown — DEFAULT (not Leader) [FRD 11.1]");

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        List<WebElement> allSelects = getDriver().findElements(By.cssSelector("select"));
        boolean serviceLineVisible = allSelects.stream()
                .filter(s -> !s.equals(roleEl))
                .anyMatch(WebElement::isDisplayed);
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (serviceLineVisible) {
            allSelects.stream()
                    .filter(s -> !s.equals(roleEl) && s.isDisplayed())
                    .forEach(s -> highlight(s, "red", "Service Line visible before Leader selected — VIOLATION [FRD 11.1]"));
        } else {
            highlight(roleEl, "green", "Service Line correctly hidden before Leader role selected");
        }

        Assert.assertFalse(serviceLineVisible,
                "FAIL [FRD 11.1] — Service Line dropdown should NOT be visible before Leader role is selected.");
        System.out.println("TC-NEG-LDR-003 PASSED. Service Line correctly hidden before role selection.");
    }

    @Test(priority = 4,
            groups = {"negative", "regression"},
            description = "TC-NEG-LDR-004 [FRD 11.1]: All fields blank — clicking Login shows validation alert")
    public void tc_neg_ldr_004_allFieldsBlank() {
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='e.g. 123456']")));
        highlight(userIdEl, "yellow", "User ID — intentionally BLANK [FRD 11.1]");

        WebElement roleEl = getDriver().findElement(By.cssSelector("div.space-y-5 select"));
        highlight(roleEl, "yellow", "Role — not changed [FRD 11.1]");

        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-LDR-004 | Alert: \"" + alert + "\" | URL: " + getDriver().getCurrentUrl());

        try {
            highlight(getDriver().findElement(By.cssSelector("input[placeholder='e.g. 123456']")),
                    "green", "Blank login correctly rejected");
        } catch (Exception ignored) {}

        Assert.assertTrue(isOnLoginPage(),
                "FAIL [FRD 11.1] — Page should stay on login when all fields are blank. URL: " + getDriver().getCurrentUrl());
        Assert.assertNotNull(alert,
                "FAIL [FRD 11.1] — A validation alert must appear when Login is clicked with no fields filled.");
        System.out.println("TC-NEG-LDR-004 PASSED.");
    }
}
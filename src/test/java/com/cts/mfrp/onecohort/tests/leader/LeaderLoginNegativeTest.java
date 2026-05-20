package com.cts.mfrp.onecohort.tests.leader;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
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

    private final By userIdField = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown = By.cssSelector("select.ng-touched, div.space-y-5 div:nth-of-type(2) select");
    private final By serviceLineDropdown = By.cssSelector("div.space-y-5 div:nth-of-type(3) select");
    private final By loginButton = By.xpath("//button[text()=' Login ']");

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
        String url = getDriver().getCurrentUrl();
        String base = ConfigReader.getBaseUrl();
        if (url.contains("/login") || url.equals(base) || url.equals(base + "/")) {
            return true;
        }
        return !getDriver().findElements(userIdField).isEmpty();
    }

    @Test(priority = 1, groups = {"negative", "regression"}, description = "TC-NEG-LDR-001 [FRD 11.1]: Empty User ID")
    public void tc_neg_ldr_001_emptyUserId() {
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(userIdField));
        userIdEl.clear();

        WebElement roleEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(roleDropdown));
        Select selectRole = new Select(roleEl);
        selectRole.selectByVisibleText("Leader");

        WebElement btn = wait(10).until(ExpectedConditions.elementToBeClickable(loginButton));
        btn.click();

        String alert = getAlertText();
        Assert.assertTrue(isOnLoginPage());
        Assert.assertEquals(alert, AppConstants.ALERT_EMPTY_USER_ID);
    }

    @Test(priority = 2, groups = {"negative", "regression"}, description = "TC-NEG-LDR-002 [FRD 11.1]: Leader role with no Service Line")
    public void tc_neg_ldr_002_noServiceLine() {
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(userIdField));
        userIdEl.clear();
        userIdEl.sendKeys(ConfigReader.getLeaderUserId());

        WebElement roleEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(roleDropdown));
        Select selectRole = new Select(roleEl);
        selectRole.selectByVisibleText("Leader");

        WebElement btn = wait(10).until(ExpectedConditions.elementToBeClickable(loginButton));
        btn.click();

        String alert = getAlertText();
        Assert.assertTrue(isOnLoginPage());
        Assert.assertEquals(alert, AppConstants.ALERT_SELECT_SERVICE_LINE);
    }

    @Test(priority = 3, groups = {"negative", "regression"}, description = "TC-NEG-LDR-003 [FRD 11.1]: Service Line dropdown hidden check")
    public void tc_neg_ldr_003_serviceLineHiddenBeforeRoleSelect() {
        wait(10).until(ExpectedConditions.visibilityOfElementLocated(userIdField));

        List<WebElement> serviceLines = getDriver().findElements(serviceLineDropdown);
        boolean isVisible = false;
        if (!serviceLines.isEmpty()) {
            isVisible = serviceLines.get(0).isDisplayed();
        }

        Assert.assertFalse(isVisible);
    }

    @Test(priority = 4, groups = {"negative", "regression"}, description = "TC-NEG-LDR-004 [FRD 11.1]: All fields blank")
    public void tc_neg_ldr_004_allFieldsBlank() {
        WebElement userIdEl = wait(10).until(ExpectedConditions.visibilityOfElementLocated(userIdField));
        userIdEl.clear();

        WebElement btn = wait(10).until(ExpectedConditions.elementToBeClickable(loginButton));
        btn.click();

        String alert = getAlertText();
        Assert.assertTrue(isOnLoginPage());
        Assert.assertNotNull(alert);
    }
}
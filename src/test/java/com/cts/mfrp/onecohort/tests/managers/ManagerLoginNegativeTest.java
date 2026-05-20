package com.cts.mfrp.onecohort.tests.managers;

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
public class ManagerLoginNegativeTest extends BaseTest {

    private final By userIdInput  = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown = By.cssSelector("div.space-y-5 select");
    private final By loginButton  = By.cssSelector("button[type='button']");

    private WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        getDriver().get(ConfigReader.getBaseUrl());
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(userIdInput));
    }

    private void enterUserId(String value) {
        WebElement input = getDriver().findElement(userIdInput);
        input.clear();
        if (!value.isEmpty()) {
            input.sendKeys(value);
        }
    }

    private void selectRole(String roleName) {
        WebElement dropdown = getDriver().findElement(roleDropdown);
        new Select(dropdown).selectByVisibleText(roleName);
    }

    private void clickLogin() {
        getDriver().findElement(loginButton).click();
    }

    private String getAlertText() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            String message = getDriver().switchTo().alert().getText();
            getDriver().switchTo().alert().accept();
            return message;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isOnLoginPage() {
        String url = getDriver().getCurrentUrl();
        return url.contains("/login")
                || url.equals(ConfigReader.getBaseUrl())
                || url.equals(ConfigReader.getBaseUrl() + "/");
    }

    @Test(priority = 1,
            description = "TC-NEG-MGR-001 [FRD 3.3]: Empty User ID — alert fires and stays on login page")
    public void tc_neg_mgr_001_emptyUserId() {
        enterUserId("");
        selectRole("Manager");
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-MGR-001 | Alert: " + alert);

        Assert.assertTrue(isOnLoginPage(),
                "FAIL - Page should stay on login when User ID is empty [FRD 3.3]");
        Assert.assertEquals(alert, AppConstants.ALERT_EMPTY_USER_ID,
                "FAIL - Wrong alert message for empty User ID [FRD 3.3]");

        System.out.println("PASS - TC-NEG-MGR-001: Empty User ID correctly rejected.");
    }

    @Test(priority = 2,
            description = "TC-NEG-MGR-002 [FRD 3.3]: Manager role with no Service Line — alert fires")
    public void tc_neg_mgr_002_noServiceLine() {
        enterUserId(ConfigReader.getManagerUserId());
        selectRole("Manager");
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-MGR-002 | Alert: " + alert);

        Assert.assertTrue(isOnLoginPage(),
                "FAIL - Page should stay on login when Service Line is missing [FRD 3.3]");
        Assert.assertEquals(alert, AppConstants.ALERT_SELECT_SERVICE_LINE,
                "FAIL - Wrong alert message for missing Service Line [FRD 3.3]");

        System.out.println("PASS - TC-NEG-MGR-002: Missing Service Line correctly rejected.");
    }

    @Test(priority = 3,
            description = "TC-NEG-MGR-003 [FRD 3.3]: Service Line dropdown is hidden before Manager role is selected")
    public void tc_neg_mgr_003_serviceLineHiddenBeforeRoleSelect() {
        WebElement roleSelect = getDriver().findElement(roleDropdown);

        List<WebElement> allSelects = getDriver().findElements(By.cssSelector("select"));

        boolean serviceLineVisible = allSelects.stream()
                .filter(s -> !s.equals(roleSelect))
                .anyMatch(WebElement::isDisplayed);

        Assert.assertFalse(serviceLineVisible,
                "FAIL - Service Line dropdown should NOT be visible before Manager role is selected [FRD 3.3]");

        System.out.println("PASS - TC-NEG-MGR-003: Service Line correctly hidden before Manager is selected.");
    }

    @Test(priority = 4,
            description = "TC-NEG-MGR-004 [FRD 3.3]: All fields blank — clicking Login shows a validation alert")
    public void tc_neg_mgr_004_allFieldsBlank() {
        clickLogin();

        String alert = getAlertText();
        System.out.println("TC-NEG-MGR-004 | Alert: " + alert);

        Assert.assertTrue(isOnLoginPage(),
                "FAIL - Page should stay on login when all fields are blank [FRD 3.3]");
        Assert.assertNotNull(alert,
                "FAIL - A validation alert must appear when Login is clicked with no input [FRD 3.3]");

        System.out.println("PASS - TC-NEG-MGR-004: Blank login correctly rejected. Alert: " + alert);
    }
}
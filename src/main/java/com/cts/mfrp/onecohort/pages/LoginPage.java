package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    private final By userIdInput         = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown        = By.cssSelector("div.space-y-5 select");
    private final By serviceLineDropdown = By.xpath("/html/body/app-root/app-login/div/div/div[2]/div[3]/select");
    private final By pocIdInput          = By.cssSelector("input[placeholder='e.g. USR-40002']");
    private final By cohortIdInput       = By.cssSelector("input[placeholder='e.g. COH-10001']");
    private final By loginButton         = By.xpath("//button[normalize-space()='Login']");

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

    public LoginPage selectServiceLine(String serviceLineId) {
        Select select = new Select(driver.findElement(serviceLineDropdown));
        select.selectByVisibleText(serviceLineId);
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

    public String acceptAlertAndGetMessage() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String message = alert.getText();
        alert.accept();
        return message;
    }

    public boolean isOnLoginPage() {
        return !driver.findElements(userIdInput).isEmpty();
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
}
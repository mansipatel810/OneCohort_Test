package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class LeftNavPanelTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        // Setup Chrome
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--window-size=1920,1080", "--no-sandbox");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Open login page
        driver.get(ConfigReader.getBaseUrl());

        // Enter User ID
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[placeholder='e.g. 123456']")))
                // NEW - correct
                .sendKeys(ConfigReader.getSuperAdminUserId());

        // Select Role from dropdown → Super Admin
        WebElement roleDropdown = driver.findElement(By.cssSelector("select"));
        Select select = new Select(roleDropdown);
        select.selectByVisibleText("Super Admin");

        // Click Login button
        driver.findElement(By.cssSelector("button[type='button']")).click();

        // Wait for dashboard to load
        wait.until(ExpectedConditions.urlContains("dashboard"));
    }

    @Test(priority = 1)
    public void testDashboardNav() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/super-admin/dashboard']")));
        link.click();
        wait.until(ExpectedConditions.urlContains("dashboard"));
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"),
                "Dashboard page not loaded!");
    }

    @Test(priority = 2)
    public void testCohortManagementNav() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/super-admin/cohorts']")));
        link.click();
        wait.until(ExpectedConditions.urlContains("cohorts"));
        Assert.assertTrue(driver.getCurrentUrl().contains("cohorts"),
                "Cohort Management page not loaded!");
    }

    @Test(priority = 3)
    public void testManagersLeadershipNav() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/super-admin/leadership']")));
        link.click();
        wait.until(ExpectedConditions.urlContains("leadership"));
        Assert.assertTrue(driver.getCurrentUrl().contains("leadership"),
                "Managers & Leadership page not loaded!");
    }

    @Test(priority = 4)
    public void testBatchOwnersNav() {
        WebElement link;
        try {
            link = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/super-admin/batch-owners']")));
        } catch (Exception e) {
            link = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href*='batch']")));
        }
        link.click();
        wait.until(ExpectedConditions.urlContains("batch"));
        Assert.assertTrue(driver.getCurrentUrl().contains("batch"),
                "Batch Owners page not loaded!");
    }

    @Test(priority = 5)
    public void testTrainingProgressNav() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/super-admin/training-progress']")));
        link.click();
        wait.until(ExpectedConditions.urlContains("training"));
        Assert.assertTrue(driver.getCurrentUrl().contains("training"),
                "Training Progress page not loaded!");
    }

    @Test(priority = 6)
    public void testSystemConfigNav() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/super-admin/system-config']")));
        link.click();
        wait.until(ExpectedConditions.urlContains("system-config"));
        Assert.assertTrue(driver.getCurrentUrl().contains("system-config"),
                "System Configuration page not loaded!");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;

import java.time.Duration;
import java.util.List;

@Listeners(ExtentReportListener.class)
public class HomePageTest {

    private WebDriver driver;
    /** Exposed for ExtentReportListener screenshot capture. */
    public WebDriver getDriver() { return driver; }
    private WebDriverWait wait;
    private void highlight(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red'", element);
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--window-size=1920,1080", "--no-sandbox");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(ConfigReader.getBaseUrl());

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[placeholder='e.g. 123456']")))
                .sendKeys(ConfigReader.getSuperAdminUserId());

        WebElement roleDropdown = driver.findElement(By.cssSelector("select"));
        Select select = new Select(roleDropdown);
        select.selectByVisibleText("Super Admin");

        driver.findElement(By.cssSelector("button[type='button']")).click();

        wait.until(ExpectedConditions.urlContains("dashboard"));
        System.out.println("Login successful - Dashboard loaded");
    }

    // -------------------------------------------------------
    // TEST 1 - Check Logo is visible
    // FRD 2.1.1 - Application logo displayed as OC
    // -------------------------------------------------------
    @Test(priority = 1)
    public void checkLogo() {
        WebElement logo = driver.findElement(By.cssSelector("img[alt='One Cohort']"));
        highlight(logo);
        Assert.assertTrue(logo.isDisplayed(), "Logo is not visible");
        System.out.println("PASS - Logo is visible");
    }

    // -------------------------------------------------------
    // TEST 2 - Check App Name "One Cohort" is visible
    // FRD 2.1.1 - Application name One Cohort
    // -------------------------------------------------------
    @Test(priority = 2)
    public void checkAppName() {
        WebElement appName = driver.findElement(By.cssSelector(".logo-section span"));
        highlight(appName);
        Assert.assertTrue(appName.getText().contains("One Cohort"), "App name is wrong");
        System.out.println("PASS - App name is: " + appName.getText());
    }

    // -------------------------------------------------------
    // TEST 3 - Check Subtitle "Super User Command Center"
    // FRD 2.1.1 - Subtitle must be visible in header
    // NOTE: If not present in build - logged as GAP
    // -------------------------------------------------------
    @Test(priority = 3)
    public void checkSubtitle() {
        List<WebElement> subtitle = driver.findElements(
                By.xpath("//*[contains(text(),'Super User Command Center')]"));

        // If subtitle is NOT found → test FAILS
        Assert.assertFalse(subtitle.isEmpty(),
                "FAIL - Subtitle 'Super User Command Center' is NOT present");

        // If found → check it is visible
        Assert.assertTrue(subtitle.get(0).isDisplayed(),
                "FAIL - Subtitle found but NOT visible");

        System.out.println("PASS - Subtitle 'Super User Command Center' is visible");
    }

    // -------------------------------------------------------
    // TEST 4 - Check Super User label is visible (top right)
    // FRD 2.1.1 - Logged in role indicator
    // -------------------------------------------------------
    @Test(priority = 4)
    public void checkSuperUserLabel() {
        WebElement roleLabel = driver.findElement(By.cssSelector(".text-gray-700"));
        highlight(roleLabel);
        Assert.assertTrue(roleLabel.getText().contains("Super User"), "Role label is wrong");
        System.out.println("PASS - Role label shows: " + roleLabel.getText());
    }

    // -------------------------------------------------------
    // TEST 5 - Check SU blue avatar is visible (top right)
    // FRD 2.1.1 - User profile icon
    // -------------------------------------------------------
    @Test(priority = 5)
    public void checkSUAvatar() {
        WebElement avatar = driver.findElement(By.cssSelector(".bg-blue-600.rounded-full"));
        highlight(avatar);
        Assert.assertTrue(avatar.isDisplayed(), "SU avatar is not visible");
        System.out.println("PASS - SU avatar is visible");
    }

    // -------------------------------------------------------
    // TEST 6 - Check User profile icon has dropdown menu
    // FRD 2.1.1 - User profile icon with dropdown menu options
    // HOW: Click SU avatar → dropdown should appear
    // NOTE: If dropdown not present - logged as GAP
    // -------------------------------------------------------
    @Test(priority = 6)
    public void checkProfileDropdown() {
       // Click the SU avatar
        driver.findElement(By.cssSelector(".bg-blue-600.rounded-full")).click();

        // Check if dropdown appeared inside header
        List<WebElement> dropdown = driver.findElements(
                By.cssSelector("app-header ul"));

        // If empty = no dropdown = FAIL
        Assert.assertFalse(dropdown.isEmpty(),
                "FAIL - Profile dropdown not present");

        System.out.println("PASS - Profile dropdown is visible");
    }

    // -------------------------------------------------------
    // TEST 7 - Check Welcome message is visible
    // FRD 2.1.5 - Dashboard greets the logged in user
    // -------------------------------------------------------
    @Test(priority = 7)
    public void checkWelcomeMessage() {
        WebElement welcome = driver.findElement(
                By.xpath("//*[contains(text(),'Welcome')]"));
        Assert.assertTrue(welcome.isDisplayed(), "Welcome message not visible");
        highlight(welcome);
        System.out.println("PASS - Welcome message: " + welcome.getText());
    }

    // -------------------------------------------------------
    // TEST 8 - Check Left Sidebar is visible
    // FRD 2.1.2 - Left Navigation Panel
    // -------------------------------------------------------
    @Test(priority = 8)
    public void checkSidebar() {
        WebElement sidebar = driver.findElement(By.cssSelector(".sidebar"));
        highlight(sidebar);
        Assert.assertTrue(sidebar.isDisplayed(), "Sidebar is not visible");
        System.out.println("PASS - Sidebar is visible");
    }

    // -------------------------------------------------------
    // TEST 9 - Check all menu items in sidebar
    // FRD 2.1.2 - All navigation menu items must be present
    // -------------------------------------------------------
    @Test(priority = 9)
    public void checkMenuItems() {
        String[] menuItems = {
                "Dashboard",
                "Cohort Management",
                "Managers",
                "Batch Owners",
                "Training Progress",
                "System Configuration"
        };

        for (String item : menuItems) {
            WebElement menuEl = driver.findElement(
                    By.xpath("//nav[contains(@class,'menu')]" +
                            "//*[contains(text(),'" + item + "')]"));
            highlight(menuEl);
            Assert.assertTrue(menuEl.isDisplayed(), item + " not visible in menu");
            System.out.println("PASS - Menu item visible: " + item);
        }
    }

    // -------------------------------------------------------
    // TEST 10 - Check Super User in right panel
    // FRD 2.1.3 - Right Frame User Controls
    // -------------------------------------------------------
    @Test(priority = 10)
    public void checkRightUserPanel() {
        WebElement superUser = driver.findElement(
                By.cssSelector("app-header .text-gray-700"));
        highlight(superUser);
        Assert.assertTrue(superUser.getText().contains("Super User"),
                "Super User not shown in header");
        System.out.println("PASS - Right panel shows: " + superUser.getText());
    }

    // -------------------------------------------------------
    // TEST 11 - Check Search bar gap
    // FRD 2.1.4 - Search bar not present in current build
    // -------------------------------------------------------
    @Test(priority = 11)
    public void checkSearchBarGap() {
        List<WebElement> searchBar = driver.findElements(
                By.cssSelector("input[type='search']"));

        // If NOT found → FAIL
        Assert.assertFalse(searchBar.isEmpty(),
                "FAIL - Search bar is NOT present. FRD 2.1.4 requires it.");

        System.out.println("PASS - Search bar is present");
    }

    // -------------------------------------------------------
    // TEST 12 - Check Total Cohorts card is visible
    // FRD 2.1.5.1 - Summary Metrics Cards
    // -------------------------------------------------------
    @Test(priority = 12)
    public void checkTotalCohortsCard() {
        WebElement card = driver.findElement(
                By.xpath("//h3[contains(text(),'Total Cohorts')]"));
        highlight(card);
        Assert.assertTrue(card.isDisplayed(), "Total Cohorts card not visible");
        System.out.println("PASS - Total Cohorts card is visible");
    }

    // -------------------------------------------------------
    // TEST 13 - Check Active card is visible
    // FRD 2.1.5.1 - Active Cohorts card
    // -------------------------------------------------------
    @Test(priority = 13)
    public void checkActiveCard() {
        WebElement card = driver.findElement(
                By.xpath("//h3[contains(text(),'Active')]"));
        highlight(card);
        Assert.assertTrue(card.isDisplayed(), "Active card not visible");
        System.out.println("PASS - Active card is visible");
    }

    // -------------------------------------------------------
    // TEST 14 - Check Completed card is visible
    // FRD 2.1.5.1 - Completed Cohorts card
    // -------------------------------------------------------
    @Test(priority = 14)
    public void checkCompletedCard() {
        WebElement card = driver.findElement(
                By.xpath("//h3[contains(text(),'Completed')]"));
        highlight(card);
        Assert.assertTrue(card.isDisplayed(), "Completed card not visible");
        System.out.println("PASS - Completed card is visible");
    }

    // -------------------------------------------------------
    // TEST 15 - Check Upcoming card is visible
    // FRD 2.1.5.1 - Upcoming Cohorts card
    // -------------------------------------------------------
    @Test(priority = 15)
    public void checkUpcomingCard() {
        WebElement card = driver.findElement(
                By.xpath("//h3[contains(text(),'Upcoming')]"));
        highlight(card);
        Assert.assertTrue(card.isDisplayed(), "Upcoming card not visible");
        System.out.println("PASS - Upcoming card is visible");
    }

    // -------------------------------------------------------
    // TEST 16 - Check all KPI cards show a number
    // FRD 2.1.5.1 - Each card must display a value
    // -------------------------------------------------------
    @Test(priority = 16)
    public void checkKpiNumbers() {
        List<WebElement> numbers = driver.findElements(By.cssSelector("p.kpi-number"));
        Assert.assertTrue(numbers.size() >= 4, "Less than 4 KPI cards found");
        for (WebElement num : numbers) {
            Assert.assertFalse(num.getText().trim().isEmpty(), "A card has no number");
            System.out.println("PASS - KPI card shows: " + num.getText());
        }
    }

    // -------------------------------------------------------
    // TEARDOWN - Closes browser after all tests
    // -------------------------------------------------------
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("Browser closed - All tests done!");
    }
}
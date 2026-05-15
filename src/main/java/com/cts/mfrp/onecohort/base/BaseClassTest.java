package com.cts.mfrp.onecohort.base;

import com.cts.mfrp.onecohort.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;
import java.util.List;

/**
 * Base class for test suites that share ONE browser session across all test methods.
 *
 * Uses @BeforeClass / @AfterClass lifecycle — one driver is created per test class,
 * not per test method. This is appropriate for integration-style tests where all
 * tests in a class flow through a single navigated session.
 *
 * Subclass @BeforeClass methods must call super.setUpDriver() first (or call
 * alwaysRun=true so it runs unconditionally), then do their own navigation.
 *
 * Provides:
 *  - protected WebDriver driver
 *  - protected WebDriverWait wait
 *  - public WebDriver getDriver()  — for ExtentReportListener screenshot capture
 *  - protected void highlight(WebElement)  — red border + scroll into view
 */
public class BaseClassTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    /** Exposed for ExtentReportListener screenshot capture via reflection. */
    public WebDriver getDriver() {
        return driver;
    }

    @BeforeClass(alwaysRun = true)
    public void setUpDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--window-size=1920,1080", "--no-sandbox");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
    }

    @AfterClass(alwaysRun = true)
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Highlights a WebElement with a red border and scrolls it into view.
     * Used in test methods to visually mark the element being asserted.
     */
    protected void highlight(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.border='3px solid red'", element);
            js.executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception ignored) {}
    }

    /**
     * Highlights a WebElement with a coloured border — useful for visual test feedback.
     *
     * @param element the element to highlight
     * @param color   "yellow" = locating | "green" = passed | "red" = failed/default
     * @param label   short label set as the element's title tooltip in the browser
     */
    protected void highlight(WebElement element, String color, String label) {
        try {
            String border = switch (color) {
                case "green" -> "3px solid #22c55e";
                case "red"   -> "3px solid #ef4444";
                default      -> "3px solid #f59e0b"; // yellow
            };
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.border     = '" + border + "';" +
                    "arguments[0].style.boxShadow  = '0 0 6px 2px " + color + "';" +
                    "arguments[0].style.transition = 'all 0.2s ease';" +
                    "arguments[0].setAttribute('title', 'TESTING: " + label + "');",
                    element);
            Thread.sleep(400);
        } catch (Exception ignored) {}
    }

    /**
     * Highlights all elements in a list with a coloured border.
     *
     * @param elements list of elements to highlight
     * @param color    "yellow" | "green" | "red"
     * @param label    tooltip label
     */
    protected void highlightAll(List<WebElement> elements, String color, String label) {
        for (WebElement el : elements) {
            highlight(el, color, label);
        }
    }

    /**
     * Checks if an element exists in the DOM (does not require it to be visible).
     * Uses a zero-second implicit wait to avoid slowdowns.
     */
    protected boolean elementExists(By locator) {
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        boolean found = !driver.findElements(locator).isEmpty();
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));
        return found;
    }
}

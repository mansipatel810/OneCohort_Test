package com.cts.mfrp.onecohort.base;

import com.cts.mfrp.onecohort.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriver driver = createDriver();
        driverThreadLocal.set(driver);
        driver.get(ConfigReader.getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driverThreadLocal.remove();
        }
    }

    /** Exposed so ExtentReportListener can capture screenshots via reflection. */
    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    private WebDriver createDriver() {
        String browser  = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();
        WebDriver driver;

        switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions opts = new FirefoxOptions();
                if (headless) opts.addArguments("--headless");
                driver = new FirefoxDriver(opts);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions opts = new EdgeOptions();
                if (headless) opts.addArguments("--headless");
                driver = new EdgeDriver(opts);
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
                if (headless) opts.addArguments("--headless", "--disable-gpu");
                opts.addArguments("--window-size=1920,1080", "--no-sandbox");
                driver = new ChromeDriver(opts);
            }
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().window().maximize();
        return driver;
    }
}

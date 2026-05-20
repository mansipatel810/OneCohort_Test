package com.cts.mfrp.onecohort.pages;

import com.cts.mfrp.onecohort.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        PageFactory.initElements(driver, this);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
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
                java.time.Duration.ofSeconds(com.cts.mfrp.onecohort.utils.ConfigReader.getImplicitWait()));
        return found;
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
        } catch (Exception ignored) {}
    }

    /**
     * Clicks an element using JavaScript — useful for elements that are
     * overlapped or not directly clickable via Selenium's normal click().
     */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /** Simple red-border highlight + scroll into view. */
    protected void highlight(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.border='3px solid red'", element);
            js.executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception ignored) {}
    }

    public String getPageTitle()   { return driver.getTitle(); }
    public String getCurrentUrl()  { return driver.getCurrentUrl(); }
}

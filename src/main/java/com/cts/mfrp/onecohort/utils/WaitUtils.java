package com.cts.mfrp.onecohort.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitUtils {

    public static WebElement waitForVisible(WebDriverWait wait, By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(WebDriverWait wait, By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void waitForInvisible(WebDriverWait wait, By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static void waitForUrlContaining(WebDriverWait wait, String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    public static Alert waitForAlert(WebDriverWait wait) {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    public static List<WebElement> waitForPresenceOfAll(WebDriverWait wait, By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Polls until the count of elements matching {@code rowLocator} stops changing
     * for two consecutive polls 300 ms apart.
     * Handles Angular live-search debounce without using Thread.sleep.
     */
    public static void waitForResultsToSettle(WebDriver driver, By rowLocator, int timeoutSeconds) {
        int[] lastCount = {-1};
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class)
                .until(d -> {
                    int count = d.findElements(rowLocator).size();
                    if (count == lastCount[0]) return true;
                    lastCount[0] = count;
                    return false;
                });
    }
}

package com.cts.mfrp.onecohort.pages.cohort;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Collections;
import java.util.List;

public class CohortFilterDropdownComponent extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By searchInput = By.cssSelector(
            "input[type='search'], " +
            "input[type='text'][placeholder*='Search'], " +
            "input[type='text'][placeholder*='search'], " +
            "input[formcontrolname*='search']");

    private final By statusDropdown = By.cssSelector(
            "select[name='status'], select#statusFilter, " +
            "select[formcontrolname='status'], select[formcontrolname='statusFilter']");

    private final By serviceLineDropdown = By.cssSelector(
            "select[name='serviceLine'], select#serviceLineFilter, " +
            "select[formcontrolname='serviceLine'], select[formcontrolname='serviceLineId']");

    private final By learningPathDropdown = By.cssSelector(
            "select[name='learningPath'], select#learningPathFilter, " +
            "select[formcontrolname='learningPath'], select[formcontrolname='learningPathId']");

    // ── Constructor ───────────────────────────────────────────────────────────
    public CohortFilterDropdownComponent(WebDriver driver) {
        super(driver);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    public boolean isSearchInputVisible() {
        return isDisplayed(searchInput);
    }

    public void search(String keyword) {
        try {
            WebElement input = waitForVisible(searchInput);
            input.clear();
            input.sendKeys(keyword);
        } catch (Exception ignored) {}
    }

    public void clearSearch() {
        try {
            WebElement input = waitForVisible(searchInput);
            input.clear();
        } catch (Exception ignored) {}
    }

    // ── Status filter ─────────────────────────────────────────────────────────

    public boolean isStatusDropdownVisible() {
        return isDisplayed(statusDropdown);
    }

    public List<WebElement> getStatusOptions() {
        try {
            return new Select(driver.findElement(statusDropdown)).getOptions();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void selectStatus(String statusValue) {
        try {
            Select select = new Select(driver.findElement(statusDropdown));
            // Try visible text first, then value attribute
            try {
                select.selectByVisibleText(statusValue);
            } catch (Exception e) {
                select.selectByValue(statusValue);
            }
        } catch (Exception ignored) {}
    }

    // ── Service Line filter ───────────────────────────────────────────────────

    public boolean isServiceLineDropdownVisible() {
        return isDisplayed(serviceLineDropdown);
    }

    public void selectServiceLine(String serviceLineId) {
        try {
            Select select = new Select(driver.findElement(serviceLineDropdown));
            try {
                select.selectByValue(serviceLineId);
            } catch (Exception e) {
                select.selectByVisibleText(serviceLineId);
            }
        } catch (Exception ignored) {}
    }

    // ── Learning Path filter ──────────────────────────────────────────────────

    public boolean isLearningPathDropdownVisible() {
        return isDisplayed(learningPathDropdown);
    }

    public void selectLearningPath(String learningPath) {
        try {
            Select select = new Select(driver.findElement(learningPathDropdown));
            try {
                select.selectByVisibleText(learningPath);
            } catch (Exception e) {
                select.selectByValue(learningPath);
            }
        } catch (Exception ignored) {}
    }
}

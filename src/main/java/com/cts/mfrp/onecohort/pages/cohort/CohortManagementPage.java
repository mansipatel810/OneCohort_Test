package com.cts.mfrp.onecohort.pages.cohort;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class CohortManagementPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By dataTable       = By.cssSelector("table");
    private final By tableHeaders    = By.cssSelector("table thead th, table thead td");
    private final By tableRows       = By.cssSelector("table tbody tr");
    private final By createCohortBtn = By.xpath(
            "//button[contains(normalize-space(),'Create Cohort') " +
            "or contains(normalize-space(),'Add Cohort') " +
            "or contains(normalize-space(),'New Cohort')]");
    private final By searchInput     = By.cssSelector(
            "input[type='search'], " +
            "input[type='text'][placeholder*='Search'], " +
            "input[type='text'][placeholder*='search'], " +
            "input[formcontrolname*='search']");
    private final By emptyState      = By.xpath(
            "//*[contains(normalize-space(),'No data found') " +
            "or contains(normalize-space(),'No records')]");
    private final By modalOverlay    = By.cssSelector("[class*='modal'], [role='dialog']");

    public CohortManagementPage(WebDriver driver) {
        super(driver);
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    public boolean isTableVisible() {
        return isDisplayed(dataTable);
    }

    /** Alias for isTableVisible() — kept for backward compatibility with EmployeeTest. */
    public boolean isGridLoaded() {
        return isTableVisible();
    }

    public List<WebElement> getTableHeaders() {
        try {
            return driver.findElements(tableHeaders);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<WebElement> getTableRows() {
        try {
            return driver.findElements(tableRows);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Returns action buttons/icons from the last cell of the first data row. */
    public List<WebElement> getFirstRowActionElements() {
        try {
            List<WebElement> rows = getTableRows();
            if (rows.isEmpty()) return Collections.emptyList();
            WebElement lastCell = rows.get(0).findElement(By.cssSelector("td:last-child"));
            List<WebElement> actions = lastCell.findElements(
                    By.cssSelector("button, a, [class*='action'], [class*='icon']"));
            return actions;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    public boolean isSearchInputVisible() {
        return isDisplayed(searchInput);
    }

    public void searchByKeyword(String keyword) {
        try {
            WebElement input = waitForVisible(searchInput);
            input.clear();
            input.sendKeys(keyword);
        } catch (Exception ignored) {}
    }

    // ── Create Cohort ─────────────────────────────────────────────────────────

    public boolean isCreateCohortButtonVisible() {
        return isDisplayed(createCohortBtn);
    }

    public void clickCreateCohort() {
        click(createCohortBtn);
    }

    // ── Empty state ───────────────────────────────────────────────────────────

    public boolean isEmptyStateDisplayed() {
        return isDisplayed(emptyState);
    }

    // ── Modal ─────────────────────────────────────────────────────────────────

    /** Closes any open modal — tries Cancel/Close button first, falls back to JS Escape. */
    public void closeModal() {
        try {
            List<WebElement> closeBtns = driver.findElements(By.xpath(
                    "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Cancel') " +
                    "or contains(normalize-space(),'Close') " +
                    "or contains(normalize-space(),'×')]"));
            if (!closeBtns.isEmpty()) {
                closeBtns.get(0).click();
                return;
            }
            // Fallback — dispatch Escape key via JavaScript
            ((JavascriptExecutor) driver).executeScript(
                    "document.dispatchEvent(new KeyboardEvent('keydown',{'key':'Escape','bubbles':true}))");
        } catch (Exception ignored) {}
    }
}

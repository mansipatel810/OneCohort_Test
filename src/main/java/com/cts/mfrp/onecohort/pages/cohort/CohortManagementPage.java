package com.cts.mfrp.onecohort.pages.cohort;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;

public class CohortManagementPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By specificTable    = By.cssSelector("table.table.table-hover.align-middle");
    private final By tableRowsAligned = By.cssSelector("tbody tr.align-middle");
    private final By tableRows        = By.cssSelector("table.table tbody tr, .table-responsive table tbody tr");
    private final By tableHeaders     = By.cssSelector("table.table thead th, table.table thead td");

    private final By pageHeading      = By.xpath(
            "//*[self::h1 or self::h2 or self::h3 or self::h4]" +
            "[contains(text(),'Cohort Management')]");

    private final By searchInput      = By.cssSelector(
            "input[type='search'], " +
            "input[type='text'][placeholder*='Search'], " +
            "input[type='text'][placeholder*='search'], " +
            "input[placeholder='Search by Cohort ID or Name...'], " +
            "input[formcontrolname*='search']");

    private final By createCohortBtn  = By.xpath(
            "//button[contains(normalize-space(),'Create Cohort') " +
            "or contains(normalize-space(),'Add Cohort') " +
            "or contains(normalize-space(),'New Cohort')]");

    private final By createBtnPrimary = By.cssSelector("button.btn.btn-primary");

    private final By editBtn          = By.cssSelector("button.btn.btn-link.text-muted.p-1");

    private final By filtersBtn       = By.cssSelector("button.btn.btn-outline-secondary");

    private final By modalCard        = By.cssSelector("div.modal-card");
    private final By cancelBtn        = By.cssSelector("button.btn-modal-secondary");

    public CohortManagementPage(WebDriver driver) {
        super(driver);
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    /** Returns all body rows from the cohort table. */
    public List<WebElement> getTableRows() {
        try {
            return driver.findElements(tableRows);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Returns all column header cells from the cohort table. */
    public List<WebElement> getTableHeaders() {
        try {
            return driver.findElements(tableHeaders);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
    }

    public WebElement getPageHeadingElement() {
        return waitForVisible(pageHeading);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    /** Types the keyword into the search bar. Pass "" to clear. */
    public void searchByKeyword(String keyword) {
        try {
            WebElement input = waitForVisible(searchInput);
            input.clear();
            input.sendKeys(keyword);
        } catch (Exception ignored) {}
    }

    // ── Create Cohort ─────────────────────────────────────────────────────────

    /**
     * Clicks the Create Cohort button.
     * Waits for any lingering modal overlay to fade out first to avoid
     * ElementClickInterceptedException from Angular CSS transitions.
     */
    public void clickCreateCohort() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.modal-overlay")));
        } catch (Exception ignored) {}

        try {
            click(createCohortBtn);
        } catch (Exception e) {
            jsClick(driver.findElement(createCohortBtn));
        }
    }

    /** Returns the primary Create button element (used to check isEnabled). */
    public WebElement getCreateBtnPrimaryElement() {
        return driver.findElement(createBtnPrimary);
    }

    // ── Edit buttons ──────────────────────────────────────────────────────────

    /** Returns all Edit action buttons in the table. */
    public List<WebElement> getEditButtons() {
        try {
            return driver.findElements(editBtn);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    public boolean isFiltersBtnVisible() {
        return isDisplayed(filtersBtn);
    }

    // ── Status badges ─────────────────────────────────────────────────────────

    /**
     * Returns status badge elements scoped to table rows.
     * Falls back to the 4th column cells if no badge elements are found.
     */
    public List<WebElement> getStatusBadgesInTable() {
        List<WebElement> result = driver.findElements(By.cssSelector(
                "table tbody tr td [class*='badge'], " +
                "table tbody tr td [class*='status'], " +
                "table tbody tr td .chip, " +
                "table tbody tr td .tag"));
        if (result.isEmpty()) {
            result = driver.findElements(By.cssSelector("table tbody tr td:nth-child(4)"));
        }
        return result;
    }

    // ── Modal ─────────────────────────────────────────────────────────────────

    public boolean isModalCardVisible() {
        return isDisplayed(modalCard);
    }

    /** JS-clicks the modal Cancel button. */
    public void clickCancelBtn() {
        jsClick(driver.findElement(cancelBtn));
    }

    public void waitForModalVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalCard));
    }

    public void waitForModalInvisible() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalCard));
    }

    /**
     * Closes any open modal — tries Cancel/Close button first, falls back to JS Escape.
     * Waits for the overlay to fully disappear before returning.
     */
    public void closeModal() {
        try {
            List<WebElement> closeBtns = driver.findElements(By.xpath(
                    "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Cancel') " +
                    "or contains(normalize-space(),'Close') " +
                    "or contains(normalize-space(),'×')]"));
            if (!closeBtns.isEmpty()) {
                closeBtns.get(0).click();
            } else {
                ((JavascriptExecutor) driver).executeScript(
                        "document.dispatchEvent(new KeyboardEvent('keydown',{'key':'Escape','bubbles':true}))");
            }
        } catch (Exception ignored) {}

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.modal-overlay")));
        } catch (Exception ignored) {}
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(modalCard));
        } catch (Exception ignored) {}
    }

    // ── Waits ─────────────────────────────────────────────────────────────────

    /**
     * Waits for the cohort table to be visible and populated with at least one row.
     * Needed for render.com latency — Angular loads cohort data asynchronously.
     */
    public void waitForTableToLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(specificTable));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(tableRowsAligned, 0));
        } catch (Exception ignored) {}
    }
}

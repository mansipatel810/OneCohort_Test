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

    // Table / grid
    private final By dataTable           = By.cssSelector("table.table, .table-responsive table");
    private final By specificTable       = By.cssSelector("table.table.table-hover.align-middle");
    private final By tableHeaders        = By.cssSelector("table.table thead th, table.table thead td");
    private final By tableHeaderLight    = By.cssSelector("thead.table-light");
    private final By tableRows           = By.cssSelector("table.table tbody tr, .table-responsive table tbody tr");
    private final By tableRowsAligned    = By.cssSelector("tbody tr.align-middle");
    private final By cohortIdSpan        = By.cssSelector("span.fw-bold.text-primary");
    private final By statusBadge         = By.cssSelector("span.badge.rounded-pill");

    // Page info
    private final By pageTitle           = By.cssSelector("h2.fw-bold");
    private final By pageSubtitle        = By.cssSelector("p.text-muted");
    private final By totalRecordsLabel   = By.cssSelector("span.text-muted b");
    private final By loadedCountLabel    = By.cssSelector("span.text-primary.px-2.border-start b");

    // Controls
    private final By createCohortBtn     = By.xpath(
            "//button[contains(normalize-space(),'Create Cohort') " +
            "or contains(normalize-space(),'Add Cohort') " +
            "or contains(normalize-space(),'New Cohort')]");
    private final By createBtnPrimary    = By.cssSelector("button.btn.btn-primary");
    private final By filtersBtn          = By.cssSelector("button.btn.btn-outline-secondary");
    private final By filterSelect        = By.cssSelector("select.form-select");
    private final By searchInput         = By.cssSelector(
            "input[type='search'], " +
            "input[type='text'][placeholder*='Search'], " +
            "input[type='text'][placeholder*='search'], " +
            "input[placeholder='Search by Cohort ID or Name...'], " +
            "input[formcontrolname*='search']");

    // Row action buttons
    private final By editBtn             = By.cssSelector("button.btn.btn-link.text-muted.p-1");
    private final By deleteBtn           = By.cssSelector("button.btn.btn-link.text-danger.p-1");

    // Modal
    private final By modalCard           = By.cssSelector("div.modal-card");
    private final By modalTitle          = By.cssSelector("div.modal-header h5");
    private final By modalOverlay        = By.cssSelector("[class*='modal'], [role='dialog']");
    private final By cancelBtn           = By.cssSelector("button.btn-modal-secondary");

    // ── CREATE modal form fields ───────────────────────────────────────────────
    // Service Line in CREATE = dropdown (<select name="serviceLine">)
    private final By createServiceLineDd = By.cssSelector("select[name='serviceLine']");
    private final By learningPathDd      = By.cssSelector("select[name='learningPath']");
    private final By batchOwnerDd        = By.cssSelector("select[name='batchOwner']");
    private final By trainerDd           = By.xpath("//select[.//option[text()='Select Trainer']]");
    private final By dateInputsByType    = By.cssSelector("input[type='date']");

    // ── EDIT modal form fields ─────────────────────────────────────────────────
    // Service Line in EDIT = disabled text input (locked after creation per FRD FR-017)
    private final By editSLLocked        = By.cssSelector("input.form-control-disabled");
    // Edit-only fields exposed for EditDeleteCohortTest
    private final By editBatchOwnerDd    = By.name("batchOwner");
    private final By editTrainerDd       = By.name("trainer");
    private final By updateCohortBtn     = By.cssSelector("button.btn-modal-primary");
    private final By disabledModalInputs = By.cssSelector(".modal-body input[disabled]");

    // Empty state
    private final By emptyState          = By.xpath(
            "//*[contains(normalize-space(),'No data found') " +
            "or contains(normalize-space(),'No records')]");

    // Page heading (FRD 2.2)
    private final By pageHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3 or self::h4]" +
            "[contains(text(),'Cohort Management')]");

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
        // If a previous modal's overlay is still fading out it intercepts normal clicks.
        // Wait for it to disappear first, then fall back to a JS click if needed.
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

    // ── Empty state ───────────────────────────────────────────────────────────

    public boolean isEmptyStateDisplayed() {
        return isDisplayed(emptyState);
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
    }

    public WebElement getPageHeadingElement() {
        return waitForVisible(pageHeading);
    }

    // ── Page title / subtitle (FRD 2.2 specific CSS) ─────────────────────────

    public boolean isPageTitleVisible() {
        return isDisplayed(pageTitle);
    }

    public String getPageTitleText() {
        return getText(pageTitle);
    }

    public boolean isPageSubtitleVisible() {
        return isDisplayed(pageSubtitle);
    }

    // ── Record count labels ───────────────────────────────────────────────────

    public boolean isTotalRecordsLabelVisible() {
        return isDisplayed(totalRecordsLabel);
    }

    public String getTotalRecordsText() {
        try { return getText(totalRecordsLabel); } catch (Exception e) { return ""; }
    }

    public boolean isLoadedCountLabelVisible() {
        return isDisplayed(loadedCountLabel);
    }

    // ── Specific table (Bootstrap styled) ────────────────────────────────────

    public boolean isSpecificTableVisible() {
        return isDisplayed(specificTable);
    }

    public boolean isTableHeaderLightVisible() {
        return isDisplayed(tableHeaderLight);
    }

    public List<WebElement> getAlignedRows() {
        try { return driver.findElements(tableRowsAligned); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Cohort ID spans & status badges ──────────────────────────────────────

    public List<WebElement> getCohortIdSpans() {
        try { return driver.findElements(cohortIdSpan); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getStatusBadges() {
        try { return driver.findElements(statusBadge); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // Status badges in table rows (scoped)
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

    // ── Filters button & select ───────────────────────────────────────────────

    public boolean isFiltersBtnVisible() {
        return isDisplayed(filtersBtn);
    }

    public WebElement getFiltersBtnElement() {
        return driver.findElement(filtersBtn);
    }

    public boolean isFilterSelectVisible() {
        return isDisplayed(filterSelect);
    }

    public WebElement getFilterSelectElement() {
        return driver.findElement(filterSelect);
    }

    // ── Primary create button (Bootstrap) ────────────────────────────────────

    public WebElement getCreateBtnPrimaryElement() {
        return driver.findElement(createBtnPrimary);
    }

    // ── Edit / Delete action buttons ─────────────────────────────────────────

    public List<WebElement> getEditButtons() {
        try { return driver.findElements(editBtn); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getDeleteButtons() {
        try { return driver.findElements(deleteBtn); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Modal card (Bootstrap modal) ─────────────────────────────────────────

    public boolean isModalCardVisible() {
        return isDisplayed(modalCard);
    }

    public WebElement getModalCardElement() {
        return driver.findElement(modalCard);
    }

    public boolean isModalTitleVisible() {
        return isDisplayed(modalTitle);
    }

    public String getModalTitleText() {
        try { return getText(modalTitle); } catch (Exception e) { return ""; }
    }

    // ── Modal ─────────────────────────────────────────────────────────────────

    /** Closes any open modal — tries Cancel/Close button first, falls back to JS Escape.
     *  Waits for the overlay to fully disappear before returning so callers are not
     *  surprised by ElementClickInterceptedException from a fading Angular transition. */
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
                // Fallback — dispatch Escape key via JavaScript
                ((JavascriptExecutor) driver).executeScript(
                        "document.dispatchEvent(new KeyboardEvent('keydown',{'key':'Escape','bubbles':true}))");
            }
        } catch (Exception ignored) {}

        // Wait for the Angular CSS transition to complete — the overlay must be gone
        // before the next click, otherwise ElementClickInterceptedException fires.
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.modal-overlay")));
        } catch (Exception ignored) {}
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
        } catch (Exception ignored) {}
    }

    // ── Additional element getters ────────────────────────────────────────────

    public WebElement getPageTitleElement()    { return driver.findElement(pageTitle); }
    public WebElement getPageSubtitleElement() { return driver.findElement(pageSubtitle); }
    public WebElement getSearchInputElement()  { return driver.findElement(searchInput); }

    public List<WebElement> getFilterSelectElements() {
        return driver.findElements(filterSelect);
    }

    public WebElement getSpecificTableElement()  { return driver.findElement(specificTable); }

    public String getTableHeaderText() {
        try { return getText(tableHeaderLight); } catch (Exception e) { return ""; }
    }

    public String getLoadedCountText() {
        try { return getText(loadedCountLabel); } catch (Exception e) { return ""; }
    }

    public WebElement getTotalRecordsElement() { return driver.findElement(totalRecordsLabel); }
    public WebElement getLoadedCountElement()  { return driver.findElement(loadedCountLabel); }

    // ── Modal element getters ─────────────────────────────────────────────────

    public WebElement getCancelBtnElement()    { return driver.findElement(cancelBtn); }

    // ── CREATE modal form element getters ─────────────────────────────────────

    public WebElement getCreateServiceLineDropdown()    { return driver.findElement(createServiceLineDd); }
    public WebElement getLearningPathDropdown()         { return driver.findElement(learningPathDd); }
    public WebElement getBatchOwnerDropdown()           { return driver.findElement(batchOwnerDd); }
    public WebElement getTrainerDropdown()              { return driver.findElement(trainerDd); }
    public List<WebElement> getDateInputs()             { return driver.findElements(dateInputsByType); }

    // ── EDIT modal element getters ────────────────────────────────────────────

    public WebElement getEditServiceLineLockedElement() { return driver.findElement(editSLLocked); }

    // ── Row-scoped helpers ────────────────────────────────────────────────────

    /** Returns the cohort-ID span element scoped to the given table row. */
    public WebElement getCohortIdSpanInRow(WebElement row)       { return row.findElement(cohortIdSpan); }

    /** Returns all status-badge elements scoped to the given table row. */
    public List<WebElement> getStatusBadgesInRow(WebElement row) { return row.findElements(statusBadge); }

    // ── Waits ─────────────────────────────────────────────────────────────────

    /**
     * Waits for the cohort table to be visible and populated with at least one row.
     * Also pauses briefly to allow Angular data-binding to settle.
     */
    public void waitForTableToLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(specificTable));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(tableRowsAligned, 0));
        } catch (Exception ignored) {}
    }

    public void waitForModalVisible()         { wait.until(ExpectedConditions.visibilityOfElementLocated(modalCard)); }
    public void waitForModalInvisible()       { wait.until(ExpectedConditions.invisibilityOfElementLocated(modalCard)); }
    public void waitForEditSLLockedPresent()  { wait.until(ExpectedConditions.presenceOfElementLocated(editSLLocked)); }

    // ── JS-backed actions ─────────────────────────────────────────────────────

    /**
     * JS-clicks the "Create" (primary) button and waits for the modal to appear.
     */
    public void openCreateModal() {
        WebElement btn = driver.findElement(createBtnPrimary);
        jsClick(btn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalCard));
    }

    /** JS-clicks the modal Cancel button. */
    public void clickCancelBtn() {
        jsClick(driver.findElement(cancelBtn));
    }

    /** JS-clicks the first Edit button in the Actions column. */
    public void clickFirstEditBtn() {
        List<WebElement> edits = driver.findElements(editBtn);
        if (!edits.isEmpty()) jsClick(edits.get(0));
    }

    /** JS-clicks the first Delete button in the Actions column. */
    public void clickFirstDeleteBtn() {
        List<WebElement> deletes = driver.findElements(deleteBtn);
        if (!deletes.isEmpty()) jsClick(deletes.get(0));
    }

    /** JS-clicks the cohort-ID span inside the given row — navigates to the detail page. */
    public void clickCohortIdSpanInRow(WebElement row) {
        jsClick(row.findElement(cohortIdSpan));
    }

    // ── Edit modal dropdown/button getters ────────────────────────────────────

    public WebElement getEditBatchOwnerDropdown()    { return driver.findElement(editBatchOwnerDd); }
    public WebElement getEditTrainerDropdown()       { return driver.findElement(editTrainerDd); }
    public WebElement getUpdateCohortButton()        { return driver.findElement(updateCohortBtn); }
    public List<WebElement> getDisabledModalInputs() { return driver.findElements(disabledModalInputs); }

    public void selectTrainerByIndex(int index) {
        try {
            new org.openqa.selenium.support.ui.Select(driver.findElement(editTrainerDd))
                    .selectByIndex(index);
        } catch (Exception ignored) {}
    }

    public void clickUpdateCohort() {
        jsClick(driver.findElement(updateCohortBtn));
    }

    public boolean isUpdateCohortButtonPresent() {
        return isDisplayed(updateCohortBtn);
    }
}

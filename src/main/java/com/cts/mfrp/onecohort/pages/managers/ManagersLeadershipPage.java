package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ManagersLeadershipPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3]" +
            "[contains(normalize-space(),'Manager') " +
            "or contains(normalize-space(),'Leadership')]");

    private final By createManagerBtn = By.xpath(
            "//button[contains(normalize-space(),'Create Manager') " +
            "or contains(normalize-space(),'Add Manager') " +
            "or contains(normalize-space(),'New Manager')]");

    // Actual HTML: <button class="tab-btn active">Managers</button> <button class="tab-btn">Leaders</button>
    private final By filterTabs = By.cssSelector("button.tab-btn");

    // Actual HTML: <div class="modal-overlay"><div class="modal">...</div></div>
    private final By modalOverlay = By.cssSelector("div.modal-overlay");

    // ── Constructor ───────────────────────────────────────────────────────────
    public ManagersLeadershipPage(WebDriver driver) {
        super(driver);
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    /**
     * Waits for the page heading to be visible before returning.
     * Uses explicit wait — safer for render.com's Angular rendering latency.
     */
    public boolean isPageHeadingVisible() {
        try {
            waitForVisible(pageHeading);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Create Manager ────────────────────────────────────────────────────────

    public boolean isCreateManagerBtnVisible() {
        return isDisplayed(createManagerBtn);
    }

    /** Clicks Create Manager and returns the modal page object. */
    public CreateManagerModal clickCreateManager() {
        click(createManagerBtn);
        return new CreateManagerModal(driver);
    }

    // ── Role filter tabs ──────────────────────────────────────────────────────

    /**
     * Returns true if filter tabs (button.tab-btn) are visible.
     * Uses explicit wait — tabs are rendered by Angular after page load.
     */
    public boolean areFilterTabsVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(filterTabs));
            return !driver.findElements(filterTabs).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Modal ─────────────────────────────────────────────────────────────────

    /**
     * Returns true if the modal overlay (div.modal-overlay) is visible.
     * Uses explicit wait — modal is injected by Angular *ngIf after button click.
     */
    public boolean isModalVisible() {
        try {
            waitForVisible(modalOverlay);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Closes the modal — tries Cancel/Close button first, falls back to JS Escape. */
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
            ((JavascriptExecutor) driver).executeScript(
                    "document.dispatchEvent(new KeyboardEvent('keydown',{'key':'Escape','bubbles':true}))");
        } catch (Exception ignored) {}
    }
}

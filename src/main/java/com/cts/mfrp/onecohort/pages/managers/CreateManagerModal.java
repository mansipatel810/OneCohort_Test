package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CreateManagerModal extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // Actual HTML: <input type="text" placeholder="Enter full name">
    private final By fullNameInput = By.cssSelector(
            "div.modal input[placeholder='Enter full name']");

    // Actual HTML: <input type="text" placeholder="e.g. USR-30010">
    private final By employeeIdInput = By.cssSelector(
            "div.modal input[placeholder*='USR']");

    // Actual HTML: custom div dropdown — NOT a <select>
    // <div class="sl-dropdown"><div class="sl-trigger">...</div></div>
    private final By serviceLineDropdown = By.cssSelector(
            "div.modal-overlay div.sl-dropdown");

    // ── Constructor ───────────────────────────────────────────────────────────
    public CreateManagerModal(WebDriver driver) {
        super(driver);
    }

    // ── Visibility checks ─────────────────────────────────────────────────────

    public boolean isFullNameInputVisible() {
        return isDisplayed(fullNameInput);
    }

    public boolean isEmployeeIdInputVisible() {
        return isDisplayed(employeeIdInput);
    }

    public boolean isServiceLineDropdownVisible() {
        return isDisplayed(serviceLineDropdown);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

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

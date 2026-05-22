package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CreateManagerModal extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // Actual HTML: <div class="modal-overlay"><div class="modal">...</div></div>
    private final By modalContainer = By.cssSelector("div.modal-overlay");

    // Actual HTML: <input type="text" placeholder="Enter full name">
    // Old selector had placeholder*='Full' (capital F) — CSS attribute selectors are case-sensitive.
    // formcontrolname is an Angular directive and is NOT emitted as a DOM attribute in this app.
    private final By fullNameInput = By.cssSelector(
            "div.modal input[placeholder='Enter full name']");

    // Actual HTML: <input type="text" placeholder="e.g. USR-30010">
    // The label says "User ID" in the UI, but the test calls it "Employee ID" — locator still matches.
    private final By employeeIdInput = By.cssSelector(
            "div.modal input[placeholder*='USR']");

    // Actual HTML: The Service Line field is a custom div dropdown — NOT a <select>.
    // <div class="sl-dropdown"><div class="sl-trigger">...</div></div>
    // Old selector used select[formcontrolname*='service'] — both wrong (no select, no formcontrolname).
    private final By serviceLineDropdown = By.cssSelector(
            "div.modal-overlay div.sl-dropdown");

    private final By roleDropdown = By.cssSelector(
            "[class*='modal'] select[formcontrolname*='role'], " +
            "[role='dialog'] select[formcontrolname*='role']");

    // FIX LM-004: Submit button is labelled "Create Entry" in the actual HTML,
    // NOT "Save Manager". Tests assert "Create Entry".
    private final By submitBtn = By.xpath(
            "//*[contains(@class,'modal') or @role='dialog']" +
            "//button[contains(normalize-space(),'Create Entry') " +
            "or contains(normalize-space(),'Create') " +
            "or contains(normalize-space(),'Save') " +
            "or contains(normalize-space(),'Submit')]" +
            "[not(contains(normalize-space(),'Cancel'))]");

    // ── Constructor ───────────────────────────────────────────────────────────
    public CreateManagerModal(WebDriver driver) {
        super(driver);
    }

    // ── Visibility checks ─────────────────────────────────────────────────────

    public boolean isModalVisible() {
        try {
            List<WebElement> modals = driver.findElements(modalContainer);
            return modals.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFullNameInputVisible() {
        return isDisplayed(fullNameInput);
    }

    public boolean isEmployeeIdInputVisible() {
        return isDisplayed(employeeIdInput);
    }

    public boolean isServiceLineDropdownVisible() {
        return isDisplayed(serviceLineDropdown);
    }

    public boolean isRoleDropdownVisible() {
        return isDisplayed(roleDropdown);
    }

    public boolean isSubmitButtonVisible() {
        return isDisplayed(submitBtn);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public String getSubmitButtonText() {
        try {
            return driver.findElement(submitBtn).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickSubmit() {
        try {
            click(submitBtn);
        } catch (Exception ignored) {}
    }

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

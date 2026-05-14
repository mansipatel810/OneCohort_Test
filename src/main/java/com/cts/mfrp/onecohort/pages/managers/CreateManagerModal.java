package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CreateManagerModal extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By modalContainer = By.cssSelector("[class*='modal'], [role='dialog']");

    private final By fullNameInput = By.cssSelector(
            "input[formcontrolname*='name'], input[formcontrolname*='Name'], " +
            "input[placeholder*='Name'], input[placeholder*='Full']");

    private final By employeeIdInput = By.cssSelector(
            "input[placeholder*='USR'], input[placeholder*='Employee'], " +
            "input[formcontrolname*='emp'], input[formcontrolname*='userId'], " +
            "input[formcontrolname*='employeeId']");

    private final By serviceLineDropdown = By.cssSelector(
            "[class*='modal'] select[formcontrolname*='service'], " +
            "[class*='modal'] select[formcontrolname*='serviceLine'], " +
            "[role='dialog'] select");

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

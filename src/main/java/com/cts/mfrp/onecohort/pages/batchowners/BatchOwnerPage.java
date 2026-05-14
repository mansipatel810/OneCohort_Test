package com.cts.mfrp.onecohort.pages.batchowners;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Collections;
import java.util.List;

public class BatchOwnerPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3]" +
            "[contains(normalize-space(),'Batch Owner') " +
            "or contains(normalize-space(),'POC')]");

    private final By serviceLineDropdown = By.cssSelector(
            "select[name='serviceLine'], select#serviceLineFilter, " +
            "select[formcontrolname='serviceLine'], select[formcontrolname='serviceLineId']");

    private final By learningPathDropdown = By.cssSelector(
            "select[name='learningPath'], select#learningPathFilter, " +
            "select[formcontrolname='learningPath'], select[formcontrolname='learningPathId']");

    private final By profileCards = By.cssSelector(
            "[class*='card'], [class*='profile-card'], [class*='poc-card'], " +
            "[class*='batch-owner-card']");

    private final By addBatchOwnerBtn = By.xpath(
            "//button[contains(normalize-space(),'Add Batch Owner') " +
            "or contains(normalize-space(),'Add POC') " +
            "or contains(normalize-space(),'New Batch Owner')]");

    private final By viewDetailsButtons = By.xpath(
            "//button[contains(normalize-space(),'View Details') " +
            "or contains(normalize-space(),'View') " +
            "or contains(normalize-space(),'Details')]");

    private final By modalOverlay = By.cssSelector("[class*='modal'], [role='dialog']");

    // ── Constructor ───────────────────────────────────────────────────────────
    public BatchOwnerPage(WebDriver driver) {
        super(driver);
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
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
            } catch (Exception e1) {
                try {
                    select.selectByVisibleText(serviceLineId);
                } catch (Exception e2) {
                    // Partial match
                    select.getOptions().stream()
                            .filter(o -> o.getText().contains(serviceLineId)
                                      && !o.getAttribute("value").trim().isEmpty())
                            .findFirst()
                            .ifPresent(o -> select.selectByVisibleText(o.getText()));
                }
            }
        } catch (Exception ignored) {}
    }

    // ── Learning Path filter ──────────────────────────────────────────────────

    public boolean isLearningPathDropdownVisible() {
        return isDisplayed(learningPathDropdown);
    }

    // ── Profile cards ─────────────────────────────────────────────────────────

    public boolean areProfileCardsVisible() {
        try {
            return !driver.findElements(profileCards).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getProfileCards() {
        try {
            return driver.findElements(profileCards);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── View Details ──────────────────────────────────────────────────────────

    public List<WebElement> getViewDetailsButtons() {
        try {
            return driver.findElements(viewDetailsButtons);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Add Batch Owner ───────────────────────────────────────────────────────

    public boolean isAddBatchOwnerBtnVisible() {
        return isDisplayed(addBatchOwnerBtn);
    }

    public AddBatchOwnerModal clickAddBatchOwner() {
        click(addBatchOwnerBtn);
        return new AddBatchOwnerModal(driver);
    }

    // ── Modal ─────────────────────────────────────────────────────────────────

    public boolean isModalVisible() {
        try {
            List<WebElement> modals = driver.findElements(modalOverlay);
            return modals.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
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

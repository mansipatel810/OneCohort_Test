package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class ManagersLeadershipPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3]" +
            "[contains(normalize-space(),'Manager') " +
            "or contains(normalize-space(),'Leadership')]");

    private final By profileCards = By.cssSelector(
            "[class*='card'], [class*='manager-card'], [class*='profile-card']");

    private final By cardNames = By.cssSelector(
            "[class*='card'] h3, [class*='card'] h4, " +
            "[class*='card'] [class*='name'], [class*='card'] p.font-bold");

    private final By viewDetailsButtons = By.xpath(
            "//button[contains(normalize-space(),'View Details') " +
            "or contains(normalize-space(),'View') " +
            "or contains(normalize-space(),'Details')]");

    private final By createManagerBtn = By.xpath(
            "//button[contains(normalize-space(),'Create Manager') " +
            "or contains(normalize-space(),'Add Manager') " +
            "or contains(normalize-space(),'New Manager')]");

    private final By filterTabs = By.cssSelector(
            "[class*='tab'], [role='tab'], button[class*='tab']");

    private final By modalOverlay = By.cssSelector("[class*='modal'], [role='dialog']");

    // ── Constructor ───────────────────────────────────────────────────────────
    public ManagersLeadershipPage(WebDriver driver) {
        super(driver);
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
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

    public List<WebElement> getCardNames() {
        try {
            return driver.findElements(cardNames);
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

    // ── Create Manager ────────────────────────────────────────────────────────

    public boolean isCreateManagerBtnVisible() {
        return isDisplayed(createManagerBtn);
    }

    public CreateManagerModal clickCreateManager() {
        click(createManagerBtn);
        return new CreateManagerModal(driver);
    }

    // ── Role filter tabs ──────────────────────────────────────────────────────

    public boolean areFilterTabsVisible() {
        try {
            return !driver.findElements(filterTabs).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getFilterTabs() {
        try {
            return driver.findElements(filterTabs);
        } catch (Exception e) {
            return Collections.emptyList();
        }
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

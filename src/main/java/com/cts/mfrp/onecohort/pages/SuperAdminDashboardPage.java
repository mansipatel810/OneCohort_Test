package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;

public class SuperAdminDashboardPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By dashboardContainer = By.cssSelector("div.dashboard-container");

    // KPI card headings (TC-SA-001)
    private final By totalCohortsCard = By.xpath("//h3[contains(text(),'Total Cohorts')]");
    private final By activeCard       = By.xpath("//h3[contains(text(),'Active')]");
    private final By completedCard    = By.xpath("//h3[contains(text(),'Completed')]");
    private final By upcomingCard     = By.xpath("//h3[contains(text(),'Upcoming')]");

    // KPI number values (TC-SA-008)
    private final By kpiNumbers       = By.cssSelector("p.kpi-number");

    // Super User badge (TC-SA-007)
    private final By superUserBadge   = By.xpath(
            "//*[self::span or self::div or self::p][normalize-space()='Super User']");

    // Avatar button for logout flow (TC-SA-006)
    private final By avatarBtn        = By.cssSelector("div.w-10.h-10.bg-blue-600");

    public SuperAdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── Waits ─────────────────────────────────────────────────────────────────

    /** Waits for the Super Admin dashboard container to be visible after login. */
    public void waitForDashboardContainer() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardContainer));
    }

    // ── Sidebar navigation ────────────────────────────────────────────────────

    /**
     * Returns a sidebar menu item element whose text contains {@code itemText}.
     * Used by tests to click sidebar links (e.g. "Managers", "Cohort Management").
     */
    public WebElement getMenuItemElement(String itemText) {
        return driver.findElement(By.xpath(
                "//nav[contains(@class,'menu')]" +
                "//*[contains(text(),'" + itemText + "')]"));
    }

    /**
     * Returns true if the sidebar menu item with the given text is visible.
     * Used by TC-SA-002 to verify all required nav links are present.
     */
    public boolean isMenuItemVisible(String itemText) {
        try {
            return getMenuItemElement(itemText).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── KPI cards (TC-SA-001) ─────────────────────────────────────────────────

    public WebElement getTotalCohortsCardElement() {
        return waitForVisible(totalCohortsCard);
    }

    public WebElement getActiveCardElement() {
        return waitForVisible(activeCard);
    }

    public WebElement getCompletedCardElement() {
        return waitForVisible(completedCard);
    }

    public WebElement getUpcomingCardElement() {
        return waitForVisible(upcomingCard);
    }

    // ── KPI numbers (TC-SA-008) ───────────────────────────────────────────────

    /** Returns all KPI number elements on the dashboard. */
    public List<WebElement> getKpiNumberElements() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(kpiNumbers));
            return driver.findElements(kpiNumbers);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Super User badge (TC-SA-007) ──────────────────────────────────────────

    /** Returns the Super User role badge text. */
    public String getSuperUserBadgeText() {
        try {
            return getText(superUserBadge);
        } catch (Exception e) {
            return "";
        }
    }

    // ── Logout (TC-SA-006) ────────────────────────────────────────────────────

    /** Clicks the avatar button to open the user menu. */
    public void clickAvatar() {
        waitForClickable(avatarBtn).click();
    }
}

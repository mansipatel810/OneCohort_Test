package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Collections;
import java.util.List;

/**
 * Page Object for the Super Admin Dashboard (Home Page).
 * Covers all elements tested in HomePageTest (FRD 2.1.x).
 *
 * Uses PageFactory @FindBy annotations — all locators live here, not in the test.
 */
public class SuperAdminDashboardPage extends BasePage {

    // ── @FindBy locators (PageFactory) ───────────────────────────────────────

    @FindBy(css = "img[alt='One Cohort']")
    private WebElement logoImage;

    @FindBy(css = ".logo-section span")
    private WebElement appNameSpan;

    @FindBy(css = ".bg-blue-600.rounded-full")
    private WebElement suAvatar;

    @FindBy(css = "app-header .text-gray-700")
    private WebElement superUserLabel;

    @FindBy(css = ".sidebar")
    private WebElement sidebar;

    // ── By-based locators (dynamic / text-based) ──────────────────────────────

    private final By subtitleLocator  = By.xpath("//*[contains(text(),'Super User Command Center')]");
    private final By welcomeLocator   = By.xpath("//*[contains(text(),'Welcome')]");
    private final By profileDropdown  = By.cssSelector("app-header ul");
    private final By searchBar        = By.cssSelector("input[type='search']");
    private final By kpiNumbers       = By.cssSelector("p.kpi-number");

    // KPI card headings
    private final By totalCohortsCard = By.xpath("//h3[contains(text(),'Total Cohorts')]");
    private final By activeCard       = By.xpath("//h3[contains(text(),'Active')]");
    private final By completedCard    = By.xpath("//h3[contains(text(),'Completed')]");
    private final By upcomingCard     = By.xpath("//h3[contains(text(),'Upcoming')]");

    public SuperAdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── Logo & branding ───────────────────────────────────────────────────────

    public boolean isLogoVisible() {
        try { return logoImage.isDisplayed(); } catch (Exception e) { return false; }
    }

    public WebElement getLogoElement() {
        return logoImage;
    }

    public String getAppName() {
        try { return appNameSpan.getText(); } catch (Exception e) { return ""; }
    }

    public WebElement getAppNameElement() {
        return appNameSpan;
    }

    // ── Subtitle ──────────────────────────────────────────────────────────────

    public List<WebElement> getSubtitleElements() {
        return driver.findElements(subtitleLocator);
    }

    public boolean isSubtitleVisible() {
        List<WebElement> els = getSubtitleElements();
        return !els.isEmpty() && els.get(0).isDisplayed();
    }

    // ── User panel ────────────────────────────────────────────────────────────

    public WebElement getSuperUserLabelElement() {
        return superUserLabel;
    }

    public String getSuperUserLabelText() {
        try { return superUserLabel.getText(); } catch (Exception e) { return ""; }
    }

    public boolean isSUAvatarVisible() {
        try { return suAvatar.isDisplayed(); } catch (Exception e) { return false; }
    }

    public WebElement getSUAvatarElement() {
        return suAvatar;
    }

    public void clickSUAvatar() {
        suAvatar.click();
    }

    public boolean isProfileDropdownVisible() {
        return !driver.findElements(profileDropdown).isEmpty();
    }

    // ── Welcome message ───────────────────────────────────────────────────────

    public WebElement getWelcomeElement() {
        return driver.findElement(welcomeLocator);
    }

    public boolean isWelcomeMessageVisible() {
        return isDisplayed(welcomeLocator);
    }

    // ── Sidebar & navigation ──────────────────────────────────────────────────

    public boolean isSidebarVisible() {
        try { return sidebar.isDisplayed(); } catch (Exception e) { return false; }
    }

    public WebElement getSidebarElement() {
        return sidebar;
    }

    public WebElement getMenuItemElement(String itemText) {
        return driver.findElement(By.xpath(
                "//nav[contains(@class,'menu')]" +
                "//*[contains(text(),'" + itemText + "')]"));
    }

    public boolean isMenuItemVisible(String itemText) {
        try {
            return getMenuItemElement(itemText).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Search bar ────────────────────────────────────────────────────────────

    public boolean isSearchBarPresent() {
        return !driver.findElements(searchBar).isEmpty();
    }

    // ── KPI cards ─────────────────────────────────────────────────────────────

    public WebElement getTotalCohortsCardElement() {
        return driver.findElement(totalCohortsCard);
    }

    public WebElement getActiveCardElement() {
        return driver.findElement(activeCard);
    }

    public WebElement getCompletedCardElement() {
        return driver.findElement(completedCard);
    }

    public WebElement getUpcomingCardElement() {
        return driver.findElement(upcomingCard);
    }

    public List<WebElement> getKpiNumberElements() {
        try {
            return driver.findElements(kpiNumbers);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

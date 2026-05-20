package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
    private final By totalCohortsCard  = By.xpath("//h3[contains(text(),'Total Cohorts')]");
    private final By activeCard        = By.xpath("//h3[contains(text(),'Active')]");
    private final By completedCard     = By.xpath("//h3[contains(text(),'Completed')]");
    private final By upcomingCard      = By.xpath("//h3[contains(text(),'Upcoming')]");

    // ── Chart / dashboard section locators (FRD 2.1.5) ────────────────────────

    private final By dashboardContainer = By.cssSelector("div.dashboard-container");
    private final By dashboardHeading   = By.xpath(
            "//*[self::h1 or self::h2 or self::h3 or self::h4]" +
            "[contains(normalize-space(),'Super Admin Dashboard') " +
            "or contains(normalize-space(),'One Cohort')]");
    private final By superUserBadge     = By.xpath(
            "//*[self::span or self::div or self::p][normalize-space()='Super User']");
    private final By sectionTitles      = By.cssSelector("div.section-title");
    private final By kpiCards           = By.cssSelector(".kpi-card");
    private final By kpiCardTitles      = By.cssSelector(".kpi-card .kpi-info h3");
    private final By kpiCardNumbers     = By.cssSelector(".kpi-card .kpi-info .kpi-number");
    private final By statCards          = By.cssSelector(".stat-card");
    private final By statLabels         = By.cssSelector(".stat-card .stat-label");
    private final By statValues         = By.cssSelector(".stat-card .stat-value");
    private final By statFills          = By.cssSelector(".stat-card .stat-fill");

    // ── Logout locators (FRD 2.1.3) ───────────────────────────────────────────

    private final By avatarBtn          = By.cssSelector("div.w-10.h-10.bg-blue-600");
    private final By logoutOption       = By.cssSelector("a[routerlink='/login'], a[href='/login']");

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

    // ── Dashboard container (FRD 2.1.5) ──────────────────────────────────────

    public WebElement getDashboardContainerElement() {
        return driver.findElement(dashboardContainer);
    }

    public boolean isDashboardContainerVisible() {
        return isDisplayed(dashboardContainer);
    }

    public void waitForDashboardContainer() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardContainer));
    }

    // ── Dashboard heading & badge (FRD 2.1.5) ────────────────────────────────

    public WebElement getDashboardHeadingElement() {
        return waitForVisible(dashboardHeading);
    }

    public String getDashboardHeadingText() {
        try { return getText(dashboardHeading); } catch (Exception e) { return ""; }
    }

    public WebElement getSuperUserBadgeElement() {
        return driver.findElement(superUserBadge);
    }

    public String getSuperUserBadgeText() {
        try { return getText(superUserBadge); } catch (Exception e) { return ""; }
    }

    // ── Section titles (FRD 2.1.5) ───────────────────────────────────────────

    public List<WebElement> getSectionTitles() {
        try { return driver.findElements(sectionTitles); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── KPI cards (FRD 2.1.5.1) ──────────────────────────────────────────────

    public List<WebElement> getKpiCards() {
        try { return driver.findElements(kpiCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getKpiCardTitles() {
        try { return driver.findElements(kpiCardTitles); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getKpiCardNumbers() {
        try { return driver.findElements(kpiCardNumbers); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Stat cards (FRD 2.1.5) ───────────────────────────────────────────────

    public List<WebElement> getStatCards() {
        try { return driver.findElements(statCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getStatLabels() {
        try { return driver.findElements(statLabels); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getStatValues() {
        try { return driver.findElements(statValues); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getStatFills() {
        try { return driver.findElements(statFills); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Logout (FRD 2.1.3) ───────────────────────────────────────────────────

    public void clickAvatar() {
        waitForClickable(avatarBtn).click();
    }

    public WebElement getLogoutOptionElement() {
        return waitForVisible(logoutOption);
    }

    public boolean isLogoutOptionVisible() {
        return isDisplayed(logoutOption);
    }

    public void clickLogout() {
        waitForVisible(logoutOption).click();
    }
}

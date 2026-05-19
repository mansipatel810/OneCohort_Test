package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Manager Dashboard.
 *
 * URL pattern  : /manager/{serviceLineId}/dashboard
 * FRD sections : 3.3 (Login), 3.4 (Dashboard content)
 *
 * The Manager Dashboard has these sections:
 *   - Header : "Welcome, Manager." greeting + "MG" avatar
 *   - Sidebar : Dashboard, Manage Cohorts, Cohort Train View, Batch Owners, Analytics
 *   - Catalog & Rates : 3 KPI cards (Service Lines, Learning Paths, Avg. Completion Rate)
 *   - Cohorts per Service Line : stat card(s)
 *   - Cohorts per Learning Path : stat cards grid
 *   - Training Completion Distribution : 3 KPI cards (Upcoming, In Progress, Completed)
 *
 * NOTE TO BEGINNER:
 *   "By.cssSelector" tells Selenium HOW to find an element on the page.
 *   Think of it like a GPS address — the selector describes the element's location in HTML.
 *   If an assertion fails with "element not found", the selector may need updating
 *   to match the actual HTML the Angular app renders for the Manager role.
 */
public class ManagerDashboardPage extends BasePage {

    // ── Header locators ───────────────────────────────────────────────────────
    // The Manager header says "Welcome, Manager." (same component structure as Leader)
    private final By welcomeHeading = By.cssSelector("h2.text-xl.font-bold.text-gray-800");

    // Role text chip in the top-right corner — shows "Manager"
    private final By roleText = By.cssSelector("span.text-sm.font-bold.text-gray-700");

    // "MG" avatar circle — blue circle in top-right
    private final By mgAvatar = By.cssSelector("div.w-10.h-10.bg-blue-600.rounded-full");

    // ── Sidebar locators ──────────────────────────────────────────────────────
    private final By sidebarContainer = By.cssSelector("aside.sidebar");
    private final By logoImage        = By.cssSelector(".logo-section img[alt='One Cohort']");
    private final By appNameSpan      = By.cssSelector(".logo-section span");

    // Manager has more nav links than Leader (FRD Section 3):
    //   Dashboard | Manage Cohorts | Cohort Train View | Batch Owners | Analytics
    private final By navLinks = By.cssSelector("nav.menu a");

    // ── Dashboard container and heading ───────────────────────────────────────
    // Similar Angular component structure to LeaderDashboardPage
    private final By dashboardContainer = By.cssSelector("div.dashboard-container");
    private final By dashboardHeading   = By.cssSelector("div.header > h2");

    // Badge that shows "Manager" role label
    private final By managerBadge = By.cssSelector("div.header span.badge");

    // ── Section titles ────────────────────────────────────────────────────────
    // FRD 3.4: Sections on Manager Dashboard:
    //   "Catalog & Rates", "Cohorts per Service Line",
    //   "Cohorts per Learning Path", "Training Completion Distribution"
    private final By sectionTitles = By.cssSelector("div.section-title");

    // ── KPI cards ─────────────────────────────────────────────────────────────
    // FRD 3.4:
    //   Catalog & Rates        → 3 cards  (Service Lines, Learning Paths, Avg. Completion Rate)
    //   Completion Distribution → 3 cards  (Upcoming, In Progress, Completed)
    //   Total: 6 KPI cards
    private final By allKpiCards   = By.cssSelector("div.kpi-card");
    private final By kpiCardTitles = By.cssSelector("div.kpi-info > h3");
    private final By kpiNumbers    = By.cssSelector("p.kpi-number");

    // ── Stat cards (service line / learning path grids) ───────────────────────
    private final By allStatCards = By.cssSelector("div.stat-card");
    private final By statLabels   = By.cssSelector("div.stat-label");
    private final By statValues   = By.cssSelector("div.stat-value");
    private final By statFills    = By.cssSelector("div.stat-bar > div.stat-fill");

    // ── Loading / error states ────────────────────────────────────────────────
    private final By loadingMsg = By.cssSelector("div.status-msg");
    private final By errorMsg   = By.cssSelector("div.status-msg.error");

    // ── Helper: find stat cards under a specific section ──────────────────────
    private By statCardsUnderSection(String sectionTitle) {
        return By.xpath(
                "//div[@class='section-title' and normalize-space()='" + sectionTitle + "']" +
                        "/following-sibling::div[@class='stats-grid'][1]//div[@class='stat-card']"
        );
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    public ManagerDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── Load / readiness ──────────────────────────────────────────────────────

    /**
     * Waits until Angular's *ngIf resolves and the dashboard heading is visible.
     * ALWAYS call this after navigating to the dashboard before making assertions.
     *
     * WHY: Angular fetches data asynchronously. The page skeleton appears first,
     * then the data loads. Without this wait, assertions will fail intermittently.
     */
    public ManagerDashboardPage waitForDashboardLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeading));
        return this;
    }

    public boolean isDashboardContainerPresent() {
        return isDisplayed(dashboardContainer);
    }

    public boolean isErrorMessageVisible() {
        return isDisplayed(errorMsg);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    /** Returns the welcome greeting text. Expected: "Welcome, Manager." */
    public String getWelcomeText() {
        return getText(welcomeHeading);
    }

    /** Returns the role label text. Expected: "Manager" */
    public String getRoleText() {
        return getText(roleText);
    }

    public boolean isAvatarVisible() {
        return isDisplayed(mgAvatar);
    }

    public String getAvatarText() {
        return getText(mgAvatar);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    public boolean isSidebarVisible() {
        return isDisplayed(sidebarContainer);
    }

    public boolean isLogoVisible() {
        return isDisplayed(logoImage);
    }

    public String getAppName() {
        return getText(appNameSpan);
    }

    public int getNavLinkCount() {
        return driver.findElements(navLinks).size();
    }

    public List<String> getNavLinkTexts() {
        return driver.findElements(navLinks)
                .stream()
                .map(el -> el.getText().trim())
                .collect(Collectors.toList());
    }

    // ── Dashboard heading / badge ─────────────────────────────────────────────

    /** Returns heading text. Expected: contains "Manager Dashboard" */
    public String getDashboardHeading() {
        return getText(dashboardHeading);
    }

    public String getManagerBadgeText() {
        return getText(managerBadge);
    }

    // ── Section titles ────────────────────────────────────────────────────────

    public List<String> getAllSectionTitles() {
        return driver.findElements(sectionTitles)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public boolean isSectionTitlePresent(String title) {
        return getAllSectionTitles().stream()
                .anyMatch(t -> t.equalsIgnoreCase(title));
    }

    // ── KPI cards ─────────────────────────────────────────────────────────────

    public int getTotalKpiCardCount() {
        return driver.findElements(allKpiCards).size();
    }

    public List<String> getKpiCardTitles() {
        return driver.findElements(kpiCardTitles)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getKpiNumbers() {
        return driver.findElements(kpiNumbers)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public boolean isKpiCardPresent(String cardTitle) {
        return getKpiCardTitles().stream()
                .anyMatch(t -> t.equalsIgnoreCase(cardTitle));
    }

    // ── Stat cards ────────────────────────────────────────────────────────────

    public int getTotalStatCardCount() {
        return driver.findElements(allStatCards).size();
    }

    public int getStatCardCountForSection(String sectionTitle) {
        return driver.findElements(statCardsUnderSection(sectionTitle)).size();
    }

    public List<String> getStatLabels() {
        return driver.findElements(statLabels)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getStatValues() {
        return driver.findElements(statValues)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public int getStatFillCount() {
        return driver.findElements(statFills).size();
    }

    // ── Navigation helpers ────────────────────────────────────────────────────

    /** Clicks a nav link in the Manager sidebar by its visible text label. */
    private void clickNavLink(String linkText) {
        List<WebElement> links = driver.findElements(navLinks);
        for (WebElement link : links) {
            if (link.getText().trim().equalsIgnoreCase(linkText)) {
                link.click();
                return;
            }
        }
        throw new RuntimeException("Nav link '" + linkText + "' not found in Manager sidebar");
    }

    public void clickDashboardNav()    { clickNavLink("Dashboard"); }
    public void clickManageCohortsNav(){ clickNavLink("Manage Cohorts"); }
    public void clickCohortTrainNav()  { clickNavLink("Cohort Train View"); }
    public void clickBatchOwnersNav()  { clickNavLink("Batch Owners"); }
    public void clickAnalyticsNav()    { clickNavLink("Analytics"); }
}
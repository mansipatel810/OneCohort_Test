package com.cts.mfrp.onecohort.pages.leader;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Leader Dashboard.
 *
 * URL pattern  : /leader/{serviceLineId}/dashboard
 * Angular gate : All content is inside *ngIf="metrics && !loading" —
 *                always call waitForDashboardLoad() before asserting on cards.
 *
 * HTML source  : features/leader/dashboard/leader-dashboard/leader-dashboard.html
 *                features/leader/shared/components/header/header.html
 *                features/leader/shared/components/sidebar/sidebar.html
 */
public class LeaderDashboardPage extends BasePage {

    // ── Header locators ───────────────────────────────────────────────────────
    // header.html: <h2 class="text-xl font-bold text-gray-800">Welcome, Leader.</h2>
    private final By welcomeHeading = By.cssSelector("h2.text-xl.font-bold.text-gray-800");

    // header.html: <span class="text-sm font-bold text-gray-700">Leader</span>
    private final By roleText = By.cssSelector("span.text-sm.font-bold.text-gray-700");

    // header.html: <div class="w-10 h-10 bg-blue-600 text-white rounded-full ...">LD</div>
    private final By ldAvatar = By.cssSelector("div.w-10.h-10.bg-blue-600.rounded-full");

    // ── Sidebar locators ──────────────────────────────────────────────────────
    // sidebar.html: <aside class="sidebar">
    private final By sidebarContainer = By.cssSelector("aside.sidebar");

    // sidebar.html: <img src="assets/logo.png" alt="One Cohort">
    private final By logoImage = By.cssSelector(".logo-section img[alt='One Cohort']");

    // sidebar.html: <span>One Cohort</span>
    private final By appNameSpan = By.cssSelector(".logo-section span");

    // sidebar.html: <nav class="menu"><a ...>Dashboard</a><a ...>Cohorts</a></nav>
    // Only 2 links for Leader role (vs 6 for Super Admin)
    private final By navLinks = By.cssSelector("nav.menu a");

    // ── Dashboard container and heading ───────────────────────────────────────
    // leader-dashboard.html: <div class="dashboard-container">
    private final By dashboardContainer = By.cssSelector("div.dashboard-container");

    // leader-dashboard.html: <div class="header"><h2>...Leader Dashboard</h2>
    private final By dashboardHeading = By.cssSelector("div.header > h2");

    // leader-dashboard.html: <span class="badge">Leader</span>
    private final By leaderBadge = By.cssSelector("div.header span.badge");

    // ── KPI section titles ────────────────────────────────────────────────────
    // Values: "Cohorts", "People", "Catalog & Rates",
    //         "Cohorts per Service Line", "Cohorts per Learning Path",
    //         "Training Completion Distribution"
    private final By sectionTitles = By.cssSelector("div.section-title");

    // ── KPI cards (3 grids: 4 + 6 + 3 = 13 total) ────────────────────────────
    private final By allKpiCards    = By.cssSelector("div.kpi-card");
    private final By kpiCardTitles  = By.cssSelector("div.kpi-info > h3");
    private final By kpiNumbers     = By.cssSelector("p.kpi-number");

    // ── Stat cards (3 stats-grids: service line / learning path / completion) ─
    private final By allStatCards = By.cssSelector("div.stat-card");
    private final By statLabels   = By.cssSelector("div.stat-label");
    private final By statValues   = By.cssSelector("div.stat-value");
    private final By statFills    = By.cssSelector("div.stat-bar > div.stat-fill");

    // ── Loading / error states ────────────────────────────────────────────────
    private final By loadingMsg = By.cssSelector("div.status-msg");
    private final By errorMsg   = By.cssSelector("div.status-msg.error");

    // ── Section-specific stat card finders (XPath following-sibling) ──────────
    private By statCardsUnderSection(String sectionTitle) {
        return By.xpath(
            "//div[@class='section-title' and normalize-space()='" + sectionTitle + "']" +
            "/following-sibling::div[@class='stats-grid'][1]//div[@class='stat-card']"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────

    public LeaderDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── Load / readiness ──────────────────────────────────────────────────────

    /**
     * Waits until Angular's *ngIf resolves and the dashboard heading is visible.
     * Must be called after navigation before any KPI assertions.
     */
    public LeaderDashboardPage waitForDashboardLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeading));
        return this;
    }


    public boolean isErrorMessageVisible() {
        return isDisplayed(errorMsg);
    }
    public boolean isLogoVisible() {
        return isDisplayed(logoImage);
    }
    public List<String> getNavLinkTexts() {
        return driver.findElements(navLinks)
                     .stream()
                     .map(el -> el.getText().trim())
                     .collect(Collectors.toList());
    }

    // ── Dashboard heading / badge ─────────────────────────────────────────────


    public String getLeaderBadgeText() {
        return getText(leaderBadge);
    }
    /** Total KPI card count across all 3 sections (expected: 13). */


    public List<String> getKpiNumbers() {
        return driver.findElements(kpiNumbers)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
    // THIS was missing — called by LeaderTest.java TC-LEADER-001
    public List<String> getKpiCardTitles() {
        return driver.findElements(kpiCardTitles).stream()
                .map(WebElement::getText)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }
}

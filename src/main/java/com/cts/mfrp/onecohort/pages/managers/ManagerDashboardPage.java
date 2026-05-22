package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerDashboardPage extends BasePage {

    private final By dashboardContainer  = By.cssSelector("div.dashboard-container");
    private final By welcomeHeading      = By.cssSelector("header h2");
    private final By roleLabel           = By.cssSelector("span.text-sm.font-bold.text-gray-700");
    private final By avatar              = By.cssSelector("div.w-10.h-10.bg-blue-600");
    private final By sidebar             = By.cssSelector("aside.sidebar");
    private final By logoImage           = By.cssSelector("aside.sidebar img");
    private final By appName             = By.cssSelector("aside.sidebar .logo-section span");
    private final By navLinks            = By.cssSelector("aside.sidebar nav.menu a");
    private final By dashboardHeading    = By.cssSelector("div.dashboard-container div.header h2");
    private final By managerBadge        = By.cssSelector("div.dashboard-container div.header span.badge");
    private final By sectionTitles       = By.cssSelector("div.section-title");
    private final By kpiCardTitles       = By.cssSelector(".kpi-card .kpi-info h3");
    private final By kpiNumbers          = By.cssSelector(".kpi-card .kpi-info .kpi-number");
    private final By statCards           = By.cssSelector(".stat-card");
    private final By statLabels          = By.cssSelector(".stat-card .stat-label");
    private final By statValues          = By.cssSelector(".stat-card .stat-value");
    private final By statFills           = By.cssSelector(".stat-card .stat-fill");
    private final By errorMessage        = By.cssSelector(".error, .alert-danger, [class*='error']");

    public ManagerDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    /**
     * Waits for the dashboard container AND at least one KPI card title to appear.
     * KPI data is loaded asynchronously on render.com so a second wait is needed.
     */
    public void waitForDashboardLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardContainer));
        // KPI cards load via an API call — wait for at least one title to be present
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(kpiCardTitles));
    }

    public boolean isDashboardContainerPresent() {
        return isDisplayed(dashboardContainer);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    public String getWelcomeText() {
        return waitForVisible(welcomeHeading).getText().trim();
    }

    /**
     * Returns the role badge text (e.g. "Manager").
     * Uses explicit wait — header renders slightly after dashboardContainer on render.com.
     */
    public String getRoleText() {
        return waitForVisible(roleLabel).getText().trim();
    }

    public boolean isAvatarVisible() {
        return isDisplayed(avatar);
    }

    public String getAvatarText() {
        return waitForVisible(avatar).getText().trim();
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    public boolean isSidebarVisible() {
        return isDisplayed(sidebar);
    }

    public boolean isLogoVisible() {
        return isDisplayed(logoImage);
    }

    public String getAppName() {
        return waitForVisible(appName).getText().trim();
    }

    public int getNavLinkCount() {
        waitForNavLinksReady();
        return driver.findElements(navLinks).size();
    }

    /**
     * Returns sidebar nav link texts.
     * Waits for links to be present — driver.findElements() returns empty immediately
     * without waiting, which fails on render.com before Angular finishes rendering.
     */
    public List<String> getNavLinkTexts() {
        try {
            waitForNavLinksReady();
            return driver.findElements(navLinks)
                    .stream()
                    .map(e -> e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Waits until at least one sidebar nav link is present in the DOM. */
    private void waitForNavLinksReady() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(navLinks));
    }

    // ── Dashboard heading & badge ─────────────────────────────────────────────

    public String getDashboardHeading() {
        return waitForVisible(dashboardHeading).getText().trim();
    }

    public String getManagerBadgeText() {
        return waitForVisible(managerBadge).getText().trim();
    }

    // ── Section titles ────────────────────────────────────────────────────────

    public List<String> getAllSectionTitles() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(sectionTitles));
            return driver.findElements(sectionTitles)
                    .stream()
                    .map(e -> e.getText().trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean isSectionTitlePresent(String title) {
        return getAllSectionTitles()
                .stream()
                .anyMatch(t -> t.equalsIgnoreCase(title));
    }

    // ── KPI cards ─────────────────────────────────────────────────────────────

    /**
     * Returns KPI card title strings.
     * KPI data loads asynchronously — uses FluentWait to poll until titles appear,
     * tolerating the stale-element exceptions common on render.com during Angular hydration.
     */
    public List<String> getKpiCardTitles() {
        try {
            new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(20))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(Exception.class)
                    .until(d -> !d.findElements(kpiCardTitles).isEmpty());
            return driver.findElements(kpiCardTitles)
                    .stream()
                    .map(e -> e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean isKpiCardPresent(String title) {
        return getKpiCardTitles()
                .stream()
                .anyMatch(t -> t.equalsIgnoreCase(title));
    }

    /**
     * Returns KPI number value strings.
     * Uses FluentWait — numeric values arrive after the API call resolves.
     */
    public List<String> getKpiNumbers() {
        try {
            new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(20))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(Exception.class)
                    .until(d -> !d.findElements(kpiNumbers).isEmpty());
            return driver.findElements(kpiNumbers)
                    .stream()
                    .map(e -> e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public int getTotalKpiCardCount() {
        return getKpiCardTitles().size();
    }

    // ── Stat cards ────────────────────────────────────────────────────────────

    public int getTotalStatCardCount() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statCards));
            return driver.findElements(statCards).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getStatCardCountForSection(String sectionTitle) {
        List<WebElement> sections = driver.findElements(sectionTitles);
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getText().trim().equalsIgnoreCase(sectionTitle)) {
                WebElement nextSibling = sections.get(i)
                        .findElement(By.xpath("following-sibling::div[@class='stats-grid'][1]"));
                return nextSibling.findElements(By.cssSelector(".stat-card")).size();
            }
        }
        return 0;
    }

    public List<String> getStatLabels() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statLabels));
            return driver.findElements(statLabels)
                    .stream()
                    .map(e -> e.getText().trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<String> getStatValues() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statValues));
            return driver.findElements(statValues)
                    .stream()
                    .map(e -> e.getText().trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public int getStatFillCount() {
        try {
            return driver.findElements(statFills).size();
        } catch (Exception e) {
            return 0;
        }
    }

    // ── Error check ───────────────────────────────────────────────────────────

    /**
     * Returns true only if a visible error element exists.
     * driver.findElements() is safe here — empty list = no error = false is the happy path.
     */
    public boolean isErrorMessageVisible() {
        try {
            List<WebElement> errors = driver.findElements(errorMessage);
            return errors.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /**
     * Clicks "Manage Cohorts" in the sidebar.
     * Waits for nav links to be present before searching to avoid empty-list on render.com.
     */
    public void clickManageCohortsNav() {
        try {
            waitForNavLinksReady();
            driver.findElements(navLinks)
                    .stream()
                    .filter(e -> e.getText().trim().equalsIgnoreCase("Manage Cohorts"))
                    .findFirst()
                    .ifPresent(WebElement::click);
        } catch (Exception ignored) {}
    }

    /**
     * Clicks "Dashboard" in the sidebar.
     * Waits for nav links to be present before searching.
     */
    public void clickDashboardNav() {
        try {
            waitForNavLinksReady();
            driver.findElements(navLinks)
                    .stream()
                    .filter(e -> e.getText().trim().equalsIgnoreCase("Dashboard"))
                    .findFirst()
                    .ifPresent(WebElement::click);
        } catch (Exception ignored) {}
    }
}

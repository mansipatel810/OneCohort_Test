package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

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

    public void waitForDashboardLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardContainer));
    }

    public boolean isDashboardContainerPresent() {
        return !driver.findElements(dashboardContainer).isEmpty();
    }

    public String getWelcomeText() {
        return driver.findElement(welcomeHeading).getText().trim();
    }

    public String getRoleText() {
        return driver.findElement(roleLabel).getText().trim();
    }

    public boolean isAvatarVisible() {
        return driver.findElement(avatar).isDisplayed();
    }

    public String getAvatarText() {
        return driver.findElement(avatar).getText().trim();
    }

    public boolean isSidebarVisible() {
        return driver.findElement(sidebar).isDisplayed();
    }

    public boolean isLogoVisible() {
        return driver.findElement(logoImage).isDisplayed();
    }

    public String getAppName() {
        return driver.findElement(appName).getText().trim();
    }

    public int getNavLinkCount() {
        return driver.findElements(navLinks).size();
    }

    public List<String> getNavLinkTexts() {
        return driver.findElements(navLinks)
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public String getDashboardHeading() {
        return driver.findElement(dashboardHeading).getText().trim();
    }

    public String getManagerBadgeText() {
        return driver.findElement(managerBadge).getText().trim();
    }

    public List<String> getAllSectionTitles() {
        return driver.findElements(sectionTitles)
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public boolean isSectionTitlePresent(String title) {
        return getAllSectionTitles()
                .stream()
                .anyMatch(t -> t.equalsIgnoreCase(title));
    }

    public List<String> getKpiCardTitles() {
        return driver.findElements(kpiCardTitles)
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public boolean isKpiCardPresent(String title) {
        return getKpiCardTitles()
                .stream()
                .anyMatch(t -> t.equalsIgnoreCase(title));
    }

    public List<String> getKpiNumbers() {
        return driver.findElements(kpiNumbers)
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public int getTotalKpiCardCount() {
        return driver.findElements(kpiCardTitles).size();
    }

    public int getTotalStatCardCount() {
        return driver.findElements(statCards).size();
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
        return driver.findElements(statLabels)
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public List<String> getStatValues() {
        return driver.findElements(statValues)
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public int getStatFillCount() {
        return driver.findElements(statFills).size();
    }

    public boolean isErrorMessageVisible() {
        List<WebElement> errors = driver.findElements(errorMessage);
        return errors.stream().anyMatch(WebElement::isDisplayed);
    }

    public void clickManageCohortsNav() {
        driver.findElements(navLinks)
                .stream()
                .filter(e -> e.getText().trim().equalsIgnoreCase("Manage Cohorts"))
                .findFirst()
                .ifPresent(WebElement::click);
    }

    public void clickDashboardNav() {
        driver.findElements(navLinks)
                .stream()
                .filter(e -> e.getText().trim().equalsIgnoreCase("Dashboard"))
                .findFirst()
                .ifPresent(WebElement::click);
    }
}
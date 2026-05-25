package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class SuperAdminDashboardPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By dashboardContainer = By.cssSelector("div.dashboard-container");

    // KPI number values and Badges
    private final By kpiNumbers       = By.cssSelector("p.kpi-number");
    private final By superUserBadge   = By.xpath("//*[self::span or self::div or self::p][contains(text(),'Super User')]");
    private final By avatarBtn        = By.cssSelector("div.w-10.h-10.bg-blue-600");

    // Primary KPI cards (Merged locators)
    private final By totalCohortsCard = By.xpath("//h3[contains(text(),'Total Cohorts') or normalize-space()='Total Cohorts']");
    private final By activeCard       = By.xpath("//h3[contains(text(),'Active') or normalize-space()='Active']");
    private final By completedCard    = By.xpath("//h3[contains(text(),'Completed') or normalize-space()='Completed']");
    private final By upcomingCard     = By.xpath("//h3[contains(text(),'Upcoming') or normalize-space()='Upcoming']");

    // Additional KPI cards from Git branch
    private final By totalInternsCard    = By.xpath("//h3[normalize-space()='Total Interns']");
    private final By internsTrainingCard = By.xpath("//h3[normalize-space()='Interns In Training']");
    private final By trainersCard        = By.xpath("//h3[normalize-space()='Trainers']");
    private final By pocsCard            = By.xpath("//h3[normalize-space()='POCs']");
    private final By managersCard        = By.xpath("//h3[normalize-space()='Managers']");
    private final By leadersCard         = By.xpath("//h3[normalize-space()='Leaders']");
    private final By serviceLinesCard    = By.xpath("//h3[normalize-space()='Service Lines']");
    private final By learningPathsCard   = By.xpath("//h3[normalize-space()='Learning Paths']");
    private final By avgCompletionCard   = By.xpath("//h3[normalize-space()='Avg. Completion Rate']");

    // Explicit Nav Locators from Git branch
    private final By navDashboard        = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/dashboard']");
    private final By navCohorts          = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/cohorts']");
    private final By navLeadership       = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/leadership']");
    private final By navBatchOwners      = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/batch-owners']");
    private final By navTrainingProgress = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/training-progress']");
    private final By navSystemConfig     = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/system-config']");

    public SuperAdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── Waits & Page State ────────────────────────────────────────────────────

    public void waitForDashboardContainer() {
        waitForVisible(dashboardContainer);
        waitForVisible(totalCohortsCard); // Extra safety check added from Git
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Sidebar Navigation ────────────────────────────────────────────────────

    public WebElement getMenuItemElement(String itemText) {
        return driver.findElement(By.xpath(
                "//nav[contains(@class,'menu')]//a[contains(normalize-space(),'" + itemText + "')]"));
    }

    public boolean isMenuItemVisible(String itemText) {
        return !driver.findElements(By.xpath(
                "//nav[contains(@class,'menu')]//a[contains(normalize-space(),'" + itemText + "')]")).isEmpty();
    }

    // Explicit nav clickers using safe BasePage waits
    public void clickNavDashboard() { waitForClickable(navDashboard).click(); }
    public void clickNavCohorts() { waitForClickable(navCohorts).click(); }
    public void clickNavLeadership() { waitForClickable(navLeadership).click(); }
    public void clickNavBatchOwners() { waitForClickable(navBatchOwners).click(); }
    public void clickNavTrainingProgress() { waitForClickable(navTrainingProgress).click(); }
    public void clickNavSystemConfig() { waitForClickable(navSystemConfig).click(); }

    // ── KPI Cards Getters ─────────────────────────────────────────────────────

    public WebElement getTotalCohortsCardElement() { return waitForVisible(totalCohortsCard); }
    public WebElement getActiveCardElement() { return waitForVisible(activeCard); }
    public WebElement getCompletedCardElement() { return waitForVisible(completedCard); }
    public WebElement getUpcomingCardElement() { return waitForVisible(upcomingCard); }
    public WebElement getTotalInternsCardElement() { return waitForVisible(totalInternsCard); }
    public WebElement getInternsTrainingCardElement() { return waitForVisible(internsTrainingCard); }
    public WebElement getTrainersCardElement() { return waitForVisible(trainersCard); }
    public WebElement getPocsCardElement() { return waitForVisible(pocsCard); }
    public WebElement getManagersCardElement() { return waitForVisible(managersCard); }
    public WebElement getLeadersCardElement() { return waitForVisible(leadersCard); }
    public WebElement getServiceLinesCardElement() { return waitForVisible(serviceLinesCard); }
    public WebElement getLearningPathsCardElement() { return waitForVisible(learningPathsCard); }
    public WebElement getAvgCompletionCardElement() { return waitForVisible(avgCompletionCard); }

    // ── KPI Numbers & Badges ──────────────────────────────────────────────────

    public List<WebElement> getKpiNumberElements() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(kpiNumbers));
            return driver.findElements(kpiNumbers);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String getSuperUserBadgeText() {
        try {
            return waitForVisible(superUserBadge).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    public void clickAvatar() {
        waitForClickable(avatarBtn).click();
    }
}
package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SuperAdminDashboardPage {

    WebDriver driver;
    WebDriverWait wait;

    By dashboardContainer  = By.xpath("//div[contains(@class,'dashboard-container')]");
    By superUserBadge      = By.xpath("//span[contains(@class,'text-sm') and contains(text(),'Super User')]");
    By kpiNumbers          = By.xpath("//p[contains(@class,'kpi-number')]");

    By totalCohortsCard    = By.xpath("//h3[normalize-space()='Total Cohorts']");
    By activeCard          = By.xpath("//h3[normalize-space()='Active']");
    By completedCard       = By.xpath("//h3[normalize-space()='Completed']");
    By upcomingCard        = By.xpath("//h3[normalize-space()='Upcoming']");

    By totalInternsCard    = By.xpath("//h3[normalize-space()='Total Interns']");
    By internsTrainingCard = By.xpath("//h3[normalize-space()='Interns In Training']");
    By trainersCard        = By.xpath("//h3[normalize-space()='Trainers']");
    By pocsCard            = By.xpath("//h3[normalize-space()='POCs']");
    By managersCard        = By.xpath("//h3[normalize-space()='Managers']");
    By leadersCard         = By.xpath("//h3[normalize-space()='Leaders']");

    By serviceLinesCard    = By.xpath("//h3[normalize-space()='Service Lines']");
    By learningPathsCard   = By.xpath("//h3[normalize-space()='Learning Paths']");
    By avgCompletionCard   = By.xpath("//h3[normalize-space()='Avg. Completion Rate']");

    By avatarBtn           = By.xpath("//div[contains(@class,'w-10') and contains(@class,'h-10') and contains(@class,'bg-blue-600')]");

    By navDashboard        = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/dashboard']");
    By navCohorts          = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/cohorts']");
    By navLeadership       = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/leadership']");
    By navBatchOwners      = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/batch-owners']");
    By navTrainingProgress = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/training-progress']");
    By navSystemConfig     = By.xpath("//nav[contains(@class,'menu')]//a[@href='/super-admin/system-config']");

    public SuperAdminDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    public void waitForDashboardToLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardContainer));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(.,'Total Cohorts')]")));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

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

    public WebElement getTotalInternsCardElement() {
        return driver.findElement(totalInternsCard);
    }

    public WebElement getInternsTrainingCardElement() {
        return driver.findElement(internsTrainingCard);
    }

    public WebElement getTrainersCardElement() {
        return driver.findElement(trainersCard);
    }

    public WebElement getPocsCardElement() {
        return driver.findElement(pocsCard);
    }

    public WebElement getManagersCardElement() {
        return driver.findElement(managersCard);
    }

    public WebElement getLeadersCardElement() {
        return driver.findElement(leadersCard);
    }

    public WebElement getServiceLinesCardElement() {
        return driver.findElement(serviceLinesCard);
    }

    public WebElement getLearningPathsCardElement() {
        return driver.findElement(learningPathsCard);
    }

    public WebElement getAvgCompletionCardElement() {
        return driver.findElement(avgCompletionCard);
    }

    public List<WebElement> getKpiNumberElements() {
        return driver.findElements(kpiNumbers);
    }

    public String getSuperUserBadgeText() {
        return driver.findElement(superUserBadge).getText();
    }

    public WebElement getMenuItemElement(String itemText) {
        return driver.findElement(By.xpath(
                "//nav[contains(@class,'menu')]//a[contains(normalize-space(),'" + itemText + "')]"));
    }

    public boolean isMenuItemVisible(String itemText) {
        return !driver.findElements(By.xpath(
                "//nav[contains(@class,'menu')]//a[contains(normalize-space(),'" + itemText + "')]")).isEmpty();
    }

    public void clickNavDashboard() {
        driver.findElement(navDashboard).click();
    }

    public void clickNavCohorts() {
        driver.findElement(navCohorts).click();
    }

    public void clickNavLeadership() {
        driver.findElement(navLeadership).click();
    }

    public void clickNavBatchOwners() {
        driver.findElement(navBatchOwners).click();
    }

    public void clickNavTrainingProgress() {
        driver.findElement(navTrainingProgress).click();
    }

    public void clickNavSystemConfig() {
        driver.findElement(navSystemConfig).click();
    }

    public void clickAvatar() {
        driver.findElement(avatarBtn).click();
    }
}
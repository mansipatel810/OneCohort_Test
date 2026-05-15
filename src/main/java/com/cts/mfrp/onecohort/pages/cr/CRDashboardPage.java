package com.cts.mfrp.onecohort.pages.cr;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;

/**
 * Page Object for the CR (Class Representative) Dashboard.
 * Covers all elements tested in CRDashboardTest (FRD Section 12).
 *
 * All locators that were previously inline in the test class live here.
 */
public class CRDashboardPage extends BasePage {

    private final String cohortId;

    // ── Dashboard locators (FRD 12.2) ─────────────────────────────────────────

    private final By welcomeGreeting = By.xpath(
            "//*[contains(text(),'Welcome') and contains(text(),'CR')]");

    private final By summaryHeaderCards = By.cssSelector(
            "[class*='card'], [class*='summary'], [class*='stat'], [class*='metric'], [class*='header-card']");

    private final By totalMembersCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total member') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'members')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");

    private final By learningPathCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'learning path')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");

    private final By statusCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'status')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");

    private final By batchOwnerField = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'batch owner')]");

    private final By startDateField = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start date')]");

    private final By totalInternsField = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total intern') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'interns')]");

    private final By currentProgressField = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'current progress') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'% complete')]");

    private final By trainingTimelineSection = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'training timeline') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'timeline')]");

    private final By weekButtons = By.xpath(
            "//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'week')] " +
            "| //*[contains(@class,'week')]");

    private final By qualifierExam = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'qualifier')]");

    private final By interimEvaluation = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'interim')]");

    private final By finalEvaluation = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'final')]");

    private final By overallProgressCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'overall progress') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'weeks remaining')]");

    private final By traineesTable = By.cssSelector("table");
    private final By traineesRows  = By.cssSelector("table tbody tr");

    private final By crudButtons = By.xpath(
            "//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'create') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'edit') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'delete') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add trainee')]");

    private final By logoutDirect = By.xpath(
            "//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'logout')" +
            " or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sign out')" +
            " or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'log out')]" +
            "|//a[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'logout')]" +
            "|//*[@aria-label='Logout' or @aria-label='Sign out' or @title='Logout']");

    private final By userMenuTrigger = By.cssSelector(
            "[class*='user-menu'],[class*='avatar'],[class*='account'],[class*='profile-icon']," +
            "[class*='user-icon'],[class*='dropdown-toggle'],[class*='user-btn']");

    public CRDashboardPage(WebDriver driver, String cohortId) {
        super(driver);
        this.cohortId = cohortId;
    }

    // ── URL ───────────────────────────────────────────────────────────────────

    public void waitForCrRoute() {
        wait.until(ExpectedConditions.urlContains("/cr/"));
    }

    public boolean isUrlContainsCohortId() {
        return driver.getCurrentUrl().contains(cohortId);
    }

    // ── Alert handling ────────────────────────────────────────────────────────

    public boolean dismissAlertIfPresent() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            return true;
        } catch (Exception e) { return false; }
    }

    // ── Welcome greeting (FRD 12.2) ───────────────────────────────────────────

    public boolean isWelcomeGreetingVisible() {
        return isDisplayed(welcomeGreeting);
    }

    public WebElement getWelcomeGreetingElement() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeGreeting));
    }

    public String getPageBodyText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    // ── Sidebar (FRD 12.3) ────────────────────────────────────────────────────

    public boolean isCohortIdVisibleOnPage() {
        By sidebarEntry = By.xpath(
                "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
                "//*[contains(text(),'" + cohortId + "')]");
        By anyEntry = By.xpath("//*[contains(text(),'" + cohortId + "')]");
        return elementExists(sidebarEntry) || elementExists(anyEntry);
    }

    public WebElement getCohortIdElement() {
        By sidebarEntry = By.xpath(
                "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
                "//*[contains(text(),'" + cohortId + "')]");
        if (elementExists(sidebarEntry)) return driver.findElement(sidebarEntry);
        return driver.findElement(By.xpath("//*[contains(text(),'" + cohortId + "')]"));
    }

    // ── Summary header cards (FRD 12.2.1) ────────────────────────────────────

    public List<WebElement> getSummaryHeaderCards() {
        try { return driver.findElements(summaryHeaderCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public boolean isTotalMembersCardVisible() {
        if (elementExists(totalMembersCard)) return true;
        By fallback = By.xpath(
                "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'member') " +
                "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'intern')]");
        return elementExists(fallback);
    }

    public WebElement getTotalMembersCardElement() {
        if (elementExists(totalMembersCard)) return driver.findElement(totalMembersCard);
        return driver.findElement(By.xpath(
                "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'member') " +
                "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'intern')]"));
    }

    public boolean isLearningPathCardVisible() {
        return elementExists(learningPathCard);
    }

    public WebElement getLearningPathCardElement() {
        return driver.findElement(learningPathCard);
    }

    public boolean isStatusCardVisible() {
        return elementExists(statusCard);
    }

    public WebElement getStatusCardElement() {
        return driver.findElement(statusCard);
    }

    // ── Cohort metadata panel (FRD 12.2.2) ───────────────────────────────────

    public boolean isBatchOwnerFieldVisible() {
        return elementExists(batchOwnerField);
    }

    public boolean isStartDateFieldVisible() {
        return elementExists(startDateField);
    }

    public boolean isTotalInternsFieldVisible() {
        return elementExists(totalInternsField);
    }

    public boolean isCurrentProgressFieldVisible() {
        return elementExists(currentProgressField);
    }

    // ── Training Timeline (FRD 12.2.3) ───────────────────────────────────────

    public boolean isTrainingTimelineSectionVisible() {
        return elementExists(trainingTimelineSection);
    }

    public WebElement getTrainingTimelineSectionElement() {
        return driver.findElement(trainingTimelineSection);
    }

    public List<WebElement> getWeekButtons() {
        try { return driver.findElements(weekButtons); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Evaluation panel (FRD 12.2.4) ────────────────────────────────────────

    public boolean isQualifierExamVisible() {
        return elementExists(qualifierExam);
    }

    public boolean isInterimEvaluationVisible() {
        return elementExists(interimEvaluation);
    }

    public boolean isFinalEvaluationVisible() {
        return elementExists(finalEvaluation);
    }

    // ── Overall progress & trainees table ─────────────────────────────────────

    public boolean isOverallProgressCardVisible() {
        return elementExists(overallProgressCard);
    }

    public boolean isTraineesTableVisible() {
        return isDisplayed(traineesTable);
    }

    public List<WebElement> getTraineesTableRows() {
        try { return driver.findElements(traineesRows); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Read-only / Access control (FRD 12.5) ────────────────────────────────

    public boolean areCrudButtonsPresent() {
        return elementExists(crudButtons);
    }

    // ── Logout (FRD 12.6) ────────────────────────────────────────────────────

    public boolean isLogoutDirectlyVisible() {
        return elementExists(logoutDirect);
    }

    public boolean isUserMenuTriggerVisible() {
        return elementExists(userMenuTrigger);
    }

    public void clickUserMenuTrigger() {
        driver.findElement(userMenuTrigger).click();
    }

    public boolean isLogoutVisibleAfterMenuOpen() {
        return elementExists(logoutDirect);
    }

    public void clickLogout() {
        driver.findElement(logoutDirect).click();
    }
}

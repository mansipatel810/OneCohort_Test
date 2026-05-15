package com.cts.mfrp.onecohort.pages.batchowners;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.Collections;
import java.util.List;

/**
 * Page Object for the Batch Owner (POC) Dashboard.
 * Covers all elements tested in BatchOwnerDashboardTest (FRD Section 13).
 *
 * All locators that were previously inline in the test class live here.
 */
public class BatchOwnerDashboardPage extends BasePage {

    // ── Dashboard locators (FRD 13.3) ─────────────────────────────────────────

    private final By welcomeGreeting = By.xpath(
            "//*[contains(text(),'Welcome') and (contains(text(),'POC') " +
            "or contains(text(),'Batch Owner'))]");

    private final By batchOwnerRoleLabel = By.xpath(
            "//*[contains(text(),'Batch Owner') and " +
            "(contains(@class,'badge') or contains(@class,'role') or contains(@class,'label') " +
            "or contains(@class,'pill') or contains(@class,'tag'))]");

    private final By dashboardHeading = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'batch owner dashboard') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'poc dashboard')]");

    // FRD 13.3.1: Cohorts Summary cards
    private final By totalCohortsCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total cohort')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By activeCohortsCard = By.xpath(
            "//*[normalize-space(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))='active']" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By completedCohortsCard = By.xpath(
            "//*[normalize-space(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))='completed']" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By upcomingCohortsCard = By.xpath(
            "//*[normalize-space(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))='upcoming']" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");

    // FRD 13.3.2: People Summary cards
    private final By totalInternsCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total intern')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By internsInTrainingCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'interns in training') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'in training')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By trainersCard = By.xpath(
            "//*[normalize-space(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))='trainers']" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By pocsCard = By.xpath(
            "//*[normalize-space(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))='pocs']" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");

    // FRD 13.3.3: Catalog & Rates cards
    private final By serviceLinesCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'service line')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By learningPathsCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'learning path')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By avgCompletionCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'avg') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'completion rate') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'average')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");

    // FRD 13.3.3/13.3.4: Stats sections
    private final By cohortsPerServiceLineSection = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cohorts per service line') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'per service')]");
    private final By cohortsPerLearningPathSection = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cohorts per learning path') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'per learning path')]");
    private final By trainingCompletionDistribution = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'training completion') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'completion distribution')]");

    // FRD 13.7: Sidebar (Dashboard + Cohorts only)
    private final By sidebarDashboardLink = By.xpath(
            "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
            "//a[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'dashboard')] " +
            "| //nav//a[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'dashboard')]");
    private final By sidebarCohortsLink = By.xpath(
            "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
            "//a[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cohort')] " +
            "| //nav//a[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cohort')]");

    // FRD 13.4: Cohorts list page
    private final By cohortsSearchBar = By.cssSelector(
            "input[type='search'], input[placeholder*='Search'], input[placeholder*='search'], " +
            "input[placeholder*='Cohort'], input[placeholder*='cohort'], " +
            "[class*='search'] input");
    private final By filtersButton = By.xpath(
            "//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'filter')]" +
            "| //*[contains(@class,'filter')][self::button or self::a]");
    private final By statusFilterDropdown = By.xpath(
            "//select[preceding-sibling::*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'status')] " +
            "or parent::*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'status')]]" +
            "| //*[contains(@class,'status')]//select | //*[contains(@class,'filter')]//select[1]");
    private final By learningPathFilterDropdown = By.xpath(
            "//select[preceding-sibling::*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'learning')] " +
            "or parent::*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'learning')]]" +
            "| //*[contains(@class,'filter')]//select[2]");
    private final By cohortsTable     = By.cssSelector("table");
    private final By cohortsTableRows = By.cssSelector("table tbody tr");
    private final By cohortIdLinks    = By.cssSelector("table tbody tr td:first-child a, table tbody tr td a");

    // FRD 13.5: Cohort detail view
    private final By backToCohortsLink = By.cssSelector("button.btn-link.text-muted, button.btn.btn-link");
    private final By detailSummaryCards = By.cssSelector(
            "[class*='card'], [class*='summary'], [class*='stat'], [class*='metric'], [class*='header-card']");
    private final By detailTotalMembersCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total member') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'members')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By detailLearningPathCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'learning path')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By detailStatusCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'status')]" +
            "/ancestor::*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')][1]");
    private final By metadataBatchOwner = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'batch owner')]");
    private final By metadataStartDate = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start date')]");
    private final By metadataTotalInterns = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total intern') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'interns')]");
    private final By trainingTimelineSection = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'training timeline') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'timeline')]");
    private final By evaluationPanel = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'evaluation')]");
    private final By overallProgressCard = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'overall progress') " +
            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'weeks remaining')]");
    private final By traineesTable    = By.cssSelector("table");
    private final By traineesRows     = By.cssSelector("table tbody tr");

    public BatchOwnerDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── URL / load ────────────────────────────────────────────────────────────

    public void waitForDashboardLoad() {
        wait.until(ExpectedConditions.urlContains("/batch-owner/"));
    }

    public boolean isDashboardUrlValid() {
        String url = driver.getCurrentUrl();
        return url.contains("batch-owner") || url.contains("poc") || url.contains("dashboard");
    }

    // ── Welcome greeting ──────────────────────────────────────────────────────

    public boolean isWelcomeGreetingVisible() {
        return elementExists(welcomeGreeting);
    }

    public WebElement getWelcomeGreetingElement() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeGreeting));
    }

    public boolean isBatchOwnerRoleLabelVisible() {
        return elementExists(batchOwnerRoleLabel);
    }

    // ── Dashboard heading ─────────────────────────────────────────────────────

    public boolean isDashboardHeadingVisible() {
        return elementExists(dashboardHeading);
    }

    public WebElement getDashboardHeadingElement() {
        return driver.findElement(dashboardHeading);
    }

    // ── Cohorts Summary cards (FRD 13.3.1) ───────────────────────────────────

    public boolean isTotalCohortsCardVisible()    { return elementExists(totalCohortsCard); }
    public boolean isActiveCohortsCardVisible()   { return elementExists(activeCohortsCard); }
    public boolean isCompletedCohortsCardVisible(){ return elementExists(completedCohortsCard); }
    public boolean isUpcomingCohortsCardVisible() { return elementExists(upcomingCohortsCard); }

    public WebElement getTotalCohortsCardElement()    { return driver.findElement(totalCohortsCard); }
    public WebElement getActiveCohortsCardElement()   { return driver.findElement(activeCohortsCard); }
    public WebElement getCompletedCohortsCardElement(){ return driver.findElement(completedCohortsCard); }
    public WebElement getUpcomingCohortsCardElement() { return driver.findElement(upcomingCohortsCard); }

    // ── People Summary cards (FRD 13.3.2) ────────────────────────────────────

    public boolean isTotalInternsCardVisible()      { return elementExists(totalInternsCard); }
    public boolean isInternsInTrainingCardVisible() { return elementExists(internsInTrainingCard); }
    public boolean isTrainersCardVisible()          { return elementExists(trainersCard); }
    public boolean isPocsCardVisible()              { return elementExists(pocsCard); }

    public WebElement getTotalInternsCardElement()      { return driver.findElement(totalInternsCard); }
    public WebElement getInternsInTrainingCardElement() { return driver.findElement(internsInTrainingCard); }
    public WebElement getTrainersCardElement()          { return driver.findElement(trainersCard); }
    public WebElement getPocsCardElement()              { return driver.findElement(pocsCard); }

    // ── Catalog & Rates (FRD 13.3.3) ─────────────────────────────────────────

    public boolean isServiceLinesCardVisible()    { return elementExists(serviceLinesCard); }
    public boolean isLearningPathsCardVisible()   { return elementExists(learningPathsCard); }
    public boolean isAvgCompletionCardVisible()   { return elementExists(avgCompletionCard); }

    public WebElement getServiceLinesCardElement()  { return driver.findElement(serviceLinesCard); }
    public WebElement getLearningPathsCardElement() { return driver.findElement(learningPathsCard); }
    public WebElement getAvgCompletionCardElement() { return driver.findElement(avgCompletionCard); }

    // ── Stats sections ────────────────────────────────────────────────────────

    public boolean isCohortsPerServiceLineSectionVisible()   { return elementExists(cohortsPerServiceLineSection); }
    public boolean isCohortsPerLearningPathSectionVisible()  { return elementExists(cohortsPerLearningPathSection); }
    public boolean isTrainingCompletionDistributionVisible() { return elementExists(trainingCompletionDistribution); }

    public WebElement getCohortsPerServiceLineSectionElement()   { return driver.findElement(cohortsPerServiceLineSection); }
    public WebElement getCohortsPerLearningPathSectionElement()  { return driver.findElement(cohortsPerLearningPathSection); }
    public WebElement getTrainingCompletionDistributionElement() { return driver.findElement(trainingCompletionDistribution); }

    // ── Sidebar (FRD 13.7) ────────────────────────────────────────────────────

    public boolean isSidebarDashboardLinkVisible() { return elementExists(sidebarDashboardLink); }
    public boolean isSidebarCohortsLinkVisible()   { return elementExists(sidebarCohortsLink); }

    public void clickSidebarCohortsLink() {
        driver.findElement(sidebarCohortsLink).click();
    }

    // ── Cohorts list page (FRD 13.4) ─────────────────────────────────────────

    public boolean isSearchBarVisible()          { return elementExists(cohortsSearchBar); }
    public boolean isFiltersButtonVisible()      { return elementExists(filtersButton); }
    public boolean isStatusFilterVisible()       { return elementExists(statusFilterDropdown); }
    public boolean isLearningPathFilterVisible() { return elementExists(learningPathFilterDropdown); }
    public boolean isCohortsTableVisible()       { return isDisplayed(cohortsTable); }

    public WebElement getSearchBarElement()     { return driver.findElement(cohortsSearchBar); }
    public WebElement getFiltersButtonElement() { return driver.findElement(filtersButton); }

    public List<WebElement> getCohortsTableRows() {
        try { return driver.findElements(cohortsTableRows); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getCohortIdLinks() {
        try { return driver.findElements(cohortIdLinks); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void selectStatusFilter(String value) {
        try { new Select(driver.findElement(statusFilterDropdown)).selectByVisibleText(value); }
        catch (Exception ignored) {}
    }

    public void selectLearningPathFilter(String value) {
        try { new Select(driver.findElement(learningPathFilterDropdown)).selectByVisibleText(value); }
        catch (Exception ignored) {}
    }

    // ── Cohort detail view (FRD 13.5) ────────────────────────────────────────

    public boolean isBackToCohortsLinkVisible()   { return elementExists(backToCohortsLink); }
    public boolean isDetailSummaryCardsPresent()  {
        return !driver.findElements(detailSummaryCards).isEmpty();
    }
    public boolean isDetailTotalMembersVisible()  { return elementExists(detailTotalMembersCard); }
    public boolean isDetailLearningPathVisible()  { return elementExists(detailLearningPathCard); }
    public boolean isDetailStatusVisible()        { return elementExists(detailStatusCard); }
    public boolean isMetadataBatchOwnerVisible()  { return elementExists(metadataBatchOwner); }
    public boolean isMetadataStartDateVisible()   { return elementExists(metadataStartDate); }
    public boolean isMetadataTotalInternsVisible(){ return elementExists(metadataTotalInterns); }
    public boolean isTrainingTimelineVisible()    { return elementExists(trainingTimelineSection); }
    public boolean isEvaluationPanelVisible()     { return elementExists(evaluationPanel); }
    public boolean isOverallProgressVisible()     { return elementExists(overallProgressCard); }
    public boolean isTraineesTableVisible()       { return isDisplayed(traineesTable); }

    public List<WebElement> getTraineesRows() {
        try { return driver.findElements(traineesRows); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void clickBackToCohorts() {
        driver.findElement(backToCohortsLink).click();
    }

    public void clickFirstCohortLink() {
        List<WebElement> links = getCohortIdLinks();
        if (!links.isEmpty()) links.get(0).click();
    }
}

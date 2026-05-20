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
            "//*[contains(normalize-space(),'Batch Owner Dashboard') " +
            "or contains(normalize-space(),'POC Dashboard')]");

    // FRD 13.3.1: Cohorts Summary cards — container-first pattern (no ancestor::* traversal)
    private final By totalCohortsCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Total Cohort')]]");
    private final By activeCohortsCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[normalize-space()='Active']]");
    private final By completedCohortsCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[normalize-space()='Completed']]");
    private final By upcomingCohortsCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[normalize-space()='Upcoming']]");

    // FRD 13.3.2: People Summary cards
    private final By totalInternsCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Total Intern')]]");
    private final By internsInTrainingCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Interns in Training') or contains(normalize-space(),'In Training')]]");
    private final By trainersCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[normalize-space()='Trainers']]");
    private final By pocsCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[normalize-space()='POCs']]");

    // FRD 13.3.3: Catalog & Rates cards
    private final By serviceLinesCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Service Line')]]");
    private final By learningPathsCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Learning Path')]]");
    private final By avgCompletionCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Avg') or contains(normalize-space(),'Completion Rate') " +
            "or contains(normalize-space(),'Average')]]");

    // FRD 13.3.3/13.3.4: Stats sections
    private final By cohortsPerServiceLineSection = By.xpath(
            "//*[contains(normalize-space(),'Cohorts per Service Line') " +
            "or contains(normalize-space(),'Per Service')]");
    private final By cohortsPerLearningPathSection = By.xpath(
            "//*[contains(normalize-space(),'Cohorts per Learning Path') " +
            "or contains(normalize-space(),'Per Learning Path')]");
    private final By trainingCompletionDistribution = By.xpath(
            "//*[contains(normalize-space(),'Training Completion') " +
            "or contains(normalize-space(),'Completion Distribution')]");

    // FRD 13.7: Sidebar (Dashboard + Cohorts only)
    private final By sidebarDashboardLink = By.xpath(
            "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
            "//a[contains(normalize-space(),'Dashboard')] " +
            "| //nav//a[contains(normalize-space(),'Dashboard')]");
    private final By sidebarCohortsLink = By.xpath(
            "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
            "//a[contains(normalize-space(),'Cohort')] " +
            "| //nav//a[contains(normalize-space(),'Cohort')]");

    // FRD 13.4: Cohorts list page
    private final By cohortsSearchBar = By.cssSelector(
            "input[type='search'], input[placeholder*='Search'], input[placeholder*='search'], " +
            "input[placeholder*='Cohort'], input[placeholder*='cohort'], " +
            "[class*='search'] input");
    private final By filtersButton = By.xpath(
            "//button[contains(normalize-space(),'Filter')]" +
            " | //*[contains(@class,'filter')][self::button or self::a]");
    private final By statusFilterDropdown = By.xpath(
            "//select[preceding-sibling::*[contains(normalize-space(),'Status')] " +
            "or parent::*[contains(normalize-space(),'Status')]]" +
            " | //*[contains(@class,'status')]//select | //*[contains(@class,'filter')]//select[1]");
    private final By learningPathFilterDropdown = By.xpath(
            "//select[preceding-sibling::*[contains(normalize-space(),'Learning')] " +
            "or parent::*[contains(normalize-space(),'Learning')]]" +
            " | //*[contains(@class,'filter')]//select[2]");
    private final By cohortsTable     = By.cssSelector("table.table, .table-responsive table");
    private final By cohortsTableRows = By.cssSelector("table.table tbody tr, .table-responsive table tbody tr");
    private final By cohortIdLinks    = By.cssSelector(
            "table.table tbody tr td:first-child a, table.table tbody tr td a, " +
            ".table-responsive table tbody tr td:first-child a");

    // FRD 13.5: Cohort detail view
    private final By backToCohortsLink = By.cssSelector("button.btn-link.text-muted, button.btn.btn-link");
    private final By detailSummaryCards = By.cssSelector(
            "[class*='card'], [class*='summary'], [class*='stat'], [class*='metric'], [class*='header-card']");
    private final By detailTotalMembersCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Total Member') or contains(normalize-space(),'Members')]]");
    private final By detailLearningPathCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Learning Path')]]");
    private final By detailStatusCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
            "[.//*[contains(normalize-space(),'Status')]]");
    private final By metadataBatchOwner     = By.xpath("//*[contains(normalize-space(),'Batch Owner')]");
    private final By metadataStartDate      = By.xpath("//*[contains(normalize-space(),'Start Date')]");
    private final By metadataTotalInterns   = By.xpath(
            "//*[contains(normalize-space(),'Total Intern') or contains(normalize-space(),'Interns')]");
    private final By trainingTimelineSection = By.xpath(
            "//*[contains(normalize-space(),'Training Timeline') or contains(normalize-space(),'Timeline')]");
    private final By evaluationPanel         = By.xpath("//*[contains(normalize-space(),'Evaluation')]");
    private final By overallProgressCard     = By.xpath(
            "//*[contains(normalize-space(),'Overall Progress') or contains(normalize-space(),'Weeks Remaining')]");
    private final By traineesTable = By.cssSelector("table.table, .table-responsive table");
    private final By traineesRows  = By.cssSelector("table.table tbody tr, .table-responsive table tbody tr");

    // FRD 13.5.1: Class Representative + Current Progress
    private final By classRepresentativeField = By.xpath(
            "//*[contains(normalize-space(),'Class Representative') " +
            "or contains(normalize-space(),'Class Rep') " +
            "or normalize-space()='CR']");
    private final By currentProgressField = By.xpath(
            "//*[contains(normalize-space(),'Current Progress') " +
            "or contains(normalize-space(),'% Complete')]");

    // FRD 13.6: Week buttons in Training Timeline
    private final By weekButtons = By.xpath(
            "//button[contains(normalize-space(),'Week')] | //*[contains(@class,'week')]");

    // FRD 13.5: Individual evaluation milestones
    private final By qualifierExam     = By.xpath("//*[contains(normalize-space(),'Qualifier')]");
    private final By interimEvaluation = By.xpath("//*[contains(normalize-space(),'Interim')]");
    private final By finalEvaluation   = By.xpath("//*[contains(normalize-space(),'Final')]");

    // FRD 13.7: CRUD buttons (must not be present — read-only role)
    private final By crudButtons = By.xpath(
            "//button[contains(normalize-space(),'Create') " +
            "or contains(normalize-space(),'Edit') " +
            "or contains(normalize-space(),'Delete') " +
            "or contains(normalize-space(),'Add Trainee') " +
            "or contains(normalize-space(),'Add Batch Owner')]");

    // FRD 13.7: Logout controls
    private final By logoutDirect = By.xpath(
            "//button[contains(normalize-space(),'Logout') " +
            "or contains(normalize-space(),'Sign Out') " +
            "or contains(normalize-space(),'Log Out')]" +
            " | //a[contains(normalize-space(),'Logout') or contains(normalize-space(),'Sign Out')]" +
            " | //*[@aria-label='Logout' or @aria-label='Sign out' or @title='Logout']" +
            " | //*[contains(@class,'logout') or contains(@class,'signout')]");
    private final By userMenuTrigger = By.cssSelector(
            "[class*='user-menu'],[class*='avatar'],[class*='account'],[class*='profile-icon']," +
            "[class*='user-icon'],[class*='dropdown-toggle'],[class*='user-btn']");

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

    public WebElement getBatchOwnerRoleLabelElement() {
        return driver.findElement(batchOwnerRoleLabel);
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

    // ── Additional element getters ─────────────────────────────────────────────

    /**
     * Returns the text of the first cell in the first row of the cohorts table.
     * Used by TC-BO-017 to extract a search term without inline By usage.
     */
    public String getFirstCohortRowFirstCellText() {
        List<WebElement> rows = getCohortsTableRows();
        if (rows.isEmpty()) return "";
        try { return rows.get(0).findElement(By.cssSelector("td:first-child")).getText().trim(); }
        catch (Exception e) { return ""; }
    }

    /** Returns both Cohort ID link cells and falls back to the first TD when no anchor tag found. */
    public List<WebElement> getCohortIdLinksOrCells() {
        List<WebElement> links = getCohortIdLinks();
        if (!links.isEmpty()) return links;
        return driver.findElements(By.cssSelector("table tbody tr td:first-child"));
    }

    public WebElement getSidebarDashboardLinkElement() { return driver.findElement(sidebarDashboardLink); }
    public WebElement getSidebarCohortsLinkElement()   { return driver.findElement(sidebarCohortsLink); }

    public void waitForSearchBarVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cohortsSearchBar));
    }
    public void waitForStatusFilterVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(statusFilterDropdown));
    }
    public WebElement getStatusFilterElement()       { return driver.findElement(statusFilterDropdown); }
    public WebElement getLearningPathFilterElement() { return driver.findElement(learningPathFilterDropdown); }

    public WebElement getCohortsTableElement()           { return driver.findElement(cohortsTable); }
    public List<WebElement> getCohortsTableHeaderCells() {
        return driver.findElements(By.cssSelector("table thead th, table th"));
    }
    public void waitForCohortsTableToSettle(int seconds) {
        By rows = cohortsTableRows;
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(seconds))
                .until(d -> !d.findElements(rows).isEmpty());
    }

    /**
     * Multi-strategy Back-to-Cohorts finder:
     * 1. CSS selector (btn-link)
     * 2. JS scan for button containing "back"
     * 3. JS text-node scan for any element containing "back"
     * Returns null if none found (callers assert != null).
     */
    public WebElement getBackToCohortsElement() {
        if (elementExists(backToCohortsLink)) return driver.findElement(backToCohortsLink);
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        WebElement el = (WebElement) js.executeScript(
                "var btns=document.querySelectorAll('button');" +
                "for(var i=0;i<btns.length;i++){" +
                "  if(btns[i].innerText&&btns[i].innerText.toLowerCase().indexOf('back')>=0) return btns[i];}" +
                "var all=document.querySelectorAll('*');" +
                "for(var i=0;i<all.length;i++){var t=all[i].childNodes;" +
                "  for(var j=0;j<t.length;j++){" +
                "    if(t[j].nodeType===3&&t[j].nodeValue&&t[j].nodeValue.toLowerCase().indexOf('back')>=0) return all[i];}" +
                "}" +
                "return null;");
        return el; // may be null
    }

    public List<WebElement> getDetailSummaryCardsList() { return driver.findElements(detailSummaryCards); }
    public WebElement getDetailTotalMembersElement()    { return driver.findElement(detailTotalMembersCard); }
    public WebElement getDetailLearningPathElement()    { return driver.findElement(detailLearningPathCard); }
    public WebElement getDetailStatusElement()          { return driver.findElement(detailStatusCard); }

    public boolean isClassRepresentativeFieldVisible() { return elementExists(classRepresentativeField); }
    public WebElement getClassRepresentativeElement()  { return driver.findElement(classRepresentativeField); }
    public boolean isCurrentProgressVisible()          { return elementExists(currentProgressField); }
    public WebElement getCurrentProgressElement()      { return driver.findElement(currentProgressField); }

    public WebElement getMetadataBatchOwnerElement()   { return driver.findElement(metadataBatchOwner); }
    public WebElement getMetadataStartDateElement()    { return driver.findElement(metadataStartDate); }
    public WebElement getMetadataTotalInternsElement() { return driver.findElement(metadataTotalInterns); }

    public WebElement getTrainingTimelineElement() { return driver.findElement(trainingTimelineSection); }
    public List<WebElement> getWeekButtons() {
        try { return driver.findElements(weekButtons); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public boolean isQualifierExamVisible()     { return elementExists(qualifierExam); }
    public WebElement getQualifierExamElement() { return driver.findElement(qualifierExam); }
    public boolean isInterimEvaluationVisible()     { return elementExists(interimEvaluation); }
    public WebElement getInterimEvaluationElement() { return driver.findElement(interimEvaluation); }
    public boolean isFinalEvaluationVisible()     { return elementExists(finalEvaluation); }
    public WebElement getFinalEvaluationElement() { return driver.findElement(finalEvaluation); }

    public WebElement getEvaluationPanelElement() { return driver.findElement(evaluationPanel); }

    public WebElement getOverallProgressElement() {
        if (elementExists(overallProgressCard)) return driver.findElement(overallProgressCard);
        return driver.findElement(By.cssSelector("canvas,[class*='progress'],[role='progressbar'],[class*='bar']"));
    }

    public WebElement getTraineesTableElement()      { return driver.findElement(traineesTable); }
    public List<WebElement> getTraineesTableHeaders() {
        return driver.findElements(By.cssSelector("table thead th, table th"));
    }

    public boolean isCrudButtonsPresent() { return elementExists(crudButtons); }
    public List<WebElement> getCrudButtonElements() {
        try { return driver.findElements(crudButtons); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public boolean isLogoutDirectlyVisible() { return elementExists(logoutDirect); }
    public List<WebElement> getLogoutDirectElements() {
        try { return driver.findElements(logoutDirect); }
        catch (Exception e) { return Collections.emptyList(); }
    }
    public void clickLogout() { driver.findElement(logoutDirect).click(); }

    public boolean isUserMenuTriggerVisible() { return elementExists(userMenuTrigger); }
    public List<WebElement> getUserMenuTriggerElements() {
        try { return driver.findElements(userMenuTrigger); }
        catch (Exception e) { return Collections.emptyList(); }
    }
    public void clickFirstUserMenuTrigger() { driver.findElement(userMenuTrigger).click(); }
    public boolean isLogoutVisibleAfterMenuOpen() { return elementExists(logoutDirect); }

    public String getPageBodyText() {
        return driver.findElement(By.tagName("body")).getText();
    }
}

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
            "//*[normalize-space()='Training Timeline']");
    private final By evaluationPanel         = By.xpath("//*[contains(normalize-space(),'Evaluation')]");
    private final By overallProgressCard     = By.xpath(
            "//*[contains(normalize-space(),'Overall Progress') or contains(normalize-space(),'Weeks Remaining')]");
    private final By traineesTable = By.cssSelector("table.table, .table-responsive table");
    private final By traineesRows  = By.cssSelector("table.table tbody tr, .table-responsive table tbody tr");

    // FRD 13.5.1: Class Representative + Current Progress


    // FRD 13.5: Individual evaluation milestones
    private final By qualifierExam = By.xpath("//*[normalize-space()='Qualifier Exam']");
    private final By interimEvaluation = By.xpath("//*[normalize-space()='Interim Evaluation']");
    private final By finalEvaluation = By.xpath("//*[normalize-space()='Final Evaluation']");

    // FRD 13.7: CRUD buttons (must not be present — read-only role)



    public BatchOwnerDashboardPage(WebDriver driver) {
        super(driver);
    }




    public boolean isDashboardHeadingVisible() {
        return elementExists(dashboardHeading);
    }
    // ── Cohorts Summary cards (FRD 13.3.1) ───────────────────────────────────

    public boolean isTotalCohortsCardVisible()    { return elementExists(totalCohortsCard); }
    public boolean isActiveCohortsCardVisible()   { return elementExists(activeCohortsCard); }
    public boolean isCompletedCohortsCardVisible(){ return elementExists(completedCohortsCard); }
    public boolean isUpcomingCohortsCardVisible() { return elementExists(upcomingCohortsCard); }


    public boolean isSidebarDashboardLinkVisible() { return elementExists(sidebarDashboardLink); }
    public boolean isSidebarCohortsLinkVisible()   { return elementExists(sidebarCohortsLink); }





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
    public boolean isDetailTotalMembersVisible()  { return elementExists(detailTotalMembersCard); }
    public boolean isDetailLearningPathVisible()  { return elementExists(detailLearningPathCard); }
    public boolean isDetailStatusVisible()        { return elementExists(detailStatusCard); }
    public boolean isTrainingTimelineVisible() {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                    .until(d -> {
                        java.util.List<org.openqa.selenium.WebElement> els =
                                d.findElements(trainingTimelineSection);
                        return !els.isEmpty() && els.get(0).isDisplayed();
                    });
            return true;
        } catch (Exception e) { return false; }
    }
    public boolean isOverallProgressVisible()     { return elementExists(overallProgressCard); }
    public boolean isTraineesTableVisible()       { return isDisplayed(traineesTable); }




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
    public WebElement getSidebarCohortsLinkElement()   { return driver.findElement(sidebarCohortsLink); }

    public void waitForSearchBarVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cohortsSearchBar));
    }
    public void waitForStatusFilterVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(statusFilterDropdown));
    }
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
    public boolean isQualifierExamVisible() {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                    .until(d -> {
                        java.util.List<org.openqa.selenium.WebElement> els =
                                d.findElements(qualifierExam);
                        return !els.isEmpty() && els.get(0).isDisplayed();
                    });
            return true;
        } catch (Exception e) { return false; }
    }
    public boolean isInterimEvaluationVisible() {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                    .until(d -> {
                        java.util.List<org.openqa.selenium.WebElement> els =
                                d.findElements(interimEvaluation);
                        return !els.isEmpty() && els.get(0).isDisplayed();
                    });
            return true;
        } catch (Exception e) { return false; }
    }
    public boolean isFinalEvaluationVisible() {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                    .until(d -> {
                        java.util.List<org.openqa.selenium.WebElement> els =
                                d.findElements(finalEvaluation);
                        return !els.isEmpty() && els.get(0).isDisplayed();
                    });
            return true;
        } catch (Exception e) { return false; }
    }
}

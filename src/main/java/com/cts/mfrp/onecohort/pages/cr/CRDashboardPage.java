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
 *
 * FIX: All isFooVisible() methods now use explicit WebDriverWait
 *      (via presenceOfElementLocated) instead of a bare findElements()
 *      call. This prevents false negatives when the cohort metadata card
 *      renders slightly after the /cr/ URL is reached.
 */

public class CRDashboardPage extends BasePage {

    private final String cohortId;

    // ── Locators ──────────────────────────────────────────────────────────────

    private final By batchOwnerField = By.xpath(
            "//*[contains(normalize-space(),'Batch Owner')]");

    private final By startDateField = By.xpath(
            "//*[contains(normalize-space(),'Start Date')]");

    // UI renders "Total Interns" — kept broad so minor copy changes still match
    private final By totalInternsField = By.xpath(
            "//*[contains(normalize-space(),'Total Interns') " +
                    "or contains(normalize-space(),'Total Intern')]");

    private final By trainingTimelineSection = By.xpath(
            "//*[contains(normalize-space(),'Training Timeline') " +
                    "or contains(normalize-space(),'Timeline')]");

    private final By qualifierExam = By.xpath(
            "//*[contains(normalize-space(),'Qualifier')]");

    private final By interimEvaluation = By.xpath(
            "//*[contains(normalize-space(),'Interim')]");

    private final By finalEvaluation = By.xpath(
            "//*[contains(normalize-space(),'Final')]");

    private final By overallProgressCard = By.xpath(
            "//*[contains(normalize-space(),'Overall Progress') " +
                    "or contains(normalize-space(),'Weeks Remaining')]");

    private final By traineesTable = By.cssSelector(
            "table.table, .table-responsive table");

    private final By crudButtons = By.xpath(
            "//button[contains(normalize-space(),'Create') " +
                    "or contains(normalize-space(),'Edit') " +
                    "or contains(normalize-space(),'Delete') " +
                    "or contains(normalize-space(),'Add Trainee')]");

    // ── Constructor ───────────────────────────────────────────────────────────

    public CRDashboardPage(WebDriver driver, String cohortId) {
        super(driver);
        this.cohortId = cohortId;
    }

    // ── Private helper ────────────────────────────────────────────────────────

    /**
     * Returns true if at least one element matching {@code by} becomes present
     * in the DOM within the configured WebDriverWait timeout.
     * Uses presenceOfElementLocated (not visibilityOf) so hidden elements that
     * are still in the DOM are also counted — consistent with the previous
     * elementExists() behaviour but with proper waiting.
     */
    private boolean waitForPresence(By by) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if at least one element matching {@code by} is both present
     * AND visible within the configured WebDriverWait timeout.
     * Use this for elements the user is expected to actually see on screen.
     */
    private boolean waitForVisibility(By by) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Cohort metadata panel (FRD 12.2.2) ───────────────────────────────────

    public boolean isBatchOwnerFieldVisible() {
        return waitForVisibility(batchOwnerField);
    }

    public boolean isStartDateFieldVisible() {
        return waitForVisibility(startDateField);
    }

    public boolean isTotalInternsFieldVisible() {
        return waitForVisibility(totalInternsField);
    }

    // ── Training Timeline (FRD 12.2.3) ───────────────────────────────────────

    public boolean isTrainingTimelineSectionVisible() {
        return waitForVisibility(trainingTimelineSection);
    }

    // ── Evaluation panel (FRD 12.2.4) ────────────────────────────────────────

    public boolean isQualifierExamVisible() {
        return waitForVisibility(qualifierExam);
    }

    public boolean isInterimEvaluationVisible() {
        return waitForVisibility(interimEvaluation);
    }

    public boolean isFinalEvaluationVisible() {
        return waitForVisibility(finalEvaluation);
    }

    // ── Overall progress & trainees table ────────────────────────────────────

    public boolean isOverallProgressCardVisible() {
        return waitForVisibility(overallProgressCard);
    }

    public boolean isTraineesTableVisible() {
        return waitForVisibility(traineesTable);
    }

    public List<WebElement> getTraineesTableHeaders() {
        // Table must be present before we can read its headers
        waitForPresence(traineesTable);
        return driver.findElements(By.cssSelector("table thead th, table th"));
    }

    /**
     * Returns any Create / Edit / Delete buttons currently in the DOM.
     * Implicit wait is disabled by the caller (TC-CR-004) before this call,
     * so no additional wait is added here — we want an immediate snapshot.
     */
    public List<WebElement> getCrudButtonElements() {
        try {
            return driver.findElements(crudButtons);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
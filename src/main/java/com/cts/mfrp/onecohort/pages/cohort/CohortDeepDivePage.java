package com.cts.mfrp.onecohort.pages.cohort;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class CohortDeepDivePage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By cohortNameHeading = By.cssSelector(
            "h1, h2, [class*='cohort-title'], [class*='cohort-name'], [class*='page-title']");

    private final By summaryCards = By.cssSelector(
            "[class*='card'], [class*='stat'], [class*='kpi'], [class*='metric'], [class*='summary']");

    private final By kpiNumbers = By.cssSelector(
            "[class*='kpi-number'], [class*='stat-value'], [class*='metric-value'], " +
            "p.kpi-number, .stat-value");

    private final By timelineSection = By.xpath(
            "//*[self::section or self::div or self::h2 or self::h3 or self::h4]" +
            "[contains(normalize-space(),'Timeline')]");

    private final By evaluationSection = By.xpath(
            "//*[self::section or self::div or self::h2 or self::h3 or self::h4]" +
            "[contains(normalize-space(),'Evaluation')]");

    private final By addTraineeBtn = By.xpath(
            "//button[contains(normalize-space(),'Add Trainee') " +
            "or contains(normalize-space(),'Add Intern') " +
            "or contains(normalize-space(),'Enroll')]");

    private final By traineeIdInput = By.cssSelector(
            "input[placeholder*='USR'], input[placeholder*='Employee'], " +
            "input[placeholder*='Trainee'], input[formcontrolname*='emp'], " +
            "input[formcontrolname*='trainee'], input[formcontrolname*='userId']");

    private final By modalSubmitBtn = By.xpath(
            "//*[contains(@class,'modal') or @role='dialog']" +
            "//button[contains(normalize-space(),'Add') " +
            "or contains(normalize-space(),'Submit') " +
            "or contains(normalize-space(),'Save') " +
            "or contains(normalize-space(),'Enroll')]" +
            "[not(contains(normalize-space(),'Cancel'))]");

    private final By backButton = By.xpath(
            "//button[contains(normalize-space(),'Back') " +
            "or contains(normalize-space(),'Return')] | " +
            "//a[contains(normalize-space(),'Back')]");

    // ── Constructor ───────────────────────────────────────────────────────────
    public CohortDeepDivePage(WebDriver driver) {
        super(driver);
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    /** Returns true if the deep-dive page has loaded at least one identifiable element. */
    public boolean isPageLoaded() {
        return isDisplayed(cohortNameHeading) || isDisplayed(summaryCards);
    }

    // ── Heading ───────────────────────────────────────────────────────────────

    public boolean isCohortNameHeadingVisible() {
        return isDisplayed(cohortNameHeading);
    }

    // ── KPI cards ─────────────────────────────────────────────────────────────

    public List<WebElement> getSummaryCards() {
        try {
            return driver.findElements(summaryCards);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<WebElement> getKpiNumbers() {
        try {
            return driver.findElements(kpiNumbers);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    public boolean isTimelineSectionVisible() {
        return isDisplayed(timelineSection);
    }

    public boolean isEvaluationSectionVisible() {
        return isDisplayed(evaluationSection);
    }

    // ── Add Trainee ───────────────────────────────────────────────────────────

    public boolean isAddTraineeBtnVisible() {
        return isDisplayed(addTraineeBtn);
    }

    public void clickAddTraineeButton() {
        click(addTraineeBtn);
    }

    public boolean isTraineeIdInputVisible() {
        // First check inside modal, then anywhere on page
        try {
            List<WebElement> modalInputs = driver.findElements(By.cssSelector(
                    "[class*='modal'] input, [role='dialog'] input"));
            if (!modalInputs.isEmpty()) return true;
        } catch (Exception ignored) {}
        return isDisplayed(traineeIdInput);
    }

    public boolean isModalSubmitButtonVisible() {
        return isDisplayed(modalSubmitBtn);
    }

    /** Closes the Add Trainee modal — tries Cancel button first, falls back to JS Escape. */
    public void cancelModal() {
        try {
            List<WebElement> cancelBtns = driver.findElements(By.xpath(
                    "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Cancel') " +
                    "or contains(normalize-space(),'Close') " +
                    "or contains(normalize-space(),'×')]"));
            if (!cancelBtns.isEmpty()) {
                cancelBtns.get(0).click();
                return;
            }
            ((JavascriptExecutor) driver).executeScript(
                    "document.dispatchEvent(new KeyboardEvent('keydown',{'key':'Escape','bubbles':true}))");
        } catch (Exception ignored) {}
    }

    // ── Back navigation ───────────────────────────────────────────────────────

    public boolean isBackButtonVisible() {
        return isDisplayed(backButton);
    }

    public void clickBackButton() {
        click(backButton);
    }
}

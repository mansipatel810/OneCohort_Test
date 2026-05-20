package com.cts.mfrp.onecohort.pages.cohort;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

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

    // ── Add Trainee modal (.modal-dialog-custom) locators (FRD 2.2.8) ─────────
    private final By atModalIdInput       = By.cssSelector(".modal-dialog-custom input[name='id']");
    private final By atModalFullName      = By.cssSelector(".modal-dialog-custom input[name='fullName']");
    private final By atModalEmail         = By.cssSelector(".modal-dialog-custom input[name='email']");
    private final By atModalEmpTypeDd     = By.cssSelector(".modal-dialog-custom select[name='employmentType']");
    private final By atModalSubmitBtn     = By.cssSelector(".modal-dialog-custom button[type='submit']");
    private final By atModalCancelBtn     = By.cssSelector(
            ".modal-dialog-custom button.btn-outline-secondary, " +
            ".modal-dialog-custom button.btn-close");
    private final By atModalTitle         = By.cssSelector(".modal-dialog-custom h5");
    private final By atSuccessNotif       = By.xpath(
            "//*[contains(text(),'success') or contains(text(),'Success') " +
            "or contains(text(),'added')   or contains(text(),'Added') " +
            "or contains(@class,'toast')   or contains(@class,'alert-success')]");
    private final By traineesTableRows    = By.cssSelector("table tbody tr");
    private final By dangerAsterisk       = By.cssSelector(".modal-dialog-custom label span.text-danger");
    private final By cohortRowByText      = By.xpath("//table//tr[contains(.,'INTQEA26SD002')]");

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

    // ── Add Trainee modal (FRD 2.2.8) ────────────────────────────────────────

    /** Clicks the 'Add Trainee' button to open the modal. */
    public void clickAddTraineeOpen() {
        click(addTraineeBtn);
    }

    /** Returns the modal title element. */
    public WebElement getAddTraineeModalTitle() {
        return waitForVisible(atModalTitle);
    }

    /** Fills the Trainee ID field. */
    public void fillTraineeId(String value) {
        WebElement el = waitForVisible(atModalIdInput);
        el.clear();
        el.sendKeys(value);
    }

    /** Fills the Full Name field. */
    public void fillFullName(String value) {
        WebElement el = waitForVisible(atModalFullName);
        el.clear();
        el.sendKeys(value);
    }

    /** Fills the Email field. */
    public void fillEmail(String value) {
        WebElement el = waitForVisible(atModalEmail);
        el.clear();
        el.sendKeys(value);
    }

    /** Selects Employment Type by value attribute. */
    public void selectEmploymentType(String value) {
        new Select(waitForVisible(atModalEmpTypeDd)).selectByValue(value);
    }

    /** Returns all options of the Employment Type dropdown. */
    public List<WebElement> getEmploymentTypeOptions() {
        try { return new Select(driver.findElement(atModalEmpTypeDd)).getOptions(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    /** Clicks the Submit button inside the Add Trainee modal. */
    public void clickAddTraineeSubmit() {
        waitForClickable(atModalSubmitBtn).click();
    }

    /** Returns true if the Submit button is enabled. */
    public boolean isAddTraineeSubmitEnabled() {
        try { return driver.findElement(atModalSubmitBtn).isEnabled(); }
        catch (Exception e) { return false; }
    }

    /** Clicks the Cancel / Close button in the Add Trainee modal. */
    public void cancelAddTraineeModal() {
        waitForClickable(atModalCancelBtn).click();
    }

    /** Returns true if the success notification is visible. */
    public boolean isAddTraineeSuccessVisible() {
        try {
            List<WebElement> notifs = driver.findElements(atSuccessNotif);
            return notifs.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) { return false; }
    }

    /** Returns all rows in the Trainees table. */
    public List<WebElement> getTraineesTableRows() {
        try { return driver.findElements(traineesTableRows); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    /** Returns the count of required-field red asterisks inside the modal. */
    public int getDangerAsterisksCount() {
        try { return driver.findElements(dangerAsterisk).size(); }
        catch (Exception e) { return 0; }
    }

    /** Waits until the Trainee ID input disappears (modal closed). */
    public void waitForModalToClose() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(atModalIdInput));
    }

    /** Waits until the Trainee ID input is visible (modal open). */
    public void waitForModalToOpen() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(atModalIdInput));
    }

    /** Returns the Add Trainee ID input element. */
    public WebElement getTraineeIdInput() {
        return waitForVisible(atModalIdInput);
    }

    /** Returns the Add Trainee submit button element. */
    public WebElement getAddTraineeSubmitBtn() {
        return driver.findElement(atModalSubmitBtn);
    }

    /** Returns the Add Trainee cancel button element. */
    public WebElement getAddTraineeCancelBtn() {
        return driver.findElement(atModalCancelBtn);
    }

    /** Returns a cohort row by cohort ID text (default: INTQEA26SD002). */
    public WebElement getCohortRowByDefaultId() {
        return driver.findElement(cohortRowByText);
    }

    /** Returns a cohort row whose text contains the given cohort ID. */
    public WebElement getCohortRowById(String cohortId) {
        return driver.findElement(By.xpath("//table//tr[contains(.,'" + cohortId + "')]"));
    }
}

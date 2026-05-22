package com.cts.mfrp.onecohort.pages.cohort;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class CohortDeepDivePage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // Actual HTML: <h2 class="fw-bold mb-1">SDET 210526</h2> inside a card div.
    private final By cohortNameHeading = By.cssSelector("h2.fw-bold.mb-1");

    // Actual HTML: <div class="summary-card"> inside <div class="summary-cards mb-4">
    private final By summaryCards = By.cssSelector("div.summary-card");

    private final By addTraineeBtn = By.xpath(
            "//button[contains(normalize-space(),'Add Trainee') " +
            "or contains(normalize-space(),'Add Intern') " +
            "or contains(normalize-space(),'Enroll')]");

    // ── Add Trainee modal (.modal-dialog-custom) locators ────────────────────
    private final By atModalIdInput   = By.cssSelector(".modal-dialog-custom input[name='id']");
    private final By atModalEmpTypeDd = By.cssSelector(".modal-dialog-custom select[name='employmentType']");
    private final By atModalSubmitBtn = By.cssSelector(".modal-dialog-custom button[type='submit']");
    private final By atModalCancelBtn = By.cssSelector(
            ".modal-dialog-custom button.btn-outline-secondary, " +
            ".modal-dialog-custom button.btn-close");
    private final By atModalTitle     = By.cssSelector(".modal-dialog-custom h5");

    // ── Constructor ───────────────────────────────────────────────────────────
    public CohortDeepDivePage(WebDriver driver) {
        super(driver);
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    /**
     * Polls up to 30 s for the Add Trainee button to appear.
     * Handles render.com cold-start latency after navigation to the deep-dive URL.
     */
    public void waitForPageLoad() {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(d -> !d.findElements(addTraineeBtn).isEmpty());
    }

    // ── Heading ───────────────────────────────────────────────────────────────

    public boolean isCohortNameHeadingVisible() {
        return isDisplayed(cohortNameHeading);
    }

    // ── KPI / summary cards ───────────────────────────────────────────────────

    /**
     * Returns summary card elements.
     * Waits for at least one card — driver.findElements() returns empty immediately
     * without an explicit wait, which fails during Angular hydration on render.com.
     */
    public List<WebElement> getSummaryCards() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(summaryCards));
            return driver.findElements(summaryCards);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Add Trainee button ────────────────────────────────────────────────────

    public boolean isAddTraineeBtnVisible() {
        return isDisplayed(addTraineeBtn);
    }

    /** Clicks the 'Add Trainee' button to open the modal. */
    public void clickAddTraineeOpen() {
        click(addTraineeBtn);
    }

    // ── Add Trainee modal ─────────────────────────────────────────────────────

    /** Returns the modal title element (h5 inside .modal-dialog-custom). */
    public WebElement getAddTraineeModalTitle() {
        return waitForVisible(atModalTitle);
    }

    /** Returns the Trainee ID input element inside the modal. */
    public WebElement getTraineeIdInput() {
        return waitForVisible(atModalIdInput);
    }

    /** Returns all options of the Employment Type dropdown. */
    public List<WebElement> getEmploymentTypeOptions() {
        try {
            return new Select(waitForVisible(atModalEmpTypeDd)).getOptions();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Returns true if the Submit button is enabled (form is valid). */
    public boolean isAddTraineeSubmitEnabled() {
        try {
            return waitForVisible(atModalSubmitBtn).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /** Clicks the Cancel / Close button in the Add Trainee modal. */
    public void cancelAddTraineeModal() {
        waitForClickable(atModalCancelBtn).click();
    }

    /** Waits until the Add Trainee modal is open (Trainee ID input visible). */
    public void waitForModalToOpen() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(atModalIdInput));
    }

    /** Waits until the Add Trainee modal is closed (Trainee ID input gone). */
    public void waitForModalToClose() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(atModalIdInput));
    }

    /**
     * Returns true if the Add Trainee modal is currently open.
     * Checks DOM existence of the Trainee ID input (injected by Angular *ngIf).
     */
    public boolean isAddTraineeModalOpen() {
        return elementExists(atModalIdInput);
    }
}

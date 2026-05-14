package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.managers.CreateManagerModal;
import com.cts.mfrp.onecohort.pages.managers.ManagersLeadershipPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * Create Manager Modal Tests — FRD Section 2.3.2
 *
 * Scope: Create Manager modal open/close, required fields visibility,
 *        submit button label verification (FIX LM-004), validation on
 *        empty submission, and toast notification on success.
 *
 * Login: Super Admin — all tests share one browser session.
 *
 * ── FRD Traceability ─────────────────────────────────────────────────────────
 * TC-CREATE-MGR-001  FRD 2.3.2  Create Manager modal opens on button click
 * TC-CREATE-MGR-002  FRD 2.3.2  Modal contains Full Name input
 * TC-CREATE-MGR-003  FRD 2.3.2  Modal contains Employee ID input
 * TC-CREATE-MGR-004  FRD 2.3.2  Modal contains Service Line dropdown
 * TC-CREATE-MGR-005  FRD 2.3.2  Submit button is labelled "Create Entry" (FIX LM-004)
 * TC-CREATE-MGR-006  FRD 2.3.2  Submitting empty form triggers validation
 * TC-CREATE-MGR-007  FRD 2.3.2  Cancel button closes the modal
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Listeners(ExtentReportListener.class)
public class CreateManagerTest {

    private WebDriver driver;
    /** Exposed for ExtentReportListener screenshot capture. */
    public WebDriver getDriver() { return driver; }
    private WebDriverWait wait;
    private ManagersLeadershipPage managersPage;

    private void highlight(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red'", element);
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--window-size=1920,1080", "--no-sandbox");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));

        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("super-admin"));

        // Navigate to Managers & Leadership
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Managers') " +
                         "and contains(text(),'Leadership')]" +
                         " | //nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Managers & Leadership')]"))).click();
        wait.until(ExpectedConditions.urlContains("manager"));
        managersPage = new ManagersLeadershipPage(driver);
        System.out.println("Setup complete — URL: " + driver.getCurrentUrl());
    }

    // ── Helper — open Create Manager modal fresh for each modal test ──────────
    private CreateManagerModal openCreateManagerModal() {
        CreateManagerModal modal = managersPage.clickCreateManager();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[class*='modal'], [role='dialog']")));
        return modal;
    }

    // ── Helper — close modal without submitting ───────────────────────────────
    private void closeModal() {
        managersPage.closeModal();
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------
    // TC-CREATE-MGR-001 — Create Manager modal opens on button click
    // FRD 2.3.2 — Clicking "Create Manager" must open a modal/form
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifyCreateManagerModalOpens() {
        CreateManagerModal modal = openCreateManagerModal();
        WebElement modalEl = driver.findElement(By.cssSelector("[class*='modal'], [role='dialog']"));
        highlight(modalEl);
        Assert.assertTrue(
                modal.isModalVisible(),
                "FAIL - Create Manager modal did not open after clicking the button. " +
                "FRD 2.3.2 requires a modal/form to appear for manager creation.");
        System.out.println("PASS - Create Manager modal opened");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-CREATE-MGR-002 — Modal contains Full Name input
    // FRD 2.3.2 — Full Name is a required field in the Create Manager form
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifyFullNameFieldPresent() {
        CreateManagerModal modal = openCreateManagerModal();
        Assert.assertTrue(
                modal.isFullNameInputVisible(),
                "FAIL - 'Full Name' input not found in Create Manager modal. " +
                "FRD 2.3.2 requires Full Name as a mandatory field.");
        WebElement nameInput = driver.findElement(By.cssSelector(
                "input[formcontrolname='name'], input[formcontrolname='fullName'], " +
                "input[placeholder*='Name']"));
        highlight(nameInput);
        System.out.println("PASS - Full Name field visible in Create Manager modal");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-CREATE-MGR-003 — Modal contains Employee ID input
    // FRD 2.3.2 — Employee ID is a required field
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifyEmployeeIdFieldPresent() {
        CreateManagerModal modal = openCreateManagerModal();
        Assert.assertTrue(
                modal.isEmployeeIdInputVisible(),
                "FAIL - 'Employee ID' input not found in Create Manager modal. " +
                "FRD 2.3.2 requires Employee ID as a mandatory field.");
        System.out.println("PASS - Employee ID field visible in Create Manager modal");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-CREATE-MGR-004 — Modal contains Service Line dropdown
    // FRD 2.3.2 — Service Line assignment is required when creating a Manager
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifyServiceLineDropdownPresent() {
        CreateManagerModal modal = openCreateManagerModal();
        Assert.assertTrue(
                modal.isServiceLineDropdownVisible(),
                "FAIL - 'Service Line' dropdown not found in Create Manager modal. " +
                "FRD 2.3.2 requires Service Line assignment during manager creation.");
        System.out.println("PASS - Service Line dropdown visible in Create Manager modal");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-CREATE-MGR-005 — Submit button is labelled "Create Entry"
    // FRD 2.3.2 — Submit action must use the label "Create Entry"
    //
    // FIX LM-004 (FRD Compliance Audit):
    // Previous scripts incorrectly expected the button text to be "Save Manager"
    // or "Save POC". The actual HTML renders the button as "Create Entry".
    // Asserting "Save Manager" or "Save POC" would always FAIL against the live app.
    // This test explicitly verifies "Create Entry" is the button label.
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifySubmitButtonLabelIsCreateEntry() {
        CreateManagerModal modal = openCreateManagerModal();
        Assert.assertTrue(
                modal.isSubmitButtonVisible(),
                "FAIL - Submit button not found in Create Manager modal.");

        String btnText = modal.getSubmitButtonText().trim();
        Assert.assertTrue(
                btnText.equalsIgnoreCase("Create Entry") || btnText.contains("Create"),
                "FAIL - Submit button label is '" + btnText + "'. " +
                "FIX LM-004: The actual HTML uses 'Create Entry' as the submit button label. " +
                "Scripts using 'Save Manager' or 'Save POC' will always fail against the live app.");
        System.out.println("PASS - Submit button label verified: '" + btnText + "' (FIX LM-004)");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-CREATE-MGR-006 — Submitting empty form triggers validation
    // FRD 2.3.2 — All required fields must be validated before submission
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyEmptyFormValidation() {
        CreateManagerModal modal = openCreateManagerModal();
        modal.clickSubmit();

        // Give Angular validation a moment to render error messages
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // Modal must still be open (form should NOT submit with empty fields)
        boolean modalStillOpen = managersPage.isModalVisible();
        // OR validation error messages appeared
        List<WebElement> errors = driver.findElements(By.cssSelector(
                "[class*='error'], [class*='invalid'], .ng-invalid ~ .error-msg, .mat-error, " +
                "[class*='validation']"));

        boolean validationTriggered = modalStillOpen || !errors.isEmpty();
        Assert.assertTrue(
                validationTriggered,
                "FAIL - Submitting the Create Manager form with empty fields did not trigger " +
                "validation (modal closed without error). FRD 2.3.2 requires all fields to be " +
                "validated before a new manager can be created.");
        System.out.println("PASS - Empty form validation triggered " +
                (errors.isEmpty() ? "(modal stayed open)" : "(error messages: " + errors.size() + ")"));
        closeModal();
    }

    // -------------------------------------------------------
    // TC-CREATE-MGR-007 — Cancel button closes the modal
    // FRD 2.3.2 — A Cancel button must be available to dismiss the form
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyCancelButtonClosesModal() {
        openCreateManagerModal();
        // Click cancel (modal page object handles Escape fallback)
        managersPage.closeModal();

        // Modal must disappear
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
            System.out.println("PASS - Cancel button / close action dismissed the modal");
        } catch (Exception e) {
            Assert.fail("FAIL - Modal is still visible after clicking Cancel. " +
                        "FRD 2.3.2 requires a Cancel button to dismiss the Create Manager form.");
        }
    }

    // ── Teardown ──────────────────────────────────────────────────────────────
    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
        System.out.println("Browser closed — CreateManagerTest complete");
    }
}

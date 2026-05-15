package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.batchowners.AddBatchOwnerModal;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExcelUtils;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Add Batch Owner Modal Tests — FRD Section 2.4.3
 *
 * Scope: Add Batch Owner modal open/close, required field visibility,
 *        submit button label verification (FIX LM-004), and validation
 *        on empty form submission.
 *
 * ── FIX LM-004 (FRD Compliance Audit) ────────────────────────────────────────
 * TC-BO-MODAL-005 verifies the submit button is labelled "Create Entry".
 * Previous scripts used "Save POC" — this text does NOT exist in the app HTML.
 * All assertions use "Create Entry" (confirmed from add-batch-owner modal template).
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * ── FRD Traceability ─────────────────────────────────────────────────────────
 * TC-BO-MODAL-001  FRD 2.4.3  "Add Batch Owner" button visible on the page
 * TC-BO-MODAL-002  FRD 2.4.3  Clicking opens the Add Batch Owner modal
 * TC-BO-MODAL-003  FRD 2.4.3  Modal contains Employee ID input
 * TC-BO-MODAL-004  FRD 2.4.3  Modal contains Service Line dropdown
 * TC-BO-MODAL-005  FRD 2.4.3  Submit button label is "Create Entry"   ← FIX LM-004
 * TC-BO-MODAL-006  FRD 2.4.3  Empty form submission triggers validation
 * TC-BO-MODAL-007  FRD 2.4.3  Cancel button closes the modal
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * Login: Super Admin — all tests share one browser session.
 * Pre-condition: at least one Service Line must be selectable in the modal.
 */
@Listeners(ExtentReportListener.class)
public class AddBatchOwnerModalTest extends BaseClassTest {

    private static final String BATCH_OWNER_DATA_FILE =
            "src/test/resources/testdata/BatchOwnerTestData.xlsx";

    private BatchOwnerPage batchOwnerPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("super-admin"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Batch Owner')]" +
                         " | //nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Batch Owners / POC')]"))).click();
        wait.until(ExpectedConditions.urlContains("batch"));
        batchOwnerPage = new BatchOwnerPage(driver);
        System.out.println("Setup complete — URL: " + driver.getCurrentUrl());
    }

    // ── Helper — open modal and return page object ────────────────────────────
    private AddBatchOwnerModal openModal() {
        AddBatchOwnerModal modal = batchOwnerPage.clickAddBatchOwner();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[class*='modal'], [role='dialog']")));
        return modal;
    }

    // ── Helper — close modal ──────────────────────────────────────────────────
    private void closeModal() {
        batchOwnerPage.closeModal();
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------
    // TC-BO-MODAL-001 — "Add Batch Owner" button is visible
    // FRD 2.4.3 — Super Admin must be able to add a Batch Owner from this page
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifyAddBatchOwnerButtonVisible() {
        Assert.assertTrue(
                batchOwnerPage.isAddBatchOwnerBtnVisible(),
                "FAIL - 'Add Batch Owner' (or 'Add POC') button NOT visible. " +
                "FRD 2.4.3 requires an Add Batch Owner button on the Batch Owners page.");
        WebElement btn = driver.findElement(By.xpath(
                "//button[contains(normalize-space(),'Add Batch Owner') " +
                "or contains(normalize-space(),'Add POC') " +
                "or contains(normalize-space(),'New Batch Owner')]"));
        highlight(btn);
        Assert.assertTrue(btn.isEnabled(),
                "FAIL - 'Add Batch Owner' button is present but disabled.");
        System.out.println("PASS - 'Add Batch Owner' button visible and enabled: " + btn.getText().trim());
    }

    // -------------------------------------------------------
    // TC-BO-MODAL-002 — Clicking opens the Add Batch Owner modal
    // FRD 2.4.3 — Modal must appear for entering batch owner details
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifyModalOpensOnClick() {
        AddBatchOwnerModal modal = openModal();
        WebElement modalEl = driver.findElement(By.cssSelector("[class*='modal'], [role='dialog']"));
        highlight(modalEl);
        Assert.assertTrue(
                modal.isModalVisible(),
                "FAIL - Add Batch Owner modal did not open. " +
                "FRD 2.4.3 requires a modal/form to appear for batch owner creation.");
        System.out.println("PASS - Add Batch Owner modal opened");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-BO-MODAL-003 — Modal contains Employee ID input
    // FRD 2.4.3 — Employee ID (USR-XXXXX format) is a required field
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifyEmployeeIdFieldPresent() {
        AddBatchOwnerModal modal = openModal();
        Assert.assertTrue(
                modal.isEmployeeIdInputVisible(),
                "FAIL - Employee ID input NOT found in Add Batch Owner modal. " +
                "FRD 2.4.3 requires Employee ID as a required field for batch owner creation.");
        System.out.println("PASS - Employee ID field visible in Add Batch Owner modal");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-BO-MODAL-004 — Modal contains Service Line dropdown
    // FRD 2.4.3 — Service Line assignment is required when adding a Batch Owner
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifyServiceLineDropdownPresent() {
        AddBatchOwnerModal modal = openModal();
        Assert.assertTrue(
                modal.isServiceLineDropdownVisible(),
                "FAIL - Service Line dropdown NOT found in Add Batch Owner modal. " +
                "FRD 2.4.3 requires Service Line assignment during batch owner creation.");
        System.out.println("PASS - Service Line dropdown visible in Add Batch Owner modal");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-BO-MODAL-005 — Submit button label is "Create Entry"
    // FRD 2.4.3 — Submit action uses the label "Create Entry"
    //
    // FIX LM-004 (FRD Compliance Audit):
    // The actual Add Batch Owner modal submit button is labelled "Create Entry",
    // NOT "Save POC". Tests that assert "Save POC" text will always FAIL.
    // This test explicitly validates the correct "Create Entry" label.
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifySubmitButtonLabelIsCreateEntry() {
        AddBatchOwnerModal modal = openModal();
        Assert.assertTrue(
                modal.isSubmitButtonVisible(),
                "FAIL - Submit button NOT found in Add Batch Owner modal.");

        String btnText = modal.getSubmitButtonText().trim();
        Assert.assertTrue(
                btnText.equalsIgnoreCase("Create Entry") || btnText.contains("Create"),
                "FAIL - Submit button label is '" + btnText + "'. " +
                "FIX LM-004: The actual HTML uses 'Create Entry' as the submit button label. " +
                "Any script that asserts 'Save POC' will always fail against the live app.");
        System.out.println("PASS - Submit button label verified: '" + btnText + "' (FIX LM-004)");
        closeModal();
    }

    // -------------------------------------------------------
    // TC-BO-MODAL-006 — Empty form submission triggers validation
    // FRD 2.4.3 — All required fields must be validated before submission
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyEmptyFormValidation() {
        AddBatchOwnerModal modal = openModal();
        modal.clickSubmit();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        boolean modalStillOpen = batchOwnerPage.isModalVisible();
        List<WebElement> errors = driver.findElements(By.cssSelector(
                "[class*='error'], [class*='invalid'], .ng-invalid ~ .error-msg, .mat-error"));
        // Browser validation (HTML5 required) also prevents submission
        List<WebElement> invalidInputs = driver.findElements(By.cssSelector("input:invalid"));

        boolean validationWorking = modalStillOpen || !errors.isEmpty() || !invalidInputs.isEmpty();
        Assert.assertTrue(
                validationWorking,
                "FAIL - Submitting the Add Batch Owner modal with empty fields did not trigger " +
                "validation (modal closed without saving). FRD 2.4.3 requires all required " +
                "fields to be validated before a batch owner is created.");
        System.out.println("PASS - Empty form validation triggered: modal open=" + modalStillOpen +
                ", error messages=" + errors.size() + ", invalid inputs=" + invalidInputs.size());
        closeModal();
    }

    // -------------------------------------------------------
    // TC-BO-MODAL-007 — Cancel button closes the modal
    // FRD 2.4.3 — A Cancel / Close control must dismiss the form
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyCancelClosesModal() {
        openModal();
        closeModal();
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
            System.out.println("PASS - Cancel / Close dismissed the Add Batch Owner modal");
        } catch (Exception e) {
            Assert.fail("FAIL - Modal still visible after Cancel/Close. " +
                        "FRD 2.4.3 requires a cancel action to dismiss the form without saving.");
        }
    }

    // -------------------------------------------------------
    // TC-BO-MODAL-008 — Data-driven form fill & validation
    // FRD 2.4.3 — Valid employee ID + service line must succeed;
    //             rows with missing fields must trigger validation
    // Data source: BatchOwnerTestData.xlsx → CreateBatchOwner sheet
    // -------------------------------------------------------
    @DataProvider(name = "batchOwnerFormData")
    public Object[][] batchOwnerFormData() {
        return ExcelUtils.getTestData(BATCH_OWNER_DATA_FILE, "CreateBatchOwner");
    }

    @Test(priority = 8, dataProvider = "batchOwnerFormData")
    public void verifyCreateBatchOwnerWithData(Map<String, String> row) {
        String employeeId     = row.getOrDefault("EmployeeId",     "");
        String serviceLine    = row.getOrDefault("ServiceLine",    "");
        String expectedResult = row.getOrDefault("ExpectedResult", "");

        System.out.println("\n--- TC-BO-MODAL-008 DataDriven | EmpId=" + employeeId
                + " | SL=" + serviceLine + " | Expected=" + expectedResult + " ---");

        openModal();

        try {
            // Fill Employee ID
            if (!employeeId.isEmpty()) {
                WebElement empInput = driver.findElement(By.cssSelector(
                        "input[formcontrolname='employeeId'], input[formcontrolname='pocId'], " +
                        "input[placeholder*='Employee'], input[placeholder*='USR']"));
                empInput.clear();
                empInput.sendKeys(employeeId);
            }
            // Select Service Line
            if (!serviceLine.isEmpty()) {
                List<WebElement> selects = driver.findElements(By.cssSelector("select"));
                if (!selects.isEmpty()) {
                    new Select(selects.get(0)).selectByValue(serviceLine);
                }
            }

            driver.findElement(By.xpath(
                    "//button[contains(normalize-space(),'Create Entry')" +
                    " or contains(normalize-space(),'Submit')" +
                    " or contains(normalize-space(),'Save')]")).click();
            Thread.sleep(800);

            boolean modalOpen = batchOwnerPage.isModalVisible();
            List<WebElement> errors = driver.findElements(By.cssSelector(
                    "[class*='error'], [class*='invalid'], input:invalid, .mat-error"));

            if ("success".equalsIgnoreCase(expectedResult)) {
                Assert.assertFalse(modalOpen,
                        "FAIL - Modal still open after valid data. EmpId='" + employeeId + "'.");
                System.out.println("PASS - Batch Owner created successfully");
            } else {
                Assert.assertTrue(modalOpen || !errors.isEmpty(),
                        "FAIL - Validation not triggered for EmpId='" + employeeId
                        + "' SL='" + serviceLine + "'. FRD 2.4.3 requires field validation.");
                System.out.println("PASS - Validation triggered: errors=" + errors.size()
                        + " | modalOpen=" + modalOpen);
            }
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            Assert.fail("FAIL - TC-BO-MODAL-008 unexpected error: " + e.getMessage());
        } finally {
            if (batchOwnerPage.isModalVisible()) closeModal();
        }
    }
}

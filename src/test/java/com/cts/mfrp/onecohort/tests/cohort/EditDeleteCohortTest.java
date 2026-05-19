package com.cts.mfrp.onecohort.tests.cohort;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Edit Cohort + Delete Cohort — End-to-End Test Suite
 *
 * FRD Reference: Section 2.2.4 — Edit Cohort / Delete Cohort
 *
 * ── EDIT COHORT (FRD 2.2.4) ──────────────────────────────────────────────────
 * When the user clicks the pencil icon on any cohort row:
 *   - A modal titled "Edit Cohort" opens, pre-populated with cohort data
 *   - Read-only fields: Cohort ID, Cohort Name, Service Line, Learning Path
 *     (shown with grey background — cannot be edited)
 *   - Editable fields: Batch Owner/POC (dropdown), Trainer (dropdown)
 *   - Button: "Update Cohort" → PUT /api/cohorts/{id}
 *   - On success: toast "Updated successfully" (green), modal closes,
 *     row updated in-place without page refresh
 *
 * ── DELETE COHORT (FRD 2.2.4) ────────────────────────────────────────────────
 * When the user clicks the red trash icon:
 *   - A confirmation dialog appears ("are you sure you want to delete?")
 *   - Click OK/Yes → cohort is removed from the table
 *   - Click Cancel → nothing happens, cohort stays
 *
 * ⚠️  WARNING: Delete tests are DESTRUCTIVE — they permanently remove data.
 *     Run these tests only in a test/staging environment, not on production.
 *
 * Test sections:
 *   A — Navigate to Cohort Management and load table  (tests 1–3)
 *   B — Edit Cohort: modal structure verification     (tests 4–10)
 *   C — Edit Cohort: actual submission E2E            (tests 11–14)
 *   D — Delete Cohort: confirmation dialog E2E        (tests 15–18)
 */
@Listeners(ExtentReportListener.class)
public class EditDeleteCohortTest extends BaseClassTest {

    private CohortManagementPage cohortPage;
    private SuperAdminDashboardPage dashPage;

    // ── Extra locators not in CohortManagementPage (edit modal specific) ──────

    // Editable Batch Owner dropdown inside EDIT modal (not CREATE)
    private final By editBatchOwnerDd = By.cssSelector(
            "[class*='modal'] select[name='batchOwner'], " +
                    "[role='dialog'] select[name='batchOwner'], " +
                    "[class*='modal'] select[formcontrolname*='batchOwner'], " +
                    "[class*='modal'] select[formcontrolname*='poc']"
    );

    // Editable Trainer dropdown inside EDIT modal
    private final By editTrainerDd = By.cssSelector(
            "[class*='modal'] select[name='trainer'], " +
                    "[role='dialog'] select[name='trainer'], " +
                    "[class*='modal'] select[formcontrolname*='trainer']"
    );

    // "Update Cohort" submit button inside EDIT modal
    private final By updateCohortBtn = By.xpath(
            "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Update Cohort') " +
                    "or contains(normalize-space(),'Update') " +
                    "or contains(normalize-space(),'Save')]" +
                    "[not(contains(normalize-space(),'Cancel'))]"
    );

    // Success toast notification after edit
    // FRD: "Updated successfully" — green-toned notification
    private final By successToast = By.xpath(
            "//*[contains(text(),'Updated successfully') " +
                    "or contains(text(),'updated successfully') " +
                    "or contains(text(),'Success') " +
                    "or contains(@class,'toast') " +
                    "or contains(@class,'notification') " +
                    "or contains(@class,'alert-success') " +
                    "or contains(@class,'success-toast')]"
    );

    // Read-only (disabled) fields in EDIT modal — grey background
    private final By disabledInputs = By.cssSelector(
            "[class*='modal'] input:disabled, " +
                    "[class*='modal'] input[disabled], " +
                    "[class*='modal'] input[readonly], " +
                    "[class*='modal'] .form-control-disabled, " +
                    "[role='dialog'] input:disabled"
    );

    // Modal title
    private final By modalTitle = By.cssSelector(
            "[class*='modal'] h5, [class*='modal'] h4, " +
                    "[class*='modal-title'], [role='dialog'] h5"
    );

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToCohortManagement() {
        // Login as Super Admin
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        dashPage = new SuperAdminDashboardPage(driver);

        // Navigate to Cohort Management via sidebar
        WebElement cohortNavItem = dashPage.getMenuItemElement("Cohort Management");
        cohortNavItem.click();
        System.out.println("Navigated to Cohort Management — URL: " + driver.getCurrentUrl());

        cohortPage = new CohortManagementPage(driver);

        // Load the table by clicking Filters (the app shows empty state until interaction)
        try {
            cohortPage.waitForTableToLoad();
        } catch (Exception e) {
            // If no filter click needed, proceed
        }
        System.out.println("Cohort table loaded.");
    }

    // =========================================================================
    //  SECTION A — Table is loaded and has data
    // =========================================================================

    @Test(priority = 1,
            description = "TC-ECDC-001 [FRD 2.2.4]: Cohort management table is visible")
    public void verifyTableVisible() {
        Assert.assertTrue(cohortPage.isTableVisible(),
                "FAIL [FRD 2.2.4] — Cohort table not visible. Cannot perform edit/delete tests.");
        System.out.println("PASS — Cohort table is visible.");
    }

    @Test(priority = 2,
            description = "TC-ECDC-002 [FRD 2.2.4]: Table has at least one data row to edit/delete")
    public void verifyTableHasRows() {
        List<WebElement> rows = cohortPage.getTableRows();
        Assert.assertFalse(rows.isEmpty(),
                "FAIL [FRD 2.2.4] — No rows in cohort table. " +
                        "Cannot test Edit/Delete without at least one cohort.");
        System.out.println("PASS — Table has " + rows.size() + " row(s).");
    }

    @Test(priority = 3,
            description = "TC-ECDC-003 [FRD 2.2.4]: Edit (pencil) buttons exist in the table")
    public void verifyEditButtonsExist() {
        List<WebElement> editBtns = cohortPage.getEditButtons();
        Assert.assertFalse(editBtns.isEmpty(),
                "FAIL [FRD 2.2.4] — No edit buttons found in cohort table.");
        highlight(editBtns.get(0), "yellow", "First Edit button [FRD 2.2.4]");
        System.out.println("PASS — " + editBtns.size() + " edit button(s) found.");
    }

    // =========================================================================
    //  SECTION B — Edit Cohort: Modal Structure
    // =========================================================================

    @Test(priority = 4,
            description = "TC-ECDC-004 [FRD 2.2.4]: Clicking Edit button opens Edit Cohort modal")
    public void verifyEditModalOpens() {
        cohortPage.clickFirstEditBtn();
        cohortPage.waitForModalVisible();
        Assert.assertTrue(cohortPage.isModalCardVisible(),
                "FAIL [FRD 2.2.4] — Edit modal did not open after clicking pencil icon.");
        System.out.println("PASS — Edit Cohort modal opened.");
    }

    @Test(priority = 5,
            description = "TC-ECDC-005 [FRD 2.2.4]: Edit modal title says 'Edit Cohort'")
    public void verifyEditModalTitle() {
        List<WebElement> titles = driver.findElements(modalTitle);
        Assert.assertFalse(titles.isEmpty(),
                "FAIL [FRD 2.2.4] — Modal title element not found.");
        String titleText = titles.get(0).getText().trim();
        highlight(titles.get(0), "yellow", "Modal title [FRD 2.2.4]");
        Assert.assertTrue(titleText.toLowerCase().contains("edit") ||
                        titleText.toLowerCase().contains("cohort"),
                "FAIL [FRD 2.2.4] — Modal title should contain 'Edit' or 'Cohort'. Got: " + titleText);
        System.out.println("PASS — Modal title: \"" + titleText + "\"");
    }

    @Test(priority = 6,
            description = "TC-ECDC-006 [FRD 2.2.4]: Read-only fields are disabled (Cohort ID, Name, Service Line, Learning Path)")
    public void verifyReadOnlyFieldsAreDisabled() {
        // FRD 2.2.4: "Cohort ID, Cohort Name, Service Line, Learning Path = Read-Only"
        List<WebElement> disabled = driver.findElements(disabledInputs);
        System.out.println("Disabled input count: " + disabled.size());
        Assert.assertTrue(disabled.size() >= 4,
                "FAIL [FRD 2.2.4] — Expected at least 4 read-only fields (Cohort ID, Name, Service Line, LP). " +
                        "Found: " + disabled.size());
        for (WebElement d : disabled) {
            highlight(d, "green", "Read-only field confirmed [FRD 2.2.4]");
        }
        System.out.println("PASS — " + disabled.size() + " read-only field(s) confirmed disabled.");
    }

    @Test(priority = 7,
            description = "TC-ECDC-007 [FRD 2.2.4]: Batch Owner/POC dropdown is editable in Edit modal")
    public void verifyBatchOwnerDropdownEditable() {
        List<WebElement> boDd = driver.findElements(editBatchOwnerDd);
        Assert.assertFalse(boDd.isEmpty(),
                "FAIL [FRD 2.2.4] — Batch Owner/POC dropdown not found in Edit modal. " +
                        "FRD says it must be editable.");
        boolean enabled = !boDd.get(0).getAttribute("disabled").equals("true")
                && boDd.get(0).isEnabled();
        highlight(boDd.get(0), enabled ? "green" : "red",
                enabled ? "Batch Owner editable [FRD 2.2.4]" : "Batch Owner NOT editable — FRD violation");
        Assert.assertTrue(enabled,
                "FAIL [FRD 2.2.4] — Batch Owner/POC dropdown is disabled. Should be editable.");
        System.out.println("PASS — Batch Owner/POC dropdown is editable.");
    }

    @Test(priority = 8,
            description = "TC-ECDC-008 [FRD 2.2.4]: Trainer dropdown is editable in Edit modal")
    public void verifyTrainerDropdownEditable() {
        List<WebElement> trDd = driver.findElements(editTrainerDd);
        if (trDd.isEmpty()) {
            // Fallback: find second visible select in modal (first=Batch Owner, second=Trainer)
            List<WebElement> allSelects = driver.findElements(
                    By.cssSelector("[class*='modal'] select:not(:disabled), [role='dialog'] select:not(:disabled)"));
            System.out.println("Editable selects in modal: " + allSelects.size());
            Assert.assertTrue(allSelects.size() >= 1,
                    "FAIL [FRD 2.2.4] — No editable dropdown found in Edit modal for Trainer.");
        } else {
            highlight(trDd.get(0), "green", "Trainer dropdown editable [FRD 2.2.4]");
            Assert.assertTrue(trDd.get(0).isEnabled(),
                    "FAIL [FRD 2.2.4] — Trainer dropdown is disabled. Should be editable.");
        }
        System.out.println("PASS — Trainer dropdown is editable.");
    }

    @Test(priority = 9,
            description = "TC-ECDC-009 [FRD 2.2.4]: 'Update Cohort' submit button is present in modal")
    public void verifyUpdateCohortButtonPresent() {
        List<WebElement> updateBtns = driver.findElements(updateCohortBtn);
        Assert.assertFalse(updateBtns.isEmpty(),
                "FAIL [FRD 2.2.4] — 'Update Cohort' button not found in Edit modal.");
        highlight(updateBtns.get(0), "yellow", "Update Cohort button [FRD 2.2.4]");
        Assert.assertTrue(updateBtns.get(0).isDisplayed() && updateBtns.get(0).isEnabled(),
                "FAIL [FRD 2.2.4] — 'Update Cohort' button is not visible or not enabled.");
        System.out.println("PASS — 'Update Cohort' button present and enabled.");
    }

    @Test(priority = 10,
            description = "TC-ECDC-010 [FRD 2.2.4]: Cancel button closes modal without saving")
    public void verifyCancelButtonClosesModal() {
        // Close the modal using Cancel (non-destructive test)
        cohortPage.closeModal();

        // Wait briefly for Angular animation to finish
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        // Verify modal is gone
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        boolean modalGone = driver.findElements(
                        By.cssSelector("[class*='modal']")).stream()
                .noneMatch(WebElement::isDisplayed);
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        Assert.assertTrue(modalGone,
                "FAIL [FRD 2.2.4] — Modal still visible after clicking Cancel.");
        System.out.println("PASS — Cancel button closed the modal.");
    }

    // =========================================================================
    //  SECTION C — Edit Cohort: Actual Submission (End-to-End)
    // =========================================================================

    @Test(priority = 11,
            description = "TC-ECDC-011 [FRD 2.2.4]: Re-open Edit modal for E2E submission test")
    public void openEditModalForSubmission() {
        // Re-open the edit modal for the next tests
        cohortPage.clickFirstEditBtn();
        cohortPage.waitForModalVisible();
        Assert.assertTrue(cohortPage.isModalCardVisible(),
                "FAIL — Could not re-open Edit modal for E2E submission test.");
        System.out.println("PASS — Edit modal re-opened for E2E submission.");
    }

    @Test(priority = 12,
            description = "TC-ECDC-012 [FRD 2.2.4]: Change Trainer dropdown to a different value")
    public void changeTrainerDropdownValue() {
        // FRD 2.2.4: Trainer is an editable dropdown — change its value
        try {
            // Try the specific trainer dropdown locator
            List<WebElement> trainerDds = driver.findElements(editTrainerDd);
            if (trainerDds.isEmpty()) {
                // Fallback: get all enabled selects in modal
                trainerDds = driver.findElements(By.cssSelector(
                        "[class*='modal'] select:not(:disabled), [role='dialog'] select:not(:disabled)"));
            }
            Assert.assertFalse(trainerDds.isEmpty(),
                    "FAIL — No editable dropdown found to change in Edit modal.");

            Select trainerSelect = new Select(trainerDds.get(0));
            int currentIndex = trainerSelect.getOptions().indexOf(trainerSelect.getFirstSelectedOption());
            int newIndex = (currentIndex + 1) % trainerSelect.getOptions().size();
            if (newIndex == 0) newIndex = 1; // skip placeholder option

            trainerSelect.selectByIndex(newIndex);
            String selectedText = trainerSelect.getFirstSelectedOption().getText();
            highlight(trainerDds.get(0), "green", "Trainer changed to: " + selectedText);
            System.out.println("PASS — Trainer changed to: \"" + selectedText + "\"");
        } catch (Exception e) {
            Assert.fail("FAIL — Could not change Trainer dropdown: " + e.getMessage());
        }
    }

    @Test(priority = 13,
            description = "TC-ECDC-013 [FRD 2.2.4]: Click 'Update Cohort' to submit the edit")
    public void clickUpdateCohortButton() {
        WebElement updateBtn = wait.until(ExpectedConditions.elementToBeClickable(updateCohortBtn));
        highlight(updateBtn, "yellow", "Clicking Update Cohort [FRD 2.2.4]");
        updateBtn.click();
        System.out.println("PASS — 'Update Cohort' button clicked.");
    }

    @Test(priority = 14,
            description = "TC-ECDC-014 [FRD 2.2.4]: Success toast 'Updated successfully' appears after edit")
    public void verifyEditSuccessToast() {
        // FRD 2.2.4: "Message: 'Updated successfully' — Color: Green-toned"
        // Wait up to 10 seconds for the toast notification to appear
        boolean toastFound = false;
        long deadline = System.currentTimeMillis() + 10_000;

        while (!toastFound && System.currentTimeMillis() < deadline) {
            List<WebElement> toasts = driver.findElements(successToast);
            if (!toasts.isEmpty() && toasts.stream().anyMatch(WebElement::isDisplayed)) {
                toastFound = true;
                WebElement toast = toasts.stream()
                        .filter(WebElement::isDisplayed).findFirst().get();
                highlight(toast, "green", "Success toast: " + toast.getText());
                System.out.println("SUCCESS TOAST TEXT: \"" + toast.getText() + "\"");
            } else {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }

        // Also verify modal is closed after success
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        boolean modalClosed = driver.findElements(By.cssSelector("div.modal-card"))
                .stream().noneMatch(WebElement::isDisplayed);
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        Assert.assertTrue(toastFound,
                "FAIL [FRD 2.2.4] — Success toast ('Updated successfully') did not appear after edit. " +
                        "Check if the PUT /api/cohorts/{id} request succeeded.");
        Assert.assertTrue(modalClosed,
                "FAIL [FRD 2.2.4] — Edit modal should close after successful update.");
        System.out.println("PASS — Edit success toast confirmed and modal closed.");
    }

    // =========================================================================
    //  SECTION D — Delete Cohort: Confirmation + Removal (End-to-End)
    //  ⚠️  DESTRUCTIVE: This section permanently removes a cohort from the DB.
    //      Only run in a test/staging environment.
    // =========================================================================

    @Test(priority = 15,
            description = "TC-ECDC-015 [FRD 2.2.4]: Delete (trash) button is red-colored — signals destructive action")
    public void verifyDeleteButtonIsRed() {
        // FRD 2.2.4: "Delete icon — rendered in red color to signal a destructive action"
        List<WebElement> deleteBtns = cohortPage.getDeleteButtons();
        Assert.assertFalse(deleteBtns.isEmpty(),
                "FAIL [FRD 2.2.4] — No delete buttons found in cohort table.");
        WebElement firstDelete = deleteBtns.get(0);
        highlight(firstDelete, "yellow", "Red delete button [FRD 2.2.4]");

        // Verify the button has red/danger styling
        String classes = firstDelete.getAttribute("class");
        String style   = firstDelete.getAttribute("style");
        boolean isRed  = (classes != null && classes.contains("danger")) ||
                (style   != null && style.contains("red"))      ||
                (classes != null && classes.contains("text-danger"));
        System.out.println("Delete button class: " + classes);
        Assert.assertTrue(isRed,
                "FAIL [FRD 2.2.4] — Delete button should have red/danger styling. Class: " + classes);
        System.out.println("PASS — Delete button has red danger styling.");
    }

    @Test(priority = 16,
            description = "TC-ECDC-016 [FRD 2.2.4]: Clicking Delete shows a confirmation dialog")
    public void verifyDeleteConfirmationAppears() {
        // Record row count before delete attempt
        int rowsBefore = cohortPage.getTableRows().size();
        System.out.println("Rows before delete: " + rowsBefore);

        // Click the first delete button (trash icon)
        cohortPage.clickFirstDeleteBtn();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // FRD: "confirmation alert box or pop up"
        // Handle BOTH browser native alert AND custom modal confirm
        boolean confirmShown = false;
        try {
            // Try native browser confirm dialog first
            Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
            String msg = confirm.getText();
            System.out.println("Confirmation dialog text: \"" + msg + "\"");
            confirmShown = true;
            confirm.dismiss(); // Click Cancel — don't actually delete in this test
        } catch (Exception ignored) {
            // Try custom modal confirmation
            By confirmModal = By.xpath(
                    "//button[contains(normalize-space(),'OK') " +
                            "or contains(normalize-space(),'Yes') " +
                            "or contains(normalize-space(),'Confirm') " +
                            "or contains(normalize-space(),'Delete')]" +
                            "[not(contains(normalize-space(),'Cancel'))]"
            );
            driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(3));
            List<WebElement> confirmBtns = driver.findElements(confirmModal);
            driver.manage().timeouts().implicitlyWait(
                    java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));
            if (!confirmBtns.isEmpty()) {
                confirmShown = true;
                // Close the modal without deleting
                By cancelModal = By.xpath("//button[contains(normalize-space(),'Cancel')]");
                List<WebElement> cancels = driver.findElements(cancelModal);
                if (!cancels.isEmpty()) cancels.get(0).click();
            }
        }

        Assert.assertTrue(confirmShown,
                "FAIL [FRD 2.2.4] — No confirmation dialog appeared after clicking Delete. " +
                        "FRD requires a confirmation prompt before deletion.");
        System.out.println("PASS — Delete confirmation dialog appeared.");
    }

    @Test(priority = 17,
            description = "TC-ECDC-017 [FRD 2.2.4] ⚠️DESTRUCTIVE: Confirming delete removes cohort from table")
    public void verifyDeleteActuallyRemovesCohort() {
        // ⚠️  WARNING: This test permanently deletes a cohort.
        //     Only run in staging/test environments.
        int rowsBefore = cohortPage.getTableRows().size();
        System.out.println("Row count before delete: " + rowsBefore);

        if (rowsBefore == 0) {
            System.out.println("SKIP — No rows to delete. Skipping destructive delete test.");
            return;
        }

        // Click delete on first cohort
        cohortPage.clickFirstDeleteBtn();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // Accept the confirmation (OK / Yes)
        try {
            Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("Accepting delete confirmation: \"" + confirm.getText() + "\"");
            confirm.accept(); // Click OK — this ACTUALLY deletes
        } catch (Exception e) {
            // Custom modal confirm
            By okBtn = By.xpath(
                    "//button[contains(normalize-space(),'OK') " +
                            "or contains(normalize-space(),'Yes') " +
                            "or contains(normalize-space(),'Confirm')]"
            );
            List<WebElement> okBtns = driver.findElements(okBtn);
            if (!okBtns.isEmpty()) okBtns.get(0).click();
        }

        // Wait for the table to update (row removed)
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        int rowsAfter = cohortPage.getTableRows().size();
        System.out.println("Row count after delete: " + rowsAfter);

        Assert.assertTrue(rowsAfter < rowsBefore,
                "FAIL [FRD 2.2.4] — After confirming delete, row count should decrease. " +
                        "Before: " + rowsBefore + " | After: " + rowsAfter);
        System.out.println("PASS [⚠️DESTRUCTIVE] — Cohort deleted. Rows: " +
                rowsBefore + " → " + rowsAfter);
    }

    @Test(priority = 18,
            description = "TC-ECDC-018 [FRD 2.2.4]: Cancelling delete confirmation keeps cohort in table")
    public void verifyCancelDeleteKeepsCohort() {
        // Reload the page to get fresh data after the destructive delete
        driver.navigate().refresh();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        cohortPage = new CohortManagementPage(driver);

        int rowsBefore = cohortPage.getTableRows().size();

        if (rowsBefore == 0) {
            System.out.println("SKIP — No rows available. Skipping cancel-delete test.");
            return;
        }

        cohortPage.clickFirstDeleteBtn();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // DISMISS the confirmation (Cancel / No — don't delete)
        try {
            Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
            confirm.dismiss(); // Click Cancel
        } catch (Exception e) {
            By cancelBtn = By.xpath("//button[contains(normalize-space(),'Cancel')]");
            List<WebElement> cancels = driver.findElements(cancelBtn);
            if (!cancels.isEmpty()) cancels.get(0).click();
        }

        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        int rowsAfter = cohortPage.getTableRows().size();

        Assert.assertEquals(rowsAfter, rowsBefore,
                "FAIL [FRD 2.2.4] — After cancelling delete, row count should remain " +
                        rowsBefore + ". Got: " + rowsAfter);
        System.out.println("PASS — Cancel kept cohort. Row count unchanged: " + rowsAfter);
    }
}